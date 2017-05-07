package au.edu.cdu.solarnt;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Weather.WeatherData;
import Weather.WeatherList;

public class SettingsActivity extends AppCompatActivity {
    WeatherList weatherList;
    WeatherData weatherData;
    String[] suburbs;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupButtons();

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);

        final AutoCompleteTextView settingsAutoCompleteTextViewLocation = (AutoCompleteTextView)findViewById(R.id.settingsAutoCompleteTextViewLocation);
        final ImageView imageViewSearch = (ImageView) findViewById(R.id.settingsImageViewSearch);
        final ImageView imageViewCancel = (ImageView) findViewById(R.id.settingsImageViewCancel);
        final TextView textViewLocation = (TextView) findViewById(R.id.settingsTextViewLocation);
        final ImageView imageViewGeolocate = (ImageView) findViewById(R.id.settingsImageViewGeolocate);

        loadSuburbs();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, suburbs);

        if(settingsAutoCompleteTextViewLocation!=null){

            settingsAutoCompleteTextViewLocation.setText(sharedPreferences.getString("post_code", "0800").concat(" ").concat(sharedPreferences.getString("suburb", "Darwin")));


            settingsAutoCompleteTextViewLocation.setAdapter(adapter);
            settingsAutoCompleteTextViewLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, autoText.getText(),Toast.LENGTH_LONG).show();
                    WeatherData weatherData = weatherList.getWeatherDataByDisplayName(settingsAutoCompleteTextViewLocation.getText().toString());
                    textViewLocation.setText(weatherData.getPostcode().concat(" ").concat(weatherData.getSuburb()));
                    SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                    editor.putFloat("latitude", weatherData.getLatitude());
                    editor.putFloat("longitude", weatherData.getLongitude());
                    editor.putString("post_code", weatherData.getPostcode());
                    editor.putString("suburb", weatherData.getSuburb());
                    editor.commit();
                    textViewLocation.setVisibility(View.VISIBLE);
                    imageViewSearch.setVisibility(View.VISIBLE);
                    imageViewGeolocate.setVisibility(View.VISIBLE);
                    imageViewCancel.setVisibility(View.GONE);
                    settingsAutoCompleteTextViewLocation.setVisibility(View.GONE);
                }
            });
        }

        if (textViewLocation != null) {
            textViewLocation.setText(sharedPreferences.getString("post_code", "0800").concat(" ").concat(sharedPreferences.getString("suburb", "Darwin")));
        }


