package com.hr.techlabapp;

import android.app.Activity;
import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.hr.techlabapp.Notifications.CHANNEL_1_ID;

public class Noti extends Activity {
    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    private Button Send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_noti);
        notificationManager = NotificationManagerCompat.from(this);
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextMessage = findViewById(R.id.edit_text_message);
        Send = findViewById(R.id.Channel1Button);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTitle.getText().toString();
                String message = editTextMessage.getText().toString();
                Notification notification;
                if(Build.VERSION.SDK_INT>= 26)
                    notification = new Notification.Builder(v.getContext(),CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_one)
                            .setContentTitle(title)
                            .setContentText(message).build();
                else
                    notification = new Notification.Builder(v.getContext())
                        .setSmallIcon(R.drawable.ic_one)
                        .setContentTitle(title)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentText(message).build();
                notificationManager.notify(title,stringToHash(title),notification);
            }
        });
    }

    private int stringToHash(String s){
        int res = 0;
        boolean xor = false;
        for(char c: s.toCharArray()) {
            //dat zorgt ervoor dat als je 1 enn 1 hebt dan krijg je 0, als je 1 en 0 hebt dan krijg je 1, als je 0 en 0 hebt dan krijg je 0
            if (xor)
                res ^= c;
            else
                res += c;
            xor = !xor;
        }
        return res;
    }
}
