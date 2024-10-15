package com.github.hahmadfaiq21.customnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.widget.addTextChangedListener
import com.github.hahmadfaiq21.customnotification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenDetail.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val message = binding.etMessage.text.toString()
            if (title.isEmpty()) {
                binding.etTitle.error = "Title cannot be empty"
            } else if (message.isEmpty()) {
                binding.etMessage.error = "Message cannot be empty"
            } else {
                val intent = Intent(this, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_TITLE, title)
                intent.putExtra(DetailActivity.EXTRA_MESSAGE, message)
                startActivity(intent)
            }
        }
        binding.btnSendNotification.isEnabled = false
        binding.etTitle.addTextChangedListener { checkInputFilled() }
        binding.etMessage.addTextChangedListener { checkInputFilled() }
        binding.btnSendNotification.setOnClickListener {
            sendNotification()
        }
    }

    private fun checkInputFilled() {
        binding.apply {
            btnSendNotification.isEnabled =
                etTitle.text.toString().isNotBlank() && etMessage.text.toString().isNotBlank()
        }
    }

    private fun sendNotification() {
        val title = binding.etTitle.text.toString()
        val message = binding.etMessage.text.toString()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationDetailIntent = Intent(this, DetailActivity::class.java)
        notificationDetailIntent.putExtra(DetailActivity.EXTRA_TITLE, title)
        notificationDetailIntent.putExtra(DetailActivity.EXTRA_MESSAGE, message)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(notificationDetailIntent)
            getPendingIntent(
                NOTIFICATION_ID,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "notification channel"
    }
}