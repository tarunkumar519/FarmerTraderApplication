package com.farmer.trader.applications;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;



public class OcrDetail_Activity extends AppCompatActivity {

    ImageView OcrImage;
    TextView OcrText;
    Button SubmitBtn;
    private static final int RC_OCR_CAPTURE = 9003;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr_layout);

        startOcr();

        OcrImage = (ImageView) findViewById(R.id.ocrImage);
        OcrText = (TextView) findViewById(R.id.ocr_text);
        SubmitBtn = (Button) findViewById(R.id.detect_ocrtext);

        OcrText.setText("");


        OcrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startOcr();
            }
        });


    }

    public void startOcr() {

        Intent intent = new Intent(OcrDetail_Activity.this, OcrCaptureActivity.class);
        intent.putExtra(OcrCaptureActivity.AutoFocus, true);
        intent.putExtra(OcrCaptureActivity.UseFlash, false);
        startActivityForResult(intent, RC_OCR_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_OCR_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    String text = data.getStringExtra(OcrCaptureActivity.TextBlockObject).toLowerCase();
                    try {

                        int checkValue = 0;
                        String[] items = new String[]{"very bad", "bad", "average", "good", "excellent"};
                        String[] values = new String[]{"20%", "40%", "60%", "80%", "100%"};

                        for (int i = 0; i < items.length; i++) {

                            if (text.contains(items[i])) {
                                OcrText.setText(values[i]);
                                checkValue = 1;
                                break;
                            }
                        }

                        if (checkValue == 0) {

                            Toast.makeText(this, "Could not find the soil status ", Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception e) {
                        Toast.makeText(OcrDetail_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } else {
//                    statusMessage.setText(R.string.ocr_failure);
//                    Log.d(TAG, "No Text captured, intent data is null");
                }
            } else {
//                statusMessage.setText(String.format(getString(R.string.ocr_error),
//                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
