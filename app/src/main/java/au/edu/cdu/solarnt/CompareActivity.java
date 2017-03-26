package au.edu.cdu.solarnt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import PVOutputData.PVAccountSettings;
import PVOutputData.PVSystem;
import PVOutputData.PVSystemsCollection;
import az.plainpie.PieView;
import az.plainpie.animation.PieAngleAnimation;

public class CompareActivity extends AppCompatActivity {
    PVSystemsCollection nearbyCollection;
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
        setContentView(R.layout.activity_compare);

        setupButtons();

        sid = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("sid","47892"));
        key = PreferenceManager.getDefaultSharedPreferences(this).getString("key","4012c804abb523bf7466ef415c9ba808e8aae946");
        postCode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("post_code","810"));
//        postCode = 810;
        distance = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("distance","25"));
        latestOnly = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("latest_only",true);
        numberOfSystems = PreferenceManager.getDefaultSharedPreferences(this).getInt("number_of_systems",5);

        updateDisplay();

        Button buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
        if(buttonRefresh!=null){
            buttonRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new PVOutputConnection().execute();
                }
            });
        }

    }

    class PVOutputConnection extends AsyncTask<String, Long, PVSystemsCollection> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CompareActivity.this);
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
            nearbyCollection.getNearbySystemsWithLatestOutputs(postCode, distance, true);
            return nearbyCollection;
        }

        @Override
        protected void onPostExecute(PVSystemsCollection pvSystemsCollection) {
            super.onPostExecute(pvSystemsCollection);
            progressDialog.hide();
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

            String mySystemName = "";
            float mySystemSize = 0;
            float myCurrentPower = 0;
            float myAveragePower = 0;
            float myCurrentEnergy = 0;
            float myAverageEnergy = 0;
            float myCurrentEfficiency = 0;
            float myAverageEfficiency = 0;

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

                if((PVSystem) nearbyCollection.getMySystem()!=null) {
                    mySystemName = nearbyCollection.getMySystem().getName();
                    mySystemSize = nearbyCollection.getMySystem().getSize();
                    if(nearbyCollection.getMySystem().getStatus()!=null){
                        if(Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatus().split(",")[3])>0) {
                            myCurrentPower = Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatus().split(",")[3]);
                        }
                        if(Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatus().split(",")[2])>0) {
                            myCurrentEnergy = Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatus().split(",")[2]);
                        }
                        if(Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatus().split(",")[6])>0) {
                            myCurrentEfficiency = Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatus().split(",")[6]);
                        }
                    }
                    if(nearbyCollection.getMySystem().getStatistics()!=null){
                        if(Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatistics().split(",")[2])>0) {
                            myAverageEnergy = Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatistics().split(",")[2]);
                        }
                        if(Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatistics().split(",")[5])>0) {
                            myAverageEfficiency = Float.parseFloat(((PVSystem) nearbyCollection.getMySystem()).getStatistics().split(",")[5]);
                        }
                    }
                    myAveragePower = averagePower * myAverageEfficiency / averageEfficiency*myAverageEnergy/averageEnergy;
                }

                SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                editor.putString("my_system_name", mySystemName);
                editor.putFloat("my_system_size", mySystemSize);
                editor.putFloat("my_current_power", myCurrentPower);
                editor.putFloat("my_current_energy", myCurrentEnergy);
                editor.putFloat("my_average_energy", myAverageEnergy);
                editor.putFloat("my_current_efficiency", myCurrentEfficiency);
                editor.putFloat("my_average_efficiency", myAverageEfficiency);
                editor.putFloat("my_average_power", myAveragePower);

                editor.putFloat("systems_average_size", averageSize);
                editor.putFloat("systems_current_power", currentPower);
                editor.putFloat("systems_current_energy", currentEnergy);
                editor.putFloat("systems_average_energy", averageEnergy);
                editor.putFloat("systems_current_efficiency", currentEfficiency);
                editor.putFloat("systems_average_efficiency", averageEfficiency);
                editor.putFloat("systems_average_power", averagePower);
                editor.putString("systems_last_updated", new SimpleDateFormat("dd MMM yyyy h:mm").format(new Date()));
                editor.commit();
            }

            updateDisplay();

        }
    }

    private void updateDisplay(){
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPreferences",MODE_PRIVATE);
        String mySystemName = sharedPreferences.getString("my_system_name","My System");
        float mysystemSize = sharedPreferences.getFloat("my_system_size",0);
        float myCurrentPower = sharedPreferences.getFloat("my_current_power",0);
        float myAveragePower = sharedPreferences.getFloat("my_average_power",0);
        float myCurrentEnergy = sharedPreferences.getFloat("my_current_energy",0);
        float myAverageEnergy = sharedPreferences.getFloat("my_average_energy",0);
        float myCurrentEfficiency = sharedPreferences.getFloat("my_current_efficiency",0);
        float myAverageEfficiency = sharedPreferences.getFloat("my_average_efficiency",0);

        float averageSize = sharedPreferences.getFloat("systems_average_size",0);
        float currentPower = sharedPreferences.getFloat("systems_current_power",0);
        float averagePower = sharedPreferences.getFloat("systems_average_power",0);
        float currentEnergy = sharedPreferences.getFloat("systems_current_energy",0);
        float averageEnergy = sharedPreferences.getFloat("systems_average_energy",0);
        float currentEfficiency = sharedPreferences.getFloat("systems_current_efficiency",0);
        float averageEfficiency = sharedPreferences.getFloat("systems_average_efficiency",0);
        String lastUpdated = sharedPreferences.getString("systems_last_updated","Not Available");

        TextView textViewMySystemName = (TextView) findViewById(R.id.textViewMySystemName);
        if(textViewMySystemName!=null){
            textViewMySystemName.setText(mySystemName.concat("\n").concat((new DecimalFormat("0.#")).format(mysystemSize/1000).concat(" kW")));
        }

        PieView pieViewMyPowerGeneration = (PieView) findViewById(R.id.pieViewMyPowerGeneration);
        if(pieViewMyPowerGeneration!=null){
            pieViewMyPowerGeneration.setPercentage((float) 1 + (100*(myCurrentPower/myAveragePower)));
            if(pieViewMyPowerGeneration.getPercentage()<=33)pieViewMyPowerGeneration.setPercentageBackgroundColor(Color.RED);
            if(pieViewMyPowerGeneration.getPercentage()>=67)pieViewMyPowerGeneration.setPercentageBackgroundColor(Color.GREEN);
            PieAngleAnimation animation = new PieAngleAnimation(pieViewMyPowerGeneration);
            animation.setDuration(1000); //This is the duration of the animation in millis
            pieViewMyPowerGeneration.startAnimation(animation);
            pieViewMyPowerGeneration.setInnerText((new DecimalFormat("0.#")).format(myCurrentPower/1000));
        }

        TextView textViewMyMeanPowerGeneration = (TextView) findViewById(R.id.textViewMyMeanPowerGeneration);
        if(textViewMyMeanPowerGeneration!=null){
            textViewMyMeanPowerGeneration.setText((new DecimalFormat("0.#")).format(myAveragePower/1000));
        }

        PieView pieViewMyEnergyGeneration = (PieView) findViewById(R.id.pieViewMyEnergyGeneration);
        if(pieViewMyEnergyGeneration!=null){
            pieViewMyEnergyGeneration.setPercentage((float) 1 + (100*(myCurrentEnergy/myAverageEnergy)));
            if(pieViewMyEnergyGeneration.getPercentage()<=33)pieViewMyEnergyGeneration.setPercentageBackgroundColor(Color.RED);
            if(pieViewMyEnergyGeneration.getPercentage()>=67)pieViewMyEnergyGeneration.setPercentageBackgroundColor(Color.GREEN);
            PieAngleAnimation animation = new PieAngleAnimation(pieViewMyEnergyGeneration);
            animation.setDuration(1000); //This is the duration of the animation in millis
            pieViewMyEnergyGeneration.startAnimation(animation);
            pieViewMyEnergyGeneration.setInnerText((new DecimalFormat("0")).format(myCurrentEnergy/1000));
        }

        TextView textViewMyMeanEnergyGeneration = (TextView) findViewById(R.id.textViewMyMeanEnergyGeneration);
        if(textViewMyMeanEnergyGeneration!=null){
            textViewMyMeanEnergyGeneration.setText((new DecimalFormat("0")).format(myAverageEnergy/1000));
        }

        PieView pieViewMyEfficiency = (PieView) findViewById(R.id.pieViewMyEfficiency);
        if(pieViewMyEfficiency!=null){
            pieViewMyEfficiency.setPercentage((float) 1 + (100*(myCurrentEfficiency/myAverageEfficiency)));
            if(pieViewMyEfficiency.getPercentage()<=33)pieViewMyEfficiency.setPercentageBackgroundColor(Color.RED);
            if(pieViewMyEfficiency.getPercentage()>=67)pieViewMyEfficiency.setPercentageBackgroundColor(Color.GREEN);
            PieAngleAnimation animation = new PieAngleAnimation(pieViewMyEfficiency);
            animation.setDuration(1000); //This is the duration of the animation in millis
            pieViewMyEfficiency.startAnimation(animation);
            pieViewMyEfficiency.setInnerText((new DecimalFormat("0.#")).format(myCurrentEfficiency));
        }

        TextView textViewMyMeanEfficiency = (TextView) findViewById(R.id.textViewMyMeanEfficiency);
        if(textViewMyMeanEfficiency!=null){
            textViewMyMeanEfficiency.setText((new DecimalFormat("0.#")).format(myAverageEfficiency));
        }



        PieView pieViewPowerGeneration = (PieView) findViewById(R.id.pieViewPowerGeneration);
        if(pieViewPowerGeneration!=null){
            pieViewPowerGeneration.setPercentage((float) 1 + (100*(currentPower/averagePower)));
            if(pieViewPowerGeneration.getPercentage()<=33)pieViewPowerGeneration.setPercentageBackgroundColor(Color.RED);
            if(pieViewPowerGeneration.getPercentage()>=67)pieViewPowerGeneration.setPercentageBackgroundColor(Color.GREEN);
            PieAngleAnimation animation = new PieAngleAnimation(pieViewPowerGeneration);
            animation.setDuration(1000); //This is the duration of the animation in millis
            pieViewPowerGeneration.startAnimation(animation);
            pieViewPowerGeneration.setInnerText((new DecimalFormat("0.#")).format(currentPower/1000));
        }

        TextView textViewMeanPowerGeneration = (TextView) findViewById(R.id.textViewMeanPowerGeneration);
        if(textViewMeanPowerGeneration!=null){
            textViewMeanPowerGeneration.setText((new DecimalFormat("0.#")).format(averagePower/1000));
        }

        TextView textViewNumberOfSystems = (TextView) findViewById(R.id.textViewNumberOfSystems);
        if(textViewNumberOfSystems!=null){
            textViewNumberOfSystems.setText((new DecimalFormat("0")).format(numberOfSystems).concat(" nearby systems \n").concat((new DecimalFormat("0.#")).format(averageSize/1000)).concat(" kW\n average capacity"));
        }

        PieView pieViewEnergyGeneration = (PieView) findViewById(R.id.pieViewEnergyGeneration);
        if(pieViewEnergyGeneration!=null){
            pieViewEnergyGeneration.setPercentage((float) 1 + (100*(currentEnergy/averageEnergy)));
            if(pieViewEnergyGeneration.getPercentage()<=33)pieViewEnergyGeneration.setPercentageBackgroundColor(Color.RED);
            if(pieViewEnergyGeneration.getPercentage()>=67)pieViewEnergyGeneration.setPercentageBackgroundColor(Color.GREEN);
            PieAngleAnimation animation = new PieAngleAnimation(pieViewEnergyGeneration);
            animation.setDuration(1000); //This is the duration of the animation in millis
            pieViewEnergyGeneration.startAnimation(animation);
            pieViewEnergyGeneration.setInnerText((new DecimalFormat("0")).format(currentEnergy/1000));
        }

        TextView textViewMeanEnergyGeneration = (TextView) findViewById(R.id.textViewMeanEnergyGeneration);
        if(textViewMeanEnergyGeneration!=null){
            textViewMeanEnergyGeneration.setText((new DecimalFormat("0")).format(averageEnergy/1000));
        }

        PieView pieViewEfficiency = (PieView) findViewById(R.id.pieViewEfficiency);
        if(pieViewEfficiency!=null){
            pieViewEfficiency.setPercentage((float) 1 + (100*(currentEfficiency/averageEfficiency)));
            if(pieViewEfficiency.getPercentage()<=33)pieViewEfficiency.setPercentageBackgroundColor(Color.RED);
            if(pieViewEfficiency.getPercentage()>=67)pieViewEfficiency.setPercentageBackgroundColor(Color.GREEN);
            PieAngleAnimation animation = new PieAngleAnimation(pieViewEfficiency);
            animation.setDuration(1000); //This is the duration of the animation in millis
            pieViewEfficiency.startAnimation(animation);
            pieViewEfficiency.setInnerText((new DecimalFormat("0.#")).format(currentEfficiency));
        }

        TextView textViewMeanEfficiency = (TextView) findViewById(R.id.textViewMeanEfficiency);
        if(textViewMeanEfficiency!=null){
            textViewMeanEfficiency.setText((new DecimalFormat("0.#")).format(averageEfficiency));
        }

        TextView textViewLastUpdated = (TextView) findViewById(R.id.textViewLastUpdated);
        if(textViewLastUpdated!=null){
            textViewLastUpdated.setText("Last Updated: ".concat(lastUpdated));
        }


    }

    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CompareActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CompareActivity.this, HomeActivity.class));
                }
            });
        }

        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CompareActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CompareActivity.this, HelpActivity.class));
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
