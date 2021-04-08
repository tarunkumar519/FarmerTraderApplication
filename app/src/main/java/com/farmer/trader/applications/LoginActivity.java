package com.farmer.trader.applications;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Locale;




public class LoginActivity extends AppCompatActivity {

    SharedPreferences pref;
    protected EditText EmailID, Password;
    protected Button SignIn, SignUp;
    protected RelativeLayout relativeLayout;
    Dialog mDialog;
    String LanType = "";
    private boolean ans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        mDialog = new Dialog(LoginActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ans = weHavePermission28();
        }else {
            ans = weHavePermission();
        }
        if (!ans) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                requestforPermissionFirst();
            }else {
                requestforPermissionFirst28();
            }
        }

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        String userId_pref = pref.getString("LoginType", "");
        LanType = pref.getString("LanType", "");
        Log.d("Lang","oncreate "+LanType);

        if (userId_pref.compareTo("Farmer") == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopService(new Intent(LoginActivity.this, BackgroundService.class));
                startForegroundService(new Intent(LoginActivity.this, BackgroundService.class));
            } else {
                stopService(new Intent(LoginActivity.this, BackgroundService.class));
                startService(new Intent(LoginActivity.this, BackgroundService.class));
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("position", 0);
            startActivity(intent);
            finish();

        } else if (userId_pref.compareTo("Retailer") == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopService(new Intent(LoginActivity.this, BackgroundService.class));
                startForegroundService(new Intent(LoginActivity.this, BackgroundService.class));
            } else {
                stopService(new Intent(LoginActivity.this, BackgroundService.class));
                startService(new Intent(LoginActivity.this, BackgroundService.class));
            }

            Intent intent = new Intent(LoginActivity.this, R_Home_Activity.class);
            startActivity(intent);
            finish();

        } else if (userId_pref.compareTo("MahilaUdyog") == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopService(new Intent(LoginActivity.this, BackgroundService.class));
                startForegroundService(new Intent(LoginActivity.this, BackgroundService.class));
            } else {
                stopService(new Intent(LoginActivity.this, BackgroundService.class));
                startService(new Intent(LoginActivity.this, BackgroundService.class));
            }

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("position", 0);
            startActivity(intent);
            finish();

        } else {
            setContentView(R.layout.login_activity);
            init();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.langauge_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.english) {

            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("LanType", languageToLoad);
            editor.apply();
            editor.commit();

            this.setContentView(R.layout.login_activity);
            init();

        } else if (item.getItemId() == R.id.hindi) {

            String languageToLoad = "hi"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("LanType", languageToLoad);
            editor.apply();
            editor.commit();

            this.setContentView(R.layout.login_activity);
            init();

        }
        else if (item.getItemId() == R.id.telugu) {

            String languageToLoad = "te"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("LanType", languageToLoad);
            editor.apply();
            editor.commit();

            this.setContentView(R.layout.login_activity);
            init();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        LanType = pref.getString("LanType", "en");
        Log.d("Lang","onresume "+LanType);

        if (LanType.compareTo("en")==0){

            String languageToLoad = "en"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            this.setContentView(R.layout.login_activity);
            init();

        }else if (LanType.compareTo("hi")==0){

            String languageToLoad = "hi"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            this.setContentView(R.layout.login_activity);
            init();

        }

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
        else if (LanType.compareTo("te")==0){

            String languageToLoad = "te"; // your language
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            this.setContentView(R.layout.login_activity);
            init();

        }
    }

    protected void init() {

        EmailID = (EditText) findViewById(R.id.loginUserName);
        Password = (EditText) findViewById(R.id.loginPassword);
        SignIn = (Button) findViewById(R.id.loginButton);
        SignUp = (Button) findViewById(R.id.signUp);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_login);


        SignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
                    }
                }
        );

        SignIn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (EmailID.getText().toString().equals("")) {
//                                Snackbar.make(relativeLayout, "User Name is required", Snackbar.LENGTH_SHORT).show();
                            Snackbar.make(relativeLayout, getText(R.string.empty_user), Snackbar.LENGTH_SHORT).show();
                            EmailID.requestFocus();

                        } else if (Password.getText().toString().equals("")) {
                            Snackbar.make(relativeLayout, getText(R.string.empty_password), Snackbar.LENGTH_SHORT).show();
                            Password.requestFocus();
                        } else {

                            new logintask().execute(EmailID.getText().toString(), Password.getText().toString());
                        }


                    }
                }

        );


    }


    public void showDialog() {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setContentView(R.layout.registration_type_selection_dialog);

        TableRow MahilaUdyog = (TableRow) dialog.findViewById(R.id.mahila_udyog);
        TableRow Retailers = (TableRow) dialog.findViewById(R.id.retailer);
        TableRow Farmer = (TableRow) dialog.findViewById(R.id.farmer);


        //Mahila Udyog option
        MahilaUdyog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("RegistrationType", "MahilaUdyog");
                startActivity(intent);
                dialog.dismiss();
            }
        });


        // Retailers Option
        Retailers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("RegistrationType", "Retailer");
                startActivity(intent);
                dialog.dismiss();
            }
        });


        //Farmer Option
        Farmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("RegistrationType", "Farmer");
                startActivity(intent);
                dialog.dismiss();
            }
        });


        dialog.show();


    }


    public class logintask extends AsyncTask<String, JSONObject, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {
                JSONObject json = api.Login(params[0], params[1]);
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
            mDialog.dismiss();

//            Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();

            if (s.contains("*")){
                String temp[] = s.split("\\*");

//                Toast.makeText(LoginActivity.this, temp[0], Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopService(new Intent(LoginActivity.this, BackgroundService.class));
                    startForegroundService(new Intent(LoginActivity.this, BackgroundService.class));
                } else {
                    stopService(new Intent(LoginActivity.this, BackgroundService.class));
                    startService(new Intent(LoginActivity.this, BackgroundService.class));
                }

                if (temp[0].compareTo("Farmer") == 0) {

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("UserId",EmailID.getText().toString().trim());
                    editor.putString("LoginType", temp[0]);
                    editor.putString("CertificateNo",temp[1]);
                    editor.apply();
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("position", 0);
                    startActivity(intent);
                    finish();

                } else if (temp[0].compareTo("Retailer") == 0) {

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("UserId", EmailID.getText().toString());
                    editor.putString("LoginType", temp[0]);
                    editor.apply();
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, R_Home_Activity.class);
                    startActivity(intent);
                    finish();

                } else if (temp[0].compareTo("MahilaUdyog") == 0) {


                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("UserId", EmailID.getText().toString());
                    editor.putString("LoginType", temp[0]);
                    editor.apply();
                    editor.commit();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("position", 0);
                    startActivity(intent);
                    finish();

                }


            } else if (s.compareTo("false") == 0) {
                Snackbar.make(relativeLayout, getText(R.string.Invalid_login), Snackbar.LENGTH_LONG).show();
                EmailID.setText("");
                Password.setText("");

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
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
                    Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //Android Runtime Permission
    private boolean weHavePermission() {
        return ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
    private boolean weHavePermission28() {
        return ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestforPermissionFirst() {
        if ( (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))) {
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }
    private void requestforPermissionFirst28() {
        if ( (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.FOREGROUND_SERVICE))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
                || (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE))) {
            requestForResultContactsPermission28();
        } else {
            requestForResultContactsPermission28();
        }
    }

    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE}, 111);
    }
    private void requestForResultContactsPermission28() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.CALL_PHONE}, 111);
    }

}



