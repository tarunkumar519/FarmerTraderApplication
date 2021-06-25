package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;



public class Profile extends Fragment {

    protected View mView;
    protected EditText Name, Contact, Email, AadharNo, Address;
    protected Button UpdateProfileBtn;
    protected RelativeLayout relativeLayout;
    Dialog mDialog;
    SharedPreferences pref;
    String UserId, UserType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.r_profile_layout, container, false);

        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        pref = getActivity().getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");

        Name = (EditText) mView.findViewById(R.id.Name);
        Contact = (EditText) mView.findViewById(R.id.phoneNumber);
        Email = (EditText) mView.findViewById(R.id.emailID);
        AadharNo = (EditText) mView.findViewById(R.id.aadhar_no);
        AadharNo.setEnabled(false);
        Address = (EditText) mView.findViewById(R.id.address_text);

        UpdateProfileBtn = (Button) mView.findViewById(R.id.update_btn);
        relativeLayout = (RelativeLayout) mView.findViewById(R.id.activity_profile);


        UpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCriteria()) {

                    if (Name.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Name is required", Snackbar.LENGTH_SHORT).show();
                        Name.requestFocus();

                    } else if (Contact.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Contact is required", Snackbar.LENGTH_SHORT).show();
                        Contact.requestFocus();

                    } else if (Address.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Address is required", Snackbar.LENGTH_SHORT).show();
                        Address.requestFocus();

                    } else {

//                        String name,String contact,String email,String address,String uid
                        new UpdateProfileTask().execute(Name.getText().toString(), Contact.getText().toString(),
                                Email.getText().toString(), Address.getText().toString(), UserId);

                    }
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("All fields are mandatary. Please enter all details")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });


        return mView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            //get profile API
            new getProfileTask().execute(UserId);
        }
    }

    protected boolean checkCriteria() {
        boolean b = true;
        if ((Name.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }


    public class UpdateProfileTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.updateProfile(params[0], params[1], params[2], params[3], params[4]);
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

                Snackbar.make(relativeLayout, "Profile Updated Successfully", Snackbar.LENGTH_LONG).show();


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


    public class getProfileTask extends AsyncTask<String, JSONObject, String> {


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
                JSONObject json = api.getProfile(params[0]);
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
                Snackbar.make(relativeLayout, "No Profile Detail Available", Snackbar.LENGTH_LONG).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\*");
                //Name*contact*email*aadhar*address

                Name.setText(temp[0]);
                Contact.setText(temp[1]);
                Email.setText(temp[2]);
                AadharNo.setText(temp[3]);
                Address.setText(temp[4]);

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
