package net.jnwd.origamiFinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class QueryModelList extends Activity implements OnClickListener {

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
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy);

			EditText modelName = (EditText) findViewById(R.id.txtMQueryName);
			EditText bookTitle = (EditText) findViewById(R.id.txtMQueryTitle);
			EditText creatorName = (EditText) findViewById(R.id.txtMQueryCreator);

			String urlString = "" +
				"http://puppet.pubint.com:8080/origami/findModel?" +
				"modelName=" + modelName.getText() + "&" +
				"bookTitle=" + bookTitle.getText() + "&" +
				"creatorName=" + creatorName.getText();

			URL url = new URL(urlString);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			String data = readStream(con.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String readStream(InputStream in) {
		BufferedReader reader = null;

		StringBuilder message = new StringBuilder();
		String line;

		try {
			reader = new BufferedReader(new InputStreamReader(in));

			while ((line = reader.readLine()) != null) {
				message.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return message.toString();
	}
}
