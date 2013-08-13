package net.jnwd.origamiFinder;

import net.jnwd.origamiData.Model;
import net.jnwd.origamiData.ModelTable;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class QueryModelsByName extends Activity {
	private static final String TAG = "ModelByName";

	private ModelTable oData;
	private SimpleCursorAdapter dataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "Inflating the interface...");

		setContentView(R.layout.activity_query_models_by_name);

		Log.i(TAG, "Creating the database reference...");

		oData = new ModelTable(this);

		Log.i(TAG, "Opening the database...");

		oData.open();

		Log.i(TAG, "Initial fill of the listView...");

		displayListView();
	}

	private void displayListView() {
		Log.i(TAG, "Getting a cursor to the full database...");

		Cursor cursor = oData.fetchAllModels();

		Log.i(TAG, "Got the cursor? " + (cursor == null ? "Null!?!?!?" : "Cursor Okay!"));

		String[] from = ModelTable.listColumns;

		int[] to = {
		            R.id.txtModelID,
			R.id.txtInfoModelName,
			R.id.txtInfoModelCreator,
			R.id.txtInfoBookTitle,
			R.id.txtInfoModelDifficulty
		};

		Log.i(TAG, "Layout: " + R.layout.model_info);
		Log.i(TAG, "From: ");

		for (String row : from) {
			Log.i(TAG, "row: " + row);
		}

		Log.i(TAG, "To: ");

		for (int row : to) {
			Log.i(TAG, "view: " + row);
		}

		dataAdapter = new SimpleCursorAdapter(
			this, R.layout.model_info,
			cursor,
			from,
			to,
			0);

		ListView listView = (ListView) findViewById(R.id.listView1);

		listView.setAdapter(dataAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
				Cursor cursor = (Cursor) listView.getItemAtPosition(position);

				String modelName = cursor.getString(cursor.getColumnIndexOrThrow(Model.COL_MODEL_NAME));

				Toast.makeText(getApplicationContext(), modelName, Toast.LENGTH_SHORT).show();
			}
		});

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
}
