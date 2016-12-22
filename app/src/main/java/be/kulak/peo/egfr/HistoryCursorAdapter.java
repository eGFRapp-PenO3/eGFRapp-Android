package be.kulak.peo.egfr;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by elias on 22/12/16.
 */

public class HistoryCursorAdapter extends CursorAdapter {

    private LayoutInflater cursorInflater;

    public HistoryCursorAdapter(Context context, Cursor cursor, int flags){
        super(context, cursor, flags);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
