package au.edu.cdu.solarnt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.BigDecimal;

import Weather.WeatherData;

public class ProjectionsActivity extends AppCompatActivity {
    float solarRadiation, flatRateTariff, minEfficiency, maxEfficiency, minOutput, maxOutput, capacity, cost, minSavings, maxSavings, minReturns, maxReturns;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projections);

        setupButtons();

        SharedPreferences sharedPreferences = getSharedPreferences("General", MODE_PRIVATE);
        flatRateTariff = sharedPreferences.getFloat("flat_rate_tariff", (float) 0.2595);
        minEfficiency = sharedPreferences.getFloat("min_efficiency", (float) 0.735851183);
        maxEfficiency = sharedPreferences.getFloat("max_efficiency", (float) 1.001635544);
        capacity = sharedPreferences.getFloat("recent_selected_capacity", (float) 4.5);
        cost = sharedPreferences.getFloat("recent_selected_cost", (float) 5000);

        Bundle bundle = getIntent().getExtras();
        WeatherData weatherData = bundle.getBundle("weather").getParcelable("weather");
        solarRadiation = weatherData.getSolarmean();

        TextView calcSuburb = (TextView) findViewById(R.id.calcSuburb);
        if(calcSuburb!=null)calcSuburb.setText(weatherData.getSuburb());

        TextView calcStation = (TextView) findViewById(R.id.calcStation);
        if(calcStation!=null)calcStation.setText("Data from ".concat(weatherData.getSolarname()).concat(" station ").concat(Float.toString(weatherData.getSolardistance())).concat(" kilometres away."));

        TextView calcSolarExposure = (TextView) findViewById(R.id.calcSolarExposure);
        if(calcSolarExposure!=null)calcSolarExposure.setText(Float.toString(solarRadiation));

        SeekBar calcSeekbarCapacity = (SeekBar)findViewById(R.id.calcSeekbarCapacity);
        if(calcSeekbarCapacity!=null)calcSeekbarCapacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.calcCapacity)).setText(String.valueOf((seekBar.getProgress()+1)/10.0));
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar calcSeekbarCost = (SeekBar)findViewById(R.id.calcSeekbarCost);
        if(calcSeekbarCost!=null)calcSeekbarCost.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.calcCost)).setText(String.valueOf(seekBar.getProgress()));
                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        calculate();

//        ((Button)findViewById(R.id.projectionsButtonProviders)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ProjectionsActivity.this, ProvidersActivity.class));
//            }
//        });
    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProjectionsActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProjectionsActivity.this, ProjectionsActivity.class));
                }
            });
        }

        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProjectionsActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProjectionsActivity.this, HelpActivity.class));
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

    private void calculate(){

        minOutput = solarRadiation * capacity * minEfficiency;
        maxOutput = solarRadiation * capacity * maxEfficiency;
        minSavings = (float) ((float) minOutput * flatRateTariff * 365.25);
        maxSavings = (float) ((float)maxOutput * flatRateTariff * 365.25);
        minReturns = cost / maxSavings;
        maxReturns = cost / minSavings;

        SharedPreferences.Editor editor = getSharedPreferences("General", MODE_PRIVATE).edit();
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

        ((TextView)findViewById(R.id.calcPotentialPower)).setText(String.valueOf(BigDecimal.valueOf(minOutput).setScale(1, BigDecimal.ROUND_HALF_UP)).concat(" - ").concat(String.valueOf(BigDecimal.valueOf(maxOutput).setScale(1, BigDecimal.ROUND_HALF_UP))));
        ((TextView)findViewById(R.id.calcSavings)).setText("$ ".concat(String.valueOf(((Double)Math.floor(minSavings)).intValue())).concat(" - ").concat(String.valueOf(((Double)Math.ceil(maxSavings)).intValue())));
        ((TextView)findViewById(R.id.calcReturns)).setText(String.valueOf(((Double)Math.floor(minReturns)).intValue()).concat(" - ").concat(String.valueOf(((Double)Math.ceil(maxReturns)).intValue())));
    }
}
