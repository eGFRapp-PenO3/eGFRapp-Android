package be.kulak.peo.egfr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static android.R.color.primary_text_light;
import static be.kulak.peo.egfr.R.string.hint_age;

/**
 * Created by elias on 09/12/16.
 */

public class AgePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    double age = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new DatePickerDialog(getActivity(), this, 1970, 0, 1);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //correct inconsistency between android and joda-time (0=jan in android, 1=jan in joda-time)
        monthOfYear++;
        DateTime birthdate = new DateTime(year,monthOfYear,dayOfMonth,0,0);
        age = calculateAge(birthdate);
        MainActivity.age = age;
        String hint = getResources().getString(hint_age);
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("EEE d MMM yyyy");
        MainActivity.mAgeBtn.setText(hint + dateFormat.print(birthdate));
        MainActivity.mAgeBtn.setTextColor(getResources().getColor(primary_text_light));
    }

    public double calculateAge(DateTime birthdate){
        DateTime today = new DateTime();
        Duration lifeDuration = new Duration(birthdate,today);
        double lifeDays = lifeDuration.getStandardDays();
        return lifeDays/365.25;
    }

    public double returnAge(){ return age; }

}
