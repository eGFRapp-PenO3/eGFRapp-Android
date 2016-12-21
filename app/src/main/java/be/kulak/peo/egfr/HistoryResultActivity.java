package be.kulak.peo.egfr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by elias on 20/12/16.
 */

public class HistoryResultActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent resultIntent = getIntent();
        double[] results = resultIntent.getDoubleArrayExtra(MainActivity.extra_result);
        String[] info = resultIntent.getStringArrayExtra(MainActivity.extra_info);


    }
}
