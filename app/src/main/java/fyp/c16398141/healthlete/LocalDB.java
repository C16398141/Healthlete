package fyp.c16398141.healthlete;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDB
{

    // database columns
    private static final String KEY_ROWID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_RANK = "rank";
    private static final String KEY_POINTS = "points";
    private static final String KEY_DOB = "DOB";
    private static final String KEY_nationality = "nationality";
    private static final String DATABASE_NAME = "Players1";
    private static final String DATABASE_TABLE 	= "Contact_Details2";
    private static final String KEY_TNAME 	= "tournamentname";
    private static final int DATABASE_VERSION 	= 1;

    // SQL statement to create the database
    private static final String CreateUserTable =
            "create table User (user_id integer primary key autoincrement, " +
                    "firstname text unique not null, " +
                    "surname text unique not null, " +
                    "dob text not null, " +
                    "height integer not null);";

    private static final String CreateFoodEntryTable =
            "create table FoodEntry (entry_id integer primary key autoincrement, " +
                    "foodname text unique not null, " +
                    "quantity integer not null, " +
                    "date text not null, "  +
                    "nationality text not null);";

    private static final String DATABASE_CREATE2 =
            "create table Points (_id integer primary key autoincrement, " +
                    "tournamentname text not null, " +
                    "playername text unique not null, " +
                    "points integer not null, " +
                    "date1 text not null);";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    // Constructor
    public LocalDB(Context ctx)
    {
        //
        this.context 	= ctx;
        DBHelper 		= new DatabaseHelper(context);
    }

    public LocalDB open() throws SQLException
    {
        db     = DBHelper.getWritableDatabase();
        return this;
    }

    // nested dB helper class
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        //
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        //
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CreateUserTable);
            db.execSQL(CreateFoodEntryTable);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override
        //
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {
            // dB structure change..
            db.execSQL("drop table if exists Contact_Details2;");
            db.execSQL("drop table if exists Points;");
        }
    }   // end nested class


    public void close()
    {
        DBHelper.close();
    }

    public boolean insertPerson(String name1, Integer points, String DOB, String nationality)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name1);
        initialValues.put(KEY_RANK, 100);
        initialValues.put(KEY_POINTS, points);
        initialValues.put(KEY_DOB, DOB);
        initialValues.put(KEY_nationality, nationality);
        long result = db.insert(DATABASE_TABLE, null, initialValues);
        if (result==-1) {
            return false;
        }
        else{
            return true;
        }
    }


    public boolean deletePerson(long rowId)
    {
        return true;
    }

    public Cursor getAllPeople() {

        Cursor data = db.rawQuery("SELECT * FROM Contact_Details2 ORDER BY name DESC;", null);
        /**db.delete("DATABASE_TABLE", null, null);*/
        return data;

    }

    public Cursor getAllPeople2() {

        Cursor data = db.rawQuery("SELECT * FROM Contact_Details2 ORDER BY points DESC;", null);
        /**db.delete("DATABASE_TABLE", null, null);*/
        return data;

    }

    public Cursor getAllTournaments(String playername) {

        Cursor data = db.rawQuery("SELECT * FROM Points WHERE playername LIKE '" + playername +"' ORDER BY tournamentname DESC;", null);
        /**db.delete("DATABASE_TABLE", null, null);*/
        return data;

    }


    //
    public boolean updatePoints(String tournamentname, String playername, Integer points, String date1)
    {

        ContentValues args = new ContentValues();
        args.put("tournamentname", tournamentname);
        args.put("playername", playername);
        args.put("points", points);
        args.put("date1", date1);
        long result= db.update("Points", args,
                "playername" + "=" + playername + "AND" + "tournamentname" + "=" + tournamentname, null);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertPoints(String tournamentname, String playername, Integer points, String date1)
    {
        ContentValues args = new ContentValues();
        args.put("tournamentname", tournamentname);
        args.put("playername", playername);
        args.put("points", points);
        args.put("date1", date1);
        long result = db.insert("Points", null, args);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
}
