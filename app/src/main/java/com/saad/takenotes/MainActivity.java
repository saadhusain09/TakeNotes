package com.saad.takenotes;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cursorAdapter = new NotesCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);

        // Setting a floating action button for creating new notes
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditorForNewNote(v);
            }
        });
    }

    /*
    insertNote(String noteText) inserts a new note into the database
     */
    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:
                deleteAllNotes();
                break;
            case R.id.action_about:
                showAboutPage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    showAboutPage() opens the About page activity
     */
    private void showAboutPage() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /*
    deleteAllNotes() deletes all the notes, but asks the user if they are sure using a dialog box
     */
    private void deleteAllNotes() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            // where clause is null, to delete all notes
                            getContentResolver().delete(NotesProvider.CONTENT_URI, null, null);

                            restartLoader();

                            // a Snackbar is used here over a Toast message to demonstrate
                            // the floating action button moving upward to give room for the message
                            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cv);
                            Snackbar.make(coordinatorLayout, getString(R.string.all_deleted), Snackbar.LENGTH_LONG).show();

                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure_all))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    /*
    insertSampleData() simply inserts sample data for testing purposes
     */
    private void insertSampleData() {
        insertNote("Simple note");
        insertNote("Multi-line\nnote");
        insertNote("Very long note with a lot of text that exceeds the width of the screen");

        restartLoader();
    }

    /*
    restartLoader() simply informs the loader object that it needs to re-read data from
    the back-end database
     */
    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    /*
    onCreateLoader(int id, Bundle args) is called whenever data is needed from the content provider.
    It returns a CursorLoader to manage the cursor
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // projection (list of columns) is null because it is already coded in provider
        // selection is null, which means all the data
        // selection args and sort order are null for not being used
        return new CursorLoader(this, NotesProvider.CONTENT_URI, null, null, null, null);
    }

    /*
    onLoadFinished(Loader<Cursor> loader, Cursor data) is called automatically, and passes the
    data (a cursor) to the cursor adapter
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    /*
    onLoaderReset(Loader<Cursor> loader) is called automatically whenever data needs to be wiped out
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }


}