//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationListener = new SettingsActivity.MyLocationListener();
//        if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            System.out.println("Failed to get location");
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);

        if (imageViewGeolocate != null) {
            imageViewGeolocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        if (imageViewSearch != null){
            imageViewSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageViewGeolocate!=null && settingsAutoCompleteTextViewLocation!=null) {
                        textViewLocation.setVisibility(View.GONE);
                        imageViewSearch.setVisibility(View.GONE);
                        imageViewGeolocate.setVisibility(View.GONE);
                        imageViewCancel.setVisibility(View.VISIBLE);
                        settingsAutoCompleteTextViewLocation.setVisibility(View.VISIBLE);
                        settingsAutoCompleteTextViewLocation.setText("");
                    }
                }
            });
        }

        if(imageViewCancel!=null){
            imageViewCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textViewLocation.setVisibility(View.VISIBLE);
                    imageViewSearch.setVisibility(View.VISIBLE);
                    imageViewGeolocate.setVisibility(View.VISIBLE);
                    imageViewCancel.setVisibility(View.GONE);
                    settingsAutoCompleteTextViewLocation.setVisibility(View.GONE);
                }
            });

        }

        Switch settingsSwitchSolarUser = (Switch) findViewById(R.id.settingsSwitchSolarUser);
        System.out.println(settingsSwitchSolarUser);
        if(settingsSwitchSolarUser!=null){

            final Switch settingsSwitchLiveUploader = (Switch) findViewById(R.id.settingsSwitchLiveUploader);
            settingsSwitchSolarUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(settingsSwitchLiveUploader!=null){
                        if(isChecked==true){
                            settingsSwitchLiveUploader.setVisibility(View.VISIBLE);
                        }else{
                            settingsSwitchLiveUploader.setVisibility(View.GONE);
                            settingsSwitchLiveUploader.setChecked(false);
                        }
                    }
                }
            });

            Boolean solarUser = sharedPreferences.getBoolean("solar_user", false);
            settingsSwitchSolarUser.setChecked(solarUser);

            if(settingsSwitchSolarUser.isChecked() ==true){
                settingsSwitchLiveUploader.setVisibility(View.VISIBLE);
            }else{
                settingsSwitchLiveUploader.setVisibility(View.GONE);
                settingsSwitchLiveUploader.setChecked(false);
            }
        }

        Switch settingsSwitchLiveUploader = (Switch) findViewById(R.id.settingsSwitchLiveUploader);
        if(settingsSwitchLiveUploader!=null){
            final LinearLayout settingsPVOutput = (LinearLayout) findViewById(R.id.settingsPVOutput);
            settingsSwitchLiveUploader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(settingsPVOutput!=null){
                        if(isChecked==true){
                            settingsPVOutput.setVisibility(View.VISIBLE);
                        }else{
                            settingsPVOutput.setVisibility(View.GONE);
                        }
                    }
                }
            });

            Boolean liveUploader = sharedPreferences.getBoolean("live_uploader", false);
            settingsSwitchLiveUploader.setChecked(liveUploader);

            if(settingsSwitchLiveUploader.isChecked() ==true){
                settingsPVOutput.setVisibility(View.VISIBLE);
            }else{
                settingsPVOutput.setVisibility(View.GONE);
            }
        }


        EditText editTextSystemID = (EditText) findViewById(R.id.settingsEditTextPVOutputSystemID);
        if(editTextSystemID!=null){
            String systemID = sharedPreferences.getString("sid", "47892");
            editTextSystemID.setText(systemID);
        }

        EditText editTextKey = (EditText) findViewById(R.id.settingsEditTextPVOutputKey);
        if(editTextKey!=null){
            String key = sharedPreferences.getString("key", "4012c804abb523bf7466ef415c9ba808e8aae946");
            editTextKey.setText(key);
        }


        Switch switchAutoRefresh = (Switch) findViewById(R.id.settingsSwitchAutoRefresh);
        if(switchAutoRefresh!=null){
            Boolean autoRefresh = sharedPreferences.getBoolean("auto_refresh", false);
            switchAutoRefresh.setChecked(autoRefresh);
        }


        EditText editTextNumberOfSystems = (EditText) findViewById(R.id.settingsEditTextNumberOfSystems);
        if(editTextNumberOfSystems!=null){
            int numberOfSystems = sharedPreferences.getInt("number_of_systems", 3);
            editTextNumberOfSystems.setText(String.valueOf(numberOfSystems));
        }


        EditText editTextTariff = (EditText) findViewById(R.id.settingsEditTextTariff);
        if(editTextTariff!=null){
            Float tariff = sharedPreferences.getFloat("flat_rate_tariff", (float) 0.2595);
            editTextTariff.setText(String.valueOf(tariff));
        }



        Switch switchWeatherStation = (Switch) findViewById(R.id.settingsSwitchWeatherStation);
        if(switchWeatherStation!=null){
            switchWeatherStation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Switch switchWeatherUnderground = (Switch) findViewById(R.id.settingsSwitchWeatherUnderground);
                    if(switchWeatherUnderground!=null){
                        if(isChecked==true){
                            switchWeatherUnderground.setVisibility(View.VISIBLE);
                        }else{
                            switchWeatherUnderground.setVisibility(View.GONE);
                            switchWeatherUnderground.setChecked(false);
                        }
                    }
                }
            });

            Boolean wunderUser = sharedPreferences.getBoolean("wunder_user", false);
            switchWeatherStation.setChecked(wunderUser);

            Switch switchWeatherUnderground = (Switch) findViewById(R.id.settingsSwitchWeatherUnderground);
            if(switchWeatherUnderground!=null){
                if(switchWeatherStation.isChecked()==true){
                    switchWeatherUnderground.setVisibility(View.VISIBLE);
                }else{
                    switchWeatherUnderground.setVisibility(View.GONE);
                    switchWeatherUnderground.setChecked(false);
                }
            }
        }

        Switch switchWeatherUnderground = (Switch) findViewById(R.id.settingsSwitchWeatherUnderground);
        if(switchWeatherUnderground!=null){
            switchWeatherUnderground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    LinearLayout settingsWeatherUnderground = (LinearLayout) findViewById(R.id.settingsWeatherUnderground);
                    if(settingsWeatherUnderground!=null){
                        if(isChecked==true){
                            settingsWeatherUnderground.setVisibility(View.VISIBLE);
                        }else{
                            settingsWeatherUnderground.setVisibility(View.GONE);
                        }
                    }
                }
            });
            Boolean wunderUploader = sharedPreferences.getBoolean("wunder_uploader", false);
            switchWeatherUnderground.setChecked(wunderUploader);

            LinearLayout settingsWeatherUnderground = (LinearLayout) findViewById(R.id.settingsWeatherUnderground);
            if(settingsWeatherUnderground!=null){
                if(switchWeatherUnderground.isChecked()==true){
                    settingsWeatherUnderground.setVisibility(View.VISIBLE);
                }else{
                    settingsWeatherUnderground.setVisibility(View.GONE);
                }
            }
        }


        EditText editTextWeatherKey = (EditText) findViewById(R.id.settingsEditTextWeatherKey);
        if(editTextWeatherKey!=null){
            String key = sharedPreferences.getString("wunder_key", "a5d5665e6d63f78c");
            editTextWeatherKey.setText(String.valueOf(key));
        }

    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences",MODE_PRIVATE).edit();
        editor.putBoolean("solar_user",((Switch)findViewById(R.id.settingsSwitchSolarUser)).isChecked());
        editor.putBoolean("live_uploader",((Switch)findViewById(R.id.settingsSwitchLiveUploader)).isChecked());
        editor.putString("sid",((EditText)findViewById(R.id.settingsEditTextPVOutputSystemID)).getText().toString());
        editor.putString("key",((EditText)findViewById(R.id.settingsEditTextPVOutputKey)).getText().toString());
        editor.putBoolean("auto_refresh",((Switch)findViewById(R.id.settingsSwitchAutoRefresh)).isChecked());
        editor.putInt("number_of_systems", Integer.parseInt(((EditText)findViewById(R.id.settingsEditTextNumberOfSystems)).getText().toString()));
        editor.putFloat("flat_rate_tariff", Float.parseFloat(((EditText)findViewById(R.id.settingsEditTextTariff)).getText().toString()));
        editor.putBoolean("wunder_user",((Switch)findViewById(R.id.settingsSwitchWeatherStation)).isChecked());
        editor.putBoolean("wunder_uploader",((Switch)findViewById(R.id.settingsSwitchWeatherUnderground)).isChecked());
        editor.putString("wunder_key",((EditText)findViewById(R.id.settingsEditTextWeatherKey)).getText().toString());
        editor.commit();
        finish();
    }

    private void loadSuburbs(){
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(getAssets().open("data.csv"));
            BufferedReader reader = new BufferedReader(is);
            weatherList = new WeatherList();
            try {
                reader.readLine();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split(",");
                        weatherList.addWeatherData(Integer.parseInt(data[0]), "0".concat(data[1]), data[2], Float.parseFloat(data[3]), Float.parseFloat(data[4]), Integer.parseInt(data[5]), data[6], Float.parseFloat(data[7]), Float.parseFloat(data[8]), Float.parseFloat(data[9]), Float.parseFloat(data[10]), Integer.parseInt(data[11]), data[12], Float.parseFloat(data[13]), Float.parseFloat(data[14]), Float.parseFloat(data[15]), Float.parseFloat(data[16]), Integer.parseInt(data[17]), data[18], Float.parseFloat(data[19]), Float.parseFloat(data[20]), Float.parseFloat(data[21]), Float.parseFloat(data[22]), Float.parseFloat(data[23]), Float.parseFloat(data[24]), Float.parseFloat(data[25]));
                    }
                    suburbs = new String[weatherList.getCount()];
                    for(int index = 0; index < suburbs.length; index++){
                        suburbs[index] = weatherList.getWeatherDataByIndex(index).getPostcode().concat(" ").concat(weatherList.getWeatherDataByIndex(index).getSuburb());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.settingsImageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.settingsImageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                }
            });
        }
//
//        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
//        if(imageButtonSettings != null){
//            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
//                }
//            });
//        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.settingsImageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("context", "dusting");
                    Intent intent = new Intent(SettingsActivity.this, HelpActivity.class);
                    intent.putExtra("extras", bundle);
                    startActivity(intent);
                }
            });
        }

        ImageButton imageButtonCDU = (ImageButton)findViewById(R.id.settingsImageButtonCDU);
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

        ImageButton imageButtonCRE = (ImageButton)findViewById(R.id.settingsImageButtonCRE);
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

}
