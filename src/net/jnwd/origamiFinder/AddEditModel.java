package net.jnwd.origamiFinder;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AddEditModel extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_model);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_model, menu);
		return true;
	}

}
