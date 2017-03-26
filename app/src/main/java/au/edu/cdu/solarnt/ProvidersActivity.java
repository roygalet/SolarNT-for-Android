package au.edu.cdu.solarnt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class ProvidersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_providers);

        setupButtons();

        setImageWebClickListener((ImageView) findViewById(R.id.imageViewProvider1),(Button) findViewById(R.id.buttonWeb1));
        setImageWebClickListener((ImageView) findViewById(R.id.imageViewProvider2),(Button) findViewById(R.id.buttonWeb2));
        setImageWebClickListener((ImageView) findViewById(R.id.imageViewProvider3),(Button) findViewById(R.id.buttonWeb3));
        setImageWebClickListener((ImageView) findViewById(R.id.imageViewProvider4),(Button) findViewById(R.id.buttonWeb4));
        setImageWebClickListener((ImageView) findViewById(R.id.imageViewProvider5),(Button) findViewById(R.id.buttonWeb5));
        setImageWebClickListener((ImageView) findViewById(R.id.imageViewProvider6),(Button) findViewById(R.id.buttonWeb6));

        setButtonWebClickListener((Button) findViewById(R.id.buttonWeb1));
        setButtonWebClickListener((Button) findViewById(R.id.buttonWeb2));
        setButtonWebClickListener((Button) findViewById(R.id.buttonWeb3));
        setButtonWebClickListener((Button) findViewById(R.id.buttonWeb4));
        setButtonWebClickListener((Button) findViewById(R.id.buttonWeb5));
        setButtonWebClickListener((Button) findViewById(R.id.buttonWeb6));

        setButtonPhoneClickListener((Button) findViewById(R.id.buttonPhone1));
        setButtonPhoneClickListener((Button) findViewById(R.id.buttonPhone2));
        setButtonPhoneClickListener((Button) findViewById(R.id.buttonPhone3));
        setButtonPhoneClickListener((Button) findViewById(R.id.buttonPhone41));
        setButtonPhoneClickListener((Button) findViewById(R.id.buttonPhone42));
        setButtonPhoneClickListener((Button) findViewById(R.id.buttonPhone5));
        setButtonPhoneClickListener((Button) findViewById(R.id.buttonPhone6));

        setButtonEmailClickListener((Button) findViewById(R.id.buttonEmail1));
        setButtonEmailClickListener((Button) findViewById(R.id.buttonEmail2));
        setButtonEmailClickListener((Button) findViewById(R.id.buttonEmail4));
        setButtonEmailClickListener((Button) findViewById(R.id.buttonEmail5));
        setButtonEmailClickListener((Button) findViewById(R.id.buttonEmail6));

    }

    public void setImageWebClickListener(ImageView imageView, final Button button){
        if(imageView!=null && button!=null)imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://".concat(button.getText().toString()))));
            }
        });
    }

    public void setButtonWebClickListener(final Button button){
        if(button!=null)button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://".concat(button.getText().toString()))));
            }
        });
    }

    public void setButtonPhoneClickListener(final Button button){
        if(button!=null)button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:".concat(button.getText().toString()))));
            }
        });
    }

    public void setButtonEmailClickListener(final Button button){
        if(button!=null)button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{button.getText().toString()});
                i.putExtra(Intent.EXTRA_SUBJECT, "SolarNT: Enquiry");
                i.putExtra(Intent.EXTRA_TEXT   , "Hello ".concat(button.getText().toString()));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ProvidersActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupButtons(){

        ImageButton imageButtonHome = (ImageButton)findViewById(R.id.imageButtonHome);
        if(imageButtonHome != null){
            imageButtonHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProvidersActivity.this, HomeActivity.class));
                }
            });
        }

        ImageView imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);
        if(imageViewLogo != null){
            imageViewLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProvidersActivity.this, HomeActivity.class));
                }
            });
        }

        ImageButton imageButtonSettings = (ImageButton)findViewById(R.id.imageButtonSettings);
        if(imageButtonSettings != null){
            imageButtonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProvidersActivity.this, SettingsActivity.class));
                }
            });
        }


        ImageButton imageButtonHelp = (ImageButton)findViewById(R.id.imageButtonHelp);
        if(imageButtonHelp != null) {
            imageButtonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("context", "providers");
                    Intent intent = new Intent(ProvidersActivity.this, HelpActivity.class);
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
