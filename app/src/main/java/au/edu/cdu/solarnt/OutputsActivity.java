package au.edu.cdu.solarnt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import PVOutputData.PVAccountSettings;
import PVOutputData.PVSystemsCollection;
import PVOutputData.PVSystem;
import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

public class OutputsActivity extends AppCompatActivity {
    PVOutputData.PVSystemsCollection nearbyCollection;
    int sid;
    String key;
    int postCode;
    int distance;
    int numberOfSystems;
    boolean latestOnly;

    private ProgressDialog progressDialog;
    TextView btnDaily;
    TextView btnMonthly;
    TextView btnYearly;
    List<String> systems;
    Spinner spinSystems;
    final String dontCompareMessage = "(Show my data only)";
    final int DAILY = 1;
    final int MONTHLY = 2;
    final int YEARLY = 3;
    int currentFrequency=DAILY;
    String compareSystem = dontCompareMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outputs);

        setupButtons();

        sid = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("sid","47892"));
        key = PreferenceManager.getDefaultSharedPreferences(this).getString("key","4012c804abb523bf7466ef415c9ba808e8aae946");
        postCode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("post_code","810"));
//        postCode = 810;
        distance = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("distance","100"));
        latestOnly = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("latest_only",true);
        numberOfSystems = PreferenceManager.getDefaultSharedPreferences(this).getInt("number_of_systems",5);

        new PVOutputConnection().execute();
    }

    class PVOutputConnection extends AsyncTask<String, Long, PVSystemsCollection> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(OutputsActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Retrieving Data . . . Please Wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected PVSystemsCollection doInBackground(String... params) {
            nearbyCollection = new PVSystemsCollection(new PVAccountSettings(sid, key, postCode));
//            nearbyCollection.getMySystem();
            nearbyCollection.getNearbySystemsForNonUsers(postCode, distance, numberOfSystems);
            return nearbyCollection;
        }

        @Override
        protected void onPostExecute(PVSystemsCollection pvSystemsCollection) {
            super.onPostExecute(pvSystemsCollection);
//            systems = new ArrayList<String>();
//            systems.add(dontCompareMessage);

            float averageSize = 0;
            float currentPower = 0;
            float averagePower = 0;
            float currentEnergy = 0;
            float averageEnergy = 0;
            float currentEfficiency = 0;
            float averageEfficiency = 0;
            float maxEnergy = 0;

            int numberOfSystems =nearbyCollection.getPvSystems().size();
            if(numberOfSystems>0) {
                for (int i = 0; i < numberOfSystems; i++) {
//                systems.add((String) nearbyCollection.getPvSystems().keySet().toArray()[i]);
//                System.out.println((String) nearbyCollection.getPvSystems().keySet().toArray()[i]);

                    if(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i]))!=null) {
                        averageSize = averageSize + ((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getSize();
                        if (((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus()!=null && ((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatistics()!=null) {
                            if(Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus().split(",")[3])>0) {
                                currentPower = currentPower + Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus().split(",")[3]);
                            }
                            if(Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus().split(",")[2])>0) {
                                currentEnergy = currentEnergy + Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus().split(",")[2]);
                            }
                            if(Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus().split(",")[6])>0) {
                                currentEfficiency = currentEfficiency + Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus().split(",")[6]);
                            }
                            averageEnergy = averageEnergy + Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatistics().split(",")[2]);
                            averageEfficiency = averageEfficiency + Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatistics().split(",")[5]);
                            maxEnergy = Math.max(maxEnergy,Float.parseFloat(((PVSystem) nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatistics().split(",")[4]));
                        }
                    }
                }
                averageSize = averageSize/numberOfSystems;
                currentPower = currentPower / numberOfSystems;
                currentEnergy = currentEnergy / numberOfSystems;
                averageEnergy = averageEnergy / numberOfSystems;
                currentEfficiency = currentEfficiency / numberOfSystems;
                averageEfficiency = averageEfficiency / numberOfSystems;
                averagePower = currentPower * averageEfficiency / currentEfficiency;

                SharedPreferences.Editor editor = getSharedPreferences("General", MODE_PRIVATE).edit();
                editor.putFloat("systems_average_size", averageSize);
                editor.putFloat("systems_current_power", currentPower);
                editor.putFloat("systems_current_energy", currentEnergy);
                editor.putFloat("systems_average_energy", averageEnergy);
                editor.putFloat("systems_current_efficiency", currentEfficiency);
                editor.putFloat("systems_average_efficiency", averageEfficiency);
                editor.putFloat("systems_average_power", averagePower);
                editor.commit();
            }
