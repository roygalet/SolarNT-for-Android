package au.edu.cdu.solarnt;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.math.BigDecimal;

import Weather.WeatherData;

public class DustingActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PICTURE = 100;
    Bitmap photo;
    //    WeatherData weatherData;
    ContentValues values;
    Uri imageUri;
    WeatherData weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dusting);

        Bundle bundle = getIntent().getExtras();
        weatherData = bundle.getBundle("weather").getParcelable("weather");

        ((LinearLayout)findViewById(R.id.dustLinearLayout)).setVisibility(View.INVISIBLE);

        ImageButton buttonCamera = (ImageButton) findViewById(R.id.dustButtonCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    onClickCamera();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageButton buttonGallery = (ImageButton) findViewById(R.id.dustButtonGallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        setupButtons();
    }

    private void onClickCamera() throws InterruptedException {
//        photo = null;
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ImageView imageView = (ImageView) findViewById(R.id.dustImgImage);
//            ((TextView)findViewById(R.id.dustTextNoImage)).setVisibility(View.INVISIBLE);
            if (requestCode == SELECT_PICTURE) {
                // Get the url from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    imageView.setImageURI(selectedImageUri);
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (requestCode == CAMERA_REQUEST) {
//                photo = (Bitmap) data.getExtras().get("data");
//                imageView.setImageBitmap(photo);

                try {
                    photo = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    imageView.setImageBitmap(photo);
//                    imgView.setImageBitmap(thumbnail);
//                    imageurl = getRealPathFromURI(imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            new Analyzer().execute(photo);
        }

    }

    private class Analyzer extends AsyncTask<Bitmap, Double, Double> {
        private ProgressDialog pd;
        private int width;
        private int height;
        private int divisor;

        @Override
        protected Double doInBackground(Bitmap... inputPhoto) {
            double dust=0;
            float [] hsv = new float[3];

            width = inputPhoto[0].getWidth();
            height = inputPhoto[0].getHeight();
            divisor = height/10;
            pd.setTitle("Processing Photo (".concat(String.valueOf(width)).concat(" x ").concat(String.valueOf(height).concat(") pixels.")));

            for (int y = 0; y < height; y++) {
                if(y%divisor==1)pd.setProgress((100 * y / height));
                for (int x = 0; x < width; x++) {
//                    pd.setProgress((100 * y / height));
                    int pixel = inputPhoto[0].getPixel(x, y);
                    Color.colorToHSV(pixel,hsv);
                    if(hsv[0]<90 && hsv[1] > 0.2){
                        dust = dust + 1;
                    }
                }
            }
            dust = dust / (width * height) * 100;
            return dust;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ((LinearLayout)findViewById(R.id.dustLinearLayout)).setVisibility(View.INVISIBLE);
            pd = new ProgressDialog(DustingActivity.this);
            pd.setTitle("Processing Photo");
            pd.setMessage("Please wait.");
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setCancelable(false);
            pd.setMax(100);
            pd.show();
        }

        @Override
        protected void onPostExecute(Double aDouble) {
            super.onPostExecute(aDouble);
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            String suburb = sharedPreferences.getString("suburb", "Darwin");
            float capacity = sharedPreferences.getFloat("recent_selected_capacity", (float) 4.5);
            float solarRadiation = weatherData.getSolarmean();
            float maxEfficiency = sharedPreferences.getFloat("max_efficiency", (float) 1.001635544);
            float flatRateTariff = sharedPreferences.getFloat("flat_rate_tariff", (float) 0.2595);


            double powerLoss = solarRadiation * capacity * maxEfficiency * aDouble/100;
            double cost = powerLoss * flatRateTariff;
            SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
            editor.putFloat("dusting", (float) (aDouble/1));
            editor.putFloat("power_loss", (float) (powerLoss/1));
            editor.putFloat("cost_of_power_loss", (float) (cost/1));
            editor.commit();

            if (pd!=null) {
                pd.dismiss();
            }
            ((LinearLayout)findViewById(R.id.dustLinearLayout)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.dustMessage)).setText("A fraction of the Solar Exposure is being blocked by dust resulting to lower than usual power conversion efficiency of the solar PV system."
                    .concat("\n\nA ").concat(String.valueOf(capacity)).concat(" kW PV system in ").concat(suburb).concat(" could lose up to ")
                    .concat(String.valueOf(BigDecimal.valueOf(powerLoss).setScale(1, BigDecimal.ROUND_HALF_UP))
                            .concat(" kWh amounting to $ ")
                            .concat(String.valueOf(BigDecimal.valueOf(cost).setScale(2, BigDecimal.ROUND_HALF_UP)))
                            .concat(" daily.\n\nCleaning may improve system efficiency.\n\n")));
            ((TextView)findViewById(R.id.dustTextPercent)).setText(String.valueOf(BigDecimal.valueOf(Double.valueOf(String.valueOf(aDouble))).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()).concat(" %"));
//            ((TextView)findViewById(R.id.dustTextCost)).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
        }
    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DustingActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DustingActivity.this, HomeActivity.class));
                }
            });
        }

        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DustingActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("context", "dusting");
                    Intent intent = new Intent(DustingActivity.this, HelpActivity.class);
                    intent.putExtra("extras", bundle);
                    startActivity(intent);
                }
            });
        }

        ImageButton imageButtonCDU = (ImageButton)findViewById(R.id.imageButtonCDU);
        if(imageButtonCDU != null) {
            imageButtonCDU.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://www.cdu.edu.au"));
                    startActivity(intent);
                }
            });
        }

        ImageButton imageButtonCRE = (ImageButton)findViewById(R.id.imageButtonCRE);
        if(imageButtonCRE != null) {
            imageButtonCRE.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://www.cdu.edu.au/cre"));
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
}
