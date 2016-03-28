package jvilam.com.controldegastos.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by jvilam on 23/03/2016.
 */
public class ExpensesDatabaseHelper extends SQLiteOpenHelper{

    // The index (key) column name for use in where clauses.
    public static final String KEY_ID = "_id";
    // The name and column index of each column in your database.
    // These should be descriptive.
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_AMOUNT = "AMOUNT";
    public static final String KEY_DATE = "DATE";
    public static final String KEY_DESCRIPTION = "DESCRIPTION";
    public static final String KEY_ADDRESS = "ADDRESS";

    private static final String DATABASE_NAME = "Expenses.db";
    public static final String DATABASE_TABLE = "expenses";
    private static final int DATABASE_VERSION = 1;

    // SQL Statement to create a new database.
    private static final String DATABASE_CREATE = "create table " +
    DATABASE_TABLE + " (" + KEY_ID +
            " integer primary key autoincrement, " +
            KEY_TYPE + " text not null, " +
            KEY_AMOUNT + " float, " +
            KEY_DATE + " date, " +
            KEY_DESCRIPTION + " text, " +
            KEY_ADDRESS + " text);";

    public ExpensesDatabaseHelper(Context context, String name,
                                  SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }
    // Called when there is a database version mismatch meaning that
    // the version of the database on disk needs to be upgraded to
    // the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // Log the version upgrade.
        Log.w("TaskDBAdapter", "Upgrading from version " +
                oldVersion + " to " +
                newVersion + ", which will destroy all old data");
        // Upgrade the existing database to conform to the new
        // version. Multiple previous versions can be handled by
        // comparing oldVersion and newVersion values.
        // The simplest case is to drop the old table and create a new one.
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        // Create a new one.
        onCreate(db);
    }
}
