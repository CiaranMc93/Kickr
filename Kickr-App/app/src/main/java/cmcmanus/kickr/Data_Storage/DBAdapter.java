package cmcmanus.kickr.Data_Storage;

/**
 * Created by cmcmanus on 9/5/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cmcmanus.kickr.Custom_Objects.MatchObj;

/**
 * Created by ciaran on 05/09/2017.
 */
public class DBAdapter extends SQLiteOpenHelper
{
    //database version
    private static final int DATABASE_VERSION = 1;

    //database name
    private static final String DATABASE_NAME = "MatchDB";

    //table name
    private static final String MATCH_TABLE = "matches";
    private static final String DATE_TABLE = "db_date";

    //columns
    private static final String MATCH_ID = "match_ID";
    private static final String HOME_TEAM = "homeTeam";
    private static final String AWAY_TEAM = "awayTeam";
    private static final String COMP = "competition";
    private static final String LOC = "venue";
    private static final String COUNTY = "county";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String HOME_SCORE = "homeTeamScore";
    private static final String AWAY_SCORE = "awayTeamScore";
    private static final String WINNER = "winner";
    //DATE_TABLE
    private static final String DATE_ID = "id";
    private static final String DATE_VAL = "date_val";

    //Table creates
    private static final String CREATE_MATCHES_TABLE = "CREATE TABLE " + MATCH_TABLE + " ("
            + MATCH_ID + " INTEGER PRIMARY KEY," + HOME_TEAM + " TEXT,"
            + AWAY_TEAM + " TEXT," + COMP + " TEXT," + LOC + " TEXT,"
            + COUNTY + " TEXT," + DATE + " TEXT," + TIME + " TEXT, " + HOME_SCORE + " TEXT," + AWAY_SCORE + " TEXT," + WINNER + " TEXT" + ")";

    private static final String CREATE_DATE_TABLE = "CREATE TABLE " + DATE_TABLE + " ("
            + DATE_ID + " INTEGER PRIMARY KEY," + DATE_VAL + " TEXT"+ ")";

    private static final String TAG = "DBAdapter";

    //database variables
    private Context context;
    private DBAdapter DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_MATCHES_TABLE);
        db.execSQL(CREATE_DATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion < newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + MATCH_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATE_TABLE);
            onCreate(db);
        }
    }

    // Adding new match
    public void insertMatch(MatchObj match)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        try
        {
            db.execSQL("INSERT INTO " + MATCH_TABLE +
                    "("         + MATCH_ID +        ","           + HOME_TEAM +                                          ","           + AWAY_TEAM +                                          ","     + COMP +                                                         ","           + LOC +                                            ","           + COUNTY +                    ","       + DATE +                      ","       + TIME +                      ","       + HOME_SCORE +                            ","       + AWAY_SCORE +                            ","       + WINNER + ") " +
                    "VALUES ("  + match.getId() +   ","     + "'" + match.getHomeTeam().replaceAll("'","''") + "'" +     ","     + "'" + match.getAwayTeam().replaceAll("'","''") + "'" +     ","     + "'" + match.getCompetition().replaceAll("'","''") + "'" +      ","     + "'" + match.getVenue().replaceAll("'","''") + "'" +    ","     + "'" + match.getCounty() + "'" +   "," + "'" + match.getDate() + "'" +     "," + "'" + match.getTime() + "'" +     "," + "'" + match.getHomeTeamScore() + "'" +        "," + "'" + match.getAwayTeamScore() + "'" +        "," + "'" + match.getWinner().replaceAll("'","''") + "'" + ")");
        }
        catch (SQLException e)
        {
            System.out.print("Matches already exist is Database: " + e);
        }

       db.close(); // Closing database connection
    }

    // Getting single match
    public MatchObj getMatch(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(MATCH_TABLE, new String[] { MATCH_ID,
                        HOME_TEAM, AWAY_TEAM }, MATCH_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        try
        {
            //create match JSON object first
            JSONObject match = new JSONObject();
            match.put("information", "test");
            match.put("id", 3);
            match.put("name", "course1");

            //MatchObj matchObj = new MatchObj(Integer.parseInt(cursor.getString(0)),
                   // cursor.getString(1), cursor.getString(2));
        }
        catch (JSONException e)
        {

        }
        MatchObj matchObj = null;
        // return match
        return matchObj;
    }

    // Getting All matches
    public ArrayList<MatchObj> getAllMatches(String county)
    {
        ArrayList<MatchObj> listObj = new ArrayList<MatchObj>();

        String selectQuery = "SELECT * FROM " + MATCH_TABLE + " WHERE " + COUNTY + " = '" + county + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst())
        {
            do
            {
                MatchObj mObj = new MatchObj();

                //retrieve the data from the database
                mObj.setId(c.getInt((c.getColumnIndex(MATCH_ID))));
                mObj.setHomeTeam((c.getString(c.getColumnIndex(HOME_TEAM))));
                mObj.setAwayTeam(c.getString(c.getColumnIndex(AWAY_TEAM)));
                mObj.setCompetition(c.getString(c.getColumnIndex(COMP)));
                mObj.setVenue(c.getString(c.getColumnIndex(LOC)));
                mObj.setCounty(c.getString(c.getColumnIndex(COUNTY)));
                mObj.setDate(c.getString(c.getColumnIndex(DATE)));
                mObj.setHomeTeamScore(c.getString(c.getColumnIndex(HOME_SCORE)));
                mObj.setAwayTeamScore(c.getString(c.getColumnIndex(AWAY_SCORE)));
                mObj.setWinner(c.getString(c.getColumnIndex(WINNER)));

                // adding to todo list
                listObj.add(mObj);

            } while (c.moveToNext());
        }

        return listObj;
    }

    // Getting matches Count
    public int getMatchesCount(String county)
    {
        int count = 0;

        String selectQuery = "SELECT * FROM " + MATCH_TABLE + " WHERE " + COUNTY + " = '" + county + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst())
        {
            do
            {
                count++;
            }
            while (c.moveToNext());
        }

        return count;
    }
    // Updating single match
    public int updateMatch(MatchObj match) {

        int success = 0;

        return success;
    }

    // Deleting single contact
    public void deleteMatch(MatchObj match) {

    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("matches",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    // Adding new mdate
    public void insertDate(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("INSERT INTO " + DATE_TABLE +
                "("         + DATE_ID +        ","           + DATE_VAL + ") " +
                "VALUES ("  + 0 +              ","     + "'" + date + "'" + ")");

        db.close(); // Closing database connection
    }

    // Adding new mdate
    public void updateDate(String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + DATE_TABLE + " SET " + DATE_VAL + " = " + "'" + date + "'" + " WHERE " + DATE_ID + " = 0");

        db.close(); // Closing database connection
    }

    // Adding new match
    public String getDate()
    {
        String selectQuery = "SELECT * FROM " + DATE_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        String date = "";

        try
        {
            Cursor c = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (c.moveToFirst())
            {
                do
                {
                    //retrieve the data from the database
                    int i = c.getInt(0);
                    date = c.getString(1);

                } while (c.moveToNext());
            }
        }
        catch (SQLException e)
        {
            Log.e("DATABASE ERROR","Get Date Database Error: " + e);
        }

        return date;
    }
}
