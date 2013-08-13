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
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class ModelTable {
	private static final String TAG = "ModelDatabase";
	private static final String KEY_ROWID = "_id";

	public static final String[] allColumns = {
		KEY_ROWID,
		Model.COL_MODEL_NAME,
		Model.COL_MODEL_TYPE,
		Model.COL_CREATOR,
		Model.COL_BOOK_TITLE,
		Model.COL_ISBN,
		Model.COL_ON_PAGE,
		Model.COL_DIFFICULTY,
		Model.COL_PAPER,
		Model.COL_PIECES,
		Model.COL_GLUE,
		Model.COL_CUTS
	};

	public static final String[] listColumns = {
		Model.COL_MODEL_NAME,
		Model.COL_CREATOR,
		Model.COL_BOOK_TITLE,
		Model.COL_ISBN,
		Model.COL_ON_PAGE,
		Model.COL_MODEL_TYPE,
		Model.COL_DIFFICULTY,
		Model.COL_PAPER,
		Model.COL_PIECES,
		Model.COL_GLUE,
		Model.COL_CUTS
	};

	private static final String DATABASE_NAME = "MODELS";
	private static final String FTS_VIRTUAL_TABLE = "FTS";
	private static final int DATABASE_VERSION = 1;

	private final Context mContext;

	private DatabaseOpenHelper mDatabaseOpenHelper;
	private SQLiteDatabase mDb;

	public ModelTable(Context context) {
		mContext = context;
	}

	public ModelTable open() throws SQLException {
		mDatabaseOpenHelper = new DatabaseOpenHelper(mContext);

		mDb = mDatabaseOpenHelper.getWritableDatabase();

		return this;
	}

	public void close() {
		if (mDatabaseOpenHelper != null) {
			mDatabaseOpenHelper.close();
		}
	}

	public void emptyDatabase() {
		Log.i(TAG, "About to pass the empty message along...");

		mDatabaseOpenHelper.emptyDatabase();
	}

	public void loadDatabase() {
		Log.i(TAG, "About to pass the load message along...");

		mDatabaseOpenHelper.loadDatabase();
	}

	public Cursor fetchAllModels() {
		Cursor mCursor = mDb.query(FTS_VIRTUAL_TABLE, listColumns, null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	public Cursor getModelMatches(String query) {
		return getModelMatches(query, listColumns);
	}

	public Cursor getModelMatches(String query, String[] columns) {
		String selection = Model.COL_MODEL_NAME + " MATCH ?";

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

	private static class DatabaseOpenHelper extends SQLiteOpenHelper {
		private final Context mHelperContext;
		private SQLiteDatabase mDatabase;

		private static final String FTS_TABLE_CREATE = "" +
			"CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " " +
			"USING fts3 (" +
			KEY_ROWID + ", " +
			Model.COL_MODEL_NAME + ", " +
			Model.COL_MODEL_TYPE + ", " +
			Model.COL_CREATOR + ", " +
			Model.COL_BOOK_TITLE + ", " +
			Model.COL_ISBN + ", " +
			Model.COL_ON_PAGE + ", " +
			Model.COL_DIFFICULTY + ", " +
			Model.COL_PAPER + ", " +
			Model.COL_PIECES + ", " +
			Model.COL_GLUE + ", " +
			Model.COL_CUTS + ")";

		DatabaseOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

			mHelperContext = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "About to perform the onCreate method...");

			mDatabase = db;

			mDatabase.execSQL(FTS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);

			onCreate(db);
		}

		public void emptyDatabase() {
			Log.i(TAG, "About to empty the database...Is the variable null?");

			Log.i(TAG, "mDatabase is: " + (mDatabase == null ? "Null" : "Not null"));

			mDatabase.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);

			onCreate(mDatabase);
		}

		public void loadDatabase() {
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

			initialValues.put(Model.COL_MODEL_NAME, model.name);
			initialValues.put(Model.COL_MODEL_TYPE, model.modelType);
			initialValues.put(Model.COL_CREATOR, model.creator);
			initialValues.put(Model.COL_BOOK_TITLE, model.bookTitle);
			initialValues.put(Model.COL_ISBN, model.ISBN);
			initialValues.put(Model.COL_ON_PAGE, model.page);
			initialValues.put(Model.COL_DIFFICULTY, model.difficulty);
			initialValues.put(Model.COL_PAPER, model.paper);
			initialValues.put(Model.COL_PIECES, model.pieces);
			initialValues.put(Model.COL_GLUE, model.glue);
			initialValues.put(Model.COL_CUTS, model.cuts);

			return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
		}
	}
}
