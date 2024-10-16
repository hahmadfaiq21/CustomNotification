package com.github.hahmadfaiq21.customnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
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

        with(binding) {
            btnOpenDetail.setOnClickListener { openDetailActivity() }
            etTitle.addTextWatcher { checkInputFilled() }
            etMessage.addTextWatcher { checkInputFilled() }
            btnSendNotification.isEnabled = false
            btnSendNotification.setOnClickListener { sendNotification() }
            btnSendNotificationWithAction.isEnabled = false
            btnSendNotificationWithAction.setOnClickListener { sendNotificationWithAction() }
        }
    }

    private fun openDetailActivity() {
        val title = binding.etTitle.text.toString()
        val message = binding.etMessage.text.toString()

        when {
            title.isEmpty() -> binding.etTitle.error = "Title cannot be empty"
            message.isEmpty() -> binding.etMessage.error = "Message cannot be empty"
            else -> {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra(DetailActivity.EXTRA_TITLE, title)
                    putExtra(DetailActivity.EXTRA_MESSAGE, message)
                }
                startActivity(intent)
            }
        }
    }

    private fun checkInputFilled() {
        binding.btnSendNotification.isEnabled =
            binding.etTitle.text!!.isNotBlank() && binding.etMessage.text!!.isNotBlank()
        binding.btnSendNotificationWithAction.isEnabled =
            binding.etTitle.text!!.isNotBlank() && binding.etMessage.text!!.isNotBlank()
    }

    private fun sendNotification(withAction: Boolean = false) {
        val (title, message) = binding.run { etTitle.text.toString() to etMessage.text.toString() }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = createPendingIntent(title, message)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (withAction) {
            builder.addAction(R.drawable.ic_notification, "Open Detail", pendingIntent)
        }

        createNotificationChannel(notificationManager)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createPendingIntent(title: String, message: String): PendingIntent {
        val detailIntent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_TITLE, title)
            putExtra(DetailActivity.EXTRA_MESSAGE, message)
        }
        return TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(detailIntent)
            getPendingIntent(
                NOTIFICATION_ID,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )!!
        }
    }

    private fun createNotificationChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun sendNotificationWithAction() = sendNotification(withAction = true)

    private fun EditText.addTextWatcher(afterTextChanged: () -> Unit) {
        this.addTextChangedListener { afterTextChanged() }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "notification channel"
    }
}
