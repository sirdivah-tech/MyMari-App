package com.example.data.ai

import android.util.Log
import com.example.data.model.FinanceConstants
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

data class VoiceParsedTransaction(
    val transcript: String,
    val type: String, // "INCOME" or "EXPENSE"
    val amount: Double?,
    val categoryOrSource: String,
    val paymentMethod: String,
    val description: String
)

class VoiceTranscriptionRepository {

    private val transcribeModel by lazy {
        Firebase.ai.generativeModel("gemini-3.5-transcribe")
    }

    private val transactionParserModel by lazy {
        val schema = Schema.obj(
            properties = mapOf(
                "type" to Schema.enumeration(
                    values = listOf("EXPENSE", "INCOME"),
                    description = "Whether the student spent money (EXPENSE) or received money (INCOME)."
                ),
                "amount" to Schema.double(
                    description = "Numeric amount mentioned, or 0 if not mentioned."
                ),
                "categoryOrSource" to Schema.string(
                    description = "Best matching category from: ${FinanceConstants.EXPENSE_CATEGORIES.joinToString()} or source from: ${FinanceConstants.INCOME_SOURCES.joinToString()}."
                ),
                "paymentMethod" to Schema.string(
                    description = "Best matching payment method from: ${FinanceConstants.PAYMENT_METHODS.joinToString()}."
                ),
                "description" to Schema.string(
                    description = "Short, plain description of what was bought or received."
                )
            )
        )
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = schema
            }
        )
    }

    suspend fun transcribeAudio(audioBytes: ByteArray, mimeType: String = "audio/mp4"): Result<String> {
        return try {
            require(audioBytes.isNotEmpty()) { "No audio was recorded. Please try speaking again." }
            require(audioBytes.size < 20 * 1024 * 1024) { "Recording is too large. Keep voice notes under 1 minute." }

            val response = withTimeout(60_000L) {
                transcribeModel.generateContent(
                    content {
                        inlineData(audioBytes, mimeType)
                        text("Transcribe this spoken student voice recording clearly and accurately. Return only the transcribed words.")
                    }
                )
            }
            val text = response.text?.trim().orEmpty()
            if (text.isBlank()) {
                Result.failure(IllegalStateException("Could not hear clear speech. Please try recording again."))
            } else {
                Result.success(text)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Audio transcription timed out", e)
            Result.failure(Exception("Voice transcription took too long. Please try a shorter recording."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Audio transcription failed", e)
            Result.failure(Exception(e.localizedMessage ?: "Could not transcribe your voice note."))
        }
    }

    suspend fun transcribeAndParseTransaction(
        audioBytes: ByteArray,
        mimeType: String = "audio/mp4",
        defaultType: String = "EXPENSE"
    ): Result<VoiceParsedTransaction> {
        val transcriptResult = transcribeAudio(audioBytes, mimeType)
        val transcript = transcriptResult.getOrElse { return Result.failure(it) }

        return try {
            val response = withTimeout(60_000L) {
                transactionParserModel.generateContent(
                    "Extract the student money record from this spoken text: \"$transcript\". " +
                        "Default type if unclear: $defaultType. " +
                        "Allowed expense categories: ${FinanceConstants.EXPENSE_CATEGORIES.joinToString()}. " +
                        "Allowed income sources: ${FinanceConstants.INCOME_SOURCES.joinToString()}. " +
                        "Allowed payment methods: ${FinanceConstants.PAYMENT_METHODS.joinToString()}. Default method is Cash if none mentioned."
                )
            }
            val jsonStr = response.text ?: throw IllegalStateException("Empty structured response")
            val json = JSONObject(jsonStr)
            val parsedType = json.optString("type", defaultType)
            val rawAmount = json.optDouble("amount", 0.0)
            val amount = if (rawAmount > 0.0) rawAmount else null
            val category = json.optString("categoryOrSource", "")
            val method = json.optString("paymentMethod", "Cash")
            val desc = json.optString("description", transcript.take(80))

            val validCategory = if (parsedType == "INCOME") {
                FinanceConstants.INCOME_SOURCES.firstOrNull { it.equals(category, ignoreCase = true) }
                    ?: FinanceConstants.INCOME_SOURCES.first()
            } else {
                FinanceConstants.EXPENSE_CATEGORIES.firstOrNull { it.equals(category, ignoreCase = true) }
                    ?: "Other expenses"
            }

            val validMethod = FinanceConstants.PAYMENT_METHODS.firstOrNull {
                it.equals(method, ignoreCase = true)
            } ?: "Cash"

            Result.success(
                VoiceParsedTransaction(
                    transcript = transcript,
                    type = parsedType,
                    amount = amount,
                    categoryOrSource = validCategory,
                    paymentMethod = validMethod,
                    description = desc.ifBlank { transcript.take(80) }
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Smart field extraction failed, returning raw transcript", e)
            Result.success(
                VoiceParsedTransaction(
                    transcript = transcript,
                    type = defaultType,
                    amount = null,
                    categoryOrSource = if (defaultType == "INCOME") FinanceConstants.INCOME_SOURCES.first() else "Other expenses",
                    paymentMethod = "Cash",
                    description = transcript.take(80)
                )
            )
        }
    }

    private companion object {
        const val TAG = "VoiceTranscribeRepo"
    }
}
