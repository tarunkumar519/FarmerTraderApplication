package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class R_ProductList_Activity extends AppCompatActivity {

    SharedPreferences pref;
    ListView list;
    ArrayList<String> data;
    ArrayList<String> data1;
    ArrayList<String> data2;
    Dialog mDialog;
    String UserId, UserType;
    int testing = 0;
    String CategoryType = "";
    String FilterValue = "false";
    float TotalRating;
    double lat = 0, lng = 0;

    ArrayList<String> AllData;
    ArrayList<String> RatingArray;
    ArrayList<String> LocationArray;

    EditText SearchText;
    ImageView SearchBtn;

    TableLayout NoProductLayout;
    Button ShowProductBtn;
    String ColumnName = "", Clause = "";
    CardView SearchLayout;
    String BarcodeNoText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retailor_products_list);

        mDialog = new Dialog(R_ProductList_Activity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        list = (ListView) findViewById(R.id.product_list);
        SearchLayout = (CardView) findViewById(R.id.search_layout);

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");

        SearchText = (EditText) findViewById(R.id.search_text);
        SearchBtn = (ImageView) findViewById(R.id.search_btn);
        NoProductLayout = (TableLayout) findViewById(R.id.no_product_layout);
        ShowProductBtn = (Button) findViewById(R.id.show_all_product_btn);


        getSupportActionBar().setTitle("Product List");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        CategoryType = intent.getStringExtra("CategoryType");


        try {
            GPS_Tracker gps_tracker = new GPS_Tracker(R_ProductList_Activity.this, R_ProductList_Activity.this);

            if (gps_tracker.canGetLocation()) {
                lat = gps_tracker.getLatitude();
                lng = gps_tracker.getLongitude();

            } else {
                Toast.makeText(R_ProductList_Activity.this, "Enable Your GPS(Location)", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NoProductLayout.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                SearchProduct(SearchText.getText().toString());
            }
        });


        ShowProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoProductLayout.setVisibility(View.GONE);
                new getProductList().execute(FilterValue, ColumnName, Clause, UserId, CategoryType);

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

        testing++;

        if (testing == 2) {
            testing = 0;
        } else if (testing == 1) {

//             bool check, string col, string clause, string uid, string cat
            new getProductList().execute(FilterValue, ColumnName, Clause, UserId, CategoryType);

            testing = 0;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.filter_option) {

            showFilterDialog();

        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public class getProductList extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.RetailerProductList(params[0], params[1], params[2], params[3], params[4]);
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

                list.setAdapter(null);
//                Snackbar.make(list, "There are No Product available!", Snackbar.LENGTH_SHORT).show();
                SearchLayout.setVisibility(View.GONE);
                NoProductLayout.setVisibility(View.VISIBLE);
                ShowProductBtn.setVisibility(View.GONE);

            } else if (s.compareTo("no") == 0) {

                list.setAdapter(null);
//                Snackbar.make(list, "There are No Product available!", Snackbar.LENGTH_SHORT).show();
                SearchLayout.setVisibility(View.GONE);
                NoProductLayout.setVisibility(View.VISIBLE);
                ShowProductBtn.setVisibility(View.GONE);

            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();
                data1 = new ArrayList<String>();
                data2 = new ArrayList<String>();


                for (int i = 0; i < temp.length; i++) {
                    TotalRating = 0;
                    data.add(temp[i]);

                    String temp1[] = data.get(i).split("\\*");

                    //0    1      2    3       4       5     6      7       8      9
                    //pid*pname*image*price*quantity*unit*location*rating*contact*cno#
                    //For Rating
                    try {
                        if (temp1[7].length() > 0) {
                            String[] rat = temp1[7].split("\\$");
                            for (int j = 0; j < rat.length; j++) {

                                TotalRating += Float.parseFloat(rat[j]);
                            }
                            TotalRating = TotalRating / rat.length;
                            data1.add(TotalRating + "");

                        } else {
                            data1.add("no");
                        }
                    } catch (Exception e) {
                        data1.add("no");
                    }


                    // For Location
                    String[] dis = temp1[6].split(",");
                    double dlat = Double.parseDouble(dis[0]);
                    double dlng = Double.parseDouble(dis[1]);

                    Location loc1 = new Location("");
                    loc1.setLatitude(lat);
                    loc1.setLongitude(lng);

                    Location loc2 = new Location("");
                    loc2.setLatitude(dlat);
                    loc2.setLongitude(dlng);

                    float dist = loc1.distanceTo(loc2);
                    float distance = dist / 1000;
                    DecimalFormat df = new DecimalFormat("0.##");
                    String totalDistance = df.format(distance);
                    data2.add(totalDistance);

                }


                Adapter adapt = new Adapter(R_ProductList_Activity.this, data, data1, data2);
                list.setAdapter(adapt);

                if (SearchText.getText().toString().length() > 0) {
                    NoProductLayout.setVisibility(View.GONE);
                    SearchProduct(SearchText.getText().toString());
                }


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_ProductList_Activity.this);
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
                    Toast.makeText(R_ProductList_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;
        ArrayList<String> dataset1;
        ArrayList<String> dataset2;

        public Adapter(Context context, ArrayList<String> data, ArrayList<String> data1, ArrayList<String> data2) {
            super(context, R.layout.r_product_listrow, data);
            con = context;
            dataset = data;
            dataset1 = data1;
            dataset2 = data2;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.r_product_listrow, null, true);

            TextView ProductName = (TextView) v.findViewById(R.id.productName);
            TextView ProductQuantity = (TextView) v.findViewById(R.id.product_quantity);
            TextView ProductPrice = (TextView) v.findViewById(R.id.product_price);
            ImageView ProductImage = (ImageView) v.findViewById(R.id.product_image);

            TextView ProductRating = (TextView) v.findViewById(R.id.rating);
            TextView ProductLocation = (TextView) v.findViewById(R.id.location_text);
            TextView ProductContact = (TextView) v.findViewById(R.id.phone_text);

            RelativeLayout LocationLayout = (RelativeLayout) v.findViewById(R.id.location_layout);
            RelativeLayout ContactLayout = (RelativeLayout) v.findViewById(R.id.phone_layout);
            TableRow RatingLayout = (TableRow) v.findViewById(R.id.rating_layout);

            final String temp[] = dataset.get(position).split("\\*");

            //0    1      2    3       4       5     6      7       8      9
            //pid*pname*image*price*quantity*unit*location*rating*contact*cno#

            String q = "<b> Q : </b>" + temp[4] + " " + temp[5];
            String p = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[3];

            ProductName.setText(temp[1]);
            ProductQuantity.setText(Html.fromHtml(q));
            ProductPrice.setText(Html.fromHtml(p));

            byte[] imageAsBytes = Base64.decode(temp[2].getBytes(), Base64.DEFAULT);
            ProductImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));


            ProductLocation.setText(dataset2.get(position).toString() + " Km");

            ProductContact.setText(temp[8]);

            if (dataset1.get(position).toString().compareTo("no") == 0) {
                RatingLayout.setVisibility(View.GONE);
            } else {

                ProductRating.setText(dataset1.get(position).toString());
            }

            LocationLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });


            ContactLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + temp[8]));
                    startActivity(intent);

                }
            });


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(R_ProductList_Activity.this, R_ProductDetail_Activity.class);
                    intent.putExtra("ProductId", temp[0]);
                    intent.putExtra("BarcodeNo",temp[9]);
                    startActivity(intent);
                }
            });

            return v;
        }
    }


    public void SearchProduct(String searchText) {

        AllData = new ArrayList<String>();
        RatingArray = new ArrayList<String>();
        LocationArray = new ArrayList<String>();

        for (int i = 0; i < data.size(); i++) {
            String temp[] = data.get(i).split("\\*");

//            Toast.makeText(this, temp[1].toUpperCase() + "  " + searchText.toUpperCase(), Toast.LENGTH_SHORT).show();

            if (temp[1].toUpperCase().contains(searchText.toUpperCase())) {
//                Toast.makeText(this, "true", Toast.LENGTH_SHORT).show();
                TotalRating = 0;

                AllData.add(data.get(i));

                //For Rating
                try {
                    if (temp[6].length() > 0) {
                        String[] rat = temp[6].split("\\$");
                        for (int j = 0; j < rat.length; j++) {

                            TotalRating += Float.parseFloat(rat[j]);
                        }
                        TotalRating = TotalRating / rat.length;
                        RatingArray.add(TotalRating + "");

                    } else {
                        RatingArray.add("no");
                    }
                } catch (Exception e) {
                    RatingArray.add("no");
                }


                // For Location
                String[] dis = temp[5].split(",");
                double dlat = Double.parseDouble(dis[0]);
                double dlng = Double.parseDouble(dis[1]);

                Location loc1 = new Location("");
                loc1.setLatitude(lat);
                loc1.setLongitude(lng);

                Location loc2 = new Location("");
                loc2.setLatitude(dlat);
                loc2.setLongitude(dlng);

                float dist = loc1.distanceTo(loc2);
                float distance = dist / 1000;
                DecimalFormat df = new DecimalFormat("0.##");
                String totalDistance = df.format(distance);
                LocationArray.add(totalDistance);


            }

        }

        if (AllData.size() == 0) {
            list.setAdapter(null);
            NoProductLayout.setVisibility(View.VISIBLE);

        } else {

            Adapter adapt = new Adapter(R_ProductList_Activity.this, AllData, RatingArray, LocationArray);
            list.setAdapter(adapt);
        }


    }


    public void showFilterDialog() {

        final Dialog dialog = new Dialog(R_ProductList_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.filter_dialog);

        final Button NameBtn = (Button) dialog.findViewById(R.id.name_btn);
        final Button PriceBtn = (Button) dialog.findViewById(R.id.price_btn);
        Button SubmitBtn = (Button) dialog.findViewById(R.id.submit_btn);
        Button ClearBtn = (Button) dialog.findViewById(R.id.clear_btn);

        final ImageView NameType = (ImageView) dialog.findViewById(R.id.name_type);
        final ImageView PriceType = (ImageView) dialog.findViewById(R.id.price_type);


        if (ColumnName.compareTo("") == 0) {

            NameBtn.setBackgroundResource(R.drawable.rounded_corner);
            NameBtn.setTextColor(Color.BLACK);
            NameType.setVisibility(View.GONE);
            PriceBtn.setBackgroundResource(R.drawable.rounded_corner);
            PriceBtn.setTextColor(Color.BLACK);
            PriceType.setVisibility(View.GONE);

        } else if (ColumnName.compareTo("PName") == 0) {
            NameBtn.setBackgroundResource(R.drawable.rounded_btn);
            NameBtn.setTextColor(Color.WHITE);
            NameType.setVisibility(View.VISIBLE);

            if (Clause.compareTo("ASC") == 0) {
                NameType.setImageResource(R.drawable.aesc_icon1);

            } else {

                NameType.setImageResource(R.drawable.desc_icon1);

            }

        } else if (ColumnName.compareTo("Price") == 0) {
            PriceBtn.setBackgroundResource(R.drawable.rounded_btn);
            PriceBtn.setTextColor(Color.WHITE);
            PriceType.setVisibility(View.VISIBLE);

            if (Clause.compareTo("ASC") == 0) {
                PriceType.setImageResource(R.drawable.aesc_icon1);

            } else {
                PriceType.setImageResource(R.drawable.desc_icon1);

            }


        }


        NameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NameBtn.setBackgroundResource(R.drawable.rounded_btn);
                NameBtn.setTextColor(Color.WHITE);
                NameType.setVisibility(View.VISIBLE);
                NameType.setImageResource(R.drawable.aesc_icon1);

                PriceBtn.setBackgroundResource(R.drawable.rounded_corner);
                PriceBtn.setTextColor(Color.BLACK);
                PriceType.setVisibility(View.GONE);
                ColumnName = "PName";
                Clause = "ASC";
            }
        });


        PriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PriceBtn.setBackgroundResource(R.drawable.rounded_btn);
                PriceBtn.setTextColor(Color.WHITE);
                PriceType.setVisibility(View.VISIBLE);
                PriceType.setImageResource(R.drawable.aesc_icon1);

                NameBtn.setBackgroundResource(R.drawable.rounded_corner);
                NameBtn.setTextColor(Color.BLACK);
                NameType.setVisibility(View.GONE);
                ColumnName = "Price";
                Clause = "ASC";

            }
        });


        NameType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Clause.compareTo("ASC") == 0) {
                    NameType.setImageResource(R.drawable.desc_icon1);
                    Clause = "DESC";

                } else if (Clause.compareTo("DESC") == 0) {
                    NameType.setImageResource(R.drawable.aesc_icon1);
                    Clause = "ASC";

                }
            }
        });


        PriceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Clause.compareTo("ASC") == 0) {
                    PriceType.setImageResource(R.drawable.desc_icon1);
                    Clause = "DESC";

                } else if (Clause.compareTo("DESC") == 0) {
                    PriceType.setImageResource(R.drawable.aesc_icon1);
                    Clause = "ASC";

                }

            }
        });


        ClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NameBtn.setBackgroundResource(R.drawable.rounded_corner);
                NameBtn.setTextColor(Color.BLACK);
                NameType.setVisibility(View.GONE);

                PriceBtn.setBackgroundResource(R.drawable.rounded_corner);
                PriceBtn.setTextColor(Color.BLACK);
                PriceType.setVisibility(View.GONE);

                ColumnName = "";
                Clause = "";

                // bool check, string col, string clause, string uid, string cat
                FilterValue = "false";
                dialog.dismiss();
                new getProductList().execute(FilterValue, ColumnName, Clause, UserId, CategoryType);


            }
        });


        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // bool check, string col, string clause, string uid, string cat
                FilterValue = "true";
                dialog.dismiss();
                new getProductList().execute(FilterValue, ColumnName, Clause, UserId, CategoryType);
            }
        });


        dialog.show();


    }




}
