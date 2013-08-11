package net.jnwd.origamiFinder;

import net.jnwd.origamiData.ModelTable;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
		TextView modelResults = (TextView) findViewById(R.id.txtModelResults);

		String modelNameToSearch = modelName.getText().toString();

		Log.i(TAG, "Searching for model name: " + modelNameToSearch);
		Log.i(TAG, "Clearing results box...");

		modelResults.setText("");

		Log.i(TAG, "About to do the query...");

		Cursor matchingModels = oData.getModelMatches(modelName.getText().toString(), ModelTable.allColumns);

		Log.i(TAG, "Resultset: " + matchingModels.getCount() + " rows");

		String resultText = "";

		String singleResult;

		while (matchingModels.moveToNext()) {
			singleResult = "" +
				"Model[0]: " + matchingModels.getString(0) + "\n" +
				"Model[1]: " + matchingModels.getString(1) + "\n" +
				"Model[2]: " + matchingModels.getString(2) + "\n" +
				"Model[3]: " + matchingModels.getString(3) + "\n" +
				"Model[4]: " + matchingModels.getString(4) + "\n" +
				"Model[5]: " + matchingModels.getString(5) + "\n" +
				"Model[6]: " + matchingModels.getString(6) + "\n" +
				"Model[7]: " + matchingModels.getString(7) + "\n";

			Log.i(TAG, singleResult);

			resultText += singleResult;
		}

		modelResults.setText(resultText);

		matchingModels.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.query_models_by_name, menu);

		return true;
	}

}
