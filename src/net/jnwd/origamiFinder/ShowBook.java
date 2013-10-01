
package net.jnwd.origamiFinder;

import net.jnwd.origamiData.Book;
import net.jnwd.origamiData.ModelTable;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

public class ShowBook extends Activity {
    private static final String TAG = "ShowBook";

    private ModelTable oData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Show loading screen...");

        setContentView(R.layout.loading_data);

        oData = new ModelTable(this);
        oData.open();

        Book book = oData.getBook(savedInstanceState.getString("ISBN"));

        setContentView(R.layout.activity_show_book);

        EditText title = (EditText) findViewById(R.id.sbTitle);
        title.setText(book.getTitle());

        EditText isbn = (EditText) findViewById(R.id.sbISBN);
        isbn.setText(book.getIsbn());

        TextView modelCount = (TextView) findViewById(R.id.sbShowModelCount);

        String label = getResources().getString(R.string.modelLabel) + " "
                + book.getContents().size();

        modelCount.setText(label);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_book, menu);
        return true;
    }
}
