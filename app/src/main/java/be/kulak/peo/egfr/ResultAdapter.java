package be.kulak.peo.egfr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by elias on 02/12/16.
 */

public class ResultAdapter extends ArrayAdapter<Result> {
    public ResultAdapter(Context context, ArrayList<Result> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Result result = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_result, parent, false);
        }
        // Lookup view for data population
        TextView resultName = (TextView) convertView.findViewById(R.id.item_result_name);
        TextView resultHint = (TextView) convertView.findViewById(R.id.item_result_extra);
        TextView resultValue = (TextView) convertView.findViewById(R.id.item_result_value);
        TextView resultUnit = (TextView) convertView.findViewById(R.id.item_result_unit);
        // Populate the data into the template view using the data object
        resultName.setText(result.formula);
        resultValue.setText(result.value);
        if(result.textcolor) resultValue.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
        resultHint.setText(result.hint);
        resultUnit.setText(result.unit);
        // Return the completed view to render on screen
        return convertView;
    }

}
