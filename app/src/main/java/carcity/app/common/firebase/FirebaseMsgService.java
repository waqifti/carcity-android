package carcity.app.common.firebase;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Random;

import carcity.app.R;
import carcity.app.common.activity.SplashActivity;
import carcity.app.common.utils.Session;
import kotlin.jvm.internal.Intrinsics;

//public class FirebaseMsgService extends FirebaseMessagingService {
//    public void onNewToken(@NotNull String p0) {
//        Intrinsics.checkNotNullParameter(p0, "p0");
//        super.onNewToken(p0);
//        SplashActivity.session.setSessionToken(p0);
//    }
//
//    public void onMessageReceived(@NotNull RemoteMessage p0) {
//        Intrinsics.checkNotNullParameter(p0, "p0");
//        super.onMessageReceived(p0);
//        this.generateNotification((Context)this, p0);
//    }
//
//    private final void generateNotification(Context context, RemoteMessage remoteMessage) {
//        Log.d("response", "fcm msg : " + remoteMessage.getData());
//        String body = (String)remoteMessage.getData().get("body");
//        String title = (String)remoteMessage.getData().get("title");
//        String channelId = "trackerApp";
//        int notificationID = this.getId();
//        @SuppressLint("WrongConstant") Object var10000 = context.getSystemService("notification");
//        if (var10000 == null) {
//            throw new NullPointerException("null cannot be cast to non-null type android.app.NotificationManager");
//        } else {
//            NotificationManager notificationManager = (NotificationManager)var10000;
//            if (Build.VERSION.SDK_INT >= 26) {
//                String name = "trackerNotification";
//                String notificationDescription = "tracker_notification_description";
//                NotificationChannel notificationChannel = new NotificationChannel(channelId, (CharSequence)name, 3);
//                AudioAttributes audioAttributes = (new AudioAttributes.Builder()).setContentType(4).setUsage(5).build();
//                notificationChannel.setDescription(notificationDescription);
//                notificationChannel.enableLights(true);
//                notificationChannel.enableVibration(true);
//                notificationChannel.setSound(RingtoneManager.getDefaultUri(2), audioAttributes);
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//
//            androidx.core.app.NotificationCompat.Builder var13 = (new androidx.core.app.NotificationCompat.Builder((Context)this, channelId)).setSmallIcon(1500000).setContentTitle((CharSequence)title).setContentText((CharSequence)body).setAutoCancel(true).setColor(ContextCompat.getColor(context, 500036)).setSound(RingtoneManager.getDefaultUri(2)).setPriority(0);
//            Intrinsics.checkNotNullExpressionValue(var13, "NotificationCompat.Build…nCompat.PRIORITY_DEFAULT)");
//            androidx.core.app.NotificationCompat.Builder builder = var13;
//            notificationManager.notify(notificationID, builder.build());
//        }
//    }
//
//    private final int getId() {
//        return (new Random()).nextInt();
//    }
//}




public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        sendNotification(remoteMessage.getNotification().getBody());
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "remoteMessage: " + remoteMessage.toString());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance(this).beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}




//public class FirebaseMsgService extends FirebaseMessagingService {
//
//    private static final String TAG = "MyFirebaseMsgService";
//
//    public void onNewToken(@NotNull String p0) {
//        Log.d(TAG, "onNewToken: "+ p0);
//        Intrinsics.checkNotNullParameter(p0, "p0");
//        super.onNewToken(p0);
//        SplashActivity.session.setSessionToken(p0);
//    }
//
//    public void onMessageReceived(@NotNull RemoteMessage p0) {
//        Log.d(TAG, "onMessageReceived: "+ p0);
//        Intrinsics.checkNotNullParameter(p0, "p0");
//        super.onMessageReceived(p0);
//        this.generateNotification((Context)this, p0);
//    }
//
//    private final void generateNotification(Context context, RemoteMessage remoteMessage) {
//        Log.d(TAG, "fcm msg : " + remoteMessage.getData());
//        String body = (String)remoteMessage.getData().get("body");
//        String title = (String)remoteMessage.getData().get("title");
//        String channelId = "trackerApp";
//        int notificationID = this.getId();
//        @SuppressLint("WrongConstant")
//        Object var10000 = context.getSystemService("notification");
//        if (var10000 == null) {
//            throw new NullPointerException("null cannot be cast to non-null type android.app.NotificationManager");
//        } else {
//            NotificationManager notificationManager = (NotificationManager)var10000;
//            if (Build.VERSION.SDK_INT >= 26) {
//                String name = "trackerNotification";
//                String notificationDescription = "tracker_notification_description";
//                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(channelId, (CharSequence)name, 3);
//                @SuppressLint("WrongConstant") AudioAttributes audioAttributes = (new AudioAttributes.Builder()).setContentType(4).setUsage(5).build();
//                notificationChannel.setDescription(notificationDescription);
//                notificationChannel.enableLights(true);
//                notificationChannel.enableVibration(true);
//                notificationChannel.setSound(RingtoneManager.getDefaultUri(2), audioAttributes);
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//
//            @SuppressLint("ResourceType") androidx.core.app.NotificationCompat.Builder var13 = (new androidx.core.app.NotificationCompat.Builder((Context)this, channelId)).setSmallIcon(1500000).setContentTitle((CharSequence)title).setContentText((CharSequence)body).setAutoCancel(true).setColor(ContextCompat.getColor(context, 500036)).setSound(RingtoneManager.getDefaultUri(2)).setPriority(0);
//            Intrinsics.checkNotNullExpressionValue(var13, "NotificationCompat.Build…nCompat.PRIORITY_DEFAULT)");
//            androidx.core.app.NotificationCompat.Builder builder = var13;
//            notificationManager.notify(notificationID, builder.build());
//        }
//    }
//
//    private final int getId() {
//        return (new Random()).nextInt();
//    }
//
//}
