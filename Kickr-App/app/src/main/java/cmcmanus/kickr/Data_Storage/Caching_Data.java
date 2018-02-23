package cmcmanus.kickr.Data_Storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cmcmanus on 11/27/2017.
 */

public class Caching_Data
{
    /*
    //store the match information so that it can be easily retrieved in the future
    private void matchInfoStorage(ArrayList<JSONObject> matchList)
    {
        try
        {
            if(db.getAllContacts(county).size() <= matchList.size())
            {
                for(int i = 0; i< matchList.size();i++)
                {
                    String homeTeam = matchList.get(i).getString("homeTeam");
                    String awayTeam = matchList.get(i).getString("awayTeam");
                    Integer match_id = matchList.get(i).getInt("id");
                    String competition = matchList.get(i).getString("competition");
                    String venue = matchList.get(i).getString("venue");

                    //insert into database
                    db.insertMatch(i,homeTeam,awayTeam,match_id,competition,venue,county);
                }
            }
            else
            {
                //no need to insert
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //create an SQL cursor for the functionality of getting all the contacts
        ArrayList<JSONObject> matches = db.getAllContacts(county);

        //for each competition in todayMatches
        for(int i = 0; i<matches.size();i++)
        {

        }
    }*/
}
