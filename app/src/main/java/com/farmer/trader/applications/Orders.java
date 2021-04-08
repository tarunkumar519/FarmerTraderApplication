package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;



public class Orders extends Fragment {

    protected View mView;
    SharedPreferences pref;
    ListView list;
    ArrayList<String> data;
    Dialog mDialog;
    RelativeLayout relativeLayout;
    String UserId, UserType;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.order_fragment, container, false);


        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        list = (ListView) mView.findViewById(R.id.order_list);
        relativeLayout = (RelativeLayout) mView.findViewById(R.id.order_screen);

        pref = getActivity().getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");


        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();

        new getOrdersList().execute(UserId);

    }


    public class getOrdersList extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.Orders(params[0]);
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
                Snackbar.make(list, "There is No Order Detail available!", Snackbar.LENGTH_SHORT).show();

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
            super(context, R.layout.farmer_order_listrow, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.farmer_order_listrow, null, true);

            TextView OrderId = (TextView) v.findViewById(R.id.order_id);
            TextView ProductName = (TextView) v.findViewById(R.id.product_name);
            TextView ProductPrice = (TextView) v.findViewById(R.id.product_price);
            TextView ProductQuantity = (TextView) v.findViewById(R.id.product_quantity);
            TextView DateTime = (TextView) v.findViewById(R.id.order_date_time);
            TextView OrderStatus = (TextView) v.findViewById(R.id.order_status);

            final String temp[] = dataset.get(position).split("\\*");

            // 0        1      2     3     4      5       6      7   8
            //oid*retailername*pid*pname*price*quantity*status*date*time#

            OrderId.setText("Order ID : " + temp[0]);
            ProductName.setText(temp[3]);

            String p = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[4];
            String q = "<b> Q : </b>" + temp[5];

            ProductPrice.setText(Html.fromHtml(p));
            ProductQuantity.setText(Html.fromHtml(q));

            if (temp[6].compareTo("Self Pickup") == 0 || temp[6].compareTo("Delivered") == 0) {

                OrderStatus.setTextColor(Color.parseColor("#FF4CAF50"));

            } else {
                OrderStatus.setTextColor(Color.parseColor("#FF4CAF50"));
            }

            OrderStatus.setText(temp[6]);
            DateTime.setText(temp[7] + " " + temp[8]);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (temp[6].compareTo("Self Pickup") == 0 || temp[6].compareTo("Delivered") == 0) {

                    } else {

                        updateOrderStatusDialog(temp[0], temp[2], temp[9], temp[6]);
                    }

                }
            });

            return v;
        }
    }


    public void updateOrderStatusDialog(String orderId, String pid, String retailorId, String status) {

        final String OrderId = orderId;
        final String Pid = pid;
        final String RetailorId = retailorId;
        final String Status = status;
        final ArrayAdapter<String> adapter;

        final Dialog d;
        final Spinner OrderStatusSpinner;

        d = new Dialog(getActivity());
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.farmer_order_update);

        Button proceed_btn = (Button) d.findViewById(R.id.proceed_btn);
        OrderStatusSpinner = (Spinner) d.findViewById(R.id.order_status_spinner);

        if (Status.compareTo("Placed") == 0) {



            String[] items = new String[]{"Processed", "Dispatched", "Delivered"};
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            OrderStatusSpinner.setAdapter(adapter);


        } else if (Status.compareTo("Processed") == 0) {

            String[] items = new String[]{"Dispatched", "Delivered"};
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            OrderStatusSpinner.setAdapter(adapter);

        } else if (Status.compareTo("Dispatched") == 0) {

            String[] items = new String[]{"Delivered"};
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            OrderStatusSpinner.setAdapter(adapter);
        }


        proceed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
//                string oid, string pid, string retailerid, string status
                new UpdateStatusTask().execute(OrderId, Pid, RetailorId, OrderStatusSpinner.getSelectedItem().toString());
            }
        });

        d.show();

    }


    public class UpdateStatusTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.ChangeStatus(params[0], params[1], params[2], params[3]);
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

                Snackbar.make(relativeLayout, "Order Status Updated Successfully", Snackbar.LENGTH_LONG).show();

                new getOrdersList().execute(UserId);

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


}
