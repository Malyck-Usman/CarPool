package com.sharerideexpense.easycarpool.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.sharerideexpense.easycarpool.Activities.Signin;
import com.sharerideexpense.easycarpool.DriverPanel.Activities.RideRequests;
import com.sharerideexpense.easycarpool.R;
import com.sharerideexpense.easycarpool.UserPanel.Activities.BookingStatus;
import com.sharerideexpense.easycarpool.classes.MySharedPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationReceiverService extends FirebaseMessagingService {


    private static final String ADMIN_CHANNEL_ID = "1212";
    private int Notification_Id = 0;
    FirebaseFirestore mDBRef;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Intent intent = new Intent(this, BookingStatus.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setUpChannel(notificationManager);

        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


//
//
//        Intent intent = new Intent(this, BookingStatus.class);
////        if(message.getData().get("title").equals("Booking Request")){
////            intent=null;
////            intent = new Intent(this, RideRequests.class);
////        }
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            setUpChannel(notificationManager);
//
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_fb);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ADMIN_CHANNEL_ID)
//                .setContentTitle(message.getData().get("title"))
//                .setContentText(message.getData().get("body"))
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(largeIcon)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setSound(defaultSoundUri)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//        builder.setColor(getResources().getColor(R.color.secondary));
//        notificationManager.notify(Notification_Id, builder.build());

    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpChannel(NotificationManager notificationManager) {
        String ChannelName = "New_Channel";
        String ChannelDescription = "Booking request Notification";
        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, ChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(ChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        notificationManager.createNotificationChannel(adminChannel);

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        mDBRef = FirebaseFirestore.getInstance();
        String U_id = getUserId();
        if (!U_id.equals("")) {
            mDBRef.collection("users").document(U_id).update("fcm_token", token).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d("TAG", "User token updated successfully in user data");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "Failed to update token in user data " + e.getMessage());
                }
            });
        }
    }

    private String getUserId() {
        MySharedPreferences preferences = new MySharedPreferences(this);
        return preferences.getUserId();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        Intent i;
        String Identifier = String.valueOf(intent.getExtras().get("identifier"));
        Log.d("TAG", "identifier string " + intent.getExtras().get("identifier"));
        if (Identifier.equals("BookingRequestCancelled")) {
            i = new Intent(this, RideRequests.class);
            i.putExtra("identifier", Identifier);
        } else if (Identifier.equals("NewBookingRequestReceived")) {
            i = new Intent(this, RideRequests.class);
            i.putExtra("identifier", Identifier);
        } else if (Identifier.equals("BookingRequestAccepted")) {
            i = new Intent(this, BookingStatus.class);
            i.putExtra("identifier", Identifier);
        } else if (Identifier.equals("BookingRequestDeclined")) {
            i = new Intent(this, BookingStatus.class);
            i.putExtra("identifier", Identifier);
        } else if (Identifier.equals("RideCancelled")) {
            i = new Intent(this, BookingStatus.class);
            i.putExtra("identifier", Identifier);
        }else if (Identifier.equals("RiderCancelledRequest")) {
            i = new Intent(this, BookingStatus.class);
            i.putExtra("identifier", Identifier);
        } else {
            i = new Intent(this, Signin.class);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_IMMUTABLE);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.app_icon);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), ADMIN_CHANNEL_ID)

                .setContentTitle((CharSequence) intent.getExtras().get("title"))
                .setContentText((CharSequence) intent.getExtras().get("body"))
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(largeIcon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        builder.setColor(getResources().getColor(R.color.secondary));
        notificationManager.notify(Notification_Id, builder.build());
    }
}