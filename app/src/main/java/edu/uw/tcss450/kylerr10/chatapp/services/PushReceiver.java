package edu.uw.tcss450.kylerr10.chatapp.services;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import edu.uw.tcss450.kylerr10.chatapp.AuthActivity;
import edu.uw.tcss450.kylerr10.chatapp.R;
import edu.uw.tcss450.kylerr10.chatapp.ui.chat.conversation.Conversation;
import me.pushy.sdk.Pushy;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class PushReceiver extends BroadcastReceiver {

    public static final String RECEIVED_NEW_MESSAGE = "new message from pushy";

    private static final String CHANNEL_ID = "1";

    @Override
    public void onReceive(Context context, Intent intent) {

        //the following variables are used to store the information sent from Pushy
        //In the WS, you define what gets sent. You can change it there to suit your needs
        //Then here on the Android side, decide what to do with the message you got

        //for the lab, the WS is only sending chat messages so the type will always be msg
        //for your project, the WS needs to send different types of push messages.
        //So perform logic/routing based on the "type"
        //feel free to change the key or type of values.
        String typeOfMessage = intent.getStringExtra("type");

        Conversation message = null;
        int chatId = -1;

        String contactEvent = null;
        String chatEvent = null;
        String chatName = null;

        try {
            if(typeOfMessage.equals("msg")) {
                message = Conversation.createFromJsonString(intent.getStringExtra("message"));
                chatId = intent.getIntExtra("chatid", -1);
            }
            else if(typeOfMessage.equals("contact")) {
                contactEvent = intent.getStringExtra("action");
            }
            else if(typeOfMessage.equals("chat")) {
                chatEvent = intent.getStringExtra("action");
                chatName = intent.getStringExtra("name");
                Log.e("CHAT", "PUSH");
            }
        } catch (JSONException e) {
            //Web service sent us something unexpected...I can't deal with this.
            throw new IllegalStateException("Error from Web Service. Contact Dev Support");
        }

        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            //app is in the foreground so send the message to the active Activities
            Log.d("PUSHY", "Message received in foreground: ");

            //create an Intent to broadcast a message to other parts of the app.
            Intent i = new Intent(RECEIVED_NEW_MESSAGE);

            if(typeOfMessage.equals("msg")) {
                i.putExtra("sender", message.getSenderName());
                i.putExtra("chatMessage", message.getContent());
                i.putExtra("chatid", chatId);
            }
            else if(typeOfMessage.equals("contact")) {
                i.putExtra("contact", contactEvent);
            }
            else if(typeOfMessage.equals("chat") && chatEvent.equals("newRoom")) {
                i.putExtra("chat", chatEvent);
                i.putExtra("name", chatName);
            }

            i.putExtras(intent.getExtras());
            context.sendBroadcast(i);

        } else {
            //app is in the background so create and post a notification
            Log.d("PUSHY", "Message received in background: ");

            Intent i = new Intent(context, AuthActivity.class);
            i.setAction(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);

            i.putExtras(intent.getExtras());

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, flags);

            NotificationCompat.Builder builder;

            if(typeOfMessage.equals("msg")) {
                i.putExtra("fragmentToOpen", "chatFragment");
                //research more on notifications the how to display them
                //https://developer.android.com/guide/topics/ui/notifiers/notifications
                builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_logo_foreground)
                        .setContentTitle("Message from: " + message.getSenderName())
                        .setContentText(message.getContent())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
            }
            else if(typeOfMessage.equals("contact") && contactEvent.equals("newRequest")) {
                i.putExtra("fragmentToOpen", "contactsFragment");
                builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_logo_foreground)
                        .setContentTitle("Chatr")
                        .setContentText("New contact request")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
            }
            else if(typeOfMessage.equals("chat") && chatEvent.equals("newRoom")) {
                i.putExtra("fragmentToOpen", "chatFragment");
                builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_logo_foreground)
                        .setContentTitle("Chatr")
                        .setContentText("Added to new chat room: " + chatName)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
            }
            else {
                return;
            }


            // Automatically configure a ChatMessageNotification Channel for devices running Android O+
            Pushy.setNotificationChannel(builder, context);

            // Get an instance of the NotificationManager service
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            // Build the notification and display it
            notificationManager.notify(1, builder.build());
        }
    }
}