package com.farmer.trader.applications;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;


public class HelpLine extends Fragment {

    protected View mView;
    TextView Number1, Number2;
    TextView Web1, Web2, Web3, Web4;
    Dialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.helpline_layout, container, false);

        Boolean ans = weHavePermission();
        if (!ans) {
            requestforPermissionFirst();
        }

        mDialog = new Dialog(getActivity(), R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);

        Number1 = (TextView) mView.findViewById(R.id.no_text1);
        Number2 = (TextView) mView.findViewById(R.id.no_text2);

        Web1 = (TextView) mView.findViewById(R.id.web1);
        Web2 = (TextView) mView.findViewById(R.id.web2);
        Web3 = (TextView) mView.findViewById(R.id.web3);
        Web4 = (TextView) mView.findViewById(R.id.web4);


        Number1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean ans = weHavePermission();
                if (!ans) {
                    requestforPermissionFirst();
                } else {

                    if (Number1.getText().toString().compareTo("") != 0) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + Number1.getText().toString().trim()));
                        startActivity(callIntent);
                    }
                }
            }
        });


        Number2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean ans = weHavePermission();
                if (!ans) {
                    requestforPermissionFirst();
                } else {

                    if (Number2.getText().toString().compareTo("") != 0) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + Number2.getText().toString().trim()));
                        startActivity(callIntent);

                    }

                }
            }
        });


        Web1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ShowWebViewDialog(Web1.getText().toString().trim());
            }
        });

        Web2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowWebViewDialog(Web2.getText().toString().trim());
            }
        });

        Web3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowWebViewDialog(Web3.getText().toString().trim());
            }
        });

        Web4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowWebViewDialog(Web4.getText().toString().trim());
            }
        });


        return mView;
    }


    public void ShowWebViewDialog(String WebUrl) {

        final Dialog dialog = new Dialog(getActivity(), R.style.AppTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.website_dialog);

        WebView webView = (WebView) dialog.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);


        webView.loadUrl(WebUrl);


        dialog.show();

    }

    //Android Runtime Permission

    private boolean weHavePermission() {
        return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestforPermissionFirst() {
        if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE))) {
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }

    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 111);
    }

}
