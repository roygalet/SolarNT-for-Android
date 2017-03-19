package au.edu.cdu.solarnt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import Weather.WeatherData;
import Weather.WeatherList;



public class DashboardActivity extends AppCompatActivity {
    WeatherList weatherList;
    String[] suburbs;
    LocationManager locationManager;
    LocationListener locationListener;
    String responseWeatherCondition = "{\"response\":{\"version\":\"0.1\",\"termsofService\":\"http://www.wunderground.com/weather/api/d/terms.html\",\"features\":{\"conditions\":1}},\"current_observation\":{\"image\":{\"url\":\"http://icons.wxug.com/graphics/wu2/logo_130x80.png\",\"title\":\"Weather Underground\",\"link\":\"http://www.wunderground.com\"},\"display_location\":{\"full\":\"Brinkin, Australia\",\"city\":\"Brinkin\",\"state\":\"NTR\",\"state_name\":\"Australia\",\"country\":\"AU\",\"country_iso3166\":\"AU\",\"zip\":\"00000\",\"magic\":\"8\",\"wmo\":\"94120\",\"latitude\":\"-12.370000\",\"longitude\":\"130.860000\",\"elevation\":\"14.9\"},\"observation_location\":{\"full\":\"Halkitis Court, Nightcliff, NT\",\"city\":\"Halkitis Court, Nightcliff\",\"state\":\"NT\",\"country\":\"AU\",\"country_iso3166\":\"AU\",\"latitude\":\"-12.391690\",\"longitude\":\"130.850708\",\"elevation\":\"26 ft\"},\"estimated\":{},\"station_id\":\"INTNIGHT2\",\"observation_time\":\"Last Updated on March 18, 1:16 PM ACST\",\"observation_time_rfc822\":\"Sat, 18 Mar 2017 13:16:37 +0930\",\"observation_epoch\":\"1489808797\",\"local_time_rfc822\":\"Sat, 18 Mar 2017 13:24:52 +0930\",\"local_epoch\":\"1489809292\",\"local_tz_short\":\"ACST\",\"local_tz_long\":\"Australia/Darwin\",\"local_tz_offset\":\"+0930\",\"weather\":\"Scattered Clouds\",\"temperature_string\":\"86.7 F (30.4 C)\",\"temp_f\":86.7,\"temp_c\":30.4,\"relative_humidity\":\"79%\",\"wind_string\":\"From the NNE at 3.1 MPH Gusting to 6.2 MPH\",\"wind_dir\":\"NNE\",\"wind_degrees\":19,\"wind_mph\":3.1,\"wind_gust_mph\":\"6.2\",\"wind_kph\":5,\"wind_gust_kph\":\"10.0\",\"pressure_mb\":\"1008\",\"pressure_in\":\"29.77\",\"pressure_trend\":\"0\",\"dewpoint_string\":\"80 F (26 C)\",\"dewpoint_f\":80,\"dewpoint_c\":26,\"heat_index_string\":\"101 F (39 C)\",\"heat_index_f\":101,\"heat_index_c\":39,\"windchill_string\":\"NA\",\"windchill_f\":\"NA\",\"windchill_c\":\"NA\",\"feelslike_string\":\"101 F (39 C)\",\"feelslike_f\":\"101\",\"feelslike_c\":\"39\",\"visibility_mi\":\"6.2\",\"visibility_km\":\"10.0\",\"solarradiation\":\"--\",\"UV\":\"12\",\"precip_1hr_string\":\"0.02 in ( 1 mm)\",\"precip_1hr_in\":\"0.02\",\"precip_1hr_metric\":\" 1\",\"precip_today_string\":\"0.02 in (1 mm)\",\"precip_today_in\":\"0.02\",\"precip_today_metric\":\"1\",\"icon\":\"partlycloudy\",\"icon_url\":\"http://icons.wxug.com/i/c/k/partlycloudy.gif\",\"forecast_url\":\"http://www.wunderground.com/global/stations/94120.html\",\"history_url\":\"http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=INTNIGHT2\",\"ob_url\":\"http://www.wunderground.com/cgi-bin/findweather/getForecast?query=-12.391690,130.850708\",\"nowcast\":\"\"}}";
    String responseSolarRadiation = "{\"forecasts\":[{\"ghi\":796,\"ghi90\":923,\"ghi10\":605,\"ebh\":430,\"dni\":468,\"dni10\":176,\"dni90\":763,\"dhi\":367,\"air_temp\":30,\"zenith\":22,\"azimuth\":62,\"cloud_opacity\":36,\"period_end\":\"2017-03-18T05:00:00.0000000Z\",\"period\":\"PT30M\"}]}";
    float solarRadiation, flatRateTariff, minEfficiency, maxEfficiency, minOutput, maxOutput, capacity, cost, minSavings, maxSavings, minReturns, maxReturns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        final AutoCompleteTextView autoCompleteTextViewLocation = (AutoCompleteTextView)findViewById(R.id.autoCompleteTextViewLocation);
        final ImageView imageViewSearch = (ImageView) findViewById(R.id.imageViewSearch);
        final ImageView imageViewCancel = (ImageView) findViewById(R.id.imageViewCancel);
        final TextView textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        final ImageView imageViewGeolocate = (ImageView) findViewById(R.id.imageViewGeolocate);
        Button buttonMoreWeather = (Button) findViewById(R.id.buttonMoreWeather);
        Button buttonMoreProjection = (Button) findViewById(R.id.buttonMoreProjection);
        Button buttonMoreDusting = (Button) findViewById(R.id.buttonMoreDusting);
        Button buttonMoreMonitor = (Button) findViewById(R.id.buttonMoreMonitor);
        Button buttonMoreProviders = (Button) findViewById(R.id.buttonMoreProviders);

