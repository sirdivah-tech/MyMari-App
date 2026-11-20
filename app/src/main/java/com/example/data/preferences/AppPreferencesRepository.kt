package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "mymari_settings"
)

enum class ThemeMode(val key: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromKey(key: String?): ThemeMode {
            return entries.firstOrNull { it.key == key } ?: SYSTEM
        }
    }
}

enum class AppLanguage(val code: String, val nativeLabel: String) {
    ENGLISH("en", "English"),
    SHONA("sn", "ChiShona"),
    NDEBELE("nd", "IsiNdebele");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code == code } ?: ENGLISH
        }
    }
}

class AppPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    val themeMode: Flow<ThemeMode> = context.appPreferencesDataStore.data.map { prefs ->
        ThemeMode.fromKey(prefs[Keys.THEME_MODE])
    }

    val appLanguage: Flow<AppLanguage> = context.appPreferencesDataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[Keys.APP_LANGUAGE])
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.key
        }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        context.appPreferencesDataStore.edit { prefs ->
            prefs[Keys.APP_LANGUAGE] = language.code
        }
    }
}
