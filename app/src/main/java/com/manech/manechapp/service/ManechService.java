package com.manech.manechapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.manech.manechapp.R;
import com.manech.manechapp.helper.SecureSharedPref;
import com.manech.manechapp.model.Injection;
import com.manech.manechapp.model.MStoreViewModel;

public class ManechService extends Service {
    private MStoreViewModel mStoreViewModel;
    private SecureSharedPref sharedPref;
    @Override
    public void onCreate() {
        super.onCreate();
        mStoreViewModel = Injection.provideViewModelFactory(getApplicationContext()).create(MStoreViewModel.class);
        sharedPref=SecureSharedPref.getInstance(getApplicationContext(),getString(R.string.preference_file_key));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
     /*   final long userId = sharedPref.getLong(getString(R.string.saved_user_id), -1L);
        if (userId != -1L) {
            final String action;
            if (intent != null && (action = intent.getAction()) != null) {
                if (action.equals("animate")) {

                    final int[] indexes = intent.getIntArrayExtra("indexes");
                    final int[] rgbColor = intent.getIntArrayExtra("color");
                    if (rgbColor == null || rgbColor.length < 3)
                        mStoreViewModel.locate(userId, indexes, MyColor.random(0, 200));
                    else
                        mStoreViewModel.locate(userId, indexes, new MyColor(rgbColor[0], rgbColor[1], rgbColor[2]));


                    //
                    Intent notificationIntent = new Intent(this, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText("animated")
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentIntent(pendingIntent)
                            .build();
                    //
                    startForeground(1, notification);

                }
            }
        }
*/
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
