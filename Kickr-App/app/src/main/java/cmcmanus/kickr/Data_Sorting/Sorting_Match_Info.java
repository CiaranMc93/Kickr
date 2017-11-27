package cmcmanus.kickr.Data_Sorting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cmcmanus.kickr.R;

/**
 * Created by cmcmanus on 11/27/2017.
 */

public class Sorting_Match_Info
{
    //time
    private Calendar cal = null;
    private String formatDateTime = "";

    //returned data
    private JSONArray todayMatches = null;
    private JSONArray yesterdayMatches = null;
    private JSONArray tomorrowMatches = null;
    private JSONArray earlierMatches = null;
    private JSONArray laterMatches = null;
    private JSONArray match_competitions = null;
    private JSONArray seniorFootball = null;

    //constructed data
    private JSONArray sortTodayMatches = null;
    private JSONArray sortYesterdayMatches = null;
    private JSONArray sortTomorrowMatches = null;
    private JSONArray sortEarlierMatches = null;
    private JSONArray sortLaterMatches = null;
    private JSONArray sort_match_competitions = null;
    private JSONArray sortedSeniorFootball = null;

    //result data
    private JSONArray resultData = null;

    public Sorting_Match_Info(JSONArray jsonData)
    {
        this.resultData = jsonData;
        //get instance of calendar for date/time/day of week
        this.cal = Calendar.getInstance();

        //format the date to match our information
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

        this.formatDateTime = format1.format(cal.getTime());
    }

    public JSONArray getTodaysMatches() { return todayMatches; };

