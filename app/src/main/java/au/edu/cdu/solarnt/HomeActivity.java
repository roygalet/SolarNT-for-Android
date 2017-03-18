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
