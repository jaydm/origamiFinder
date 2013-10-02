
package net.jnwd.origamiFinder;

import net.jnwd.origamiData.Model;
import net.jnwd.origamiData.ModelTable;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class QueryModelsByName extends Activity implements OnItemClickListener {
    private ModelTable oData;
    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading_data);

        oData = new ModelTable(this);

        oData.open();

        setContentView(R.layout.activity_query_models_by_name);

        displayListView();
    }

    private void displayListView() {
        Cursor cursor = oData.fetchAllModels();

        String[] from = ModelTable.listColumns;

        int[] to = {
                R.id.miModelID,
                R.id.miModelName,
                R.id.miModelCreator,
                R.id.miBookTitle,
                R.id.miModelDifficulty
        };

        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.model_info,
                cursor,
                from,
                to,
                0);

        ListView listView = (ListView) findViewById(R.id.sbModelListing);

        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(this);

        EditText myFilter = (EditText) findViewById(R.id.txtQueryModelName);

        myFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.getFilter().filter(s.toString());
            }
        });

        dataAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return oData.getModelMatches(constraint.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.query_models_by_name, menu);

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
        Cursor cursor = (Cursor) listView.getItemAtPosition(position);

        String modelName = cursor.getString(cursor
                .getColumnIndexOrThrow(Model.COL_MODEL_NAME));

        Toast.makeText(getApplicationContext(), modelName, Toast.LENGTH_SHORT).show();

        // load the model row...
        cursor = oData.getModel(id);

        Intent intent = new Intent(this, ShowBook.class);

        intent.putExtra(ShowBook.Extra_Message,
                cursor.getString(cursor.getColumnIndex(Model.COL_ISBN)));

        startActivity(intent);
    }
}
