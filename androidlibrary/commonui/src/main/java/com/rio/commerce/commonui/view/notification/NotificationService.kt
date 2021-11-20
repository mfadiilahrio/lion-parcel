package com.rio.commerce.commonui.view.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

class NotificationService {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(
            context: Context,
            channelId: String,
            channelName: String,
            channelDescription: String?,
            showBadge: Boolean,
            vibrate: Boolean,
            importance: Int
        ) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                    ?: return

            val channel = NotificationChannel(channelId, channelName, importance)

            channel.vibrationPattern = if (vibrate) {
                val dat = 70
                longArrayOf(0, (3 * dat).toLong(), dat.toLong(), dat.toLong())
            } else {
                longArrayOf()
            }

            channel.setShowBadge(showBadge)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            channel.description = channelDescription

            notificationManager.createNotificationChannel(channel)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun registerNotificationChannels(context: Context) {
//            NotificationService.createNotificationChannel(
//                context,
//                Constant.Notification.Channel.BOOKING,
//                context.getString(R.string.booking_channel),
//                context.getString(R.string.booking_channel_description),
//                true,
//                true,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//
//            NotificationService.createNotificationChannel(
//                context,
//                Constant.Notification.Channel.NEWS,
//                context.getString(R.string.news_channel),
//                context.getString(R.string.hews_channel_description),
//                true,
//                true,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//
//            NotificationService.createNotificationChannel(
//                context,
//                Constant.Notification.Channel.OTHER,
//                context.getString(R.string.other_channel),
//                null,
//                true,
//                true,
//                NotificationManager.IMPORTANCE_HIGH
//            )
        }

        fun getChannelId(type: String): String {
//            return when (type) {
//                "booking" -> Constant.Notification.Channel.BOOKING
//                "customRequest" -> Constant.Notification.Channel.BOOKING
//                "news" -> Constant.Notification.Channel.NEWS
//                else -> Constant.Notification.Channel.OTHER
//            }
            return ""
        }


    }
}