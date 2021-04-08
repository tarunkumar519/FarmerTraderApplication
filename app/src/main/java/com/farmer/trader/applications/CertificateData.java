package com.farmer.trader.applications;

import android.content.Context;


public class CertificateData {

    static String CertificateData;
    Context context;

    CertificateData(Context context) {

        this.context = context;
    }


    public void setCertificateData(String certificateData) {

        this.CertificateData = certificateData;

    }

    public String getCertificateData() {
        if (CertificateData == null) {
            CertificateData = "Na";
        }
        return CertificateData;
    }


}
