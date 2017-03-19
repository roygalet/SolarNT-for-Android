package au.edu.cdu.solarnt;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import PVOutputData.PVAccountSettings;
import PVOutputData.PVSystemsCollection;
import PVOutputData.PVSystem;

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
        postCode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("postCode","810"));
//        postCode = 810;
        distance = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("distance","100"));
        latestOnly = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("latestOnly",true);
        numberOfSystems = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("numberOfSystems","5"));

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
            nearbyCollection.getMySystem();
            nearbyCollection.getNearbySystemsWithLatestOutputs(distance,numberOfSystems, latestOnly);
            return nearbyCollection;
        }

        @Override
        protected void onPostExecute(PVSystemsCollection pvSystemsCollection) {
            super.onPostExecute(pvSystemsCollection);
//            systems = new ArrayList<String>();
//            systems.add(dontCompareMessage);
            float averageEnergy = 0;
            float currentEnergy = 0;
            for(int i=0; i<nearbyCollection.getPvSystems().size(); i++){
//                systems.add((String) nearbyCollection.getPvSystems().keySet().toArray()[i]);
//                System.out.println((String) nearbyCollection.getPvSystems().keySet().toArray()[i]);
//                System.out.println(((PVSystem)nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatistics());
                currentEnergy = averageEnergy + Float.parseFloat(((PVSystem)nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatus().split(",")[2]);
                averageEnergy = averageEnergy + Float.parseFloat(((PVSystem)nearbyCollection.getPvSystems().get((String) nearbyCollection.getPvSystems().keySet().toArray()[i])).getStatistics().split(",")[2]);
            }
            currentEnergy = currentEnergy/nearbyCollection.getPvSystems().size();
            averageEnergy = averageEnergy/nearbyCollection.getPvSystems().size();
            System.out.println(averageEnergy);
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
