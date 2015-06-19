package nu.tillbudsrapport.tillbud;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Patrik on 2015-06-16.
 */
public class DBOpenHelper  extends SQLiteOpenHelper{


    //Constants for db name and version
    private static final String DATABASE_NAME = "rapporter.db";
    private static final int DATABASE_VERSION = 2;

    //Constants for identifying table and columns
    public static final String TABLE_NOTES = "rappoter";
    public static final String NOTE_ID = "_id";
    public static final String NOTE_TEXT = "noteText";
    public static final String NOTE_CREATED = "noteCreated";
    public static final String NOTE_DATE = "noteDate";


    public static final String NOTE_TIME = "noteTime";
    public static final String NOTE_REPORTER = "noteReporter";
    public static final String NOTE_DESCRIPTION= "noteDescription";
    public static final String NOTE_WHY= "noteWhy";


    public static final String[] ALL_COLUMNS ={NOTE_ID, NOTE_TEXT, NOTE_CREATED, NOTE_DATE, NOTE_DESCRIPTION, NOTE_REPORTER, NOTE_TIME, NOTE_WHY};


    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NOTE_TEXT + " TEXT, " +
                    NOTE_DATE + " TEXT, " +
                    NOTE_TIME + " TEXT, " +
                    NOTE_REPORTER + " TEXT, " +
                    NOTE_DESCRIPTION + " TEXT, " +
                    NOTE_WHY + " TEXT, " +

                    NOTE_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";




    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(sqLiteDatabase);

    }
}
