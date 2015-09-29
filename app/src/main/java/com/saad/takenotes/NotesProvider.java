package com.saad.takenotes;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NotesProvider extends ContentProvider{
    // the Authority is a globally unique String which identifies the
    // content provider to the Android framework
    private static final String AUTHORITY = "com.saad.takenotes.notesprovider";
    // the Base Path represents the entire data set, which in this case is only one table
    private static final String BASE_PATH = "notes";
    // the Uniform Resource Identifier identifies the content provider
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    // UriMatchers parse a Uri to indicate which operation has been requested
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // used to indicate an existing note is being updated
    public static final String CONTENT_ITEM_TYPE = "Note";

    // executes the first time anything is called from this class
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

    /*
    query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    will obtain data from the database, and can either retrieve all the notes or just one.
    The columns to retrieve must be specified.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == NOTES_ID) {
            selection = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        // data is returned in descending order
        return database.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS, selection,
                null, null, null, DBOpenHelper.NOTE_CREATED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /*
    insert(Uri uri, ContentValues values) inserts a new note are returns a Uri
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_NOTES, null, values);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    /*
    delete(Uri uri, String selection, String[] selectionArgs) deletes a note
    and returns an integer value which represents the number of rows deleted
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_NOTES, selection, selectionArgs);
    }

    /*
    update(Uri uri, ContentValues values, String selection, String[] selectionArgs) updates
    an exiting note
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_NOTES, values, selection, selectionArgs);
    }
}
