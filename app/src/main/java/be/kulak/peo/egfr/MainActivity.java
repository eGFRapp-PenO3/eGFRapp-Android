package be.kulak.peo.egfr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.R.color.tertiary_text_light;
import static java.lang.Math.exp;
import static java.lang.Math.floor;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.round;

public class MainActivity extends AppCompatActivity{

    public final static String extra_result = "be.kulak.peo.egfr.result";
    public final static String extra_info = "be.kulak.peo.egfr.info";

    //0: FASNorm; 1: FASLow; 2: FASHigh
    public static double[] FASnormal = new double[3];

    public final static Map<String,String> ResultStrings = new HashMap<>();

    double scr;
    double cisc;
    boolean sex;
    double hgt;
    double wgt;
    public static double age = -1;
    double[] result;
    String[] info = new String[5];
    boolean si;
    boolean FASInUse;

    EditText mPatID;
    EditText mFN;
    EditText mLN;
    EditText mScr;
    EditText mCisC;
    EditText mHgt;
    EditText mWgt;
    Spinner mSex;
    public static Button mAgeBtn;

    Set<String> formulae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        FloatingActionButton fab_calc = (FloatingActionButton) findViewById(R.id.fab_calc);
        fab_calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (calculateGFR()) {
                    Intent resultIntent = new Intent(getBaseContext(), ResultActivity.class);
                    //pass results to resultactivity
                    resultIntent.putExtra(extra_result, result);
                    //pass info to resultactivity
                    resultIntent.putExtra(extra_info, generateInfo(
                            mPatID.getText().toString(),
                            mFN.getText().toString(),
                            mLN.getText().toString(),
                            age,
                            new DateTime(),
                            FASnormal
                    ));
                    startActivity(resultIntent);
                }
            }
        });

        //create variable objects
        mPatID = (EditText) findViewById(R.id.patID);
        mFN = (EditText) findViewById(R.id.patFN);
        mLN = (EditText) findViewById(R.id.patLN);
        mScr = (EditText) findViewById(R.id.scr);
        mCisC = (EditText) findViewById(R.id.cisc);
        mHgt = (EditText) findViewById(R.id.hgt);
        mWgt = (EditText) findViewById(R.id.wgt);
        mSex = (Spinner) findViewById(R.id.sex);
        mAgeBtn = (Button) findViewById(R.id.btn_age);

        //settings for formula selection
        formulae = settings.getStringSet("formulae", new HashSet<String>());
        si = settings.getBoolean("si", si);
        mScr.setHint(getResources().getString(R.string.hint_scr) + (si ? " (μmol/L)" : " (mg/dL)"));
        //create hashmap with result data
        fillResultMap();

        result = new double[getResources().getStringArray(R.array.result_key).length];

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.array_sex, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSex.setAdapter(adapter);
    }

    public void showAgePickerDialog(View v){
        AgePickerFragment ageFragment = new AgePickerFragment();
        ageFragment.show(getFragmentManager(), "Birthdate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_reset) {
            mPatID.setText("");
            mAgeBtn.setText(getResources().getString(R.string.hint_age_init));
            mAgeBtn.setTextColor(getResources().getColor(tertiary_text_light));
            mFN.setText("");
            mLN.setText("");
            age = 0;
            mScr.setText("");
            mCisC.setText("");
            mHgt.setText("");
            mWgt.setText("");
            mSex.setSelection(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean calculateGFR() {
        // true = female
        sex = mSex.getSelectedItemPosition() == 1;
        scr = parseDouble(mScr);
        scr = si ? scr/88.4 : scr;
        cisc = parseDouble(mCisC);
        if(scr==-1 && cisc==-1){
            mCisC.setError("Enter valid Cystatine-C!");
            mScr.setError("Enter valid Serum Creatinine!");
            return false;
        }
        hgt = parseDouble(mHgt) / 100;
        wgt = parseDouble(mWgt);
        if (age==-1){
            mAgeBtn.setError("Enter age!");
            return false;
        }
        if (scr == -1 | age < .1) {
            mAgeBtn.setError("Patient age must be above 1 month!");
            return false;
        }
        double[] var = {scr, scr, cisc};
        double[] Q = {calculateQSCr(sex, age, 0),calculateQSCr(sex, age, hgt),calculateQCisC(age)};
        //double BSA = calculateBSA(hgt, wgt);

        FASInUse = formulae.contains("FAS") | formulae.contains("FASL") | formulae.contains("FASC");

        result[1] = formulae.contains("FAS") ? calculateFAS(scr, age, Q[0]) : -1;
        result[2] = formulae.contains("FASL") ? calculateFAS(scr, age, Q[1]) : -1;
        result[3] = formulae.contains("FASC") ? calculateFAS(cisc,age, Q[2]) : -1;
        result[4] = (age > 18) && formulae.contains("CKDEPI") ? calculateCKDEPI(age, sex, scr) : -1;
        result[5] = (age < 18) && formulae.contains("S") && hgt != -1 ? calculateSchwartz(scr, hgt) : -1;
        result[6] = (age > 18) && formulae.contains("MDRD") ? calculateMDRD(age, sex, scr) : -1;
        result[7] = (age > 70) && formulae.contains("BIS1") ? calculateBIS1(age, sex, scr) : -1;
        result[8] = formulae.contains("LM") ? calculateLM(age, sex, scr) : -1;
        result[9] = formulae.contains("CG") && wgt!=-1 ? calculateCG(wgt, age, sex, scr) : -1;

        if(FASInUse) FASnormal = calculateFASNorm(age);

        int FASTot = 0;
        for (int i=1; i<4; i++){
            FASTot += result[i]==-1 ? 0 : 1;
        }
        result[0] = formulae.contains("FASCOM") && FASTot > 1 ? calculateFASCOM(calculateFASVar(var, Q), age) : -1;

        //result = (BSA == 0) ? result : applyBSA(result, BSA);
        return true;
    }

    private double[] calculateFASNorm(double age){
        double[] FASNorm = new double[3];
        if(age<40){
            FASNorm[0] = 107.3;
            FASNorm[1] = 107.3 / 1.33;
            FASNorm[2] = 107.3 / 0.67;
        }else{
            FASNorm[0] = 107.3 * pow(0.988, age - 40);
            FASNorm[1] = 107.3 / 1.33 * pow(0.988, age - 40);
            FASNorm[2] = 107.3 / 0.67 * pow(0.988, age - 40);
        }
        return FASNorm;
    }

    private double calculateFAS(double scr, double age, double Q) {
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

    private double calculateFASCOM(double var, double age) {
        if (var > .5) {
            if (age < 40) {
                return 107.3 / var * (1 - exp(-age / 0.5));
            } else {
                return (107.3 / var) * pow(0.988, age - 40);
            }
        } else {
            return -1;
        }
    }

    private double calculateFASVar(double[] mark, double[] Q){
        List<Double> marQ = new ArrayList<>();
        for(int i=0; i<mark.length; i++){
            if (mark[i] != -1){
                marQ.add(mark[i]/Q[i]);
            }
        }
        double totvar = 0;
        for (Double var : marQ) totvar += var;
        return totvar/marQ.size();
    }

    private double calculateCKDEPI(double age, boolean sex, double scr) {
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

    private double calculateMDRD(double age, boolean sex, double scr) {
        if (sex) {
            return 175 * pow(scr, -1.154) * pow(age, -0.203) * 0.742;
        } else {
            return 175 * pow(scr, -1.154) * pow(age, -0.203);
        }
    }

    private double calculateBIS1(double age, boolean sex, double scr) {
        if (sex) {
            return 3736 * pow(scr, -.87) * pow(age, -.95) * .82;
        } else {
            return 3736 * pow(scr, -.87) * pow(age, -.95);
        }
    }

    private double calculateLM(double age, boolean sex, double scr) {
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

    private double calculateSchwartz(double scr, double hgt){
        return 0.413 * hgt * 100 / scr;
    }

    private double calculateCG(double wgt, double age, boolean sex, double scr) {
        if (sex)
        {
            return (140 - age) * wgt / (72 * scr) * 0.85;
        }
        else
        {
            return (140 - age) * wgt / (72 * scr);
        }
    }

    /*
    public double calculateBSA(double hgt, double wgt) {
        if (hgt == -1 | wgt == -1) {
            return 0;
        } else {
            return 0.007184 * pow((hgt*100), 0.725) * pow(wgt, 0.425);
        }
    }
    */

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
                BSAresult[i] = (result[i] * 1.73) / BSA;
            }
        }
        return BSAresult;
    }

    /*
    if height is 0 the method calculates Q based on age alone
    if -1 is returned the calculation of Q is not applicable to the set of variables
    */
    public double calculateQSCr(boolean sex, double age, double hgt) {
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

    public double calculateQCisC(double age) {
        if (age < 70)
        {
            return 0.82;
        }
        else
        {
            return 0.95;
        }
    }

    public double parseDouble(EditText text) {
        String stringVal = text.getText().toString();
        double value;
        try {
            value = Double.parseDouble(stringVal);
        } catch (NumberFormatException e) {
            return -1;
        }
        return value;

    }

    public void fillResultMap(){
        String[] keys = getResources().getStringArray(R.array.result_key);
        String[] values = getResources().getStringArray(R.array.result_value);
        for(int i = 0; i < keys.length; i++){
            ResultStrings.put(keys[i],values[i]);
        }
    }

    public String[] generateInfo(String patID, String FN, String LN, double age, DateTime date, double[] FASnormal){
        String[] info = new String[5];
        //fill patient id
        info[0] = patID.trim();
        //fill name
        FN.trim();
        LN.trim();
        info[1] = FN.matches("") && LN.matches("") ? "" : (FN + " " + LN);
        //fill age
        info[2] = String.format("%.0f", floor(age));
        //fill date
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("EEE d MMM yyyy");
        info[3] = dateFormat.print(date);
        //fill FAS range
        double deltaFAS = FASnormal[0] - FASnormal[1];
        if(FASInUse) info[4] = String.format("[%.1f - %.1f/%.1f]", FASnormal[1], FASnormal[0]+deltaFAS,FASnormal[2]);
        else info[4] = "";
        //return full array
        return info;
    }
}
