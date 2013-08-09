package net.jnwd.origamiData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.jnwd.origamiFinder.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class ModelTable {
	private static final String TAG = "ModelDatabase";

	public static final String COL_MODEL_NAME = "MODELNAME";
	public static final String COL_MODEL_TYPE = "MODELTYPE";
	public static final String COL_CREATOR = "CREATOR";
	public static final String COL_BOOK_TITLE = "BOOKTITLE";
	public static final String COL_ISBN = "ISBN";
	public static final String COL_ON_PAGE = "ONPAGE";
	public static final String COL_DIFFICULTY = "DIFFICULTY";
	public static final String COL_PAPER = "PAPER";
	public static final String COL_PIECES = "PIECES";
	public static final String COL_GLUE = "GLUE";
	public static final String COL_CUTS = "CUTS";

	public static final String[] allColumns = {
		"MODELNAME",
		"MODELTYPE",
		"CREATOR",
		"BOOKTITLE",
		"ISBN",
		"ONPAGE",
		"DIFFICULTY",
		"PAPER",
		"PIECES",
		"GLUE",
		"CUTS"
	};

	private static final String DATABASE_NAME = "MODELS";
	private static final String FTS_VIRTUAL_TABLE = "FTS";
	private static final int DATABASE_VERSION = 1;

	private final DatabaseOpenHelper mDatabaseOpenHelper;

	public ModelTable(Context context) {
		mDatabaseOpenHelper = new DatabaseOpenHelper(context);
	}

	private static class DatabaseOpenHelper extends SQLiteOpenHelper {
		private final Context mHelperContext;
		private SQLiteDatabase mDatabase;

		private static final String FTS_TABLE_CREATE = "" +
			"CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " " +
			"USING fts3 (" +
			COL_MODEL_NAME + ", " +
			COL_MODEL_TYPE + ", " +
			COL_CREATOR + ", " +
			COL_BOOK_TITLE + ", " +
			COL_ISBN + ", " +
			COL_ON_PAGE + ", " +
			COL_DIFFICULTY + ", " +
			COL_PAPER + ", " +
			COL_PIECES + ", " +
			COL_GLUE + ", " +
			COL_CUTS + ")";

		DatabaseOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

			mHelperContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			mDatabase = db;

			mDatabase.execSQL(FTS_TABLE_CREATE);

			loadDatabase();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);

			onCreate(db);
		}

		private void loadDatabase() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						loadModels();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}).start();
		}

		private void loadModels() throws IOException {
			final Resources resources = mHelperContext.getResources();

			InputStream inputStream = resources.openRawResource(R.raw.model);

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			try {
				String line;
				Model model;

				while ((line = reader.readLine()) != null) {
					model = new Model(line);

					long id = addModel(model);

					if (id < 0) {
						Log.e(TAG, "unable to add model: " + model.toString());
					}
				}
			} finally {
				reader.close();
			}
		}

		public long addModel(Model model) {
			ContentValues initialValues = new ContentValues();

			initialValues.put(COL_MODEL_NAME, model.name);
			initialValues.put(COL_MODEL_TYPE, model.modelType);
			initialValues.put(COL_CREATOR, model.creator);
			initialValues.put(COL_BOOK_TITLE, model.bookTitle);
			initialValues.put(COL_ISBN, model.ISBN);
			initialValues.put(COL_ON_PAGE, model.page);
			initialValues.put(COL_DIFFICULTY, model.difficulty);
			initialValues.put(COL_PAPER, model.paper);
			initialValues.put(COL_PIECES, model.pieces);
			initialValues.put(COL_GLUE, model.glue);
			initialValues.put(COL_CUTS, model.cuts);

			return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
		}
	}

	public Cursor getModelMatches(String query, String[] columns) {
		String selection = COL_MODEL_NAME + " MATCH ?";

		String[] selectionArgs = new String[] {
												query + "*"
		};

		return query(selection, selectionArgs, columns);
	}

	private Cursor query(String selection, String[] selectionArgs, String[] columns) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(FTS_VIRTUAL_TABLE);

		Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(), columns, selection, selectionArgs, null, null, null);

		if (cursor == null) {
			return null;
		} else if (! cursor.moveToFirst()) {
			cursor.close();

			return null;
		}

		return cursor;
	}
}
