package au.edu.cdu.solarnt;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

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


        SharedPreferences sharedPreferences = getSharedPreferences("SharePreferences", MODE_PRIVATE);

        final AutoCompleteTextView autoCompleteTextViewLocation = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewLocation);
        final ImageView imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
        final ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        final TextView textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        final ImageView imageViewGeolocate = (ImageView) findViewById(R.id.imageViewGeolocate);

        loadSuburbs();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, suburbs);

        if(autoCompleteTextViewLocation!=null){

                autoCompleteTextViewLocation.setText(sharedPreferences.getString("post_code", "0800").concat(" ").concat(sharedPreferences.getString("suburb", "Darwin")));


            autoCompleteTextViewLocation.setAdapter(adapter);
            autoCompleteTextViewLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, autoText.getText(),Toast.LENGTH_LONG).show();
                    WeatherData weatherData = weatherList.getWeatherDataByDisplayName(autoCompleteTextViewLocation.getText().toString());
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
                    autoCompleteTextViewLocation.setVisibility(View.GONE);
                }
            });
        }




        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new SettingsActivity.MyLocationListener();
        if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            System.out.println("Failed to get location");
            return;
        }
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
                    if(imageViewGeolocate!=null && autoCompleteTextViewLocation!=null) {
                        textViewLocation.setVisibility(View.GONE);
                        imageViewSearch.setVisibility(View.GONE);
                        imageViewGeolocate.setVisibility(View.GONE);
                        imageViewCancel.setVisibility(View.VISIBLE);
                        autoCompleteTextViewLocation.setVisibility(View.VISIBLE);
                        autoCompleteTextViewLocation.setText("");
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
                    autoCompleteTextViewLocation.setVisibility(View.GONE);
                }
            });

        }

        Switch switchSolarUser = (Switch) findViewById(R.id.switchSolarUser);


        if(switchSolarUser!=null){
            final Switch switchLiveUploader = (Switch) findViewById(R.id.switchLiveUploader);

            switchSolarUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(switchLiveUploader!=null){
                        if(isChecked==true){
                            switchLiveUploader.setVisibility(View.VISIBLE);
                        }else{
                            switchLiveUploader.setVisibility(View.GONE);
                            switchLiveUploader.setChecked(false);
                        }
                    }
                }
            });

            Boolean solarUser = sharedPreferences.getBoolean("solar_user", false);
            switchSolarUser.setChecked(solarUser);

            if(switchSolarUser.isChecked() ==true){
                switchLiveUploader.setVisibility(View.VISIBLE);
            }else{
                switchLiveUploader.setVisibility(View.GONE);
                switchLiveUploader.setChecked(false);
            }
        }

        Switch switchLiveUploader = (Switch) findViewById(R.id.switchLiveUploader);
        if(switchLiveUploader!=null){
            final LinearLayout settingsPVOutput = (LinearLayout) findViewById(R.id.settingsPVOutput);
            switchLiveUploader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
            switchLiveUploader.setChecked(liveUploader);

            if(switchLiveUploader.isChecked() ==true){
                settingsPVOutput.setVisibility(View.VISIBLE);
            }else{
                settingsPVOutput.setVisibility(View.GONE);
            }
        }


        EditText editTextSystemID = (EditText) findViewById(R.id.editTextSystemID);
        if(editTextSystemID!=null){
            String systemID = sharedPreferences.getString("sid", "47892");
            editTextSystemID.setText(systemID);
        }

        EditText editTextKey = (EditText) findViewById(R.id.editTextKey);
        if(editTextKey!=null){
            String key = sharedPreferences.getString("key", "4012c804abb523bf7466ef415c9ba808e8aae946");
            editTextKey.setText(key);
        }


        Switch switchAutoRefresh = (Switch) findViewById(R.id.switchAutoRefresh);
        if(switchAutoRefresh!=null){
            Boolean autoRefresh = sharedPreferences.getBoolean("auto_refresh", false);
            switchAutoRefresh.setChecked(autoRefresh);
        }


        EditText editTextNumberOfSystems = (EditText) findViewById(R.id.editTextNumberOfSystems);
        if(editTextNumberOfSystems!=null){
            String numberOfSystems = sharedPreferences.getString("number_of_systems", "3");
            editTextNumberOfSystems.setText(numberOfSystems);
        }


        EditText editTextTariff = (EditText) findViewById(R.id.editTextTariff);
        if(editTextTariff!=null){
            Float tariff = sharedPreferences.getFloat("flat_rate_tariff", (float) 0.2595);
            editTextTariff.setText(String.valueOf(tariff));
        }



        Switch switchWeatherStation = (Switch) findViewById(R.id.switchWeatherStation);
        if(switchWeatherStation!=null){
            switchWeatherStation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Switch switchWeatherUnderground = (Switch) findViewById(R.id.switchWeatherUnderground);
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
        }

        Switch switchWeatherUnderground = (Switch) findViewById(R.id.switchWeatherUnderground);
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
            switchWeatherStation.setChecked(wunderUploader);
        }


        EditText editTextWeatherKey = (EditText) findViewById(R.id.editTextWeatherKey);
        if(editTextWeatherKey!=null){
            String key = sharedPreferences.getString("wunder_key", "a5d5665e6d63f78c");
            editTextWeatherKey.setText(String.valueOf(key));
        }

    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences",MODE_PRIVATE).edit();
        editor.putBoolean("solar_user",((Switch)findViewById(R.id.switchSolarUser)).isChecked());
        editor.putBoolean("live_uploader",((Switch)findViewById(R.id.switchLiveUploader)).isChecked());
        editor.putString("sid",((EditText)findViewById(R.id.editTextSystemID)).getText().toString());
        editor.putString("key",((EditText)findViewById(R.id.editTextKey)).getText().toString());
        editor.putBoolean("auto_refresh",((Switch)findViewById(R.id.switchAutoRefresh)).isChecked());
        editor.putInt("number_of_systems", Integer.parseInt(((EditText)findViewById(R.id.editTextNumberOfSystems)).getText().toString()));
        editor.putFloat("flat_rate_tariff", Float.parseFloat(((EditText)findViewById(R.id.editTextTariff)).getText().toString()));
        editor.putBoolean("wunder_user",((Switch)findViewById(R.id.switchWeatherStation)).isChecked());
        editor.putBoolean("wunder_uploader",((Switch)findViewById(R.id.switchWeatherUnderground)).isChecked());
        editor.putString("wunder_key",((EditText)findViewById(R.id.editTextWeatherKey)).getText().toString());
        editor.commit();

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

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
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


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
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

}
