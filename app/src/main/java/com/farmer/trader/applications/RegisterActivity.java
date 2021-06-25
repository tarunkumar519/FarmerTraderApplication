package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class RegisterActivity extends AppCompatActivity {

    protected EditText Name, Contact, EmailID, UserName, Password, ConfirmPassord, AadharNo, UploadCertificateText, Address;
    protected Button RegisterBtn;
    protected ImageView Upload_CertificateBtn;
    protected CardView Upload_CertificateLayout;
    protected RelativeLayout relativeLayout;
    Dialog mDialog;
    String RegistrationType = "";
    SharedPreferences pref;
    String LanType = "";
    String CertificateValue = "Na";
    CertificateData certificateData;
    SimpleDateFormat sdfd = new SimpleDateFormat("yyyy/MM/dd", Locale.US);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mDialog = new Dialog(RegisterActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        RegistrationType = intent.getStringExtra("RegistrationType");

        init();

    }


    @Override
    protected void onResume() {
        super.onResume();
        certificateData = new CertificateData(RegisterActivity.this);

        try {

            CertificateValue = certificateData.getCertificateData();
            if (CertificateValue.compareTo("Na")!=0){
                UploadCertificateText.setText(CertificateValue);
            }

        } catch (Exception e) {
            CertificateValue = "Na";
        }

//        Toast.makeText(RegisterActivity.this, CertificateValue, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void init() {

        getSupportActionBar().setTitle(getText(R.string.register_header));
        Name = (EditText) findViewById(R.id.Name);
        Contact = (EditText) findViewById(R.id.phoneNumber);
        EmailID = (EditText) findViewById(R.id.emailID);
        UserName = (EditText) findViewById(R.id.userName);
        Password = (EditText) findViewById(R.id.password);
        ConfirmPassord = (EditText) findViewById(R.id.confirmPassword);
        AadharNo = (EditText) findViewById(R.id.aadhar_no);
        UploadCertificateText = (EditText) findViewById(R.id.upload_certificate_text);
        Address = (EditText) findViewById(R.id.address_text);

        RegisterBtn = (Button) findViewById(R.id.registerButton);
        Upload_CertificateBtn = (ImageView) findViewById(R.id.upload_certificate_btn);
        Upload_CertificateLayout = (CardView) findViewById(R.id.upload_certificate_layout);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_registration);


        if (RegistrationType.compareTo("Farmer") == 0) {
            Upload_CertificateLayout.setVisibility(View.VISIBLE);
        } else {
            Upload_CertificateLayout.setVisibility(View.GONE);
        }


        try {

            Name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    UserName.setText(Name.getText().toString());

                }
            });

        } catch (Exception e) {

            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        UploadCertificateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterActivity.this, CertificateActivity.class);
                intent.putExtra("Source", "Register");
                startActivity(intent);
            }
        });


        Upload_CertificateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterActivity.this, CertificateActivity.class);
                intent.putExtra("Source", "Register");
                startActivity(intent);
            }
        });


        RegisterBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkCriteria()) {

                            String match = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                            if (Name.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, getText(R.string.empty_name), Snackbar.LENGTH_SHORT).show();

                                Name.requestFocus();

                            } else if (Contact.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, getText(R.string.empty_contact), Snackbar.LENGTH_SHORT).show();
                                Contact.requestFocus();

                            }
