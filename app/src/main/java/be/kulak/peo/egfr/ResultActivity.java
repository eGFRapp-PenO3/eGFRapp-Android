package be.kulak.peo.egfr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent resultIntent = getIntent();
        double[] results = resultIntent.getDoubleArrayExtra(MainActivity.extra_result);
        String[] info = resultIntent.getStringArrayExtra(MainActivity.extra_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView patID = (TextView) findViewById(R.id.info_ID);
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

        ArrayList<Result> resultArray = new ArrayList<Result>();
        ResultAdapter adapter = new ResultAdapter(this, resultArray);

        ListView resultView = (ListView) findViewById(R.id.resultList);
        resultView.setAdapter(adapter);

        String[] keys = getResources().getStringArray(R.array.result_key);
        for (int i = 0; i < results.length; i++){
            if(results[i] != -1){
                Result Item = new Result(keys[i], results[i]);
                adapter.add(Item);
            }
        }
    }

}
