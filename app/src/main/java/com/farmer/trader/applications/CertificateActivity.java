package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
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
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class CertificateActivity extends AppCompatActivity {

    TextView BarcodeNoText, FarmerNameText, SoilPerText, SoilRatingText, IssueDateText, ExpiryDateText;
    Button UploadBtn, SubmitBtn;
    RelativeLayout relativeLayout;
    Dialog mDialog;
    private IntentIntegrator qrScan;
    String SourceValue = "";
    CertificateData certificateData;
    SimpleDateFormat sdfd = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
    SharedPreferences pref;
    String UserId = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.certificate_layout);

        certificateData = new CertificateData(CertificateActivity.this);
        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");

        mDialog = new Dialog(CertificateActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        Intent intent = getIntent();
        SourceValue = intent.getStringExtra("Source");

        getSupportActionBar().setTitle("Upload Certificate");


        BarcodeNoText = (TextView) findViewById(R.id.barcode_no_text);
        FarmerNameText = (TextView) findViewById(R.id.farmer_name);
        SoilPerText = (TextView) findViewById(R.id.soil_per);
        SoilRatingText = (TextView) findViewById(R.id.soil_rating);
        IssueDateText = (TextView) findViewById(R.id.issue_date);
        ExpiryDateText = (TextView) findViewById(R.id.expiry_date);

        UploadBtn = (Button) findViewById(R.id.upload_btn);
        SubmitBtn = (Button) findViewById(R.id.submit_btn);

        relativeLayout = (RelativeLayout) findViewById(R.id.activity_certificate);

        qrScan = new IntentIntegrator(this);


        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qrScan.initiateScan();
            }
        });


        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SourceValue.compareTo("Register") == 0) {
                    certificateData.setCertificateData(BarcodeNoText.getText().toString().trim());
                    finish();

                } else if (SourceValue.compareTo("MainActivty") == 0) {

                    //Update API
                    Calendar cal = Calendar.getInstance();
                    String TodayDateText = sdfd.format(cal.getTime());
                    cal.add(Calendar.MONTH, 3);
                    String Expirydate = sdfd.format(cal.getTime());
//                    string cno, string uid, string idate, string edate

                    new update_certificate_Task().execute(BarcodeNoText.getText().toString().trim(),
                            UserId, TodayDateText, Expirydate);

                }


            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Invalid Certificate,Try again!", Toast.LENGTH_LONG).show();
            } else {
                try {

                    String bid = result.getContents();
                    BarcodeNoText.setText(bid);
                    new getCertificateTask().execute(bid);


                } catch (Exception e) {
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public class getCertificateTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.getCertificate(params[0]);
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

            if (s.compareTo("") == 0) {
                Snackbar.make(relativeLayout, "No Certificate Detail Available", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("no") == 0) {
                Snackbar.make(relativeLayout, "No Certificate Detail Available", Snackbar.LENGTH_LONG).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\*");
                //name*soilpercent*soilrate*idate*edate

                String name = "<b>Name : </b>" + temp[0];
                String soilper = "<b>Soil Percentage : </b>" + temp[1];
                String soilrate = "<b>Soil Rating : </b>" + temp[2];
                String idate = "<b>Issue Date : </b>" + temp[3];
                String edate = "<b>Expiry Date : </b>" + temp[4];

                FarmerNameText.setVisibility(View.VISIBLE);
                SoilRatingText.setVisibility(View.VISIBLE);
                SoilPerText.setVisibility(View.VISIBLE);
                IssueDateText.setVisibility(View.VISIBLE);
                ExpiryDateText.setVisibility(View.VISIBLE);
                SubmitBtn.setVisibility(View.VISIBLE);

                FarmerNameText.setText(Html.fromHtml(name));
                SoilPerText.setText(Html.fromHtml(soilper));
                SoilRatingText.setText(Html.fromHtml(soilrate));
                IssueDateText.setText(Html.fromHtml(idate));
                ExpiryDateText.setText(Html.fromHtml(edate));


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(CertificateActivity.this);
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
                    Toast.makeText(CertificateActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class update_certificate_Task extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.UpdateCertificate(params[0], params[1], params[2], params[3]);
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

                Snackbar.make(relativeLayout, "Certificate updated successfully", Snackbar.LENGTH_LONG).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        finish();
                    }
                }, 1000);


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(CertificateActivity.this);
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
                    Toast.makeText(CertificateActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