    public void setTodaysMatches()
    {
        sortTodayMatches = new JSONArray();

        //sort the todayMatches into different arrays based on date and time
        try
        {
            for (int i = 0; i < resultData.length(); i++)
            {
                //separate out all the matches into sorted order
                if (resultData.getJSONObject(i).getString("date").equals(formatDateTime))
                {
                    //add the match to the specific result set
                    sortTodayMatches.put(resultData.getJSONObject(i));
                }
                else
                {

                }
            }

            this.todayMatches = sortTodayMatches;
            this.resultData = this.todayMatches;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public JSONArray getTomorrowsMatches() { return tomorrowMatches; };

    public void setTomorrowsMatches()
    {
        sortTomorrowMatches = new JSONArray();

        //sort the todayMatches into different arrays based on date and time
        try
        { //get the day/month value in integer format
            //format the date to match our information
            SimpleDateFormat formatDay = new SimpleDateFormat("dd");
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM");

            int day = Integer.parseInt(formatDay.format(cal.getTime()));
            int month = Integer.parseInt(formatMonth.format(cal.getTime()));

            for (int i = 0; i < resultData.length(); i++)
            {
                String date = resultData.getJSONObject(i).get("date").toString();

                String[] split = date.split("-");
                int dayStr = Integer.parseInt(split[0]);
                int monthStr = Integer.parseInt(split[1]);

                //separate out all the matches into sorted order
                if ((dayStr == (day + 1) && monthStr == month) || ((dayStr == 31 || dayStr == 30) && (day == 1) && (monthStr == month - 1)))
                {
                    //add the match to the specific result set
                    sortTomorrowMatches.put(resultData.getJSONObject(i));
                }
                else
                {

                }
            }

            this.tomorrowMatches = sortTomorrowMatches;
            //update the result data to be what you have sorted already
            this.resultData = this.tomorrowMatches;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public JSONArray getYesterdaysMatches() { return yesterdayMatches; };

    public void setYesterdayMatches()
    {
        sortYesterdayMatches = new JSONArray();

        //sort the todayMatches into different arrays based on date and time
        try
        { //get the day/month value in integer format
            //format the date to match our information
            SimpleDateFormat formatDay = new SimpleDateFormat("dd");
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM");

            int day = Integer.parseInt(formatDay.format(cal.getTime()));
            int month = Integer.parseInt(formatMonth.format(cal.getTime()));

            for (int i = 0; i < resultData.length(); i++)
            {
                String date = resultData.getJSONObject(i).get("date").toString();

                String[] split = date.split("-");
                int dayStr = Integer.parseInt(split[0]);
                int monthStr = Integer.parseInt(split[1]);

                //separate out all the matches into sorted order
                if ((dayStr == (day - 1) && monthStr == month) || ((dayStr == 1) && (day == 31 || day == 30) && (monthStr == month - 1)))
                {
                    //add the match to the specific result set
                    sortYesterdayMatches.put(resultData.getJSONObject(i));
                }
                else
                {

                }
            }

            this.yesterdayMatches = sortYesterdayMatches;
            this.resultData = this.yesterdayMatches;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public JSONArray getEarlierMatches() { return earlierMatches; };
    public JSONArray getLaterMatches() { return laterMatches; };

    public JSONArray getMatchesByComp() { return seniorFootball; };

    public void setMatchesByComp()
    {
        sortedSeniorFootball = new JSONArray();

        //sort the todayMatches into different arrays based on date and time
        try
        {
            for (int i = 0; i < resultData.length(); i++)
            {
                String competition = resultData.getJSONObject(i).getString("competition").toLowerCase();

                //adding the match counties for each competition
                if (competition.contains("senior") && competition.contains("football"))
                {
                    sortedSeniorFootball.put(resultData.getJSONObject(i));
                }
                /*
                else if (competition.contains("senior") && competition.contains("hurling")) {
                    seniorHurling.add(homeTeam + " vs. " + awayTeam);
                    seniorHurlingJSON.add(match.get(i));
                }
                else if ((competition.contains("junior") || competition.contains("intermediate")) && competition.contains("football")) {
                    intermediate_junior_football_fixtures.add(homeTeam + " vs. " + awayTeam);
                    intermediate_junior_football_fixturesJSON.add(match.get(i));
                }
                else if ((competition.contains("junior") || competition.contains("intermediate")) && competition.contains("hurling")) {
                    intermediate_junior_hurling_fixtures.add(homeTeam + " vs. " + awayTeam);
                    intermediate_junior_hurling_fixturesJSON.add(match.get(i));
                }
                else if (competition.contains("minor") || competition.contains("21") || competition.contains("18")) {
                    minor_21_fixtures.add(homeTeam + " vs. " + awayTeam);
                    minor_21_fixturesJSON.add(match.get(i));
                }
                else if ((competition.contains("under") || competition.contains("u-") || competition.contains("u ")) && !competition.contains("21") && competition.contains("football")) {
                    String ageBracket = "";

                    //populate the age bracket
                    if (competition.contains("16")) {
                        ageBracket = "u16: ";
                    } else if (competition.contains("17")) {
                        ageBracket = "u17: ";
                    } else if (competition.contains("14")) {
                        ageBracket = "u14: ";
                    } else if (competition.contains("12")) {
                        ageBracket = "u12: ";
                    } else {
                        //default string
                        underageFootball.add(homeTeam + " vs. " + awayTeam);
                        underageFootballJSON.add(match.get(i));
                        continue;
                    }

                    //custom string
                    underageFootball.add(ageBracket + homeTeam + " vs. " + awayTeam);
                    underageFootballJSON.add(match.get(i));
                }
                else if ((competition.contains("under") || competition.contains("u-") || competition.contains("u ")) && !competition.contains("21") && competition.contains("hurling")) {
                    String ageBracket = "";

                    //populate the age bracket
                    if (competition.contains("16")) {
                        ageBracket = "u16: ";
                    } else if (competition.contains("17")) {
                        ageBracket = "u17: ";
                    } else if (competition.contains("14")) {
                        ageBracket = "u14: ";
                    } else if (competition.contains("12")) {
                        ageBracket = "u12: ";
                    } else {
                        //default string
                        underageHurling.add(homeTeam + " vs. " + awayTeam);
                        underageHurlingJSON.add(match.get(i));
                        continue;
                    }

                    //custom string
                    underageHurling.add(ageBracket + homeTeam + " vs. " + awayTeam);
                    underageHurlingJSON.add(match.get(i));
                }//end if*/
            }

            this.seniorFootball = sortByTime(sortedSeniorFootball);

        }//end for
        catch (JSONException e1)
        {
            e1.printStackTrace();
        }
    }

    public JSONArray sortByTime(JSONArray dataToSort)
    {
        try
        {
            JSONArray sortedJsonArray = new JSONArray();

            List<JSONObject> jsonValues = new ArrayList<JSONObject>();

            //loop through the json array
            for (int i = 0; i < dataToSort.length(); i++) {
                jsonValues.add(dataToSort.getJSONObject(i));
            }

            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                //You can change "Name" with "ID" if you want to sort by ID
                private static final String KEY_NAME = "time";

                @Override
                public int compare(JSONObject a, JSONObject b) {
                    String valA = new String();
                    String valB = new String();

                    try {
                        valA = (String) a.get(KEY_NAME);
                        valB = (String) b.get(KEY_NAME);
                    } catch (JSONException e) {
                        //do something
                    }

                    return valA.compareTo(valB);
                    //if you want to change the sort order, simply use the following:
                    //return -valA.compareTo(valB);
                }
            });

            for (int i = 0; i < dataToSort.length(); i++) {
                sortedJsonArray.put(jsonValues.get(i));
            }

            //return the sorted array based on the input
            return sortedJsonArray;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    /*
    public void sortMatches()
    {
        //instantiate the array lists to hold the matches
        sortTodayMatches = new ArrayList<JSONObject>();
        sortYesterdayMatches = new ArrayList<JSONObject>();
        sortTomorrowMatches = new ArrayList<JSONObject>();
        sortEarlierMatches = new ArrayList<JSONObject>();
        sortLaterMatches = new ArrayList<JSONObject>();

        //format the date to match our information
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");

        String formatDateTime = format1.format(cal.getTime());

        //sort the todayMatches into different arrays based on date and time
        try
        {
            //matches = new JSONArray(jsonDataResult);

            //match = new ArrayList<JSONObject>();

            for (int i = 0; i < resultData.length(); i++)
            {
                //separate out all the matches into sorted order
                if (resultData.getJSONObject(i).getString("date").equals(formatDateTime)) {
                    //add the match to the specific result set
                    sortTodayMatches.add(sortTodayMatches.listIterator().nextIndex(), resultData.getJSONObject(i));
                } else {
                    //get the day/month value in integer format
                    //format the date to match our information
                    SimpleDateFormat formatDay = new SimpleDateFormat("dd");
                    SimpleDateFormat formatMonth = new SimpleDateFormat("MM");

                    int day = Integer.parseInt(formatDay.format(cal.getTime()));
                    int month = Integer.parseInt(formatMonth.format(cal.getTime()));

                    String date = resultData.getJSONObject(i).get("date").toString();

                    String[] split = date.split("-");
                    int dayStr = Integer.parseInt(split[0]);
                    int monthStr = Integer.parseInt(split[1]);

                    if (dayStr == (day - 1) && monthStr == month) {
                        //add the match to the specific result set
                        sortYesterdayMatches.add(sortYesterdayMatches.listIterator().nextIndex(), resultData.getJSONObject(i));
                    } else if ((dayStr == (day + 1) && monthStr == month) || ((dayStr == 31 || dayStr == 30) && (day == 1) && (monthStr == month - 1))) {
                        //add the match to the specific result set
                        sortTomorrowMatches.add(sortTomorrowMatches.listIterator().nextIndex(), resultData.getJSONObject(i));
                    } else if (dayStr != day && (dayStr < day - 1)) {
                        sortEarlierMatches.add(sortEarlierMatches.listIterator().nextIndex(), resultData.getJSONObject(i));
                    } else if (dayStr != day && (dayStr > day + 1)) {
                        sortLaterMatches.add(sortLaterMatches.listIterator().nextIndex(), resultData.getJSONObject(i));
                    }
                }
            }

            this.todayMatches = sortTodayMatches;
            this.yesterdayMatches = sortYesterdayMatches;
            this.tomorrowMatches = sortTomorrowMatches;
            this.earlierMatches = sortEarlierMatches;
            this.laterMatches = sortLaterMatches;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    */
}
