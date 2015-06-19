package nu.tillbudsrapport.tillbud;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Patrik on 2015-06-16.
 */
public class RapportProvider extends ContentProvider {


    private static final String AUTHORITY = "nu.tillbudsrapport.tillbud.rapportprovider";
    private static final String BASE_PATH = "rapporter";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    public static final String CONTENT_ITEM_TYPE = "Note";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        DBOpenHelper helper = new DBOpenHelper(getContext());

        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String selection, String[] strings1, String s1) {

        if(uriMatcher.match(uri) == NOTES_ID){
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }



        return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS, selection, null, null, null, DBOpenHelper.NOTE_CREATED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = database.insert(DBOpenHelper.TABLE_NOTES, null, contentValues);


        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
      return database.delete(DBOpenHelper.TABLE_NOTES, s, strings);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return database.update(DBOpenHelper.TABLE_NOTES, contentValues, s, strings);
    }
}
