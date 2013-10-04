
package net.jnwd.origamiData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.jnwd.origamiFinder.R;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.widget.Toast;

public class ModelTable {
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

    public static final String[] contentsColumns = {
            KEY_ROWID,
            Model.COL_MODEL_NAME,
            Model.COL_MODEL_TYPE,
            Model.COL_CREATOR,
            Model.COL_ON_PAGE,
            Model.COL_DIFFICULTY,
            Model.COL_PAPER,
            Model.COL_PIECES,
            Model.COL_GLUE,
            Model.COL_CUTS
    };

    public static final String[] bookColumns = {
            Model.COL_BOOK_TITLE,
            Model.COL_ISBN
    };

    public static final String[] listColumns = {
            KEY_ROWID,
            Model.COL_MODEL_NAME,
            Model.COL_CREATOR,
            Model.COL_BOOK_TITLE,
            Model.COL_DIFFICULTY
    };

    private static final String DATABASE_NAME = "MODELS";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 3;

    private final Context mContext;

    private DatabaseOpenHelper mDatabaseOpenHelper;
    private SQLiteDatabase mDb;

    public ModelTable(Context context) {
        super();

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

    public Cursor fetchAllModels() {
        Cursor mCursor = mDb.query(FTS_VIRTUAL_TABLE, listColumns, null, null, null, null,
                Model.COL_MODEL_NAME);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public Cursor getModel(long id) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(FTS_VIRTUAL_TABLE);

        String selection = "_id = " + id;

        Cursor models = builder.query(mDatabaseOpenHelper.getReadableDatabase(), Model.allColumns,
                selection, null, null, null, Model.COL_ON_PAGE);

        if (models == null) {
            return null;
        } else if (!models.moveToFirst()) {
            models.close();

            return null;
        }

        return models;
    }

    public Cursor getBookByISBN(String isbn) {
        return mDb.query(FTS_VIRTUAL_TABLE, bookColumns, Model.COL_ISBN + " = '" + isbn + "'",
                null, null, null, null);
    }

    public Cursor getModelsByISBN(String isbn) {
        return mDb.query(FTS_VIRTUAL_TABLE, contentsColumns, Model.COL_ISBN + " = '" + isbn + "'",
                null, null, null, Model.COL_ON_PAGE);
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

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(), columns,
                selection, selectionArgs, null, null, Model.COL_MODEL_NAME);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();

            return null;
        }

        return cursor;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {
        private static String DB_Path = "";
        private static String DB_Name = DATABASE_NAME;

        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE = "" +
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE + " " +
                "USING fts3 (" +
                KEY_ROWID + " integer PRIMARY KEY autoincrement," +
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

            DB_Path = context.getFilesDir().getPath() + "/../databases/";

            mHelperContext = context;

            try {
                createDataBase();
            } catch (IOException ioe) {
                Toast.makeText(mHelperContext,
                        "Unable to create database. Please contact App Creator.",
                        Toast.LENGTH_LONG).show();
            }
        }

        private void createDataBase() throws IOException {
            if (checkDataBase()) {
                return;
            }

            this.getReadableDatabase();
            this.close();

            try {
                copyDataBase();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }

        private boolean checkDataBase() {
            String fullPath = DB_Path + DB_Name;

            File dbFile = new File(fullPath);

            return dbFile.exists();
        }

        private void copyDataBase() throws IOException {
            InputStream mInput = mHelperContext.getAssets().open(DB_Name);

            String outFileName = DB_Path + DB_Name;

            OutputStream mOutput = new FileOutputStream(outFileName);

            byte[] mBuffer = new byte[1024];
            int mLength;

            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }

            mOutput.flush();
            mOutput.close();
            mInput.close();
        }

        @Override
        public synchronized void close() {
            if (mDatabase != null) {
                mDatabase.close();
            }

            super.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            boolean bypass = true;

            if (bypass) {
                return;
            }

            mDatabase = db;

            mDatabase.execSQL(FTS_TABLE_CREATE);

            loadDatabase();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            boolean bypass = true;

            if (bypass) {
                return;
            }

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

                List<Model> allModels = new ArrayList<Model>();

                while ((line = reader.readLine()) != null) {
                    Model model = new Model(line);

                    if (!allModels.contains(model)) {
                        allModels.add(model);

                        addModel(model);
                    }
                }
            } finally {
                reader.close();
            }
        }

        public long addModel(Model model) {
            ContentValues initialValues = new ContentValues();

            initialValues.put("_id", model.id);
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
