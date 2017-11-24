package cmcmanus.kickr.DBAdapter;

/**
 * Created by cmcmanus on 9/5/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ciaran on 05/09/2017.
 */
public class DBAdapter extends SQLiteOpenHelper{

    private static final String ID = "Id";
    private static final String MATCH_ID = "match_ID";
    private static final String HOME_TEAM = "homeTeam";
    private static final String AWAY_TEAM = "awayTeam";
    private static final String COMP = "competition";
    private static final String LOC = "venue";
    private static final String TIME = "time";
    private static final String DATE = "date";
    private static final String DATABASE_TABLE = "matches";
    private static final String DATABASE_NAME = "MatchDB";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "DBAdapter";
    private static final String DATABASE = "CREATE TABLE " + DATABASE_TABLE + " (ID integer primary key AUTOINCREMENT, " +
            "homeTeam varchar(255) not null, AWAY_TEAM varchar(255) not null, COMP varchar(255) not null, LOC varchar(255) not null, match_id int not null);";

    //database variables
    private Context context;
    private DBAdapter DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table matches " +
                        "(match_ID integer primary key, homeTeam text,awayTeam text,competition text,venue text,county text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion < newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS matches");
            onCreate(db);
        }
    }

    public boolean insertMatch(Integer id, String home, String away, Integer ID, String comp,String loc,String county) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("homeTeam", home);
            contentValues.put("awayTeam", away);
            contentValues.put("match_ID", ID);
            contentValues.put("competition", comp);
            contentValues.put("venue", loc);
        contentValues.put("county", county);
            db.insert("matches", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from matches where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DATABASE_TABLE);
        return numRows;
    }

    public boolean updateContact (Integer id, String home, String away, Integer ID, String comp,String loc,String county) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("homeTeam", home);
        contentValues.put("awayTeam", away);
        contentValues.put("match_ID", ID);
        contentValues.put("competition", comp);
        contentValues.put("venue", loc);
        contentValues.put("county", county);
        db.update("matches", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("matches",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<JSONObject> getAllContacts(String county) {
        ArrayList<JSONObject> match_list = new ArrayList<JSONObject>();

        JSONObject match = null;
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;

        try
        {
            res = db.rawQuery("SELECT * FROM " + DATABASE_TABLE,null);
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            return null;
        }

        res.moveToFirst();

        //here i can recreate the list of objects so it can be parsed into the application
        while(res.isAfterLast() == false)
        {
            match = new JSONObject();
            try
            {
                //create a JSONObject
                match.put("homeTeam",res.getString(res.getColumnIndex(HOME_TEAM)));
                match.put("awayTeam",res.getString(res.getColumnIndex(AWAY_TEAM)));
                match.put("competition",res.getString(res.getColumnIndex(COMP)));
                match.put("venue",res.getString(res.getColumnIndex(LOC)));
                match.put("id",res.getString(res.getColumnIndex(MATCH_ID)));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            match_list.add(match);
            res.moveToNext();
        }

        if(match_list.size() == 0)
        {
            return null;
        }

        if(null == match)
        {
            return null;
        }

        return match_list;
    }

}
