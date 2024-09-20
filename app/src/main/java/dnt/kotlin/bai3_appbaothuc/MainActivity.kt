package dnt.kotlin.bai3_appbaothuc

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var timePicker: TimePicker
    lateinit var repeatCheckbox: CheckBox
    lateinit var editTextTime: EditText
    lateinit var alarmManager: AlarmManager
    lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)
        repeatCheckbox = findViewById(R.id.cbRepeat)
        editTextTime = findViewById(R.id.edtTime)
        val setAlarmButton = findViewById<Button>(R.id.btSetAlarm)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        setAlarmButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            }
            else {
                val hour = timePicker.hour
                val minute = timePicker.minute

                editTextTime.setText(String.format("%02d:%02d", hour, minute))

                //---   Thiết lập báo thức với AlarmManager     ---
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }

                //---   Intent để gửi tới BroadcastReceiver ---
                val intent = Intent(this, AlarmReceiver::class.java)
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)

                if (repeatCheckbox.isChecked) {
                    //---   Báo thức lặp lại hàng ngày  ---
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                } else {
                    //---   Báo thức chỉ kích hoạt một lần  ---
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }
        }
    }
}
