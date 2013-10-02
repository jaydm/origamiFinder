
package net.jnwd.origamiFinder;

import net.jnwd.origamiData.Model;
import net.jnwd.origamiData.ModelTable;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ShowBook extends Activity {
    private static final String TAG = "ShowBook";

    private ModelTable oData;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Show loading screen...");

        setContentView(R.layout.loading_data);

        oData = new ModelTable(this);
        oData.open();

        long isbn = Long.parseLong(savedInstanceState.getString("ISBN"));

        Cursor cursor;

        cursor = oData.getBookByISBN(isbn);

        if (cursor == null) {
            return;
        }

        String title = cursor.getString(cursor.getColumnIndex(Model.COL_BOOK_TITLE));
        String isbnNumber = cursor.getString(cursor.getColumnIndex(Model.COL_ISBN));

        setContentView(R.layout.activity_show_book);

        ((EditText) findViewById(R.id.sbTitle)).setText(title);

        ((EditText) findViewById(R.id.sbISBN)).setText(isbnNumber);

        TextView modelCount = (TextView) findViewById(R.id.miTitleLabel);

        String label = getResources().getString(R.string.modelLabel) + " "
                + cursor.getCount();

        modelCount.setText(label);

        cursor = oData.getModelsByISBN(isbn);

        Log.i(TAG, "Got the cursor? " + (cursor == null ? "Null!?!?!?" : "Cursor Okay!"));

        String[] from = ModelTable.listColumns;

        int[] to = {
                R.id.fmiModelID,
                R.id.miModelName,
                R.id.fmiModelType,
                R.id.miModelCreator,
                R.id.fmiModelOnPage,
                R.id.miModelDifficulty,
                R.id.fmiModelShape,
                R.id.fmiModelSheets,
                R.id.fmiModelGlue,
                R.id.fmiModelCuts
        };

        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.full_model_info,
                cursor,
                from,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.sbModelListing);

        listView.setAdapter(dataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_book, menu);
        return true;
    }
}
