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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nevon-Ravi on 29-Sep-17.
 */

public class Home extends Fragment {

    protected View mView;
    SharedPreferences pref;
    ListView list;
    ArrayList<String> data;
    FloatingActionButton floatingActionButton;
    Dialog mDialog;
    Spinner TypeSpinner;
    double Lat = 0, Lng = 0;
    String UserId, UserType;
    int testing = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.home_fragment, container, false);

        testing++;

        TypeSpinner = (Spinner) mView.findViewById(R.id.type_spinner);

        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        list = (ListView) mView.findViewById(R.id.product_list);
        floatingActionButton = (FloatingActionButton) mView.findViewById(R.id.addFloatButton);

        pref = getActivity().getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GPS_Tracker gps_tracker = new GPS_Tracker(getActivity(), getActivity());
                if (gps_tracker.canGetLocation()) {
                    Lat = gps_tracker.getLatitude();
                    Lng = gps_tracker.getLongitude();


                    if (Lat != 0 && Lng != 0) {

                        if (UserType.compareTo("Farmer") == 0) {

                            showFarmerDialog();

                        } else if (UserType.compareTo("MahilaUdyog") == 0) {

                            showMahilaUdyogDialog();
                        }


                    } else {
                        Toast.makeText(getActivity(), "Determining Your cordinates,Click on Add Button Again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Enable Your GPS(Location)", Toast.LENGTH_SHORT).show();
                }


            }
        });


        if (UserType.compareTo("Farmer") == 0)

        {

            String[] items = new String[]{"All", "Fruits", "Vegetable", "Grains"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_textview, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            TypeSpinner.setAdapter(adapter);

        } else if (UserType.compareTo("MahilaUdyog") == 0) {

            String[] items = new String[]{"All", "Eatable", "Handcrafted"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_textview, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            TypeSpinner.setAdapter(adapter);
        }


        TypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                string category, string usertype, string uid
                new getProductList().execute(TypeSpinner.getSelectedItem().toString(), UserType, UserId);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();

        testing++;

        if (testing == 2) {
            testing = 0;
        } else if (testing == 1) {

            if (isAdded()) {

                new getProductList().execute(TypeSpinner.getSelectedItem().toString(), UserType, UserId);

            }
            testing = 0;

        }
    }


    public void showFarmerDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_famer_product_dialog);

        TableRow Fruits_Type = (TableRow) dialog.findViewById(R.id.fruits_type);
        TableRow Vegetable_Type = (TableRow) dialog.findViewById(R.id.vegetable_type);
        TableRow Grains_Type = (TableRow) dialog.findViewById(R.id.grains_type);

        //fruits type
        Fruits_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                intent.putExtra("MainProductType", "Fruits");
                intent.putExtra("FromScreen", "Add");
                startActivity(intent);
                dialog.dismiss();

            }
        });


        //vegetable type
        Vegetable_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                intent.putExtra("MainProductType", "Vegetable");
                intent.putExtra("FromScreen", "Add");
                startActivity(intent);
                dialog.dismiss();

            }
        });


        //grains_type
        Grains_Type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                intent.putExtra("MainProductType", "Grains");
                intent.putExtra("FromScreen", "Add");
                startActivity(intent);
                dialog.dismiss();


            }
        });

        dialog.show();

    }


    public void showMahilaUdyogDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_mahilaudyog_product_dialog);

        TableRow EatableType = (TableRow) dialog.findViewById(R.id.eatable_type);
        TableRow HandcraftedType = (TableRow) dialog.findViewById(R.id.handcrafted_type);


        //Eatable Type
        EatableType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                intent.putExtra("MainProductType", "Eatable");
                intent.putExtra("FromScreen", "Add");
                startActivity(intent);
                dialog.dismiss();
            }
        });


        //Handcrafted Type
        HandcraftedType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), AddProductActivity.class);
                intent.putExtra("MainProductType", "Handcrafted");
                intent.putExtra("FromScreen", "Add");
                startActivity(intent);
                dialog.dismiss();
            }
        });


        dialog.show();

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
                JSONObject json = api.ProductList(params[0], params[1], params[2]);
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
                Snackbar.make(list, "There is No data available!", Snackbar.LENGTH_SHORT).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();
                for (int i = 0; i < temp.length; i++) {
                    data.add(temp[i]);
                }

                Adapter adapt = new Adapter(getActivity(), data);
                list.setAdapter(adapt);

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
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
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class Adapter extends ArrayAdapter<String> {

        Context con;
        ArrayList<String> dataset;

        public Adapter(Context context, ArrayList<String> data) {
            super(context, R.layout.farmer_product_listrow, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.farmer_product_listrow, null, true);

            TextView ProductName = (TextView) v.findViewById(R.id.productName);
            TextView ProductQuantity = (TextView) v.findViewById(R.id.product_quantity);
            TextView ProductPrice = (TextView) v.findViewById(R.id.product_price);
            ImageView ProductImage = (ImageView) v.findViewById(R.id.product_image);

            final String temp[] = dataset.get(position).split("\\*");

            // 0   1      2      3     4
            //pid*pname*image*price*quantity

            String q = "<b> Q : </b>" + temp[4];
            String p = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[3];

            ProductName.setText(temp[1]);
            ProductQuantity.setText(Html.fromHtml(q));
            ProductPrice.setText(Html.fromHtml(p));

            byte[] imageAsBytes = Base64.decode(temp[2].getBytes(), Base64.DEFAULT);
            ProductImage.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), FarmerProductDetail_Activity.class);
                    intent.putExtra("ProductId", temp[0]);
                    startActivity(intent);
                }
            });

            return v;
        }
    }


}
