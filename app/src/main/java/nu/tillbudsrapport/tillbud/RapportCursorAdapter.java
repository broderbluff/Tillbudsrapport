package nu.tillbudsrapport.tillbud;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Patrik on 2015-06-16.
 */
public class RapportCursorAdapter extends CursorAdapter {



    public RapportCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.rapport_list_item, viewGroup, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String rapportText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));

        int pos = rapportText.indexOf(10);
        if (pos != -1){
            rapportText = rapportText.substring(0, pos) + " ...";
        }
        TextView tv = (TextView) view.findViewById(R.id.tvNote);
        tv.setText(rapportText);


    }
}
