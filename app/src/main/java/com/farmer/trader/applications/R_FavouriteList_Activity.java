package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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



public class R_FavouriteList_Activity extends AppCompatActivity {

    SharedPreferences pref;
    String UserId, UserType;
    ListView list;
    ArrayList<String> data;
    Dialog mDialog;
    RelativeLayout relativeLayout;
    Adapter adapt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_productlist_layout);

        mDialog = new Dialog(R_FavouriteList_Activity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        relativeLayout = (RelativeLayout) findViewById(R.id.payment_screen);
        list = (ListView) findViewById(R.id.product_list);

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");

        getSupportActionBar().setTitle("Favourite List");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();

//       string uid
        new getFavouriteListTask().execute(UserId);
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


    public class getFavouriteListTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.FavList(params[0]);
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
                Snackbar.make(list, "You don't have any favourite Farmer", Snackbar.LENGTH_SHORT).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();

                for (int i = 0; i < temp.length; i++) {
                    data.add(temp[i]);
                }

                adapt = new Adapter(R_FavouriteList_Activity.this, data);
                list.setAdapter(adapt);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_FavouriteList_Activity.this);
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
                    Toast.makeText(R_FavouriteList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;

        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.r_favourite_listrow, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.r_favourite_listrow, null, true);

            TextView SellerName = (TextView) v.findViewById(R.id.seller_name);
            ImageView removeSellerBtn = (ImageView) v.findViewById(R.id.remove_btn);

            final String temp[] = dataset.get(position).split("\\*");

            //fmid*sellername#

            SellerName.setText(temp[1]);

            removeSellerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(R_FavouriteList_Activity.this).setTitle("Remove")
                            .setMessage("Are you sure to Remove ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


//                                  string uid, string fmid
                                    new RemoveFavTask().execute(UserId, temp[0]);
                                }

                            })
                            .setNegativeButton(android.R.string.no, null).show();

                }
            });

            return v;
        }
    }


    public class RemoveFavTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.RemoveFav(params[0], params[1]);
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

                Snackbar.make(relativeLayout, "Problem in Removing from Favorite", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {

                Snackbar.make(relativeLayout, "Removed From Favorite", Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new getFavouriteListTask().execute(UserId);
                    }
                }, 1500);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_FavouriteList_Activity.this);
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
                    Toast.makeText(R_FavouriteList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