        loadSuburbs();
        setupButtons();

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        float latitude = sharedPreferences.getFloat("latitude", (float) -12.459);
        float longitude = sharedPreferences.getFloat("longitude", (float) 130.847);
        responseWeatherCondition = sharedPreferences.getString("weather_condition", responseWeatherCondition);
        responseSolarRadiation = sharedPreferences.getString("solar_radiation", responseSolarRadiation);

        final WeatherData closestSuburb = weatherList.getClosestSuburb(latitude, longitude);

        solarRadiation = closestSuburb.getSolarmean();
        flatRateTariff = sharedPreferences.getFloat("flat_rate_tariff", (float) 0.2595);
        minEfficiency = sharedPreferences.getFloat("min_efficiency", (float) 0.735851183);
        maxEfficiency = sharedPreferences.getFloat("max_efficiency", (float) 1.001635544);
        capacity = sharedPreferences.getFloat("recent_selected_capacity", (float) 4.5);
        System.out.println("Capacity: " + capacity);
        cost = sharedPreferences.getFloat("recent_selected_cost", (float) 5000);

        minOutput = solarRadiation * capacity * minEfficiency;
        maxOutput = solarRadiation * capacity * maxEfficiency;
        minSavings = (float) ((float) minOutput * flatRateTariff * 365.25);
        maxSavings = (float) ((float)maxOutput * flatRateTariff * 365.25);
        minReturns = cost / maxSavings;
        maxReturns = cost / minSavings;

        float dusting = sharedPreferences.getFloat("dusting", 0);
        float powerLoss = sharedPreferences.getFloat("power_loss", 0);
        float costOfPowerLoss = sharedPreferences.getFloat("cost_of_power_loss", 0);

        SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
        editor.putFloat("flat_rate_tariff", (float) flatRateTariff);
        editor.putFloat("min_efficiency", (float) minEfficiency);
        editor.putFloat("max_efficiency", (float) maxEfficiency);
        editor.putFloat("recent_selected_capacity", capacity);
        editor.putFloat("recent_selected_cost", cost);
        editor.putFloat("minOutput", minOutput);
        editor.putFloat("maxOutput", maxOutput);
        editor.putFloat("minSavings", minSavings);
        editor.putFloat("maxSavings", maxSavings);
        editor.putFloat("minReturns", minReturns);
        editor.putFloat("maxReturns", maxReturns);

        editor.commit();

        if (textViewLocation != null) {
            if (closestSuburb != null) {
                textViewLocation.setText(closestSuburb.getPostcode().concat(" ").concat(closestSuburb.getSuburb()));
            }
        }


