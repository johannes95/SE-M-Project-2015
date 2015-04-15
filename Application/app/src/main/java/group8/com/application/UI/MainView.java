package group8.com.application.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalId;
import android.swedspot.scs.data.SCSFloat;
import android.swedspot.scs.data.Uint8;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveListener;
import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;
import com.swedspot.vil.distraction.LightMode;
import com.swedspot.vil.distraction.StealthMode;
import com.swedspot.vil.policy.AutomotiveCertificate;

import group8.com.application.Application.GradingSystem;
import group8.com.application.Application.Session;
import group8.com.application.R;
import group8.com.application.UI.Graphs.DriverDistractionGraph;
import group8.com.application.UI.Graphs.FuelConsumptionGraph;
import group8.com.application.UI.Graphs.SpeedGraph;
import group8.com.application.UI.Login.LoginView;


public class MainView extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_display);

        Button graphBtn = (Button) findViewById(R.id.graphBtn);
        Button testMeasurementBtn = (Button) findViewById(R.id.testMeasurementsBtn);
        TextView userTxt = (TextView) findViewById(R.id.username);

        graphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ResultsView.class);
                startActivityForResult(intent, 0);
            }
        });

        testMeasurementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TestGrading.class);
                startActivityForResult(intent, 0);
            }
        });

        userTxt.setText(Session.getUserName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_view_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.mainviewmenu_logout:
                //Does the
                if (Profile.getCurrentProfile() != null)
                    LoginManager.getInstance().logOut();
                SharedPreferences sp = PreferenceManager
                        .getDefaultSharedPreferences(MainView.this);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("username", "");
                edit.commit();
                Session.restart();

                //Goes back to the login view.
                Intent intent = new Intent(this, LoginView.class);
                this.startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}