//            System.out.println(averageEnergy);

            PieView pieViewPowerGeneration = (PieView) findViewById(R.id.pieViewPowerGeneration);
            if(pieViewPowerGeneration!=null){
                pieViewPowerGeneration.setPercentage((float) 1 + (100*(currentPower/averagePower)));
                PieAngleAnimation animation = new PieAngleAnimation(pieViewPowerGeneration);
                animation.setDuration(1000); //This is the duration of the animation in millis
                pieViewPowerGeneration.startAnimation(animation);
                pieViewPowerGeneration.setInnerText((new DecimalFormat("0.#")).format(currentPower/1000));
            }

            TextView textViewMeanPowerGeneration = (TextView) findViewById(R.id.textViewMeanPowerGeneration);
            if(textViewMeanPowerGeneration!=null){
                textViewMeanPowerGeneration.setText((new DecimalFormat("0.#")).format(averagePower/1000).concat(" kW"));
            }

            TextView textViewNumberOfSystems = (TextView) findViewById(R.id.textViewNumberOfSystems);
            if(textViewNumberOfSystems!=null){
                textViewNumberOfSystems.setText((new DecimalFormat("0")).format(nearbyCollection.getPvSystems().size()).concat(" nearby systems with average capacity of ").concat((new DecimalFormat("0.#")).format(averageSize/1000)).concat(" kW"));
            }

            PieView pieViewEnergyGeneration = (PieView) findViewById(R.id.pieViewEnergyGeneration);
            if(pieViewEnergyGeneration!=null){
                pieViewEnergyGeneration.setPercentage((float) 1 + (100*(currentEnergy/averageEnergy)));
                PieAngleAnimation animation = new PieAngleAnimation(pieViewEnergyGeneration);
                animation.setDuration(1000); //This is the duration of the animation in millis
                pieViewEnergyGeneration.startAnimation(animation);
                pieViewEnergyGeneration.setInnerText((new DecimalFormat("0")).format(currentEnergy/1000));
            }

            TextView textViewMeanEnergyGeneration = (TextView) findViewById(R.id.textViewMeanEnergyGeneration);
            if(textViewMeanEnergyGeneration!=null){
                textViewMeanEnergyGeneration.setText((new DecimalFormat("0")).format(averageEnergy/1000).concat(" kWh daily"));
            }

            PieView pieViewEfficiency = (PieView) findViewById(R.id.pieViewEfficiency);
            if(pieViewEfficiency!=null){
                pieViewEfficiency.setPercentage((float) 1 + (100*(currentEfficiency/averageEfficiency)));
                PieAngleAnimation animation = new PieAngleAnimation(pieViewEfficiency);
                animation.setDuration(1000); //This is the duration of the animation in millis
                pieViewEfficiency.startAnimation(animation);
                pieViewEfficiency.setInnerText((new DecimalFormat("0.#")).format(currentEfficiency));
            }

            TextView textViewMeanEfficiency = (TextView) findViewById(R.id.textViewMeanEfficiency);
            if(textViewMeanEfficiency!=null){
                textViewMeanEfficiency.setText((new DecimalFormat("0.#")).format(averageEfficiency).concat(" kWh/kW"));
            }
//            ArrayAdapter<String> adapter = new ArrayAdapter <String> (OutputsActivity.this,R.layout.spinner_item, systems);
//            spinSystems.setAdapter(adapter);
//            currentFrequency = DAILY;
//            compareSystem = dontCompareMessage;

            progressDialog.hide();

        }
    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(OutputsActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(OutputsActivity.this, HomeActivity.class));
                }
            });
        }

        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(OutputsActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(OutputsActivity.this, HelpActivity.class));
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
