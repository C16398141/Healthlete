package fyp.c16398141.healthlete;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static java.lang.String.valueOf;

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
    private static final int DATABASE_VERSION = 5;

    private static final String foreignKeyCheck =
            "PRAGMA foreign_keys = ON;";

    private static final String foreignKeyOff =
            "PRAGMA foreign_keys = OFF;";

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
                    "weight real not null, " +
                    "repetitions integer not null, " +
                    "sets integer not null, " +
                    "repmax real not null," +
                    "date text not null," +
                    "user_id text not null, " +
                    "FOREIGN KEY (exercisename)" +
                    "REFERENCES Exercise (exercisename)," +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";

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
                    "user_id text not null, " +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";

    private static final String CreateWorkoutAvailabilityTable =
            "create table if not exists WorkoutAvailability (slot_id integer primary key autoincrement, " +
                    "day text not null, " +
                    "type text not null, " +
                    "opening_time integer not null, " +
                    "closing_time integer not null, " +
                    "reminder integer not null," +
                    "area_id integer not null, " +
                    "FOREIGN KEY (area_id)" +
                    "REFERENCES WorkoutArea (area_id));";

    private static final String CreateAchievementTable =
            "create table if not exists Achievement (achievement_id integer primary key autoincrement, " +
                    "title text not null, " +
                    "description text not null, " +
                    "expected_time_frame text not null, " +
                    "difficulty integer not null," +
                    "date_created text not null," +
                    "date_completed text," +
                    "user_id text not null, " +
                    "FOREIGN KEY (user_id)" +
                    "REFERENCES User (user_id));";


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
            db.execSQL(CreateAchievementTable);
            Log.i("tags", "TABLES CREATED");
        }

        @Override
        //
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            // dB structure change..
            db.execSQL(foreignKeyOff);
            db.execSQL("drop table if exists User;");
            db.execSQL("drop table if exists FoodEntry;");
            db.execSQL("drop table if exists Exercise;");
            db.execSQL("drop table if exists WorkoutEntry;");
            db.execSQL("drop table if exists Goal;");
            db.execSQL("drop table if exists WorkoutArea;");
            db.execSQL("drop table if exists WorkoutAvailablity;");
            db.execSQL("drop table if exists Achievement;");
            onCreate(db);
        }
    }


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

        String whereClause = "entry_id=?";
        String whereArgs[] = {rowId.toString()};

        long result = db.delete("FoodEntry", whereClause, whereArgs);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllFoodEntries(String selected_date, String username) {
        Log.i("DATE DB", selected_date);
        Cursor data = db.rawQuery("SELECT * FROM FoodEntry WHERE date LIKE " + selected_date + " AND user_id LIKE '" + username + "' ORDER BY foodname DESC;", null);
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

    public Cursor getGoals(String user_id) {

        Cursor data = db.rawQuery("SELECT * FROM Goals WHERE user_id LIKE '" + user_id + "' ;", null);
        return data;
    }

    public boolean deleteGoals(String user_id) {

        String whereClause = "user_id=?";
        String whereArgs[] = {user_id};

        long result = db.delete("Goals", whereClause, whereArgs);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
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

    public boolean addWorkoutAvailability(String day, String type, Integer opening_time, Integer closing_time, Integer reminder, Integer area_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("day", day);
        initialValues.put("type", type);
        initialValues.put("opening_time", opening_time);
        initialValues.put("closing_time", closing_time);
        initialValues.put("reminder", reminder);
        initialValues.put("area_id", area_id);
        long result = db.insert("WorkoutAvailability", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public Cursor getWorkoutArea(String username) {
        Log.i("Area", "Selected");
        Cursor data = db.rawQuery("SELECT * FROM WorkoutArea WHERE user_id LIKE '" + username + "';", null);
        return data;
    }

    public Cursor getWorkoutAvailability(int area_id) {
        Cursor data = db.rawQuery("SELECT * FROM WorkoutAvailability WHERE area_id LIKE '" + area_id + "';", null);
        return data;
    }

    public boolean updateWorkoutAvailability(int slot_id, int reminder){
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("slot_id", slot_id);
        updatedValues.put("reminder", reminder);
        boolean result = db.update("WorkoutAvailability", updatedValues, "slot_id" + "=" + slot_id, null)>0;
        return result;
    }

    public void deletePreviousWorkoutArea() {

        db.execSQL("delete from WorkoutArea;");
    }

    public boolean addExercise(String exercisename, String musclegroup, String user_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("exercisename", exercisename);
        initialValues.put("musclegroup", musclegroup);
        initialValues.put("user_id", user_id);
        long result = db.insert("Exercise", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllExercises(String username) {
        Cursor data = db.rawQuery("SELECT * FROM Exercise WHERE user_id LIKE '" + username + "';", null);
        return data;
    }

    public boolean addWorkoutEntry(String exercisename, Double weight, Integer reps, Integer sets, Double repmax, String date, String user_id) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("exercisename", exercisename);
        initialValues.put("weight", weight);
        initialValues.put("repetitions", reps);
        initialValues.put("sets", sets);
        initialValues.put("repmax", repmax);
        initialValues.put("date", date);
        initialValues.put("user_id", user_id);
        long result = db.insert("WorkoutEntry", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getExerciseEntries(String exercisename, String username) {
        Cursor data = db.rawQuery("SELECT * FROM WorkoutEntry WHERE user_id LIKE '" + username + "' AND exercisename LIKE '" + exercisename + "' ORDER BY date DESC;", null);
        return data;
    }

    public String getExerciseName(Integer e_id) {
        Log.i("In DB",valueOf(e_id));
        Cursor data = db.rawQuery("SELECT exercisename FROM Exercise WHERE exercise_id = " + e_id + ";", null);
        data.moveToFirst();
        String name = data.getString(0);
        return name;
    }

    public Cursor getWorkoutEntry(String date, String username) {
        Cursor data = db.rawQuery("SELECT * FROM WorkoutEntry WHERE user_id LIKE '" + username + "' AND date LIKE '" + date + "' ORDER BY weight DESC;", null);
        return data;
    }

    public boolean deleteExercise(Integer rowId) {

        String whereClause = "exercise_id=?";
        String whereArgs[] = {rowId.toString()};

        long result = db.delete("Exercise", whereClause, whereArgs);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteWorkoutEntry(Integer rowId) {

        String whereClause = "entry_id=?";
        String whereArgs[] = {rowId.toString()};

        long result = db.delete("WorkoutEntry", whereClause, whereArgs);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean addAchievement(String name, String description, String expected_time_frame, Integer difficulty, String date_created, String user) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("title", name);
        initialValues.put("description", description);
        initialValues.put("expected_time_frame", expected_time_frame);
        initialValues.put("difficulty", difficulty);
        initialValues.put("date_created", date_created);
        initialValues.put("date_completed", "TBD");
        initialValues.put("user_id", user);
        long result = db.insert("Achievement", null, initialValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean completeAchievement(int achievement_id, String time){
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("date_completed", time);
        boolean result = db.update("Achievement", updatedValues, "achievement_id" + "=" + achievement_id, null)>0;
        return result;
    }

    public Cursor getUncompletedAchievements(String username) {
        Cursor data = db.rawQuery("SELECT * FROM Achievement WHERE user_id LIKE '" + username + "' AND date_completed LIKE 'TBD' ORDER BY date_created DESC;", null);
        return data;
    }

    public Cursor getCompletedAchievements(String username) {
        Cursor data = db.rawQuery("SELECT * FROM Achievement WHERE user_id LIKE '" + username + "' AND date_completed NOT LIKE 'TBD' ORDER BY date_created DESC;", null);
        return data;
    }

    public Cursor getAchievement(Integer id) {
        Cursor data = db.rawQuery("SELECT * FROM Achievement WHERE achievement_id = " + id + ";", null);
        return data;
    }

    public boolean deleteAchievement(Integer achievement_id) {

        String whereClause = "achievement_id=?";
        String whereArgs[] = {achievement_id.toString()};

        long result = db.delete("Achievement", whereClause, whereArgs);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
}