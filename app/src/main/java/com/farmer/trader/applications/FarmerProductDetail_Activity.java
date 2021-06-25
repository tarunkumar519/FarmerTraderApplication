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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;



public class FarmerProductDetail_Activity extends AppCompatActivity {

    TextView ProductName, ProductQuantity, ProductPrice, ProductDescription, MainType, SubType, Location;
    ImageView ProductImage;
    Button UpdateBtn, DeleteBtn;
    Dialog mDialog;
    SharedPreferences pref;
    String UserId, UserType;
    RelativeLayout relativeLayout;
    String ProductID;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmer_product_detail_activity);

        mDialog = new Dialog(FarmerProductDetail_Activity.this, R.style.AppTheme);
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

        ProductName = (TextView) findViewById(R.id.product_name);
        ProductQuantity = (TextView) findViewById(R.id.product_quantity);
        ProductPrice = (TextView) findViewById(R.id.product_price);
        ProductDescription = (TextView) findViewById(R.id.product_description);
        MainType = (TextView) findViewById(R.id.main_type);
        SubType = (TextView) findViewById(R.id.sub_type);
        Location = (TextView) findViewById(R.id.location);

        ProductImage = (ImageView) findViewById(R.id.product_image);

        UpdateBtn = (Button) findViewById(R.id.update_btn);
        DeleteBtn = (Button) findViewById(R.id.delete_btn);

        relativeLayout = (RelativeLayout) findViewById(R.id.farmer_product_detail_screen);

        new productDetailTask().execute(ProductID);


        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FarmerProductDetail_Activity.this, AddProductActivity.class);
                intent.putExtra("FromScreen", "FromUpdate");
                intent.putExtra("PId", ProductID);
                startActivity(intent);
                finish();
            }
        });


        DeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(FarmerProductDetail_Activity.this).setTitle("Delete")
                        .setMessage("Are you sure to Delete ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

//                              string pid, string uid
                                new DeleteProductTask().execute(ProductID, UserId);
                            }

                        })
                        .setNegativeButton(android.R.string.no, null).show();

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
                JSONObject json = api.ProductDetails(params[0]);
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
                Snackbar.make(relativeLayout, "No Detail Available", Snackbar.LENGTH_LONG).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\*");

                // 0      1     2    3         4      5        6      7      8
                //pname*image*desc*maintype*subtype*location*price*quantity*unit
                relativeLayout.setVisibility(View.VISIBLE);
                ProductName.setText(temp[0]);

                byte[] decodedString = Base64.decode(temp[1], Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ProductImage.setImageBitmap(decodedByte);

                ProductDescription.setText(temp[2]);
                MainType.setText(temp[3]);
                SubType.setText(temp[4]);
                Location.setText(temp[5]);

                String quantity = "<b> Q : </b>" + temp[7]+" "+temp[8];
                String price = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[6];
                String punit = "<b>  : </b>" + temp[6];
                ProductQuantity.setText(Html.fromHtml(quantity));
                ProductPrice.setText(Html.fromHtml(price));

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(FarmerProductDetail_Activity.this);
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
                    Toast.makeText(FarmerProductDetail_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class DeleteProductTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.DeleteProduct(params[0], params[1]);
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
                Snackbar.make(relativeLayout, "Problem in Product Deletion, Try Again!", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {
                Snackbar.make(relativeLayout, "Product Deleted successfully", Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(FarmerProductDetail_Activity.this);
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
                    Toast.makeText(FarmerProductDetail_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}



