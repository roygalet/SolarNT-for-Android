package au.edu.cdu.solarnt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupButtons();
        setupPreferences();

    }

    private void setupPreferences(){
        if(getSharedPreferences("General", MODE_PRIVATE)==null) {
            SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
            editor.putBoolean("solar_user", false);
            editor.putBoolean("live_uploader", false);

            editor.putLong("location_last_refresh", (new Date()).getTime());
            editor.putString("post_code", "0800");
            editor.putString("suburb", "Darwin");
            editor.putFloat("latitude", (float) -12.459);
            editor.putFloat("longitude", (float) 130.847);

            editor.putLong("weather_last_refresh", (new Date()).getTime());
            editor.putString("weather_condition", "{\"response\":{\"version\":\"0.1\",\"termsofService\":\"http://www.wunderground.com/weather/api/d/terms.html\",\"features\":{\"conditions\":1}},\"current_observation\":{\"image\":{\"url\":\"http://icons.wxug.com/graphics/wu2/logo_130x80.png\",\"title\":\"Weather Underground\",\"link\":\"http://www.wunderground.com\"},\"display_location\":{\"full\":\"Brinkin, Australia\",\"city\":\"Brinkin\",\"state\":\"NTR\",\"state_name\":\"Australia\",\"country\":\"AU\",\"country_iso3166\":\"AU\",\"zip\":\"00000\",\"magic\":\"8\",\"wmo\":\"94120\",\"latitude\":\"-12.370000\",\"longitude\":\"130.860000\",\"elevation\":\"14.9\"},\"observation_location\":{\"full\":\"Halkitis Court, Nightcliff, NT\",\"city\":\"Halkitis Court, Nightcliff\",\"state\":\"NT\",\"country\":\"AU\",\"country_iso3166\":\"AU\",\"latitude\":\"-12.391690\",\"longitude\":\"130.850708\",\"elevation\":\"26 ft\"},\"estimated\":{},\"station_id\":\"INTNIGHT2\",\"observation_time\":\"Last Updated on March 18, 1:16 PM ACST\",\"observation_time_rfc822\":\"Sat, 18 Mar 2017 13:16:37 +0930\",\"observation_epoch\":\"1489808797\",\"local_time_rfc822\":\"Sat, 18 Mar 2017 13:24:52 +0930\",\"local_epoch\":\"1489809292\",\"local_tz_short\":\"ACST\",\"local_tz_long\":\"Australia/Darwin\",\"local_tz_offset\":\"+0930\",\"weather\":\"Scattered Clouds\",\"temperature_string\":\"86.7 F (30.4 C)\",\"temp_f\":86.7,\"temp_c\":30.4,\"relative_humidity\":\"79%\",\"wind_string\":\"From the NNE at 3.1 MPH Gusting to 6.2 MPH\",\"wind_dir\":\"NNE\",\"wind_degrees\":19,\"wind_mph\":3.1,\"wind_gust_mph\":\"6.2\",\"wind_kph\":5,\"wind_gust_kph\":\"10.0\",\"pressure_mb\":\"1008\",\"pressure_in\":\"29.77\",\"pressure_trend\":\"0\",\"dewpoint_string\":\"80 F (26 C)\",\"dewpoint_f\":80,\"dewpoint_c\":26,\"heat_index_string\":\"101 F (39 C)\",\"heat_index_f\":101,\"heat_index_c\":39,\"windchill_string\":\"NA\",\"windchill_f\":\"NA\",\"windchill_c\":\"NA\",\"feelslike_string\":\"101 F (39 C)\",\"feelslike_f\":\"101\",\"feelslike_c\":\"39\",\"visibility_mi\":\"6.2\",\"visibility_km\":\"10.0\",\"solarradiation\":\"--\",\"UV\":\"12\",\"precip_1hr_string\":\"0.02 in ( 1 mm)\",\"precip_1hr_in\":\"0.02\",\"precip_1hr_metric\":\" 1\",\"precip_today_string\":\"0.02 in (1 mm)\",\"precip_today_in\":\"0.02\",\"precip_today_metric\":\"1\",\"icon\":\"partlycloudy\",\"icon_url\":\"http://icons.wxug.com/i/c/k/partlycloudy.gif\",\"forecast_url\":\"http://www.wunderground.com/global/stations/94120.html\",\"history_url\":\"http://www.wunderground.com/weatherstation/WXDailyHistory.asp?ID=INTNIGHT2\",\"ob_url\":\"http://www.wunderground.com/cgi-bin/findweather/getForecast?query=-12.391690,130.850708\",\"nowcast\":\"\"}}");

            editor.putLong("solar_last_refresh", (new Date()).getTime());
            editor.putString("solar_radiation","\"{\\\"forecasts\\\":[{\\\"ghi\\\":796,\\\"ghi90\\\":923,\\\"ghi10\\\":605,\\\"ebh\\\":430,\\\"dni\\\":468,\\\"dni10\\\":176,\\\"dni90\\\":763,\\\"dhi\\\":367,\\\"air_temp\\\":30,\\\"zenith\\\":22,\\\"azimuth\\\":62,\\\"cloud_opacity\\\":36,\\\"period_end\\\":\\\"2017-03-18T05:00:00.0000000Z\\\",\\\"period\\\":\\\"PT30M\\\"}]}\"");

            editor.putFloat("flat_rate_tariff", (float) 0.2595);
            editor.putFloat("min_efficiency", (float) 0.735851183);
            editor.putFloat("max_efficiency", (float) 1.001635544);
            editor.putFloat("recent_selected_capacity", (float) 4.5);
            editor.putFloat("recent_selected_cost", (float) 5000);

            editor.putLong("output_last_refresh", (new Date()).getTime());
            editor.commit();
        }

        final Switch switchSolarUser = (Switch) findViewById(R.id.switchSolarUser);
        final Switch switchLiveUploader = (Switch) findViewById(R.id.switchLiveUploader);
        if (switchSolarUser!=null){
            switchSolarUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                    editor.putBoolean("solar_user", switchSolarUser.isChecked());


                    if(!switchSolarUser.isChecked()){
                        editor.putBoolean("live_uploader", false);
                    }
                    editor.commit();
                    if(switchLiveUploader!=null){
                        if(switchSolarUser.isChecked()){
                            switchLiveUploader.setVisibility(View.VISIBLE);
                        }else {
                            switchLiveUploader.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });

            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            Boolean solarUser = sharedPreferences.getBoolean("solar_user", false);
            switchSolarUser.setChecked(solarUser);
        }

        if (switchLiveUploader!=null){
            if(switchSolarUser!=null){
                if(switchSolarUser.isChecked()){
                    switchLiveUploader.setVisibility(View.VISIBLE);
                }else {
                    switchLiveUploader.setVisibility(View.INVISIBLE);
                }
            }
            switchLiveUploader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                    editor.putBoolean("live_uploader", switchLiveUploader.isChecked());
                    editor.commit();
                }
            });

            SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
            Boolean solarUser = sharedPreferences.getBoolean("solar_user", false);
            switchSolarUser.setChecked(solarUser);
        }
    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                }
            });
        }

        Button imageButtonSettings = (Button)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, HelpActivity.class));
                }
            });
        }

        Button buttonDashboard = (Button)findViewById(R.id.buttonDashboard);
        if(buttonDashboard != null) {
            buttonDashboard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, DashboardActivity.class));
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
