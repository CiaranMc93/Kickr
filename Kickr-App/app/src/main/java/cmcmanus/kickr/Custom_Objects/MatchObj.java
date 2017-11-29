package cmcmanus.kickr.Custom_Objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cmcmanus on 8/25/2017.
 */

public class MatchObj {
    //member variables
    private String time = "";
    private String homeTeam = "";
    private String awayTeam = "";
    private String venue = "";
    private String competition = "";
    private String date = "";
    private String homeTeamScore = "";
    private String awayTeamScore = "";
    private String winner = "";
    private int id = 0;

    public void Matches(JSONObject obj)
    {
        try
        {
            //create a hash of the following string
            this.setId(((String) obj.get("Home") + "-" + (String) obj.get("Away") + "-" + (String) obj.get("Competition") + "-" + (String) obj.get("Date")).hashCode());

            this.setHomeTeam((String) obj.get("Home"));
            this.setAwayTeam((String) obj.get("Away"));
            this.setCompetition((String) obj.get("Competition"));
            this.setDate((String) obj.get("Date"));

            if(obj.has("HomeScore"))
            {
                this.setHomeTeamScore((String)obj.get("HomeScore"));
                this.setAwayTeamScore((String)obj.get("AwayScore"));
                this.setWinner((String)obj.get("Winner"));

                //check if time is in the object
                if(obj.has("Time") && obj.has("Venue"))
                {
                    this.setTime((String) obj.get("Time"));
                    this.setVenue((String) obj.get("Venue"));
                }
            }
            else
            {
                this.setTime((String) obj.get("Time"));
                this.setVenue((String) obj.get("Venue"));
            }
        }
        catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getAwayTeamScore() {
        return awayTeamScore;
    }

    public void setAwayTeamScore(String awayTeamScore) {
        this.awayTeamScore = awayTeamScore;
    }

    public String getHomeTeamScore() {
        return homeTeamScore;
    }

    public void setHomeTeamScore(String homeTeamScore) {
        this.homeTeamScore = homeTeamScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}