        if (imageViewGeolocate != null) {
            imageViewGeolocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    locationListener = new MyLocationListener();
                    if (ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        if(buttonMoreWeather!=null){
            buttonMoreWeather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", closestSuburb);
                    Intent intent = new Intent(DashboardActivity.this, WeatherActivity.class);
                    intent.putExtra("weather", bundle);
                    startActivity(intent);
                }
            });
        }

        TextView textViewMaxSavings = (TextView) findViewById(R.id.textViewMaxSavings);
        if(textViewMaxSavings!=null){
            textViewMaxSavings.setText("$ ".concat(String.valueOf((int) maxSavings)));
        }

        TextView textViewMinReturns = (TextView) findViewById(R.id.textViewMinReturns);
        if(textViewMinReturns!=null){
            textViewMinReturns.setText(String.valueOf((int) minReturns));
        }

        if(buttonMoreProjection!=null){
            buttonMoreProjection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", closestSuburb);
                    Intent intent = new Intent(DashboardActivity.this, ProjectionsActivity.class);
                    intent.putExtra("weather", bundle);
                    startActivity(intent);
                }
            });
        }

        TextView textViewDusting = (TextView)findViewById(R.id.textViewDusting);
        if(textViewDusting!=null){
            System.out.println(dusting);
            textViewDusting.setText(String.valueOf(BigDecimal.valueOf(Double.valueOf(String.valueOf(dusting))).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()).concat(" %"));
        }

        TextView textViewDailyLoss = (TextView)findViewById(R.id.textViewDailyLoss);
        if(textViewDailyLoss!=null){
            System.out.println(costOfPowerLoss);
            textViewDailyLoss.setText("$ ".concat(String.valueOf(BigDecimal.valueOf(costOfPowerLoss).setScale(2, BigDecimal.ROUND_HALF_UP))));
        }

        if(buttonMoreDusting!=null){
            buttonMoreDusting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", closestSuburb);
                    Intent intent = new Intent(DashboardActivity.this, DustingActivity.class);
                    intent.putExtra("weather", bundle);
                    startActivity(intent);
                }
            });
        }

        if(buttonMoreMonitor!=null){
            buttonMoreMonitor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", closestSuburb);
                    Intent intent = new Intent(DashboardActivity.this, OutputsActivity.class);
                    intent.putExtra("weather", bundle);
                    startActivity(intent);
                }
            });
        }

        if(buttonMoreProviders!=null){
            buttonMoreProviders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", closestSuburb);
                    Intent intent = new Intent(DashboardActivity.this, ProvidersActivity.class);
                    intent.putExtra("weather", bundle);
                    startActivity(intent);
                }
            });
        }
//        if(((new Date().getTime())-sharedPreferences.getLong("weather_last_refresh", new Date().getTime()))>1000*60*30) {
            new WeatherConditionsReader().execute();
//        }else{
//            refreshDisplayWeather(sharedPreferences.getString("weather_condition", responseWeatherCondition));
//        }

//        if(((new Date().getTime())-sharedPreferences.getLong("solar_last_refresh", new Date().getTime()))>1000*60*30) {
            new SolarRadiationReader().execute();
//        }else{
//            refreshDisplaySolarRadiation(sharedPreferences.getString("solar_radiation", responseSolarRadiation));
//        }

    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashboardActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashboardActivity.this, HomeActivity.class));
                }
            });
        }

        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DashboardActivity.this, HelpActivity.class));
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

            String longitude = "Longitude: " + loc.getLongitude();
            String latitude = "Latitude: " + loc.getLatitude();
            SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
            editor.putFloat("latitude", (float) loc.getLatitude());
            editor.putFloat("longitude", (float) loc.getLongitude());
            editor.commit();

            WeatherData closestSuburb = weatherList.getClosestSuburb((float) loc.getLatitude(), (float) loc.getLongitude());
            solarRadiation = closestSuburb.getSolarmean();

            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            flatRateTariff = sharedPreferences.getFloat("flat_rate_tariff", (float) 0.2595);
            minEfficiency = sharedPreferences.getFloat("min_efficiency", (float) 0.735851183);
            maxEfficiency = sharedPreferences.getFloat("max_efficiency", (float) 1.001635544);
            capacity = sharedPreferences.getFloat("recent_selected_capacity", (float) 4.5);
            cost = sharedPreferences.getFloat("recent_selected_cost", (float) 5000);

            minOutput = solarRadiation * capacity * minEfficiency;
            maxOutput = solarRadiation * capacity * maxEfficiency;
            minSavings = (float) ((float) minOutput * flatRateTariff * 365.25);
            maxSavings = (float) ((float)maxOutput * flatRateTariff * 365.25);
            minReturns = cost / maxSavings;
            maxReturns = cost / minSavings;

            editor.putFloat("flat_rate_tariff", (float) flatRateTariff);
            editor.putFloat("min_efficiency", (float) minEfficiency);
            editor.putFloat("max_efficiency", (float) maxEfficiency);
            editor.putFloat("recent_selected_capacity", capacity);
            editor.putFloat("recent_selected_cost", cost);
            editor.putFloat("minOutput", minOutput);
            editor.putFloat("maxOutput", maxOutput);
            editor.putFloat("minSavings", minSavings);
            editor.putFloat("maxSavings", maxSavings);
            editor.putFloat("minReturns", minReturns);
            editor.putFloat("maxReturns", maxReturns);

            editor.putLong("location_last_refresh", (new Date()).getTime());
            editor.putString("post_code", closestSuburb.getPostcode());
            editor.putString("suburb", closestSuburb.getSuburb());
            editor.commit();

            Toast.makeText(getBaseContext(), "Location changed: ".concat(closestSuburb.getPostcode().concat(" ").concat(closestSuburb.getSuburb())),Toast.LENGTH_SHORT);

            TextView textViewLocation = (TextView) findViewById(R.id.textViewLocation);
            if (textViewLocation != null) {
                if (closestSuburb != null) {
                    textViewLocation.setText(closestSuburb.getPostcode().concat(" ").concat(closestSuburb.getSuburb()));
                }
            }
            locationManager.removeUpdates(locationListener);
            locationManager = null;
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public String generateWeatherConditionURL(){
        float latitude, longitude;
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        latitude = sharedPreferences.getFloat("latitude", (float) -12.459);
        longitude = sharedPreferences.getFloat("longitude", (float) 130.847);
        System.out.println("http://api.wunderground.com/api/a5d5665e6d63f78c/conditions/q/" + String.valueOf(latitude) + ","+ String.valueOf(longitude) +".json");
        return "http://api.wunderground.com/api/a5d5665e6d63f78c/conditions/q/" + String.valueOf(latitude) + ","+ String.valueOf(longitude) +".json";
    }

    private class WeatherConditionsReader extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = generateWeatherConditionURL();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
