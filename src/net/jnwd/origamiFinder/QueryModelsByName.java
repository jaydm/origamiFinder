package net.jnwd.origamiFinder;

import net.jnwd.origamiData.ModelTable;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class QueryModelsByName extends Activity {
	private static final String TAG = "ModelByName";

	private ModelTable oData;
	private SimpleCursorAdapter dataAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_query_models_by_name);

		Button doQueryButton = (Button) findViewById(R.id.btnQueryModelByName);

		doQueryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				queryModelsByName();
			}
		});

		oData = new ModelTable(this);
	}

	public void queryModelsByName() {
		TextView modelName = (TextView) findViewById(R.id.txtQueryModelName);

		String modelNameToSearch = modelName.getText().toString();

		Log.i(TAG, "Searching for model name: " + modelNameToSearch);

		Log.i(TAG, "About to do the query...");

		Cursor matchingModels = oData.getModelMatches(modelName.getText().toString(), ModelTable.allColumns);

		int[] to = {
			R.id.lstModelName,
			R.id.lstModelType,
			R.id.lstCreatorName
		};

		Log.i(TAG, "Resultset: " + matchingModels.getCount() + " rows");

		dataAdapter = new SimpleCursorAdapter(
			this, R.layout.model_info,
			matchingModels,
			ModelTable.allColumns,
			to,
			0);

		ListView listView = (ListView) findViewById(R.id.listView1);

		listView.setAdapter(dataAdapter);

		matchingModels.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.query_models_by_name, menu);

		return true;
	}

}
