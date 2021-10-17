package com.ibrhmuyar.voicechat.notification

import com.ibrhmuyar.voicechat.notification.NotificationData

data class PushNotification(

    val data: NotificationData,
    val to: String
)