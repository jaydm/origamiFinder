package net.jnwd.origamiFinder;

import java.util.ArrayList;
import java.util.List;

import net.jnwd.origamiData.Model;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class QueryModelList extends Activity implements OnClickListener {
	List<Model> allModels = new ArrayList<Model>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_query_model_list);

		Button btnExecuteQuery = (Button) findViewById(R.id.btnModelQuery);

		btnExecuteQuery.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.query_model_list, menu);

		return true;
	}

	@Override
	public void onClick(View view) {
		Button clickedButton = (Button) view;

		switch (clickedButton.getId()) {
		case R.id.btnModelQuery:
			findModels();

			break;
		default:
			break;
		}
	}

	private void findModels() {

	}
}
