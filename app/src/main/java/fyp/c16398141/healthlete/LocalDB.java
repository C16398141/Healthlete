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

    private static final String foreignKeyCheck =
            "PRAGMA foreign_keys = ON;";
    // SQL statement to create the database
    private static final String CreateUserTable =
            "create table if not exists User (user_id text primary key, " +
                    "firstname text not null, " +
                    "surname text, " +
                    "dob text, " +
                    "height real, " +
                    "weight real);";

    private static final String CreateFoodEntryTable =
            "create table if not exists FoodEntry (entry_id integer primary key autoincrement, " +
                    "foodname text not null, " +
                    "quantity integer not null, " +
                    "qty_type text not null, " +
                    "date text not null, " +
                    "cals_per_qty integer not null, " +
                    "carbs_per_qty integer not null, " +
                    "protein_per_qty integer not null, " +
                    "user_id text not null, " +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";
    //later on make food table with nutrient per qty values and take from them - remove from entry table

    private static final String CreateExerciseTable =
            "create table if not exists Exercise (exercise_id integer primary key autoincrement, " +
                    "exercisename text not null, " +
                    "musclegroup text not null, " +
                    "image blob," +
                    "user_id text not null, " +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";

    private static final String CreateWorkoutEntryTable =
            "create table if not exists WorkoutEntry (entry_id integer primary key autoincrement, " +
                    "exercisename text not null, " +
                    "weight real unique not null, " +
                    "sets integer not null, " +
                    "repetitions integer not null, " +
                    "repmax real not null," +
                    "date text not null," +
                    "user_id text not null, " +
                    "FOREIGN KEY (exercisename)" +
                    "REFERENCES Exercise (exercisename)," +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";
    //add running/aerobic exercise formats and sports with duration perhaps

    private static final String CreateGoalTable =
            "create table if not exists Goals (entry_id integer primary key autoincrement, " +
                    "type text not null, " +
                    "targetCals integer not null, " +
                    "targetCarbs integer not null, " +
                    "targetProtein integer not null," +
                    "user_id text not null, " +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";

    private static final String CreateWorkoutAreaTable =
            "create table if not exists WorkoutArea (area_id integer primary key autoincrement, " +
                    "place_id text not null, " +
                    "name text not null, " +
                    "latitude real not null, " +
                    "longitude real not null, " +
                    "times int not null, " +
                    "user_id text not null);";
                    /*, " +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";*/

    private static final String CreateWorkoutAvailabilityTable =
            "create table if not exists WorkoutAvailability (slot_id integer primary key autoincrement, " +
                    "day text not null, " +
                    "type text not null, " +
                    "opening_time integer not null, " +
                    "closing_time integer not null, " +
                    "area_id integer not null, " +
                    "FOREIGN KEY (area_id)" +
                    "REFERENCES WorkoutArea (area_id));";


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
            db.execSQL(foreignKeyCheck);
            db.execSQL(CreateUserTable);
            db.execSQL(CreateFoodEntryTable);
            db.execSQL(CreateExerciseTable);
            db.execSQL(CreateWorkoutEntryTable);
            db.execSQL(CreateGoalTable);
            db.execSQL(CreateWorkoutAreaTable);
            db.execSQL(CreateWorkoutAvailabilityTable);
            Log.i("tags", "TABLES CREATED");
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

    public boolean addUser(String email, String firstname, String surname) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("user_id", email);
        initialValues.put("firstname", firstname);
        initialValues.put("surname", surname);
        long result = db.insert("User", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getUsers() {
        Cursor data = db.rawQuery("SELECT user_id FROM User ORDER BY user_id DESC;", null);
        return data;
    }

    public boolean addFoodEntry(String foodname, Integer quantity, String qty_type, String date, Integer cals_per_qty, Integer carbs_per_qty, Integer proteins_per_qty, String user_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("foodname", foodname);
        initialValues.put("quantity", quantity);
        initialValues.put("qty_type", qty_type);
        initialValues.put("date", date);
        initialValues.put("cals_per_qty", cals_per_qty);
        initialValues.put("carbs_per_qty", carbs_per_qty);
        initialValues.put("protein_per_qty", proteins_per_qty);
        initialValues.put("user_id", user_id);
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

    public Cursor getAllFoodEntries(String selected_date) {
        //String selected_date = "2232020";
        Log.i("DATE DB", selected_date);
        Cursor data = db.rawQuery("SELECT * FROM FoodEntry WHERE date LIKE " + selected_date + " ORDER BY foodname DESC;", null);
        //Cursor data = db.rawQuery("SELECT * FROM FoodEntry ORDER BY foodname DESC;", null);
        return data;
    }

    //make updateGoals
    public boolean addGoals(String type, Integer targetCals, Integer targetCarbs, Integer targetProtein, String user_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("type", type);
        initialValues.put("targetCals", targetCals);
        initialValues.put("targetCarbs", targetCarbs);
        initialValues.put("targetProtein", targetProtein);
        initialValues.put("user_id", user_id);
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

    public long addWorkoutArea(String place_id, String name, Double lat, Double lng, Integer times, String user_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("place_id", place_id);
        initialValues.put("name", name);
        initialValues.put("latitude", lat);
        initialValues.put("longitude", lng);
        initialValues.put("times", times);
        initialValues.put("user_id", user_id);
        long result = db.insert("WorkoutArea", null, initialValues);
        return result;
    }

    public boolean addWorkoutAvailability(String day, String type, Integer opening_time, Integer closing_time, Integer area_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("day", day);
        initialValues.put("type", type);
        initialValues.put("opening_time", opening_time);
        initialValues.put("closing_time", closing_time);
        initialValues.put("area_id", area_id);
        long result = db.insert("WorkoutAvailability", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Cursor getWorkoutArea() {
        String username = "2013chrisclarke@gmail.com";
        Log.i("Area", "Selected");
        Cursor data = db.rawQuery("SELECT * FROM WorkoutArea WHERE user_id LIKE '" + username + "';", null);
        //Cursor data = db.rawQuery("SELECT * FROM FoodEntry ORDER BY foodname DESC;", null);
        return data;
    }

    public Cursor getWorkoutAvailability(int area_id) {
        String username = "2013chrisclarke@gmail.com";
        Cursor data = db.rawQuery("SELECT * FROM WorkoutAvailability WHERE area_id LIKE " + area_id + ";", null);
        return data;
    }

    public void deletePreviousWorkoutArea() {

        db.execSQL("delete from WorkoutArea;");
    }

    public long addExercise(String exercisename, String musclegroup, String user_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("exercisename", exercisename);
        initialValues.put("musclegroup", musclegroup);
        initialValues.put("user_id", user_id);
        long result = db.insert("Exercise", null, initialValues);
        return result;
    }

    public Cursor getAllExercises() {
        String username = "2013chrisclarke@gmail.com";
        Cursor data = db.rawQuery("SELECT * FROM Exercise WHERE user_id LIKE " + username + ";", null);
        return data;
    }

    public long addWorkoutEntry(String exercisename, Float weight, Integer reps, Integer sets, Float repmax, String date, String user_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("exercisename", exercisename);
        initialValues.put("weight", weight);
        initialValues.put("repetitions", reps);
        initialValues.put("sets", sets);
        initialValues.put("repmax", repmax);
        initialValues.put("date", date);
        initialValues.put("user_id", user_id);
        long result = db.insert("WorkoutEntry", null, initialValues);
        return result;
    }

    public Cursor getExerciseEntries() {
        String username = "2013chrisclarke@gmail.com";
        String exercisename = "Benchpress";
        Cursor data = db.rawQuery("SELECT * FROM WorkoutEntry WHERE user_id LIKE " + username + "AND exercisename LIKE " + exercisename + " ORDER BY date DESC;", null);
        return data;
    }

    public Cursor getWorkoutEntry(String date) {
        String username = "2013chrisclarke@gmail.com";
        Cursor data = db.rawQuery("SELECT * FROM WorkoutEntry WHERE user_id LIKE " + username + "AND date LIKE " + date + " ORDER BY weight DESC;", null);
        return data;
    }
}