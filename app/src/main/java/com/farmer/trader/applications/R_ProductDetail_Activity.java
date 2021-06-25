package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;



public class R_ProductDetail_Activity extends AppCompatActivity {

    TextView ProductName, ProductQuantity, ProductPrice, ProductDescription, MainType, SubType, Location;
    ImageView ProductImage;
    Dialog mDialog;
    SharedPreferences pref;
    String UserId, UserType;
    RelativeLayout relativeLayout;
    String ProductID;
    Spinner QuantitySpinner;

    CardView ReviewLayout;
    LinearLayout reviewlay;
    float TotalRating;
    RatingBar ratingBar;
    Button AddtoCart;
    double lat = 0, lng = 0;
    String Tquantity = "1";
    String TotalPrice, FMID,PImage,UnitText;
    String BarcodeNoText="";
    TextView ViewCertificate;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_product_detail_activity);


        mDialog = new Dialog(R_ProductDetail_Activity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
//        mDialog.setCancelable(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Product Detail");

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");

        Intent intent = getIntent();
        ProductID = intent.getStringExtra("ProductId");
        BarcodeNoText = intent.getStringExtra("BarcodeNo");

        try {
            GPS_Tracker gps_tracker = new GPS_Tracker(R_ProductDetail_Activity.this, R_ProductDetail_Activity.this);

            if (gps_tracker.canGetLocation()) {
                lat = gps_tracker.getLatitude();
                lng = gps_tracker.getLongitude();

            } else {
//                Toast.makeText(R_ProductDetail_Activity.this, "Enable Your GPS(Location)", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        init();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void init() {

        ViewCertificate  = (TextView) findViewById(R.id.view_certificate);
        ProductName = (TextView) findViewById(R.id.product_name);
        ProductQuantity = (TextView) findViewById(R.id.total_quantity);
        ProductPrice = (TextView) findViewById(R.id.product_price);
        ProductDescription = (TextView) findViewById(R.id.product_description);
        MainType = (TextView) findViewById(R.id.main_type);
        SubType = (TextView) findViewById(R.id.sub_type);
        Location = (TextView) findViewById(R.id.location);
        QuantitySpinner = (Spinner) findViewById(R.id.product_quantity);

        ProductImage = (ImageView) findViewById(R.id.product_image);

        AddtoCart = (Button) findViewById(R.id.add_to_card_btn);

        relativeLayout = (RelativeLayout) findViewById(R.id.r_product_detail_screen);

        reviewlay = (LinearLayout) findViewById(R.id.review_lay);
        ReviewLayout = (CardView) findViewById(R.id.review_layout);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);

        ViewCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new getCertificateTask().execute(BarcodeNoText);

            }
        });

        QuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Tquantity = QuantitySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new productDetailTask().execute(ProductID);

        AddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                string pid,string pname,string price,string quantity,string unit,string uid,string fmid,string pic

                int TPrice = Integer.parseInt(TotalPrice) * Integer.parseInt(Tquantity);
                new AddtoCartTask().execute(ProductID,ProductName.getText().toString(),TPrice+"",
                        Tquantity,UnitText,UserId,FMID,PImage);
            }
        });


    }


    public class productDetailTask extends AsyncTask<String, JSONObject, String> {


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
                JSONObject json = api.getProductDetails(params[0]);
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
                Snackbar.make(relativeLayout, "Product Detail Not Available", Snackbar.LENGTH_LONG).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\*");

                relativeLayout.setVisibility(View.VISIBLE);

                //  0     1     2     3        4       5       6      7       8   9     10     11
                //pname*image*desc*maintype*subtype*location*price*quantity*unit*fmid*rating*review

                ProductName.setText(temp[0]);

                PImage = temp[1];
                byte[] decodedString = Base64.decode(temp[1], Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ProductImage.setImageBitmap(decodedByte);

                ProductDescription.setText(temp[2]);
                MainType.setText(temp[3]);
                SubType.setText(temp[4]);

                FMID = temp[9];
                UnitText = temp[8];
                // For Location
                String[] dis = temp[5].split(",");
                double dlat = Double.parseDouble(dis[0]);
                double dlng = Double.parseDouble(dis[1]);

                android.location.Location loc1 = new Location("");
                loc1.setLatitude(lat);
                loc1.setLongitude(lng);

                Location loc2 = new Location("");
                loc2.setLatitude(dlat);
                loc2.setLongitude(dlng);

                float dist = loc1.distanceTo(loc2);
                float distance = dist / 1000;
                DecimalFormat df = new DecimalFormat("0.##");
                String totalDistance = df.format(distance);
                Location.setText(totalDistance + "Km");


                String quantity = "<b> / </b>" + temp[7]+" "+temp[8];
                String price = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[6];
                TotalPrice = temp[6];
                ProductQuantity.setText(Html.fromHtml(quantity));
                ProductPrice.setText(Html.fromHtml(price));

                int qLength = Integer.parseInt(temp[7]);

                ArrayList<String> tempValue = new ArrayList<String>();

                for (int i = 1; i <= qLength; i++) {

                    tempValue.add(i + "");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(R_ProductDetail_Activity.this, R.layout.quantity_spinner_textview, tempValue);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                QuantitySpinner.setAdapter(adapter);


                try {

                    if (temp[10].length() > 0) {

                        String[] rat = temp[10].split("\\$");
                        String[] rev = temp[11].split("\\$");

                        for (int i = 0; i < rat.length; i++) {

                            View view = LayoutInflater.from(R_ProductDetail_Activity.this).inflate(R.layout.review_rating_layout, null);
                            TextView reviewtext = (TextView) view.findViewById(R.id.review_text);
                            TextView ratingtext = (TextView) view.findViewById(R.id.rating_text);
                            View v = (View) view.findViewById(R.id.divider_line);
                            reviewtext.setText(rev[i]);
                            ratingtext.setText(rat[i]);

                            if (i == rat.length - 1) {
                                v.setVisibility(View.GONE);
                            } else {
                                v.setVisibility(View.VISIBLE);
                            }

                            TotalRating += Float.parseFloat(rat[i]);

                            reviewlay.addView(view);

                        }

                        TotalRating = TotalRating / rat.length;
                        ratingBar.setRating(TotalRating);

                    } else {

                        ReviewLayout.setVisibility(View.GONE);
                        ratingBar.setVisibility(View.GONE);
                    }


                } catch (Exception e) {

                    ReviewLayout.setVisibility(View.GONE);
                    ratingBar.setVisibility(View.GONE);

                }



            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ProductDetail_Activity.this);
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
                    Toast.makeText(R_ProductDetail_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class AddtoCartTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.AddtoCart(params[0], params[1], params[2], params[3], params[4], params[5],params[6],params[7]);
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
                Snackbar.make(relativeLayout, "Problem in Adding product to Cart", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {
                Snackbar.make(relativeLayout, "Product Added To Cart", Snackbar.LENGTH_LONG).show();

            }else if (s.compareTo("already") == 0) {
                Snackbar.make(relativeLayout, "Product already added in Cart", Snackbar.LENGTH_LONG).show();

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ProductDetail_Activity.this);
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
                    Toast.makeText(R_ProductDetail_Activity.this,"exp:"+ s, Toast.LENGTH_SHORT).show();
                }
            }
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
                Toast.makeText(R_ProductDetail_Activity.this, "No Certificate Detail Available", Toast.LENGTH_SHORT).show();

            } else if (s.compareTo("no") == 0) {
                Toast.makeText(R_ProductDetail_Activity.this, "No Certificate Detail Available", Toast.LENGTH_SHORT).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\*");
                //name*soilpercent*soilrate*idate*edate

                showCertificateDialog(BarcodeNoText, temp[0], temp[1], temp[2], temp[3], temp[4]);


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ProductDetail_Activity.this);
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
                    Toast.makeText(R_ProductDetail_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void showCertificateDialog(String barcodeno, String username, String soilper, String soilrating,
                                      String issuedate, String expirydate) {

        final Dialog dialog = new Dialog(R_ProductDetail_Activity.this,R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.certificate_dialog);

        TextView BarcodeNo = (TextView) dialog.findViewById(R.id.barcode_no);
        TextView MainText = (TextView) dialog.findViewById(R.id.main_text);
        TextView IssueDateText = (TextView) dialog.findViewById(R.id.issue_date_text);
        TextView ExpiryDateText = (TextView) dialog.findViewById(R.id.expiry_date_text);
        TextView SoilPer = (TextView) dialog.findViewById(R.id.soil_per_text);
        TextView SoilRating = (TextView) dialog.findViewById(R.id.soil_rating_text);
        TextView ExtraText = (TextView) dialog.findViewById(R.id.main_text1);


        String barcode = "Certificate No:" + barcodeno;
        String maintx = "This document certifies that the operator " + username + "'s land and soil has been inspected " +
                "and satisfies the requirement of the soil association organic standard.";

        String issueD = "Issue Date:" + "<b>" + issuedate + "</b>";
        String expiryD = "Expiry Date: " + "<b>" + expirydate + "</b>";
        String soilP = "Soil per:" + "<b>" + soilper + "</b>";
        String soilR = "Soil Rating:" + "<b>" + soilrating + "</b>";

        String extrax = "Valid until the date stated,unless surrendered,suspended or revoked, To verify the current" +
                "validation of the document,contact Soil Association Certification directly.";

        BarcodeNo.setText(barcode);
        MainText.setText(Html.fromHtml(maintx));
        IssueDateText.setText(Html.fromHtml(issueD));
        ExpiryDateText.setText(Html.fromHtml(expiryD));
        SoilPer.setText(Html.fromHtml(soilP));
        SoilRating.setText(Html.fromHtml(soilR));
        ExtraText.setText(Html.fromHtml(extrax));

        dialog.show();

    }



}
