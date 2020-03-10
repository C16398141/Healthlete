package fyp.c16398141.healthlete;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDB {

    // database columns
    private static final String KEY_ROWID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_RANK = "rank";
    private static final String KEY_POINTS = "pointss";
    private static final String KEY_DOB = "DOB";
    private static final String KEY_nationality = "nationality";
    private static final String DATABASE_NAME = "Players1";
    private static final String DATABASE_TABLE = "Contact_Details2";
    private static final String KEY_TNAME = "tournamentname";
    private static final int DATABASE_VERSION = 1;

    // SQL statement to create the database
    private static final String CreateUserTable =
            "create table if not exists User (user_id integer primary key autoincrement, " +
                    "firstname text unique not null, " +
                    "surname text unique not null, " +
                    "dob text not null, " +
                    "height integer not null);";

    private static final String CreateFoodEntryTable =
            "create table if not exists FoodEntry (entry_id integer primary key autoincrement, " +
                    "foodname text unique not null, " +
                    "quantity integer not null, " +
                    "date text not null, " +
                    "cals_per_qty integer not null, " +
                    "carbs_per_qty integer not null, " +
                    "protein_per_qty integer not null);";
    //later on make food table with nutrient per qty values and take from them - remove from entry table

    private static final String CreateWorkoutEntryTable =
            "create table if not exists WorkoutEntry (entry_id integer primary key autoincrement, " +
                    "exercisename text not null, " +
                    "weight integer unique not null, " +
                    "sets integer not null, " +
                    "repetitions integer not null, " +
                    "date text not null);";

    private static final String CreateGoalTable =
            "create table if not exists Goals (entry_id integer primary key autoincrement, " +
                    "type text not null, " +
                    "targetCals integer not null, " +
                    "targetCarbs integer not null, " +
                    "targetProtein integer not null);";
    //add running/aerobic exercise formats and sports with duration perhaps

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    // Constructor
    public LocalDB(Context ctx) {
        //
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public LocalDB open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // nested dB helper class
    private static class DatabaseHelper extends SQLiteOpenHelper {
        //
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        //
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CreateUserTable);
            db.execSQL(CreateFoodEntryTable);
            db.execSQL(CreateWorkoutEntryTable);
            db.execSQL(CreateGoalTable);
            Log.i("tags","TABLES CREATED");
        }

        @Override
        //
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            // dB structure change..
            db.execSQL("drop table if exists Contact_Details2;");
            db.execSQL("drop table if exists Points;");
        }
    }  // end nested class


    public void close() {
        DBHelper.close();
    }

    public boolean addFoodEntry(String foodname, Integer quantity, String date, Integer cals_per_qty, Integer carbs_per_qty, Integer proteins_per_qty) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("foodname", foodname);
        initialValues.put("quantity", quantity);
        initialValues.put("date", date);
        initialValues.put("cals_per_qty", cals_per_qty);
        initialValues.put("carbs_per_qty", carbs_per_qty);
        initialValues.put("protein_per_qty", proteins_per_qty);
        long result = db.insert("FoodEntry", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteFoodEntry(Integer rowId) {
        //String strId = String.valueOf(rowId);
        String whereClause = "entry_id=?";
        String whereArgs[] = {rowId.toString()};
        //new String[] { strId }
        long result = db.delete("FoodEntry", whereClause, whereArgs);
        //long result = db.execSQL(DELETE FROM TABLE);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllFoodEntries() {

        Cursor data = db.rawQuery("SELECT * FROM FoodEntry ORDER BY foodname DESC;", null);
        return data;
    }

    //make updateGoals
    public boolean addGoals(String type, Integer targetCals, Integer targetCarbs, Integer targetProtein) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("type", type);
        initialValues.put("targetCals", targetCals);
        initialValues.put("targetCarbs", targetCarbs);
        initialValues.put("targetProtein", targetProtein);
        long result = db.insert("Goals", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getGoals() {

        Cursor data = db.rawQuery("SELECT * FROM Goals;", null);
        return data;
    }
}