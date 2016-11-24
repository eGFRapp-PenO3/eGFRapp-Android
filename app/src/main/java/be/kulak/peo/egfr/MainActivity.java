package be.kulak.peo.egfr;

import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.IllegalFormatException;

import static java.lang.Math.pow;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String extra_results = "be.kulak.peo.egfr.results";

    String patID;
    double scr;
    boolean sex;
    int hgt;
    double wgt;
    double[] result;

    EditText mPatID;
    EditText mAge;
    EditText mScr;
    EditText mHgt;
    EditText mWgt;
    Spinner mSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab_calc = (FloatingActionButton) findViewById(R.id.fab_calc);
        fab_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculateGFR()) {
                    Intent resultIntent = new Intent(getBaseContext(), ResultActivity.class);
                    resultIntent.putExtra(extra_results, result);
                    startActivity(resultIntent);
                }
            }
        });

        mPatID = (EditText) findViewById(R.id.patID);
        mAge = (EditText) findViewById(R.id.age);
        mScr = (EditText) findViewById(R.id.scr);
        mHgt = (EditText) findViewById(R.id.hgt);
        mWgt = (EditText) findViewById(R.id.wgt);
        mSex = (Spinner) findViewById(R.id.sex);

        FloatingActionButton fab_reset = (FloatingActionButton) findViewById(R.id.fab_reset);
        fab_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPatID.setText("");
                mAge.setText("");
                mScr.setText("");
                mHgt.setText("");
                mWgt.setText("");
                mSex.setSelection(0);
            }
        });

        Button btnPatID = (Button) findViewById(R.id.btn_patID);
        btnPatID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Database not yet supported.", Toast.LENGTH_SHORT).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Spinner sexSpinner = (Spinner) findViewById(R.id.sex);
        //sexSpinner.getOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.array_sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexSpinner.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calc) {

        } else if (id == R.id.nav_hist) {

        } else if (id == R.id.nav_set) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean calculateGFR() {
        patID = mPatID.getText().toString();
        // true = female
        sex = mSex.getSelectedItemPosition()==1;
        //scr = Double.parseDouble(mScr.toString());
        //hgt = Integer.parseInt(mHgt.toString());
        //wgt = Double.parseDouble(mWgt.toString());
        double Q = calculateQ(sex, 4);
        double FAS = calculateFAS();
        result[0] = FAS;
        return true;
    }

    public double calculateFAS() {
        return 42;
    }

    /*
    if height is 0 the method calculates Q based on age alone
    if -1 is returned the calculation of Q is not applicable to the set of variables
    */
    public double calculateQ(boolean sex, double age, double hgt) {
        double a, b, c, d, e, var;
        if (age < 20) {
            // if no hgt, calculate with age
            if (hgt == 0) {
                // choose age as calculating variable
                var = age;
                // if female else male
                if (sex) {
                    a = 2.1e-1;
                    b = 5.7e-2;
                    c = 7.5e-3;
                    d = 6.4e-4;
                    e = 1.6e-5;
                } else {
                    a = 2.3e-1;
                    b = 3.4e-2;
                    c = 1.8e-3;
                    d = 1.7e-4;
                    e = 5.1e-6;
                }
                // if hgt within boundaries else return N/A
            } else if (hgt > .7 & hgt < 1.815) {
                // choose height as calculating variable
                var = hgt;
                a = 3.94;
                b = -13.4;
                c = -17.6;
                d = -9.84;
                e = -2.04;
            } else {
                return -1;
            }
            // if no N/A has been returned yet, calculate Q based on chosen variable
            return a + (b * var) - (c * pow(var, 2)) + (d * pow(var, 3)) - (e * pow(var, 4));
        } else {
            if (hgt == 0) {
                return sex ? .7 : .9;
            } else {
                return -1;
            }
        }
    }

    public double parseDouble(EditText text, boolean required) {
        String stringVal = text.toString();
        double value;
        try {
            value = Double.parseDouble(stringVal);
            return value;
        } catch (IllegalFormatException e) {
            if (required) {
                text.setError("Enter valid " + text.getHint());
                return -1;
            } else {
                return 0;
            }
        }

    }
    //linde is een traag kindje
}
