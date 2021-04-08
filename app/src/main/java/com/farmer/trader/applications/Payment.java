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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;



public class Payment extends Fragment {

    protected View mView;
    SharedPreferences pref;
    ListView list;
    ArrayList<String> data;
    Dialog mDialog;
    String UserId, UserType;
    RelativeLayout relativeLayout;

    TableRow PaymentLabel;
    TextView TotalPaymentText;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.payment_fragment, container, false);


        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        list = (ListView) mView.findViewById(R.id.payment_list);
        relativeLayout = (RelativeLayout) mView.findViewById(R.id.payment_screen);

        PaymentLabel = (TableRow) mView.findViewById(R.id.payment_label);
        TotalPaymentText = (TextView) mView.findViewById(R.id.total_payment_text);

        pref = getActivity().getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");


        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();

        new getPaymentList().execute(UserId);
    }


    public class getPaymentList extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.Payments(params[0]);
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
                PaymentLabel.setVisibility(View.GONE);
                Snackbar.make(list, "There is No Payment Detail available!", Snackbar.LENGTH_SHORT).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\#");
                data = new ArrayList<String>();
                float addData = 0;

                for (int i = 0; i < temp.length; i++) {
                    data.add(temp[i]);
                    String temp1[] = data.get(i).split("\\*");
                    addData += Float.parseFloat(temp1[1]);
                }

                TotalPaymentText.setText("Total : " + addData);

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
            super(context, R.layout.farmer_payment_listrow, data);
            con = context;
            dataset = data;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(con).inflate(R.layout.farmer_payment_listrow, null, true);

            TextView ProductName = (TextView) v.findViewById(R.id.product_name);
            TextView RetailorName = (TextView) v.findViewById(R.id.ratailor_name);
            TextView ProductPrice = (TextView) v.findViewById(R.id.price);
            TextView DateTime = (TextView) v.findViewById(R.id.payment_date_time);

            final String temp[] = dataset.get(position).split("\\*");

            //  0     1      2           3    4
            //pname*price*retailername*date*time#

            ProductName.setText(temp[0]);
            RetailorName.setText("Retailer : " + temp[2]);

            String p = "<b> "+getResources().getString(R.string.currency)+" : </b>" + temp[1];
            ProductPrice.setText(Html.fromHtml(p));

            DateTime.setText(temp[3] + " " + temp[4]);


            return v;
        }
    }

}
