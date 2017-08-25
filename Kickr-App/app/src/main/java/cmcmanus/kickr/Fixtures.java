package cmcmanus.kickr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fixtures extends AppCompatActivity
{
    //create the views
    public View mProgressView;
    public View backgroundView;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    FixtureRetrieval retrieveData = null;
    private String countyName = "";
    private String jsonDataResult = "";
    private ArrayList<JSONObject> match = null;

    private JSONArray matches = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);

        //instantiate the views
        mProgressView = findViewById(R.id.info_progress);
        backgroundView = findViewById(R.id.lvExp);

        showProgress(true);
        //retrieve the data from the server
        retrieveData = new FixtureRetrieval("Carlow");
        retrieveData.execute();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            backgroundView.setVisibility(show ? View.GONE : View.VISIBLE);
            backgroundView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    backgroundView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            backgroundView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous task to retrieve the users choice of informationn
     */
    public class FixtureRetrieval extends AsyncTask<Void, Void, String>
    {
        public FixtureRetrieval(String county)
        {
            countyName = county.toLowerCase();
        }

        @Override
        protected String doInBackground(Void... params)
        {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder sb = null;

            try
            {
                url = new URL("https://kickr-api.herokuapp.com/fixtures/" + countyName);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader br = null;
                sb = new StringBuilder();

                String line;

                try {

                    br = new BufferedReader(new InputStreamReader(in));

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (br != null)
                    {
                        try
                        {
                            br.close();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }//end if
                }//end finally
            }//end try
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            //set the response to the result string
            return sb.toString();
        }


        @Override
        protected void onPostExecute(final String success)
        {
            showProgress(false);

            jsonDataResult = success;

            if (jsonDataResult.equals(""))
            {
                String fixtures = success;

                System.out.print("Success: " + fixtures);
            }
            else
            {
                //logic here
                try
                {
                    matches = new JSONArray(jsonDataResult);
                    match = new ArrayList<JSONObject>();

                    for(int i=0; i < matches.length(); i++)
                    {
                        //create a useable list of json objects
                        match.add(i,matches.getJSONObject(i));
                    }

                    // preparing list data
                    prepareListData();

                    // get the listview
                    expListView = (ExpandableListView) findViewById(R.id.lvExp);

                    listAdapter = new ExpandableListAdapter(Fixtures.this, listDataHeader, listDataChild);

                    // setting list adapter
                    expListView.setAdapter(listAdapter);

                    // Listview on child click listener
                    expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                        @Override
                        public boolean onChildClick(ExpandableListView parent, View v,
                                                    int groupPosition, int childPosition, long id) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    listDataHeader.get(groupPosition)
                                            + " : "
                                            + listDataChild.get(
                                            listDataHeader.get(groupPosition)).get(
                                            childPosition), Toast.LENGTH_SHORT)
                                    .show();
                            return false;
                        }
                    });

                    // Listview Group expanded listener
                    expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                        @Override
                        public void onGroupExpand(int groupPosition) {
                            Toast.makeText(getApplicationContext(),
                                    listDataHeader.get(groupPosition) + " Expanded",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Listview Group collasped listener
                    expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                        @Override
                        public void onGroupCollapse(int groupPosition) {
                            Toast.makeText(getApplicationContext(),
                                    listDataHeader.get(groupPosition) + " Collapsed",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }


            }
        }

        @Override
        protected void onCancelled()
        {
            showProgress(false);
        }
    }

    /*
    * Preparing the list data
    */
    private void prepareListData()
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        //this will be the competition in order of importance eg. senior football, hurling down to u12.
        listDataHeader.add("Senior Football Fixtures");
        listDataHeader.add("Senior Hurling Fixtures");
        listDataHeader.add("Intermediate/Junior Football Fixtures");
        listDataHeader.add("Intermediate/Junior Hurling Fixtures");
        listDataHeader.add("Minor/U21 Fixtures");
        listDataHeader.add("Underage Football Fixtures");
        listDataHeader.add("Underage Hurling Fixtures");

        //create a list for the different types of competitions
        List<String> seniorFootball = new ArrayList<String>();
        List<String> seniorHurling = new ArrayList<String>();
        List<String> intermediate_junior_football_fixtures = new ArrayList<String>();
        List<String> intermediate_junior_hurling_fixtures = new ArrayList<String>();
        List<String> minor_21_fixtures = new ArrayList<String>();
        List<String> underageFootball = new ArrayList<String>();
        List<String> underageHurling = new ArrayList<String>();

        try
        {
            for(int i=0; i<match.size(); i++)
            {
                String competition = match.get(i).getString("competition").toLowerCase();
                String homeTeam = match.get(i).getString("homeTeam");
                String awayTeam = match.get(i).getString("awayTeam");

                //adding the match data for each competition
                if(competition.contains("senior") && competition.contains("football"))
                {
                    seniorFootball.add(homeTeam + " vs. " + awayTeam);
                }
                else if(competition.contains("senior") && competition.contains("hurling"))
                {
                    seniorHurling.add(homeTeam + " vs. " + awayTeam);
                }
                else if((competition.contains("junior") || competition.contains("intermediate")) && competition.contains("football"))
                {
                    intermediate_junior_football_fixtures.add(homeTeam + " vs. " + awayTeam);
                }
                else if((competition.contains("junior") || competition.contains("intermediate")) && competition.contains("hurling"))
                {
                    intermediate_junior_hurling_fixtures.add(homeTeam + " vs. " + awayTeam);
                }
                else if(competition.contains("minor") || competition.contains("21") || competition.contains("18"))
                {
                    minor_21_fixtures.add(homeTeam + " vs. " + awayTeam);
                }
                else if((competition.contains("under") || competition.contains("u-") || competition.contains("u ")) && !competition.contains("21") && competition.contains("football"))
                {
                    String ageBracket = "";

                    //populate the age bracket
                    if(competition.contains("16"))
                    {
                        ageBracket = "u16: ";
                    }
                    else if(competition.contains("17"))
                    {
                        ageBracket = "u17: ";
                    }
                    else if(competition.contains("14"))
                    {
                        ageBracket = "u14: ";
                    }
                    else if(competition.contains("12"))
                    {
                        ageBracket = "u12: ";
                    }
                    else
                    {
                        //default string
                        underageFootball.add(homeTeam + " vs. " + awayTeam);
                        continue;
                    }

                    //custom string
                    underageFootball.add(ageBracket + homeTeam + " vs. " + awayTeam);
                }
                else if((competition.contains("under") || competition.contains("u-") || competition.contains("u ")) && !competition.contains("21") && competition.contains("hurling"))
                {
                    String ageBracket = "";

                    //populate the age bracket
                    if(competition.contains("16"))
                    {
                        ageBracket = "u16: ";
                    }
                    else if(competition.contains("17"))
                    {
                        ageBracket = "u17: ";
                    }
                    else if(competition.contains("14"))
                    {
                        ageBracket = "u14: ";
                    }
                    else if(competition.contains("12"))
                    {
                        ageBracket = "u12: ";
                    }
                    else
                    {
                        //default string
                        underageHurling.add(homeTeam + " vs. " + awayTeam);
                        continue;
                    }

                    //custom string
                    underageHurling.add(ageBracket + homeTeam + " vs. " + awayTeam);
                }//end if
            }//end for

            listDataChild.put(listDataHeader.get(0), seniorFootball); // Header, Child data
            listDataChild.put(listDataHeader.get(1), seniorHurling); // Header, Child data
            listDataChild.put(listDataHeader.get(2), intermediate_junior_football_fixtures); // Header, Child data
            listDataChild.put(listDataHeader.get(3), intermediate_junior_hurling_fixtures); // Header, Child data
            listDataChild.put(listDataHeader.get(4), minor_21_fixtures); // Header, Child data
            listDataChild.put(listDataHeader.get(5), underageFootball); // Header, Child data
            listDataChild.put(listDataHeader.get(6), underageHurling); // Header, Child data

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        //String time = match.getString("time");
        //String homeTeam = match.getString("homeTeam");
        //String awayTeam = match.getString("awayTeam");
        //String venue = match.getString("venue");
        //String competition = match.getString("competition");
        //String date = match.getString("date");
        //long id = match.getLong("id");
    }
}
