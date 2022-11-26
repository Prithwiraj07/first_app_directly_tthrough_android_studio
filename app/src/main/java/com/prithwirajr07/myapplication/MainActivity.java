package com.prithwirajr07.myapplication;
import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference data1 = database.getReference("data");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.image_view);
        Button button = findViewById(R.id.button);
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);


//.........................................ANIMATION WALA PART....................................................//
        Animation animation = new AlphaAnimation((float) 0.5, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        // animation
        // rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        // infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
        // end so the button will
        // fade back in
//................................................................................................................//


//............................................PUSH NOTIFICATION...................................................//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("app_noti", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "app_noti")
                .setSmallIcon(R.drawable.warning_sign)
                .setContentTitle("Emergency Notification")
                .setContentText("Fluid Value has reached the set level !");

        notification = builder.build();
        notificationManagerCompat = NotificationManagerCompat.from(this);
        //..................................................................................................//

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //imageView.clearAnimation();
                //imageView.setVisibility(View.INVISIBLE);
                stopPlayer();
                //button.setVisibility(View.INVISIBLE);

            }
        });
        data1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(int.class);

                if (value != 0) {
                    textView2.setVisibility(View.VISIBLE);
                    imageView.startAnimation(animation);
                    button.setVisibility(View.VISIBLE);
                    play();
                    notificationManagerCompat.notify(1, notification);

                } else {
                    textView2.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                    imageView.clearAnimation();
                    imageView.setVisibility(View.INVISIBLE);
                    stopPlayer();
                }
                Log.d(TAG, "Value is: " + value);
                textView.setText(value.toString());

                textView.setVisibility(View.VISIBLE);
                textView.setText("Failed");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                textView.setVisibility(View.VISIBLE);
                textView.setText("Failed");
            }
        });

    }

    public void play() {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.noti_alarm);
        }
        player.start();
        player.setLooping(true);

    }

    public void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }


}