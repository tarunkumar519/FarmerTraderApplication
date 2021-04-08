package com.farmer.trader.applications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;


public class BackgroundService extends Service {

    Timer timer;
    TimerTask timerTask;
    Handler handler = new Handler();
    SharedPreferences pref;
    String UserId, UserType;
    private Context mContext;

    boolean bValue = false;
    private Intent homeIntent;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");
        startServices();

        timer = new Timer();
        demo();
        timer.schedule(timerTask, 0, 5000);

    }

    public void demo() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        UserId = pref.getString("UserId", "");
                        UserType = pref.getString("LoginType", "");
                        if (UserId.compareTo("") != 0) {
                            Log.i("run noti", "run: ");
//                            if (!bValue) {
                            new getNotificationTask().execute(UserId);
//                            }

                        }
                    }
                });
            }
        };
    }


    public class getNotificationTask extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            bValue = true;
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.GetNotifications(params[0]);
                JSONPARSE jp = new JSONPARSE();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i(TAG, "onPostExecute: "+s);
            super.onPostExecute(s);

            if (s.compareTo("no") == 0) {

            } else if (s.compareTo("") == 0) {

            } else if (s.contains("*")) {

                String temp[] = s.split("\\#");
                for (int i = 0; i < temp.length; i++) {

                    String temp1[] = temp[i].split("\\*");
                    //nid*title*mesg
                    String Nid = temp1[0];
                    String Title = temp1[1];
                    String msg = temp1[2];

                    TaskDetailNotification(Nid, Title, msg);
                    new DeleteNotificationTask().execute(Nid);
                }

            }

//            bValue = false;

        }
    }


    public class DeleteNotificationTask extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            bValue = true;
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.DeleteNotifications(params[0]);
                JSONPARSE jp = new JSONPARSE();
                a = jp.parse(json);
            } catch (Exception e) {
                a = e.getMessage();
            }
            return a;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.compareTo("") == 0) {

            } else if (s.compareTo("true") == 0) {

//                Toast.makeText(BackgroundService.this, "Notification Deleted", Toast.LENGTH_SHORT).show();

            }

//            bValue = false;

        }
    }


    public int getKey() {

        int ranArray[] = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random random = new Random();
        return ranArray[random.nextInt(ranArray.length)];
    }


    public void TaskDetailNotification(String Nid, String Title, String Msg) {

        Intent homeIntent = null;

        int randomValue = getKey() + getKey() + getKey() + getKey() + getKey();

        if (UserType.compareTo("Farmer") == 0) {

            homeIntent = new Intent(BackgroundService.this, MainActivity.class);
            homeIntent.putExtra("NotificationId", randomValue);
            homeIntent.putExtra("position", 2);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } else if (UserType.compareTo("Retailer") == 0) {

            homeIntent = new Intent(BackgroundService.this, R_OrderList_Activity.class);
            homeIntent.putExtra("NotificationId", randomValue);

        } else if (UserType.compareTo("MahilaUdyog") == 0) {

            homeIntent = new Intent(BackgroundService.this, MainActivity.class);
            homeIntent.putExtra("NotificationId", randomValue);
            homeIntent.putExtra("position", 2);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }


        PendingIntent pendingFinishIntent = PendingIntent.getActivity(BackgroundService.this, randomValue, homeIntent, 0);


        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        createNotificationChannel(getApplicationContext(), CHANNEL_ID);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(BackgroundService.this, CHANNEL_ID);

        builder.setContentIntent(pendingFinishIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(Nid)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(Title);

        Notification n = builder.build();
        nm.notify("cc", randomValue, n);

        startForeground(101, n);
    }


    private void startServices() {
        // int randomValue = new Random().nextInt(10000) + 1;
        if (UserType.compareTo("Farmer") == 0) {
            homeIntent = new Intent(BackgroundService.this, MainActivity.class);
        } else if (UserType.compareTo("Retailer") == 0) {
            homeIntent = new Intent(BackgroundService.this, R_Home_Activity.class);
        } else if (UserType.compareTo("MahilaUdyog") == 0) {
            homeIntent = new Intent(BackgroundService.this, MainActivity.class);
        }
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(BackgroundService.this, 0, homeIntent, 0);

        createNotificationChannel(getApplicationContext(), CHANNEL_ID);

        Notification notification = new NotificationCompat
                .Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Farm-E-Go")
                .setContentText("Your app is running in background")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(pendingIntent)
                //.setPriority(Notification.PRIORITY_MIN)
                .setAutoCancel(true)
                .build();

        startForeground(101, notification);
    }

    public static String CHANNEL_ID = "FarmEGo";

    public static void createNotificationChannel(@NonNull Context context, @NonNull String CHANNEL_ID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FarmEGo";
            String description = "FarmEGo";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            } else {
                Log.d("NotificationLog", "NotificationManagerNull");
            }
        }
    }


}
