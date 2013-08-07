package net.jnwd.origamiFinder;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class QueryModelList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_query_model_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.query_model_list, menu);
		return true;
	}

}
