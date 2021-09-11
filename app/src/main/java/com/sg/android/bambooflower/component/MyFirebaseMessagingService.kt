package com.sg.android.bambooflower.component

import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.NotificationMaker
import com.sg.android.bambooflower.ui.MainActivity
import com.sg.android.bambooflower.ui.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named
import kotlin.random.Random

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    @Named(Contents.PREF_SETTING)
    lateinit var settingPref: SharedPreferences

    @Inject
    lateinit var auth: FirebaseAuth

    // 알림 수신
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val action = remoteMessage.data["action"] // 알림 종류
        val title = remoteMessage.data["title"].toString()
        val message = remoteMessage.data["content"].toString()
        val uid = remoteMessage.data["userId"].toString()
        val currentUid = auth.currentUser?.uid // 현재 유저 아이디

        // 알림 활성화 여부
        val isActive = if (action == "Quest" && uid == currentUid) {
            settingPref.getBoolean(
                "${Contents.PREF_KEY_QUEST_ALARM}-${uid}",
                false
            )
        } else if (action == "Diary" && uid == currentUid) { // 일기 알림
            settingPref.getBoolean(
                "${Contents.PREF_KEY_QUEST_ALARM}-${uid}",
                false
            )
        } else {
            false
        }

        // 활성화 하였을경우
        if (isActive) {
            val notificationMaker = NotificationMaker(
                applicationContext,
                Contents.CHANNEL_ID_NOTIFY,
                "알림"
            )

            // 알림 생성
            with(notificationMaker) {
                val randId = Random(System.currentTimeMillis())
                    .nextInt(2000)
                val pendingIntent = getPendingIntent(randId)

                defaultNotification(title, message)
                setPendingIntent(pendingIntent)

                buildNotify(randId)
            }
        }
    }

    // 토큰 갱신될 때
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    // 알림창 intent 생성
    private fun getPendingIntent(requestCode: Int): PendingIntent {
        // TODO: 테스트 때 확인해보기
        val isActive = settingPref.getBoolean(Contents.PREF_KEY_IS_ACTIVE, false)
        val intent = if (isActive) { // 앱이 실행중일 때
            Intent(applicationContext, MainActivity::class.java)
        } else { // 앱이 실행중이 아닐 때
            Intent(applicationContext, SplashActivity::class.java)
        }.apply {
            action = Contents.SHOW_HOME_FRAG
        }

        return PendingIntent.getActivity(
            applicationContext,
            requestCode,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
    }
}