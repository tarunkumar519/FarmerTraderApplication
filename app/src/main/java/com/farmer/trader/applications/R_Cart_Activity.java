package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.IdRes;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class R_Cart_Activity extends AppCompatActivity {


    SharedPreferences pref;
    ListView list;
    ArrayList<String> data;
    Dialog mDialog;
    String UserId, UserType;
    Button BuyBtn;
    TextView Tquantity, Tprice;
    int TotalQuantity = 0, TotalPrice = 0;
    TableRow Layout1, Layout2, NocartLayout;
    RelativeLayout relativeLayout;

    ArrayList<String> PID, PName, Fmid, Price, Quantity, Pics;
    String Status = "Placed";

    String DateText, TimeText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_cartlist_layout);

        mDialog = new Dialog(R_Cart_Activity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        relativeLayout = (RelativeLayout) findViewById(R.id.r_cart_screen);

        list = (ListView) findViewById(R.id.product_list);
        Tquantity = (TextView) findViewById(R.id.quantity_text);
        Tprice = (TextView) findViewById(R.id.price_text);
        BuyBtn = (Button) findViewById(R.id.buy_btn);

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");

        Layout1 = (TableRow) findViewById(R.id.layout1);
        Layout2 = (TableRow) findViewById(R.id.layout2);
        NocartLayout = (TableRow) findViewById(R.id.nocart_layout1);


        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BuyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

//             string uid
        new getCartList().execute(UserId);
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


    public class getCartList extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.getCartList(params[0]);
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
//                Snackbar.make(list, "There is No data available!", Snackbar.LENGTH_SHORT).show();
                Layout1.setVisibility(View.GONE);
                Layout2.setVisibility(View.GONE);
                NocartLayout.setVisibility(View.VISIBLE);

            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();

                PID = new ArrayList<String>();
                PName = new ArrayList<String>();
                Fmid = new ArrayList<String>();
                Price = new ArrayList<String>();
                Quantity = new ArrayList<String>();
                Pics = new ArrayList<String>();

                TotalQuantity = temp.length;
                TotalPrice = 0;
                for (int i = 0; i < temp.length; i++) {
                    data.add(temp[i]);
                    String temp1[] = data.get(i).split("\\*");

                    TotalPrice += Integer.parseInt(temp1[3]);

                    //   0    1    2     3      4      5   6    7    8
                    //cartid*pid*pname*price*quantity*unit*uid*fmid*img#
                    PID.add(temp1[1]);
                    PName.add(temp1[2]);
                    Fmid.add(temp1[7]);
                    Price.add(temp1[3]);
                    Quantity.add(temp1[4]+" "+temp1[5]);
                    Pics.add(temp1[8]);
                }

                Tprice.setText(""+getResources().getString(R.string.currency)+" : " + TotalPrice);
                Tquantity.setText("Items (" + TotalQuantity + ")");


                Adapter adapt = new Adapter(R_Cart_Activity.this, data);
                list.setAdapter(adapt);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_Cart_Activity.this);
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
                    Toast.makeText(R_Cart_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;

        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.r_cart_listrow, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.r_cart_listrow, null, true);

            TextView ProductName = (TextView) v.findViewById(R.id.productName);
            TextView ProductQuantity = (TextView) v.findViewById(R.id.product_quantity);
            TextView ProductPrice = (TextView) v.findViewById(R.id.product_price);
            ImageView ProductImage = (ImageView) v.findViewById(R.id.product_image);
            Button DeleteBtn = (Button) v.findViewById(R.id.delete_btn);

            final String temp[] = dataset.get(position).split("\\*");

            // 0      1    2      3     4      5   6    7     8
            //cartid*pid*pname*price*quantity*unit*uid*fmid*img#

            String q = "<b> Q : </b>" + temp[4]+" "+temp[5];
            String p = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[3];

            ProductName.setText(temp[2]);
            ProductQuantity.setText(Html.fromHtml(q));
            ProductPrice.setText(Html.fromHtml(p));

            byte[] imageAsBytes = Base64.decode(temp[8].getBytes(), Base64.DEFAULT);
            ProductImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

            DeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(R_Cart_Activity.this).setTitle("Delete")
                            .setMessage("Are you sure to Delete ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

//                                   string cid
                                    new DeleteProductTask().execute(temp[0]);
                                }

                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });


            return v;
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
                JSONObject json = api.deleteCart(params[0]);
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
                Snackbar.make(list, "Problem in Product Deletion, Try Again!", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {
                Snackbar.make(list, "Product Deleted successfully", Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        new getCartList().execute(UserId);
                    }
                }, 1000);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_Cart_Activity.this);
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
                    Toast.makeText(R_Cart_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void showDialog() {

        final Dialog dialog = new Dialog(R_Cart_Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.r_payment_dialog);

        final EditText CardNumber = (EditText) dialog.findViewById(R.id.card_no);
        final EditText ValidityMonth = (EditText) dialog.findViewById(R.id.validity_month);
        final EditText ValidityYear = (EditText) dialog.findViewById(R.id.validity_year);
        final EditText CVVNumber = (EditText) dialog.findViewById(R.id.cvv_number_text);
        final Button Submit = (Button) dialog.findViewById(R.id.submit_btn);

        final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radio_group);
        final RadioButton delivery = (RadioButton) dialog.findViewById(R.id.delivery);
        final RadioButton pickup = (RadioButton) dialog.findViewById(R.id.pickup);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if (checkedId == R.id.delivery) {
                    Status = "Placed";



                } else if (checkedId == R.id.pickup) {
                    Status = "Self Pickup";

                }
            }
        });


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CardNumber.getText().toString().equals("")) {
                    Toast.makeText(R_Cart_Activity.this, "Card Number is required", Toast.LENGTH_SHORT).show();
                    CardNumber.requestFocus();

                } else if (CardNumber.getText().toString().length() != 16) {
                    Toast.makeText(R_Cart_Activity.this, "Card Number should be 16 digit", Toast.LENGTH_SHORT).show();
                    CardNumber.requestFocus();

                } else if (ValidityMonth.getText().toString().equals("")) {
                    Toast.makeText(R_Cart_Activity.this, "Validity Month is required", Toast.LENGTH_SHORT).show();
                    ValidityMonth.requestFocus();

                } else if (ValidityMonth.getText().toString().length() != 2) {
                    Toast.makeText(R_Cart_Activity.this, "Month Should be 2 digit", Toast.LENGTH_SHORT).show();
                    ValidityMonth.requestFocus();


                }  else if (Integer.parseInt(ValidityMonth.getText().toString()) >12 || Integer.parseInt(ValidityMonth.getText().toString()) <=0) {
                    Toast.makeText(R_Cart_Activity.this, "Invalid Month", Toast.LENGTH_SHORT).show();
                    ValidityMonth.requestFocus();


                } else if (ValidityYear.getText().toString().equals("")) {
                    Toast.makeText(R_Cart_Activity.this, "Validity Year is required", Toast.LENGTH_SHORT).show();
                    ValidityYear.requestFocus();

                } else if (ValidityYear.getText().toString().length() != 4) {
                    Toast.makeText(R_Cart_Activity.this, "Year Should be 4 digit", Toast.LENGTH_SHORT).show();
                    ValidityYear.requestFocus();

                } else if (CVVNumber.getText().toString().equals("")) {
                    Toast.makeText(R_Cart_Activity.this, "CVV Number is required", Toast.LENGTH_SHORT).show();
                    CVVNumber.requestFocus();

                } else if (CVVNumber.getText().toString().length() != 3) {
                    Toast.makeText(R_Cart_Activity.this, "CVV Number should be 3 digit", Toast.LENGTH_SHORT).show();
                    CVVNumber.requestFocus();

                } else {

                    // string uid,string[] pid,string[] pname,string[] fmid,string[] price,string[] quantity,string date,
                    //string time,string delivery,string[] pics
                    SimpleDateFormat sdfd = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat sdft = new SimpleDateFormat("HH:mm");

                    Date d = new Date();
                    DateText = sdfd.format(d.getTime());
                    TimeText = sdft.format(d.getTime());

//                string uid,string[] pid,string[] pname,string[] fmid,string[] price,string[]
//                quantity,string date,string time,string delivery,string[] pics
                    new PlaceOrderTask().execute(PID, PName, Fmid, Price, Quantity, Pics);
                    dialog.dismiss();
                }

            }
        });


        dialog.show();

    }


    public class PlaceOrderTask extends AsyncTask<ArrayList<String>, JSONObject, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            String a = "back";
            RestAPI api = new RestAPI();
            try {

//                string uid,string[] pid,string[] pname,string[] fmid,string[] price,string[]
//                quantity,string date,string time,string delivery,string[] pics

                JSONObject json = api.PlaceOrder(UserId, params[0], params[1], params[2], params[3],
                        params[4], DateText, TimeText, Status, params[5]);

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
                Snackbar.make(list, "Problem in Place Order, Try Again!", Snackbar.LENGTH_LONG).show();

            } else if (s.compareTo("true") == 0) {
                Snackbar.make(list, "Order Placed successfully", Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(R_Cart_Activity.this, R_Home_Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }, 1000);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(R_Cart_Activity.this);
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
                    Toast.makeText(R_Cart_Activity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
