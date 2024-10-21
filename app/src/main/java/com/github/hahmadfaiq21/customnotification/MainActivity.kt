package com.github.hahmadfaiq21.customnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.widget.addTextChangedListener
import com.github.hahmadfaiq21.customnotification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                Log.i("PermissionStatus", "Notification permission granted")
            } else {
                Log.e("PermissionStatus", "Notification permission rejected")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        binding.apply {
            btnOpenDetail.setOnClickListener { openDetailActivity() }

            etTitle.addTextWatcher { checkInputFilled() }
            etMessage.addTextWatcher { checkInputFilled() }

            btnSendNotification.isEnabled = false
            btnSendNotification.setOnClickListener {
                sendNotification()
            }

            btnSendNotificationWithAction.isEnabled = false
            btnSendNotificationWithAction.setOnClickListener {
                sendNotificationWithAction()
            }

            btnSendNotificationWithInboxStyle.isEnabled = false
            btnSendNotificationWithInboxStyle.setOnClickListener {
                sendNotificationWithInboxStyle()
            }

            btnSendNotificationWithBigTextStyle.isEnabled = false
            btnSendNotificationWithBigTextStyle.setOnClickListener {
                sendNotificationWithBigTextStyle()
            }

            btnSendNotificationWithBigPictureStyle.isEnabled = false
            btnSendNotificationWithBigPictureStyle.setOnClickListener {
                sendNotificationWithBigPictureStyle()
            }
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
        binding.apply {
            btnSendNotification.isEnabled =
                etTitle.text!!.isNotBlank() && etMessage.text!!.isNotBlank()
            btnSendNotificationWithAction.isEnabled =
                etTitle.text!!.isNotBlank() && etMessage.text!!.isNotBlank()
            btnSendNotificationWithInboxStyle.isEnabled =
                etTitle.text!!.isNotBlank() && etMessage.text!!.isNotBlank()
            btnSendNotificationWithBigTextStyle.isEnabled =
                etTitle.text!!.isNotBlank() && etMessage.text!!.isNotBlank()
            btnSendNotificationWithBigPictureStyle.isEnabled =
                etTitle.text!!.isNotBlank() && etMessage.text!!.isNotBlank()
        }
    }

    private fun sendNotification(
        withAction: Boolean = false,
        withInboxStyle: Boolean = false,
        withBigTextStyle: Boolean = false,
        withBigPictureStyle: Boolean = false
    ) {
        val (title, message) = binding.run { etTitle.text.toString() to etMessage.text.toString() }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val pendingIntent = createPendingIntent(title, message)
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setSubText("Subtext")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (withAction) {
            builder.addAction(R.drawable.ic_notification, "DETAIL", pendingIntent)
        } else if (withInboxStyle) {
            builder.setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle("Inbox Style")
                    .addLine("First line")
                    .addLine("Second line")
                    .addLine("Third line")
                    .addLine("Fourth line")
                    .addLine("Fifth line")
                    .addLine("Sixth line")
                    .addLine("Seventh line")
            )
        } else if (withBigTextStyle) {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Big Text Style")
                    .bigText(getString(R.string.text_dummy))
            )
        } else if (withBigPictureStyle) {
            val picture = BitmapFactory.decodeResource(resources, R.drawable.android_logo)
            builder.setLargeIcon(picture).setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigLargeIcon(null as Bitmap?)
                    .setBigContentTitle("Big Picture Style")
                    .bigPicture(picture)
            )
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

    private fun sendNotificationWithAction() =
        sendNotification(withAction = true)

    private fun sendNotificationWithInboxStyle() =
        sendNotification(withInboxStyle = true)

    private fun sendNotificationWithBigTextStyle() =
        sendNotification(withBigTextStyle = true)

    private fun sendNotificationWithBigPictureStyle() =
        sendNotification(withBigPictureStyle = true)

    private fun EditText.addTextWatcher(afterTextChanged: () -> Unit) {
        this.addTextChangedListener { afterTextChanged() }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "notification channel"
    }
}
