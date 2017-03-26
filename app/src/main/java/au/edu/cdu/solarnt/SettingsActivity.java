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
import android.widget.ImageView;
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
    String[] suburbs;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final AutoCompleteTextView autoCompleteTextViewLocation = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewLocation);
        final ImageView imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
        final ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        final TextView textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        final ImageView imageViewGeolocate = (ImageView) findViewById(R.id.imageViewGeolocate);

        loadSuburbs();

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);

        if (imageViewGeolocate != null) {
            imageViewGeolocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, suburbs);
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

}
