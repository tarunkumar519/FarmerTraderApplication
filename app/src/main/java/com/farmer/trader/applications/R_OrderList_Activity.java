package com.farmer.trader.applications;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;



public class R_OrderList_Activity extends AppCompatActivity {

    SharedPreferences pref;
    ListView list;
    ArrayList<String> data;
    Dialog mDialog;
    String UserId, UserType;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_productlist_layout);

        mDialog = new Dialog(R_OrderList_Activity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        relativeLayout = (RelativeLayout) findViewById(R.id.payment_screen);
        list = (ListView) findViewById(R.id.product_list);

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");

        getSupportActionBar().setTitle("Order List");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        int NotificationId = intent.getIntExtra("NotificationId", 1000);

        if (NotificationId != 1000) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel("cc", NotificationId);

        }

    }

    @Override
    public void onResume() {
        super.onResume();

//             string uid
        new getOrderList().execute(UserId);
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


    public class getOrderList extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.getOrders(params[0]);
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

            if (s.compareTo("no") == 0) {
                list.setAdapter(null);
                Snackbar.make(list, "You have not made any order", Snackbar.LENGTH_SHORT).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();

                for (int i = 0; i < temp.length; i++) {
                    data.add(temp[i]);
//                    String temp1[] = data.get(i).split("\\*");
                }

                Adapter adapt = new Adapter(R_OrderList_Activity.this, data);
                list.setAdapter(adapt);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_OrderList_Activity.this);
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
                    Toast.makeText(R_OrderList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;

        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.r_order_listrow, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.r_order_listrow, null, true);

            TextView ProductName = (TextView) v.findViewById(R.id.productName);
            TextView ProductQuantity = (TextView) v.findViewById(R.id.product_quantity);
            TextView ProductPrice = (TextView) v.findViewById(R.id.product_price);
            ImageView ProductImage = (ImageView) v.findViewById(R.id.product_image);

            TextView OrderId = (TextView) v.findViewById(R.id.order_id);
            TextView DateTime = (TextView) v.findViewById(R.id.datetime_text);
            TextView SellerName = (TextView) v.findViewById(R.id.seller_name);
            TextView OrderStatus = (TextView) v.findViewById(R.id.order_status);
            ImageView ReviewImage = (ImageView) v.findViewById(R.id.review_btn);

            final String temp[] = dataset.get(position).split("\\*");

            // 0   1    2    3      4          5      6      7      8    9      10
            //oid*pid*pname*pic*sellername*price*quantity*status*date*time*user_rated#

            String q = "<b> Q : </b>" + temp[6];
            String p = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[5];

            ProductName.setText(temp[2]);
            ProductQuantity.setText(Html.fromHtml(q));
            ProductPrice.setText(Html.fromHtml(p));
            OrderStatus.setText(temp[7]);

            byte[] imageAsBytes = Base64.decode(temp[3].getBytes(), Base64.DEFAULT);
            ProductImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

            OrderId.setText("Order Id: "+temp[0]);
            SellerName.setText(temp[4]);
//            OrderStatus.setText(temp[7]);
            DateTime.setText(temp[8]+" "+temp[9]);

//            Toast.makeText(R_OrderList_Activity.this, temp[7], Toast.LENGTH_SHORT).show();

            if (temp[7].compareTo("Self Pickup") == 0 || temp[7].compareTo("Delivered") == 0) {

                OrderStatus.setTextColor(Color.parseColor("#FF0D800F"));

            } else {
                OrderStatus.setTextColor(Color.parseColor("#ff6849"));
            }


            if (temp[10].compareTo("no")==0 && (temp[7].compareTo("Delivered")==0 || temp[7].compareTo("Self Pickup")==0)){

                ReviewImage.setVisibility(View.VISIBLE);
            }

            ReviewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(R_OrderList_Activity.this,R_ReviewList_Activity.class);
                    startActivity(intent);
                    finish();
                }
            });


            return v;
        }
    }


}