//                    System.out.println("Response: > " + line);   //here u ll get whole responseTemp...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);

            refreshDisplayWeather(aString);
        }


    }

    public void refreshDisplayWeather(String weatherCondition){
        JSONObject mainObject = null;
        try {
            mainObject = (new JSONObject(weatherCondition)).getJSONObject("current_observation");
            if(mainObject!=null) {
                responseWeatherCondition = weatherCondition;
                SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                editor.putString("weather_condition", responseWeatherCondition);
                editor.putLong("weather_last_refresh", (new Date()).getTime());
                editor.commit();
                ImageView imageViewWeatherCondition = (ImageView) findViewById(R.id.imageViewWeatherCondition);
                if (imageViewWeatherCondition != null) {
                    imageViewWeatherCondition.setImageResource(getResources().getIdentifier("@drawable/" + mainObject.getString("icon"), "id", getPackageName()));
                }


                TextView textViewTemperature = (TextView) findViewById(R.id.textViewMeanSolarRadiation);
                if (textViewTemperature != null) {
                    textViewTemperature.setText(String.valueOf(mainObject.getDouble("temp_c")).concat("\u00B0C"));
                }

                TextView textViewWeatherCondition = (TextView) findViewById(R.id.textViewWeatherCondition);
                if (textViewWeatherCondition != null) {
                    textViewWeatherCondition.setText(mainObject.getString("weather"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String generateSolarRadiationURL(){
        float latitude, longitude;
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        latitude = sharedPreferences.getFloat("latitude", (float) -12.459);
        longitude = sharedPreferences.getFloat("longitude", (float) 130.847);
        System.out.println("https://api.solcast.com.au/radiation/forecasts?longitude="+ String.valueOf(longitude) +"&latitude=" + String.valueOf(latitude) + "&api_key=f_uSmb6y_aXCua_Wal8UJXKMdDK_JmGi&format=json");
        return "https://api.solcast.com.au/radiation/forecasts?longitude="+ String.valueOf(longitude) +"&latitude=" + String.valueOf(latitude) + "&api_key=f_uSmb6y_aXCua_Wal8UJXKMdDK_JmGi&format=json";

    }

    private class SolarRadiationReader extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = generateSolarRadiationURL();
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);

            refreshDisplaySolarRadiation(aString);
        }


    }

    public void refreshDisplaySolarRadiation(String solarRadiation){
        JSONObject mainObject = null;
        try {
            mainObject = (new JSONObject(solarRadiation)).getJSONArray("forecasts").getJSONObject(0);
            if(mainObject!=null) {
                responseSolarRadiation = solarRadiation;
                SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                editor.putString("solar_radiation", responseSolarRadiation);
                editor.putLong("solar_last_refresh", (new Date()).getTime());
                editor.commit();

                TextView textViewSolarRadiation = (TextView) findViewById(R.id.textViewSolarRadiation);
                if (textViewSolarRadiation != null) {
                    textViewSolarRadiation.setText(Integer.toString(mainObject.getInt("dni")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
