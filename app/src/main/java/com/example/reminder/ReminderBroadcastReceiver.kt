package com.example.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra(ReminderScheduler.EXTRA_TYPE) ?: ReminderScheduler.TYPE_EVENING

        if (type == ReminderScheduler.TYPE_MORNING) {
            NotificationHelper.showNotification(
                context = context,
                notificationId = NotificationHelper.NOTIFICATION_ID_MORNING,
                title = "Good morning from MyMari!",
                message = "Check your remaining balance and today's budget before you start your day."
            )
            // Re-arm for next day
            ReminderScheduler.scheduleDailyReminder(context, ReminderScheduler.TYPE_MORNING, 7, 30, true)
        } else {
            NotificationHelper.showNotification(
                context = context,
                notificationId = NotificationHelper.NOTIFICATION_ID_EVENING,
                title = "Time for your MyMari check-in!",
                message = "Have you recorded everything you spent or received today? Review your money before you sleep."
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
