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

		Log.i(TAG, "Creatinng the database reference...");

		oData = new ModelTable(this);

		oData.open();

		oData.emptyDatabase();

		oData.loadDatabase();

		displayListView();
	}

	private void displayListView() {
		Cursor cursor = oData.fetchAllModels();

		int[] to = {
			R.id.txtInfoModelName,
			R.id.txtInfoModelCreator,
			R.id.txtInfoBookTitle,
			R.id.txtInfoBookISBN,
			R.id.txtInfoModelPage,
			R.id.txtInfoModelType,
			R.id.txtInfoModelDifficulty,
			R.id.txtInfoModelPaper,
			R.id.txtLinfoPaperPieces,
			R.id.txtInfoGlue,
			R.id.txtInfoCuts
		};

		dataAdapter = new SimpleCursorAdapter(
			this, R.layout.model_info,
			cursor,
			ModelTable.listColumns,
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
