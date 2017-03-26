package au.edu.cdu.solarnt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import Weather.WeatherData;
import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

public class WeatherActivity extends AppCompatActivity {
    WeatherData weatherData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        Bundle bundle = getIntent().getExtras();
        weatherData = bundle.getBundle("weather").getParcelable("weather");

        setupButtons();
        if(weatherData!=null) {
            populateWeatherInfo();
        }
    }

    public void populateWeatherInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        String weatherCondition  = sharedPreferences.getString("weather_condition","");
        if(weatherCondition!=""){
            JSONObject mainObject = null;
            try {
                mainObject = (new JSONObject(weatherCondition)).getJSONObject("current_observation");
                if(mainObject!=null) {
                    ImageView imageViewWeatherCondition = (ImageView) findViewById(R.id.imageViewWeatherCondition);
                    if (imageViewWeatherCondition != null) {
                        imageViewWeatherCondition.setImageResource(getResources().getIdentifier("@drawable/" + mainObject.getString("icon"), "id", getPackageName()));
                    }

                    double currentTemperature = mainObject.getDouble("temp_c");
                    PieView pieViewTemperature = (PieView) findViewById(R.id.pieViewTemperature);
                    if(pieViewTemperature!=null){
                        float interval = (weatherData.getTempmaxmean()-weatherData.getTempminmean())/2;
                        float minTemperature = weatherData.getTempminmean() - interval;
                        pieViewTemperature.setPercentage((float) (0.1 + 100*(currentTemperature-minTemperature)/(4*interval)));
                        PieAngleAnimation animation = new PieAngleAnimation(pieViewTemperature);
                        animation.setDuration(1000); //This is the duration of the animation in millis
                        pieViewTemperature.startAnimation(animation);
                        pieViewTemperature.setInnerText(String.valueOf((int) currentTemperature).concat(getString(R.string.degrees_Celcius)));
                    }

                    TextView textViewWeatherCondition = (TextView) findViewById(R.id.textViewWeatherCondition);
                    if (textViewWeatherCondition != null) {
                        textViewWeatherCondition.setText(mainObject.getString("weather"));
                    }

                    double currentRainfall = mainObject.getDouble("precip_today_metric");
                    PieView pieViewRainfall = (PieView) findViewById(R.id.pieViewRainfall);
                    if(pieViewRainfall!=null){
                        pieViewRainfall.setPercentage((float) (0.1 + 100*(currentRainfall)/(2*weatherData.getRainmean())));
                        PieAngleAnimation animation = new PieAngleAnimation(pieViewRainfall);
                        animation.setDuration(1000); //This is the duration of the animation in millis
                        pieViewRainfall.startAnimation(animation);
                        pieViewRainfall.setInnerText(String.valueOf((int) currentRainfall));
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String solarRadiation = sharedPreferences.getString("solar_radiation","");
        if(solarRadiation!=""){
            JSONObject mainObject = null;
            try {
                mainObject = (new JSONObject(solarRadiation)).getJSONArray("forecasts").getJSONObject(0);
                if(mainObject!=null) {
                    PieView pieViewSolarRadiation = (PieView)findViewById(R.id.pieViewSolarRadiation);
                    if(pieViewSolarRadiation!=null){
                        float interval = (weatherData.getTempmaxmean()-weatherData.getTempminmean())/2;
                        float minTemperature = weatherData.getTempminmean() - interval;
                        pieViewSolarRadiation.setPercentage((float) (mainObject.getInt("dni")/10));
                        PieAngleAnimation animation = new PieAngleAnimation(pieViewSolarRadiation);
                        animation.setDuration(1000); //This is the duration of the animation in millis
                        pieViewSolarRadiation.startAnimation(animation);
                        pieViewSolarRadiation.setInnerText(String.valueOf(mainObject.getInt("dni")));
                    }
                    TextView textViewSolarRadiation = (TextView) findViewById(R.id.textViewSolarRadiation);
                    if (textViewSolarRadiation != null) {
                        textViewSolarRadiation.setText(Integer.toString(mainObject.getInt("dni")));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        TextView textViewPostCodeSuburb = (TextView) findViewById(R.id.textViewPostCodeSuburb);
        if(textViewPostCodeSuburb!=null){
            textViewPostCodeSuburb.setText(weatherData.getPostcode().concat(" ").concat(weatherData.getSuburb()));
        }

        TextView textViewMeanTemperature = (TextView) findViewById(R.id.textViewMeanTemperature);
        if(textViewMeanTemperature!=null){
            textViewMeanTemperature.setText("Nov-Apr: ".concat(String.valueOf((int)weatherData.getTempminwet()).concat(getString(R.string.degrees_Celcius)).concat(" - ").concat(String.valueOf((int)weatherData.getTempmaxwet()).concat(getString(R.string.degrees_Celcius))).concat("\nMay-Oct: ").concat(String.valueOf((int)weatherData.getTempmindry()).concat(getString(R.string.degrees_Celcius)).concat(" - ").concat(String.valueOf((int)weatherData.getTempmaxdry()).concat(getString(R.string.degrees_Celcius))))));
        }

        TextView textViewMeanSolarRadiation = (TextView) findViewById(R.id.textViewMeanSolarRadiation);
        if(textViewMeanSolarRadiation!=null){
            textViewMeanSolarRadiation.setText("Nov-Apr: ".concat(String.valueOf((int)(weatherData.getSolarwet()*1000)).concat(" W/sqm")).concat("\nMay-Oct: ").concat(String.valueOf((int)(weatherData.getSolardry()*1000)).concat(" W/sqm")));
        }

        TextView textViewMeanRainfall = (TextView) findViewById(R.id.textViewMeanRainfall);
        if (textViewMeanRainfall != null) {
            textViewMeanRainfall.setText("Nov-Apr: ".concat(String.valueOf((int) Math.ceil(weatherData.getRainwet() * 12 / 365.25)).concat(" mm").concat("\nMay-Oct: ").concat(String.valueOf((int) Math.ceil(weatherData.getRaindry() * 12 / 365.25)).concat(" mm"))));
        }


    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WeatherActivity.this, WeatherActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WeatherActivity.this, WeatherActivity.class));
                }
            });
        }

        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(WeatherActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("context", "weather");
                    Intent intent = new Intent(WeatherActivity.this, HelpActivity.class);
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
