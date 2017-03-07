package be.kulak.peo.egfr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent resultIntent = getIntent();
        double[] results = resultIntent.getDoubleArrayExtra(MainActivity.extra_result);
        String[] info = resultIntent.getStringArrayExtra(MainActivity.extra_info);

        fillInfo(info);

        ArrayList<Result> resultArray = new ArrayList<Result>();
        ResultAdapter adapter = new ResultAdapter(this, resultArray);

        ListView resultView = (ListView) findViewById(R.id.resultList);
        resultView.setAdapter(adapter);
        String[] keys = getResources().getStringArray(R.array.result_key);
        fillResults(results, keys, adapter);
    }

    public void fillResults(double[] results, String[] keys, ResultAdapter adapter){
        for (int i = 0; i < results.length; i++){
            if(results[i] != -1){
                Result Item = new Result(keys[i], results[i]);
                adapter.add(Item);
            }
        }
    }

    public void fillInfo(String[] info){
        TextView[] textViews = {
                (TextView) findViewById(R.id.info_ID),
                (TextView) findViewById(R.id.info_name),
                (TextView) findViewById(R.id.info_age),
                (TextView) findViewById(R.id.info_date),
                (TextView) findViewById(R.id.info_FAS)
        };
        int[] resID = {
                R.string.info_patient_id,
                R.string.info_patient_name,
                R.string.info_patient_age,
                R.string.info_date,
                R.string.info_FAS
        };
        for(int i=0; i<info.length; i++){
            if(!info[i].matches("")){
                textViews[i].setText(getResources().getString(resID[i]) + " " + info[i]);
            }else{
                textViews[i].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.result, menu);
        return true;
    }

    /*
    if(!info[0].matches("")){
            patID.setText(getResources().getString(R.string.info_patient_id) + " " + info[0]);
        }
        else{
            patID.setVisibility(View.GONE);
        }
        TextView patName = (TextView) findViewById(R.id.info_name);
        if(!info[1].matches("")) {
            patName.setText(getResources().getString(R.string.info_patient_name) + " " + info[1]);
        }
        else {
            patName.setVisibility(View.GONE);
        }
        TextView patAge = (TextView) findViewById(R.id.info_age);
        patAge.setText(getResources().getString(R.string.info_patient_age) + " " + info[2]);

     */
}
