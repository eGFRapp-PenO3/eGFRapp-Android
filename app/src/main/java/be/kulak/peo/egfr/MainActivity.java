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

import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static String extra_results = "be.kulak.peo.egfr.results";

    String patID;
    double scr;
    boolean sex;
    double hgt;
    double wgt;
    double age;
    double[] result = new double[6];

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

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean calculateGFR() {
        patID = mPatID.getText().toString();
        // true = female
        sex = mSex.getSelectedItemPosition() == 1;
        scr = parseDouble(mScr, true);
        age = parseDouble(mAge, true);
        hgt = parseDouble(mHgt, false) / 100;
        wgt = parseDouble(mWgt, false);
        if (scr == -1 | age == -1) {
            return false;
        }
        double Q = calculateQ(sex, age, 0);
        double QL = calculateQ(sex, age, hgt);
        double BSA = calculateBSA(hgt, wgt);

        result[0] = calculateFAS(scr, age, Q);
        result[1] = calculateFAS(scr, age, QL);
        result[2] = (age > 18) ? calculateCKDEPI(age, sex, scr) : -1;
        result[3] = (age > 18) ? calculateMDRD(age, sex, scr) : -1;
        result[4] = (age > 70) ? calculateBIS1(age, sex, scr) : -1;
        result[5] = calculateLM(age, sex, scr);


        result = (BSA == 0) ? result : applyBSA(result, BSA);
        return true;
    }

    public double calculateFAS(double scr, double age, double Q) {
        if (Q == -1) {
            return -1;
        } else if ((scr / Q) > .5) {
            if (age < 40) {
                return 107.3 / (scr / Q) * (1 - exp(-age / 0.5));
            } else {
                return (107.3 / (scr / Q)) * pow(0.988, age - 40);
            }
        } else {
            return -1;
        }
    }

    public double calculateCKDEPI(double age, boolean sex, double scr) {
        if (sex) {
            if (scr < .7) {
                return 141 * pow(scr / .9, -.411) * pow(0.993, age);
            } else {
                return 141 * pow(scr / 0.9, -1.209) * pow(0.993, age);
            }
        } else {
            if (scr < .9) {
                return 141 * pow(scr / .9, -.411) * pow(0.993, age);
            } else {
                return 141 * pow(scr / 0.9, -1.209) * pow(0.993, age);
            }
        }
    }

    public double calculateMDRD(double age, boolean sex, double scr) {
        if (sex) {
            return 175 * pow(scr, -1.154) * pow(age, -0.203) * 0.742;
        } else {
            return 175 * pow(scr, -1.154) * pow(age, -0.203);
        }
    }

    public double calculateBIS1(double age, boolean sex, double scr) {
        if (sex) {
            return 3736 * pow(scr, -.87) * pow(age, -.95) * .82;
        } else {
            return 3736 * pow(scr, -.87) * pow(age, -.95);
        }
    }

    public double calculateLM(double age, boolean sex, double scr) {
        if (scr * 88.4 < 150) {
            if (sex) {
                return exp(4.62 - 0.0112 * scr * 88.4 - 0.0124 * age + 0.339 * log(age) - 0.226);
            } else {
                return exp(4.62 - 0.0112 * scr * 88.4 - 0.0124 * age + 0.339 * log(age));
            }
        } else {
            if (sex) {
                return exp(8.17 + 0.0005 * scr * 88.4 - 1.07 * log(scr * 88.4) - 0.0124 * age + 0.339 * log(age) - 0.226);
            } else {
                return exp(8.17 + 0.0005 * scr * 88.4 - 1.07 * log(scr * 88.4) - 0.0124 * age + 0.339 * log(age));
            }
        }
    }



    public double calculateBSA(double hgt, double wgt) {
        if (hgt == -1 | wgt == -1) {
            return 0;
        } else {
            return 0.007184 * pow(hgt, 0.725) * pow(wgt, 0.425);
        }
    }


    /*
    multiplies all elements of array with double BSA,
    except if element is -1 (aka N/A)
     */
    public double[] applyBSA(double[] result, double BSA) {
        double[] BSAresult = new double[result.length];
        for (int i = 0; i < result.length; i++) {
            if (result[i] == -1) {
                BSAresult[i] = -1;
            } else {
                BSAresult[i] = result[i] * 1.73 / BSA;
            }
        }
        return BSAresult;
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
        String stringVal = text.getText().toString();
        double value;
        try {
            value = Double.parseDouble(stringVal);
        } catch (NumberFormatException e) {
            if (required) {
                text.setError("Enter valid " + text.getHint());
            }
            return -1;
        }
        return value;

    }
    //linde is een traag kindje
}
