package com.farmer.trader.applications;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;



public class AddProductActivity extends AppCompatActivity {

    Spinner TypeSpinner;
    Dialog mapd;
    GoogleMap Gmap;
    double lat = 0, lng = 0;
    EditText LocationText, ProductName, ProductPrice, Quantity, Description;
    ImageView LocationIcon, ProductImage;
    Button AddProduct;
    RelativeLayout relativeLayout;

    ImageHelper imageHelper;
    String encodedImage = "", SubType = "na";
    CardView SpinnerLayout;
    Dialog mDialog;
    String MainProductType = "";
    SharedPreferences pref;
    String UserId, UserType;
    String FromScreen = "";
    String ProductID = "";

    Spinner UnitSpinner;
    String SelectedUnit="";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_activity);
        init();
        MapDailog();

        imageHelper = new ImageHelper();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pref = getSharedPreferences("FarmerTrader", Context.MODE_PRIVATE);
        UserId = pref.getString("UserId", "");
        UserType = pref.getString("LoginType", "");


        mDialog = new Dialog(AddProductActivity.this, R.style.AppTheme);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.circular_dialog);
        mDialog.setCancelable(false);


        Intent intent = getIntent();

        FromScreen = intent.getStringExtra("FromScreen");

        if (FromScreen.compareTo("FromUpdate") == 0) {

            ProductID = intent.getStringExtra("PId");
            getSupportActionBar().setTitle("Update Product");
            new productDetailTask().execute(ProductID);
            AddProduct.setText("Update Product");

        } else if (FromScreen.compareTo("Add") == 0) {

            MainProductType = intent.getStringExtra("MainProductType");
            getSupportActionBar().setTitle("Add Product");

            if (MainProductType.compareTo("Fruits") == 0 || MainProductType.compareTo("Eatable") == 0 || MainProductType.compareTo("Handcrafted") == 0 ) {
                SpinnerLayout.setVisibility(View.GONE);

            } else if (MainProductType.compareTo("Vegetable") == 0) {
//                SubType = TypeSpinner.getSelectedItem().toString();

            } else if (MainProductType.compareTo("Grains") == 0) {
                new getGrainsTask().execute();
//                    SubType = TypeSpinner.getSelectedItem().toString();
            }

        }

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


    public void init() {

        UnitSpinner = (Spinner) findViewById(R.id.unit_spinner);
        LocationText = (EditText) findViewById(R.id.location_text);
        ProductName = (EditText) findViewById(R.id.productName);
        ProductPrice = (EditText) findViewById(R.id.productPrice);
        Quantity = (EditText) findViewById(R.id.productQuantity);
        Description = (EditText) findViewById(R.id.product_description);

        relativeLayout = (RelativeLayout) findViewById(R.id.add_product_screen);
        AddProduct = (Button) findViewById(R.id.addProductBtn);

        TypeSpinner = (Spinner) findViewById(R.id.subtype_spinner);
        LocationIcon = (ImageView) findViewById(R.id.location_btn);
        ProductImage = (ImageView) findViewById(R.id.productImage);

        SpinnerLayout = (CardView) findViewById(R.id.spinner_layout);

        String[] items = new String[]{"Root Vegetable", "Leafy Vegetable", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.spinner_textview, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TypeSpinner.setAdapter(adapter);

        TypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SubType = TypeSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        String[] item1 = new String[]{"Dozen","Litre","kilogram","Number"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(AddProductActivity.this,R.layout.spinner_textview,item1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        UnitSpinner.setAdapter(adapter1);


        try {
            GPS_Tracker gps_tracker = new GPS_Tracker(AddProductActivity.this, AddProductActivity.this);

            if (gps_tracker.canGetLocation()) {
                lat = gps_tracker.getLatitude();
                lng = gps_tracker.getLongitude();
                LocationText.setText(lat + "," + lng);
            } else {
                Toast.makeText(AddProductActivity.this, "Enable Your GPS(Location)", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        LocationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapd.show();

            }
        });

        ProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadFiles(v);
            }
        });


        AddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkCriteria()) {

                    if (ProductName.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Product Name is required", Snackbar.LENGTH_SHORT).show();
                        ProductName.requestFocus();

                    } else if (encodedImage.equals("")) {
                        Snackbar.make(relativeLayout, "Product Image is required", Snackbar.LENGTH_SHORT).show();

                    } else if (ProductPrice.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Product Price is required", Snackbar.LENGTH_SHORT).show();
                        ProductPrice.requestFocus();

                    } else if (Quantity.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Quantity is required", Snackbar.LENGTH_SHORT).show();
                        Quantity.requestFocus();

                    } else if (LocationText.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Location is required", Snackbar.LENGTH_SHORT).show();
                        LocationText.requestFocus();

                    } else if (Description.getText().toString().equals("")) {
                        Snackbar.make(relativeLayout, "Description is required", Snackbar.LENGTH_SHORT).show();
                        Description.requestFocus();

                    } else {

                        if (FromScreen.compareTo("FromUpdate") == 0) {

//                            string pid, string name, string maintype, string subtype, string image, string desc,
//                                    string location, string price, string quantity,string unit

                            SubType = TypeSpinner.getSelectedItem().toString();

                            new UpdateProductTask().execute(ProductID, ProductName.getText().toString(), MainProductType, SubType,
                                    encodedImage, Description.getText().toString(), LocationText.getText().toString(),
                                    ProductPrice.getText().toString(), Quantity.getText().toString(),UnitSpinner.getSelectedItem().toString());

                        } else if (FromScreen.compareTo("Add") == 0) {

//                        string name, string image, string desc, string maintype, string subtype, string location, string price,
//                                string quantity, string uid, string usertype,string unit

                            new AddProductTask().execute(ProductName.getText().toString(), encodedImage, Description.getText().toString(),
                                    MainProductType, SubType, LocationText.getText().toString(), ProductPrice.getText().toString(),
                                    Quantity.getText().toString(), UserId, UserType,UnitSpinner.getSelectedItem().toString());
                        }

                    }
                } else {
                    new AlertDialog.Builder(AddProductActivity.this)
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

    }


    protected boolean checkCriteria() {
        boolean b = true;
        if ((ProductName.getText().toString()).equals("")) {
            b = false;
        }
        return b;
    }


    public void MapDailog() {

        mapd = new Dialog(AddProductActivity.this);
        mapd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mapd.setContentView(R.layout.map_activity);

        Button Submit = (Button) mapd.findViewById(R.id.map_submit);
        Submit.setVisibility(View.VISIBLE);
        final SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.onCreate(mapd.onSaveInstanceState());
        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Gmap = googleMap;

                if (LocationText.getText().toString().compareTo("No Location") != 0) {
                    try {
                        String t[] = LocationText.getText().toString().split(",");
                        lat = Double.parseDouble(t[0]);
                        lng = Double.parseDouble(t[1]);

                        MarkerOptions mo = new MarkerOptions();
                        mo.position(new LatLng(lat, lng));
                        mo.draggable(true);
                        Marker m = Gmap.addMarker(mo);
                        m.showInfoWindow();
                        Gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
                    } catch (Exception e) {
                        Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


                Gmap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(com.google.android.gms.maps.model.LatLng ll) {
                        Gmap.clear();
                        lat = ll.latitude;
                        lng = ll.longitude;

                        MarkerOptions mo = new MarkerOptions();
                        mo.position(new LatLng(lat, lng));
                        mo.draggable(true);
                        Marker m = Gmap.addMarker(mo);
                        m.showInfoWindow();
                        Gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
                    }
                });


                Gmap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        LatLng ll = marker.getPosition();
                        lat = ll.latitude;
                        lng = ll.longitude;
                        Gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
                    }
                });


            }
        });


        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String l = lat + "";
                String l1 = lng + "";

                if (l.length() > 3 && l1.length() > 3) {
                    LocationText.setText(lat + "," + lng);
                    LocationText.setTextColor(Color.BLACK);
                    mapd.hide();
                } else {
                    Snackbar.make(view, "Select a Place", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void loadFiles(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == 1 && data != null && data.getData() != null) {
                Uri uri = data.getData();
                Bitmap bm = imageHelper.loadSizeLimitedBitmapFromUri(uri, getContentResolver());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 30, baos); //bm is the bitmap object
                ProductImage.setImageBitmap(bm);

                Bitmap ReviewImage = bm;
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
            }
        } catch (OutOfMemoryError error) {
            Toast.makeText(this, "Select a lower size image", Toast.LENGTH_SHORT).show();
        }

    }


    public class AddProductTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.AddProduct(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]
                        , params[8], params[9],params[10]);
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

                Snackbar.make(relativeLayout, "Product Added Successfully", Snackbar.LENGTH_LONG).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
