package be.kulak.peo.egfr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent resultIntent = getIntent();
        double[] results = resultIntent.getDoubleArrayExtra(MainActivity.extra_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        formatResult(results[0], (TextView) findViewById(R.id.res_FAS));
        formatResult(results[1], (TextView) findViewById(R.id.res_FASL));
    }

    private void formatResult(double result, TextView view){
        if (result==-1){
            LinearLayout p = (LinearLayout) view.getParent().getParent();
            p.setVisibility(View.GONE);
        }else{
            view.setText(String.format("%.1f", result));
        }
    }

}
