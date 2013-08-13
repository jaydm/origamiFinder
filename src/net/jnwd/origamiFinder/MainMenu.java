package net.jnwd.origamiFinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenu extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_menu);

		final Button queryModelButton = (Button) findViewById(R.id.btnFindModels);

		queryModelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		Button clickedButton = (Button) view;

		switch (clickedButton.getId()) {
		case R.id.btnFindModels:
			startActivity(new Intent(this, QueryModelsByName.class));

			break;
		default:
			// by default...search models
			// startActivity(new Intent(this, QueryModelList.class));

			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);

		return true;
	}

}