//                        Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                }, 3000);


            } else if (s.compareTo("already") == 0) {
                ProductName.setText("");
                Snackbar.make(relativeLayout, "Entered Product Name is already existing", Snackbar.LENGTH_LONG).show();

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(AddProductActivity.this);
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
                    Toast.makeText(AddProductActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class getGrainsTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.getGrains();
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

            if (s.contains("*")) {
                String temp[] = s.split("\\*");

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddProductActivity.this, R.layout.spinner_textview, temp);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                TypeSpinner.setAdapter(adapter);


                if (FromScreen.compareTo("FromUpdate") == 0) {

                    for (int i = 0; i < temp.length; i++) {

                        if (SubType.compareTo(temp[i]) == 0) {

                            TypeSpinner.setSelection(i);
                        }
                    }
                }


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(AddProductActivity.this);
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
                    Toast.makeText(AddProductActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class UpdateProductTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.UpdateProduct(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7]
                        , params[8],params[9]);
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

                Snackbar.make(relativeLayout, "Product Updated Successfully", Snackbar.LENGTH_LONG).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
//                        Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
//                        startActivity(intent);
                        finish();
                    }
                }, 3000);


            } else if (s.compareTo("already") == 0) {
                ProductName.setText("");
                Snackbar.make(relativeLayout, "Entered Product Name is already existing", Snackbar.LENGTH_LONG).show();

            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(AddProductActivity.this);
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
                    Toast.makeText(AddProductActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public class productDetailTask extends AsyncTask<String, JSONObject, String> {

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
                JSONObject json = api.ProductDetails(params[0]);
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
                Snackbar.make(relativeLayout, "No Detail Available", Snackbar.LENGTH_LONG).show();

            } else if (s.contains("*")) {
                String temp[] = s.split("\\*");
                //pname*image*desc*maintype*subtype*location*price*quantity*unit

                ProductName.setText(temp[0]);

                encodedImage = temp[1];
                byte[] decodedString = Base64.decode(temp[1], Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ProductImage.setImageBitmap(decodedByte);

                Description.setText(temp[2]);
                MainProductType = temp[3];
                SubType = temp[4];

                if (MainProductType.compareTo("Fruits") == 0 || MainProductType.compareTo("Eatable") == 0 || MainProductType.compareTo("Handcrafted") == 0 ) {
                    SpinnerLayout.setVisibility(View.GONE);

                } else if (MainProductType.compareTo("Vegetable") == 0) {

                    if (SubType.compareTo("Root Vegetable") == 0) {

                        TypeSpinner.setSelection(0);

                    } else if (SubType.compareTo("Leafy Vegetable") == 0) {

                        TypeSpinner.setSelection(1);

                    } else if (SubType.compareTo("Others") == 0) {

                        TypeSpinner.setSelection(2);

                    }

                } else if (MainProductType.compareTo("Grains") == 0) {
                    new getGrainsTask().execute();
//                    SubType = TypeSpinner.getSelectedItem().toString();

                }

                if (temp[8].compareTo("@string/Dozen")==0){
                    UnitSpinner.setSelection(0);

                }else if (temp[8].compareTo("@string/Litre")==0){
                    UnitSpinner.setSelection(1);

                }else if (temp[8].compareTo("@string/kilogram")==0){
                    UnitSpinner.setSelection(2);

                }else if (temp[8].compareTo("@string/Number")==0){
                    UnitSpinner.setSelection(3);
                }


                LocationText.setText(temp[5]);
                ProductPrice.setText(temp[6]);
                Quantity.setText(temp[7]);


            } else {
                if (s.contains("Unable to resolve host")) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(AddProductActivity.this);
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
                    Toast.makeText(AddProductActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
