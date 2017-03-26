package au.edu.cdu.solarnt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        String contextString = bundle.getBundle("extras").getString("context");

        if(contextString.compareToIgnoreCase("home")==0)((View) findViewById(R.id.includeHome)).setVisibility(View.VISIBLE);
        if(contextString.compareToIgnoreCase("compare")==0)((View) findViewById(R.id.includeCompare)).setVisibility(View.VISIBLE);
        if(contextString.compareToIgnoreCase("dashboard")==0)((View) findViewById(R.id.includeDashboard)).setVisibility(View.VISIBLE);
        if(contextString.compareToIgnoreCase("dusting")==0)((View) findViewById(R.id.includeDusting)).setVisibility(View.VISIBLE);
        if(contextString.compareToIgnoreCase("outputs")==0)((View) findViewById(R.id.includeOutputs)).setVisibility(View.VISIBLE);
        if(contextString.compareToIgnoreCase("projections")==0)((View) findViewById(R.id.includeProjections)).setVisibility(View.VISIBLE);
        if(contextString.compareToIgnoreCase("providers")==0)((View) findViewById(R.id.includeProviders)).setVisibility(View.VISIBLE);
        if(contextString.compareToIgnoreCase("weather")==0)((View) findViewById(R.id.includeWeather)).setVisibility(View.VISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"roy.galet@cdu.edu.au","wai.yap@cdu.edu.au"});
                i.putExtra(Intent.EXTRA_SUBJECT, "SolarNT: Help");
                i.putExtra(Intent.EXTRA_TEXT   , "Hello SolarNT Team");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(HelpActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