//                            else if (EmailID.getText().toString().equals("")) {
//                                Snackbar.make(relativeLayout, "Email Id is required", Snackbar.LENGTH_SHORT).show();
//                                EmailID.requestFocus();
//
//                            } else if (!EmailID.getText().toString().matches(match)) {
//                                Snackbar.make(relativeLayout, "Please Follow Email Standards", Snackbar.LENGTH_SHORT).show();
//                                EmailID.requestFocus();
//
//                            }
                            else if (UserName.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, getText(R.string.empty_username), Snackbar.LENGTH_SHORT).show();
                                UserName.requestFocus();

                            } else if (Password.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, getText(R.string.empty_password), Snackbar.LENGTH_SHORT).show();
                                Password.requestFocus();

                            } else if (ConfirmPassord.getText().toString().equals("")) {
                                ConfirmPassord.requestFocus();
                                Snackbar.make(relativeLayout, getText(R.string.empty_confirmpass), Snackbar.LENGTH_SHORT).show();

                            } else if (!Password.getText().toString().equals(ConfirmPassord.getText().toString())) {
                                Snackbar.make(relativeLayout, getText(R.string.password_notmatch), Snackbar.LENGTH_SHORT).show();
                                Password.requestFocus();
                                Password.setText("");
                                ConfirmPassord.setText("");

                            } else if (AadharNo.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, getText(R.string.empty_aadhar), Snackbar.LENGTH_SHORT).show();
                                AadharNo.requestFocus();

                            } else if (Address.getText().toString().equals("")) {
                                Snackbar.make(relativeLayout, getText(R.string.empty_address), Snackbar.LENGTH_SHORT).show();
                                Address.requestFocus();

                            } else {

                                if (RegistrationType.compareTo("Farmer") == 0) {
//                                   string username, string name, string contact, string email, string adhar, string address,
//                                   string usertype,string pass, string cno,string idate,string edate

//                                    Toast.makeText(RegisterActivity.this, CertificateValue, Toast.LENGTH_SHORT).show();

                                    if (CertificateValue.compareTo("Na") != 0) {

                                        Calendar cal = Calendar.getInstance();
                                        String TodayDateText = sdfd.format(cal.getTime());
                                        cal.add(Calendar.MONTH, 3);
                                        String Expirydate = sdfd.format(cal.getTime());

                                        new MahilaFarmer_RegistrationTask().execute(UserName.getText().toString(), Name.getText().toString(),
                                                Contact.getText().toString(), EmailID.getText().toString(), AadharNo.getText().toString(),
                                                Address.getText().toString(), RegistrationType, Password.getText().toString(), CertificateValue,
                                                TodayDateText, Expirydate);

                                    } else {

                                        Toast.makeText(RegisterActivity.this, "Certificate Not found,Try again!", Toast.LENGTH_SHORT).show();
                                    }

                                } else if (RegistrationType.compareTo("Retailer") == 0) {

                                    new MahilaFarmer_RegistrationTask().execute(UserName.getText().toString(), Name.getText().toString(),
                                            Contact.getText().toString(), EmailID.getText().toString(), AadharNo.getText().toString(),
                                            Address.getText().toString(), RegistrationType, Password.getText().toString(), "Na", "Na", "Na");

                                } else if (RegistrationType.compareTo("MahilaUdyog") == 0) {
//                                    string username, string name, string contact, string email, string adhar,
//                                            string address, string certi_image, string certi_rating

                                    new MahilaFarmer_RegistrationTask().execute(UserName.getText().toString(), Name.getText().toString(),
                                            Contact.getText().toString(), EmailID.getText().toString(), AadharNo.getText().toString(),
                                            Address.getText().toString(), RegistrationType, Password.getText().toString(), "Na", "Na", "Na");


                                }

                            }
                        } else {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setMessage(getText(R.string.register_all))
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    }


                });


    }


    protected boolean checkCriteria() {
        boolean b = true;
        if ((Name.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }


    public class MahilaFarmer_RegistrationTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.Register(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]
                        , params[8], params[9], params[10]);
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

            if (s.compareTo("true") == 0) {

                Snackbar.make(relativeLayout, getText(R.string.register_sucess), Snackbar.LENGTH_LONG).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 1500);


            } else if (s.compareTo("already") == 0) {
                UserName.setText("");
                Snackbar.make(relativeLayout, getText(R.string.register_already), Snackbar.LENGTH_LONG).show();

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(RegisterActivity.this);
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
                    Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
