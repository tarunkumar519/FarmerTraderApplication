package com.farmer.trader.applications;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RelativeLayout menu_drawer;
    ListView list;
    TextView heading;
    ImageView home;

    ArrayList<String> title;
    ArrayList<Integer> images;
    int pos = 0;
    Adapter adapt;
    boolean check = false;
    SharedPreferences pref;
    String LanType = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        LanType = pref.getString("LanType", "en");

        if (LanType.compareTo("en") == 0) {

            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            this.setContentView(R.layout.activity_main);


            init();

        } else if (LanType.compareTo("hi") == 0) {

            String languageToLoad = "hi"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            this.setContentView(R.layout.activity_main);


            init();

        } else {

            setContentView(R.layout.activity_main);
            getSupportActionBar().hide();
            init();

        }

        String LoginId = pref.getString("UserId", "");
        String LoginType = pref.getString("LoginType", "");
        String CertificateNo = pref.getString("CertificateNo", "");

        if (LoginType.compareTo("Farmer") == 0) {

            SimpleDateFormat sdfd = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            Calendar cal = Calendar.getInstance();
            String TodayDateText = sdfd.format(cal.getTime());

//            string cno, string uid , string date
            new checkCertificateTask().execute(CertificateNo, LoginId, TodayDateText);


        }


    }

    @Override
    protected void onResume() {
        super.onResume();


//        else {
//
//            String languageToLoad = "en"; // your language
//            Locale locale = new Locale(languageToLoad);
//            Locale.setDefault(locale);
//            Configuration config = new Configuration();
//            config.locale = locale;
//            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString("LanType", languageToLoad);
//            editor.apply();
//            editor.commit();
//            this.setContentView(R.layout.login_activity);
//            init();
//
//        }

    }


    public void init() {

        list = (ListView) findViewById(R.id.list);
        menu_drawer = (RelativeLayout) findViewById(R.id.menu_drawer);
        heading = (TextView) findViewById(R.id.heading);
        home = (ImageView) findViewById(R.id.home);

        String pro = getResources().getString(R.string.products);
        String profile = getResources().getString(R.string.profile);
        String ord = getResources().getString(R.string.orders);
        String Pay = getResources().getString(R.string.payment);
        String help = getResources().getString(R.string.Helpline);
        String Log = getResources().getString(R.string.logout);

        title = new ArrayList<String>();
        title.add(pro);
        title.add(profile);
        title.add(ord);
        title.add(Pay);
        title.add(help);
        title.add(Log);


        images = new ArrayList<Integer>();
        images.add(R.drawable.h_products);
        images.add(R.drawable.h_profile);
        images.add(R.drawable.h_orders);
        images.add(R.drawable.h_payment);
        images.add(R.drawable.h_helpline);
        images.add(R.drawable.h_logout);

        pos = getIntent().getIntExtra("position", 0);
        displayview(pos);


        Intent intent = getIntent();
        int NotificationId = intent.getIntExtra("NotificationId", 1000);

        if (NotificationId != 1000) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel("cc", NotificationId);

        }


        adapt = new Adapter(MainActivity.this, title, images);
        list.setAdapter(adapt);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (check) {
                    menu_drawer.setVisibility(View.GONE);
                    check = false;
                } else {
                    menu_drawer.setVisibility(View.VISIBLE);
                    check = true;
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pos = i;
                adapt.notifyDataSetChanged();
                displayview(i);
            }
        });
    }


    public void displayview(int pos) {
        Fragment frag = null;
        switch (pos) {
            case 0:
                heading.setText(title.get(pos));
                frag = new Home();
                break;

            case 1:
                heading.setText(title.get(pos));
                frag = new Profile();
                break;

            case 2:
                heading.setText(title.get(pos));
                frag = new Orders();
                break;

            case 3:
                heading.setText(title.get(pos));
                frag = new Payment();
                break;

            case 4:
                heading.setText(title.get(pos));
                frag = new HelpLine();
                break;

            case 5:
                heading.setText(title.get(pos));

                SharedPreferences pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("UserId","");
                editor.putString("LoginType","");
                editor.commit();
                editor.apply();

                if (UtilConstants.isMyServiceRunning(MainActivity.this)) {
                    //run if not running
                    Intent serviceNotification = new Intent(MainActivity.this, BackgroundService.class);
                    stopService(serviceNotification);
                }
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;


            default:
                break;
        }

        if (frag != null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.frameid, frag).commit();
            menu_drawer.setVisibility(View.GONE);
            check = false;
        }
    }


    public class Adapter extends ArrayAdapter<String> {
        Context con;

        public Adapter(@NonNull Context context, ArrayList<String> data, ArrayList<Integer> data1) {
            super(context, R.layout.menu_listitem, data);
            con = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.menu_listitem, null, true);
            LinearLayout linear = (LinearLayout) v.findViewById(R.id.linear);

            if (pos == position) {
                linear.setBackground(new ColorDrawable(getResources().getColor(R.color.peach)));
            } else {
                if (position % 2 == 0) {
                    linear.setBackground(new ColorDrawable(getResources().getColor(R.color.darkgray1)));
                } else {
                    linear.setBackground(new ColorDrawable(getResources().getColor(R.color.darkgray)));
                }
            }

            ImageView img = (ImageView) v.findViewById(R.id.mli_img);
            TextView text = (TextView) v.findViewById(R.id.mli_text);
            text.setText(title.get(position));
            img.setImageResource(images.get(position));
            return v;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    public class checkCertificateTask extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.checkCertificate(params[0], params[1], params[2]);
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

//            Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();

            if (s.compareTo("expired") == 0) {

                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setTitle("Certificate Expired!");
                ad.setMessage("your certificate has been expired");
                ad.setCancelable(false);
                ad.setPositiveButton("Renew", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        Intent intent = new Intent(MainActivity.this, CertificateActivity.class);
                        intent.putExtra("Source", "MainActivty");
                        startActivity(intent);

                    }
                });

                ad.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                ad.show();


            }else  if (s.compareTo("valid") == 0) {


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                    ad.setTitle("Unable to Connect!");
                    ad.setMessage("Check your Internet Connection,Unable to connect the Server");
                    ad.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    ad.show();
                } else {
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
