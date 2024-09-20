package dnt.kotlin.bai3_appbaothuc

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //---   Phát chuông báo thức trong 1 phút   ---
        Log.d("AlarmService", "onStartCommand")
        mediaPlayer = MediaPlayer.create(this, R.raw.dung1)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        handler.postDelayed({
            stopAlarm()
        }, 60000)

        //---   Chạy dịch vụ dưới nền và hiển thị thông báo  ---
        val notification = createNotification()
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
    }

    //===   Tạo thông báo   ===
    private fun createNotification() : Notification {
        //---   Đăng kí kênh thông báo  ---
        val channel = NotificationChannel(
            "alarm_channel",
            "Alarm Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        //---   Khởi tạo action     ---
        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE)

        //---   Khởi tạo thông báo  ---
        return NotificationCompat.Builder(this, "alarm_channel")
            .setContentTitle("Báo Thức")
            .setContentText("Đến giờ!")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .addAction(R.drawable.ic_launcher_background, "Dừng", stopPendingIntent)
            .setAutoCancel(true)
            .build()
    }

    //===   Dừng phát báo thức   ===
    private fun stopAlarm() {
        mediaPlayer.stop()
        mediaPlayer.release()
        stopForeground(true)
        stopSelf()
    }
}


