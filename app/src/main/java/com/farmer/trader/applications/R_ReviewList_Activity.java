package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
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
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


public class R_ReviewList_Activity extends AppCompatActivity {

    SharedPreferences pref;
    ListView list;
    ArrayList<String> data;
    ArrayList<String> FavList;
    Dialog mDialog;
    String UserId, UserType;
    RelativeLayout relativeLayout;
    int sellerPosition = 0;
    Adapter adapt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_productlist_layout);

        mDialog = new Dialog(R_ReviewList_Activity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        relativeLayout = (RelativeLayout) findViewById(R.id.payment_screen);
        list = (ListView) findViewById(R.id.product_list);

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");

        getSupportActionBar().setTitle("Review Order");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onResume() {
        super.onResume();

//             string uid
        new getReviewList().execute(UserId);
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


    public class getReviewList extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.ReviewOrders(params[0]);
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
                Snackbar.make(list, "There are no items for Review", Snackbar.LENGTH_SHORT).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();
                FavList = new ArrayList<String>();

                for (int i = 0; i < temp.length; i++) {
                    data.add(temp[i]);
                    String temp1[] = data.get(i).split("\\*");
                    FavList.add(temp1[10]);
                }

                adapt = new Adapter(R_ReviewList_Activity.this, data, FavList);
                list.setAdapter(adapt);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ReviewList_Activity.this);
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
                    Toast.makeText(R_ReviewList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;
        ArrayList<String> dataset1;

        public Adapter(Context context, ArrayList<String> data, ArrayList<String> favList) {
            super(context, R.layout.r_review_listrow, data);
            con = context;
            dataset = data;
            dataset1 = favList;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.r_review_listrow, null, true);

            TextView ProductName = (TextView) v.findViewById(R.id.productName);
            TextView ProductQuantity = (TextView) v.findViewById(R.id.product_quantity);
            TextView ProductPrice = (TextView) v.findViewById(R.id.product_price);
            ImageView ProductImage = (ImageView) v.findViewById(R.id.product_image);

            TextView OrderId = (TextView) v.findViewById(R.id.order_id);
            TextView DateTime = (TextView) v.findViewById(R.id.datetime_text);
            TextView SellerName = (TextView) v.findViewById(R.id.seller_name);
            ImageView FavSellerImage = (ImageView) v.findViewById(R.id.fav_btn);

            final String temp[] = dataset.get(position).split("\\*");

            // 0  1    2    3    4       5       6      7       8    9     10
            //oid*pid*pname*pic*fmid*sellername*price*quantity*date*time*isfav#

            String q = "<b> Q : </b>" + temp[7];
            String p = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[6];

            ProductName.setText(temp[2]);
            ProductQuantity.setText(Html.fromHtml(q));
            ProductPrice.setText(Html.fromHtml(p));

            byte[] imageAsBytes = Base64.decode(temp[3].getBytes(), Base64.DEFAULT);
            ProductImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

            OrderId.setText("Order Id: " + temp[0]);
            SellerName.setText(temp[5]);
            DateTime.setText(temp[8] + " " + temp[9]);


            if (dataset1.get(position).compareTo("true") == 0) {
                FavSellerImage.setImageResource(R.drawable.like_icon);

            } else if (dataset1.get(position).compareTo("false") == 0) {
                FavSellerImage.setImageResource(R.drawable.dislike_icon);

            }


            FavSellerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sellerPosition = position;

                    if (dataset1.get(position).compareTo("true") == 0) {
//                        string uid, string fmid
                        new RemoveFavTask().execute(UserId, temp[4]);

                    } else if (dataset1.get(position).compareTo("false") == 0) {
//                        string uid, string fmid
                        new AddtoFavTask().execute(UserId, temp[4]);

                    }
                }
            });


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDialog(temp[0], temp[1]);
                }
            });

            return v;
        }
    }


    public class AddtoFavTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.AddtoFav(params[0], params[1]);
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

                Snackbar.make(relativeLayout, "Problem In Adding To Favorite", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {

//                Snackbar.make(relativeLayout, "Added To Favorite", Snackbar.LENGTH_LONG).show();
                Toast.makeText(R_ReviewList_Activity.this, "Added To Favorite", Toast.LENGTH_SHORT).show();
                FavList.set(sellerPosition, "true");
                adapt.notifyDataSetChanged();

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ReviewList_Activity.this);
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
                    Toast.makeText(R_ReviewList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
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

                Snackbar.make(relativeLayout, "Problem In Removing from Favorite", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {

//                Snackbar.make(relativeLayout, "Removed From Favorite", Snackbar.LENGTH_LONG).show();
                Toast.makeText(R_ReviewList_Activity.this, "Removed From Favorite", Toast.LENGTH_SHORT).show();
                FavList.set(sellerPosition, "false");
                adapt.notifyDataSetChanged();

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ReviewList_Activity.this);
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
                    Toast.makeText(R_ReviewList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void showDialog(final String Oid, final String Pid) {

        final Dialog dialog = new Dialog(R_ReviewList_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.r_review_dialog);

        final RatingBar ratingbar = (RatingBar) dialog.findViewById(R.id.ratingbar);
        final EditText reviewText = (EditText) dialog.findViewById(R.id.review_text);
        Button SubmitBtn = (Button) dialog.findViewById(R.id.submit_btn);


        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Float ratingNumber = ratingbar.getRating();

                if (reviewText.getText().toString().equals("")) {

                    Toast.makeText(R_ReviewList_Activity.this, "Review is Required", Toast.LENGTH_SHORT).show();
                } else {

                    //  string oid, string pid, string rating, string review
                    new AddReviewTask().execute(Oid, Pid, ratingNumber + "", reviewText.getText().toString());
                    dialog.dismiss();
                }
            }
        });


        dialog.show();

    }


    public class AddReviewTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.AddReview(params[0], params[1], params[2], params[3]);
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

                Snackbar.make(relativeLayout, "Problem in Adding Review,Try Again!", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {
                Snackbar.make(relativeLayout, "Review Added Successfully", Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new getReviewList().execute(UserId);
                    }
                }, 1500);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ReviewList_Activity.this);
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
                    Toast.makeText(R_ReviewList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
