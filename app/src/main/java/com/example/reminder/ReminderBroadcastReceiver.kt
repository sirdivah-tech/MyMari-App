package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.preferences.AppPreferencesRepository
import com.example.ui.i18n.stringsForLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(ReminderScheduler.EXTRA_TYPE) ?: ReminderScheduler.TYPE_EVENING
        val lang = runBlocking { AppPreferencesRepository(context.applicationContext).appLanguage.first() }
        val strings = stringsForLanguage(lang)

        if (type == ReminderScheduler.TYPE_MORNING) {
            NotificationHelper.showNotification(
                context = context,
                notificationId = NotificationHelper.NOTIFICATION_ID_MORNING,
                title = strings.reminderMorningTitle,
                message = strings.reminderMorningMsg
            )
            // Re-arm for next day
            ReminderScheduler.scheduleDailyReminder(context, ReminderScheduler.TYPE_MORNING, 7, 30, true)
        } else {
            NotificationHelper.showNotification(
                context = context,
                notificationId = NotificationHelper.NOTIFICATION_ID_EVENING,
                title = strings.reminderEveningTitle,
                message = strings.reminderEveningMsg
            )
            // Re-arm for next day
            ReminderScheduler.scheduleDailyReminder(context, ReminderScheduler.TYPE_EVENING, 20, 0, true)
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule defaults
            ReminderScheduler.scheduleDailyReminder(context, ReminderScheduler.TYPE_MORNING, 7, 30, true)
            ReminderScheduler.scheduleDailyReminder(context, ReminderScheduler.TYPE_EVENING, 20, 0, true)
        }
    }
}
