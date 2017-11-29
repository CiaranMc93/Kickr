package cmcmanus.kickr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cmcmanus.kickr.Async_Tasks.AsyncResponse;
import cmcmanus.kickr.Async_Tasks.FixtureRetrieval;
import cmcmanus.kickr.Custom_Views.CustomViews;
import cmcmanus.kickr.Data_Storage.DBAdapter;
import cmcmanus.kickr.Data_Sorting.Sorting_Match_Info;
import cmcmanus.kickr.Information.Information;

public class Fixtures extends AppCompatActivity implements AsyncResponse
{

    //progress view variable
    public View mProgressView;
    public View backgroundView;

    //define our expandable list variables
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //network variable
    FixtureRetrieval retrieveData = null;

    //class variable
    Fixtures fixtureVar;

    //display the cards in a relative layout
    RelativeLayout const_action_bar = null;
    LinearLayout ovrCardLayout = null;
    CustomViews customCard = null;
    CalendarView calendar = null;

    //button variable
    Button info;

    //define variables needed
    private String countyName = "";
    private String jsonDataResult = "";
    private ArrayList<JSONObject> match = null;
    private DBAdapter db = null;
    private String county = "";
    private Boolean storeDataFlag = false;
    private int dbSize = 0;

    //define variables here
    Calendar cal = null;
    Button yesterday;
    Button today;
    Button tomorrow;
    Button dateSearch;

    private JSONArray matches = null;
    private Sorting_Match_Info sortMatches = null;


    List<JSONObject> seniorFootballJSON = null;
    List<JSONObject> seniorHurlingJSON = null;
    List<JSONObject> intermediate_junior_football_fixturesJSON = null;
    List<JSONObject> intermediate_junior_hurling_fixturesJSON = null;
    List<JSONObject> minor_21_fixturesJSON = null;
    List<JSONObject> underageFootballJSON = null;
    List<JSONObject> underageHurlingJSON = null;

    JSONArray testArray = new JSONArray("[{\"time\":\"7 01 PM\",\"homeTeam\":\"Courtwood\",\"awayTeam\":\"Emo\",\"venue\":\"Hawkfield\",\"competition\":\"GAA Senior Football League Division 1\",\"date\":\"27-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875243}," +
            " {\"time\":\"7 00 PM\",\"homeTeam\":\"Portarlington\",\"awayTeam\":\"Ballylinan\",\"venue\":\"Hawkfield\",\"competition\":\"GAA Senior Football League Division 1\",\"date\":\"28-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875244}," +
            " {\"time\":\"7 02 PM\",\"homeTeam\":\"O'Dempseys\",\"awayTeam\":\"Portlaoise\",\"venue\":\"The Old Pound\",\"competition\":\"GAA Senior Football League Division 1\",\"date\":\"28-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875245}," +
            " {\"time\":\"8 00 PM\",\"homeTeam\":\"Portarlington\",\"awayTeam\":\"Emo\",\"venue\":\"McCann Park\",\"competition\":\"Senior Hurling League Finals\",\"date\":\"28-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875246}," +
            " {\"time\":\"8 30 PM\",\"homeTeam\":\"Ballylinan\",\"awayTeam\":\"O'Dempseys\",\"venue\":\"Athy\",\"competition\":\"Senior Football Division 1\",\"date\":\"29-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875247}," +
            " {\"time\":\"7 35 PM\",\"homeTeam\":\"Portarlington\",\"awayTeam\":\"Emo\",\"venue\":\"Athy\",\"competition\":\"GAA Senior Hurling League Division 2\",\"date\":\"29-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875248}," +
            " {\"time\":\"7 32 PM\",\"homeTeam\":\"Ballylinan\",\"awayTeam\":\"Courtwood\",\"venue\":\"Arles\",\"competition\":\"GAA Senior Football League Division 2\",\"date\":\"27-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875249}," +
            " {\"time\":\"8 00 PM\",\"homeTeam\":\"Emo\",\"awayTeam\":\"Portlaoise\",\"venue\":\"The Old Pound\",\"competition\":\"GAA Senior Football League Division 2\",\"date\":\"27-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875210}," +
            " {\"time\":\"8 01 PM\",\"homeTeam\":\"Arles-Kileen\",\"awayTeam\":\"Arles-Kilcruise\",\"venue\":\"McCann Park\",\"competition\":\"GAA Senior Hurling League Division 2\",\"date\":\"27-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875211}," +
            " {\"time\":\"8 30 PM\",\"homeTeam\":\"Courtwood\",\"awayTeam\":\"Arles-Kileen\",\"venue\":\"Emo\",\"competition\":\"GAA Senior Hurling League Division 2\",\"date\":\"28-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875212}," +
            " {\"time\":\"7 30 PM\",\"homeTeam\":\"O'Dempseys\",\"awayTeam\":\"Arles-Kileen\",\"venue\":\"Emo\",\"competition\":\"GAA Senior Football League Division 2\",\"date\":\"27-11-2017\",\"homeTeamScore\":\"\",\"awayTeamScore\":\"\",\"winner\":\"\",\"id\":1137875213}]");

    public Fixtures() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);

        //instantiate class
        try
        {
            fixtureVar = new Fixtures();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //instantiate the views
        mProgressView = findViewById(R.id.info_progress);
        backgroundView = findViewById(R.id.lvExp);

        Bundle bundle = getIntent().getExtras();
        county = bundle.getString("county");

        const_action_bar = (RelativeLayout)findViewById(R.id.action_bar_const);
        ovrCardLayout = (LinearLayout)findViewById(R.id.comp_display);

        //get the button
        info = (Button)findViewById(R.id.button2);

        info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), Information.class);
                startActivity(intent);
            }
        });

        //instantiate the new database
        //db = new DBAdapter(this);
        //db.onUpgrade(db.getWritableDatabase(),1,2);

        //ArrayList<JSONObject> mat =  db.getAllContacts(county);

        showProgress(true);
        updateDatabase();

        //show the database information
        //if(null == mat || mat.size() == 0)
        //{
            //pass in the JSONObject list of todayMatches
          //  createRecyclerView(db.getAllContacts(county));
        //}
        //else
        //{
          //  showProgress(true);
            //updateDatabase();
        //}
    }

    private void updateDatabase()
    {

        //retrieve the counties from the server
        storeDataFlag = false;
        retrieveData = new FixtureRetrieval(county);
        retrieveData.execute();
        retrieveData.delegate = this;
    }

    //this override the implemented method from asyncTask
    @Override
    public void processFinish(String output)
    {
        //result = output
        //jsonDataResult = output;

        showProgress(false);
        initMenu();
    }

    public void initMenu()
    {
        //initialize the data
        sortMatches = new Sorting_Match_Info(testArray);
        sortMatches.setTodaysMatches();
        createCards();

        //init the buttons
        yesterday = (Button)findViewById(R.id.yesterday);
        today  = (Button)findViewById(R.id.today);
        tomorrow = (Button)findViewById(R.id.tomorrow);
        dateSearch = (Button)findViewById(R.id.date);

        dateSearch.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                //inflate the calendar
                CustomViews calendarView = new CustomViews(Fixtures.this);

                calendarView.setCalendarView();
                calendar = calendarView.getCalendarView();

                //remove all views and display the calendar
                ovrCardLayout.removeAllViews();
                ovrCardLayout.addView(calendar);

                cal = Calendar.getInstance();
                calendar.setDate(cal.getTimeInMillis(), true, true);

                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
                {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
                    {

                    }
                });
            }
        });

        //set the default
        today.setTextColor(getResources().getColor(R.color.white));

        today.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                today.setTextColor(getResources().getColor(R.color.white));
                tomorrow.setTextColor(getResources().getColor(R.color.black));
                yesterday.setTextColor(getResources().getColor(R.color.black));

                //instantiate the sorting class
                sortMatches = new Sorting_Match_Info(testArray);
                sortMatches.resetData();
                ovrCardLayout.removeAllViews();
                sortMatches.setTodaysMatches();
                createCards();
            }
        });

        //we need to search for the results from the API in order to show this data
        yesterday.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                yesterday.setTextColor(getResources().getColor(R.color.white));
                today.setTextColor(getResources().getColor(R.color.black));
                tomorrow.setTextColor(getResources().getColor(R.color.black));

                //instantiate the sorting class
                sortMatches = new Sorting_Match_Info(testArray);
                sortMatches.resetData();
                ovrCardLayout.removeAllViews();
                sortMatches.setYesterdayMatches();
                createCards();
            }
        });

        tomorrow.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                tomorrow.setTextColor(getResources().getColor(R.color.white));
                today.setTextColor(getResources().getColor(R.color.black));
                yesterday.setTextColor(getResources().getColor(R.color.black));

                //instantiate the sorting class
                sortMatches = new Sorting_Match_Info(testArray);
                sortMatches.resetData();
                ovrCardLayout.removeAllViews();
                sortMatches.setTomorrowsMatches();
                createCards();
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
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

    private void createCards()
    {
        LinearLayout cardLayout = null;
        CardView cardView = null;

        sortMatches.setMatchesByComp();

        //sort the selected days matches by competition
        int allCompsSize = sortMatches.getTotalNumComps();

        HashMap<Integer,Sorting_Match_Info.MatchMap> displayMatches = sortMatches.getAllCompsMap();

        Set keys = displayMatches.keySet();

        for(int i=0; i < allCompsSize; i++)
        {
            //get the list of matches based
            Sorting_Match_Info.MatchMap matchList = displayMatches.get(i);

            JSONArray matches = matchList.getMatches();
            String compTitle = matchList.getTitle();

            //for each object in the json array
            // Initialize a new custom CardView
            customCard = new CustomViews(Fixtures.this);
            customCard.setCustomCardView(matches,compTitle);

            //create a new card view with our custom card
            cardView = customCard.getCard();

            //add cardview to linearLayout
            ovrCardLayout.addView(cardView);
        }
    }

    /*
    * Preparing the list counties
    */
    private void prepareListData()
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child counties
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

        //create a list for each competition to hold all of their counties
        seniorFootballJSON = new ArrayList<JSONObject>();
        seniorHurlingJSON = new ArrayList<JSONObject>();
        intermediate_junior_football_fixturesJSON = new ArrayList<JSONObject>();
        intermediate_junior_hurling_fixturesJSON = new ArrayList<JSONObject>();
        minor_21_fixturesJSON = new ArrayList<JSONObject>();
        underageFootballJSON = new ArrayList<JSONObject>();
        underageHurlingJSON = new ArrayList<JSONObject>();

        try
        {
            for(int i=0; i<match.size(); i++)
            {
                String competition = match.get(i).getString("competition").toLowerCase();
                String homeTeam = match.get(i).getString("homeTeam");
                String awayTeam = match.get(i).getString("awayTeam");

                //adding the match counties for each competition
                if(competition.contains("senior") && competition.contains("football"))
                {
                    seniorFootball.add(homeTeam + " vs. " + awayTeam);
                    seniorFootballJSON.add(match.get(i));
                }
                else if(competition.contains("senior") && competition.contains("hurling"))
                {
                    seniorHurling.add(homeTeam + " vs. " + awayTeam);
                    seniorHurlingJSON.add(match.get(i));
                }
                else if((competition.contains("junior") || competition.contains("intermediate")) && competition.contains("football"))
                {
                    intermediate_junior_football_fixtures.add(homeTeam + " vs. " + awayTeam);
                    intermediate_junior_football_fixturesJSON.add(match.get(i));
                }
                else if((competition.contains("junior") || competition.contains("intermediate")) && competition.contains("hurling"))
                {
                    intermediate_junior_hurling_fixtures.add(homeTeam + " vs. " + awayTeam);
                    intermediate_junior_hurling_fixturesJSON.add(match.get(i));
                }
                else if(competition.contains("minor") || competition.contains("21") || competition.contains("18"))
                {
                    minor_21_fixtures.add(homeTeam + " vs. " + awayTeam);
                    minor_21_fixturesJSON.add(match.get(i));
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
                        underageFootballJSON.add(match.get(i));
                        continue;
                    }

                    //custom string
                    underageFootball.add(ageBracket + homeTeam + " vs. " + awayTeam);
                    underageFootballJSON.add(match.get(i));
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
                        underageHurlingJSON.add(match.get(i));
                        continue;
                    }

                    //custom string
                    underageHurling.add(ageBracket + homeTeam + " vs. " + awayTeam);
                    underageHurlingJSON.add(match.get(i));
                }//end if
            }//end for

            listDataChild.put(listDataHeader.get(0), seniorFootball); // Header, Child counties
            listDataChild.put(listDataHeader.get(1), seniorHurling); // Header, Child counties
            listDataChild.put(listDataHeader.get(2), intermediate_junior_football_fixtures); // Header, Child counties
            listDataChild.put(listDataHeader.get(3), intermediate_junior_hurling_fixtures); // Header, Child counties
            listDataChild.put(listDataHeader.get(4), minor_21_fixtures); // Header, Child counties
            listDataChild.put(listDataHeader.get(5), underageFootball); // Header, Child counties
            listDataChild.put(listDataHeader.get(6), underageHurling); // Header, Child counties

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

     /*
    *
    *  Handle the list counties
    *
     */
    /*
    private void createRecyclerView()
    {
        //logic here
        try
        {
            //update the match json object list
            match = todayMatches;

            //create cards for the competitions and matches
            createCards();

            // preparing list counties
            //prepareListData();

            // get the listview
            expListView = (ExpandableListView) findViewById(R.id.lvExp);

            listAdapter = new ExpandableListAdapter(Fixtures.this, listDataHeader, listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            Intent i = null;
            // Listview on child click listener
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id)
                {
                    switch (groupPosition)
                    {
                        case 0:
                            //send the selected county to the fixture retrieval class
                            Intent i = new Intent(Fixtures.this,Pop.class);
                            i.putExtra("county",seniorFootballJSON.get(childPosition).toString());
                            startActivity(i);
                            break;
                        case 1:
                            //send the selected county to the fixture retrieval class
                            i = new Intent(Fixtures.this,Pop.class);
                            i.putExtra("county",seniorHurlingJSON.get(childPosition).toString());
                            startActivity(i);
                            break;
                        case 2:
                            //send the selected county to the fixture retrieval class
                            i = new Intent(Fixtures.this,Pop.class);
                            i.putExtra("county",intermediate_junior_football_fixturesJSON.get(childPosition).toString());
                            startActivity(i);
                            break;
                        case 3:
                            //send the selected county to the fixture retrieval class
                            i = new Intent(Fixtures.this,Pop.class);
                            i.putExtra("county",intermediate_junior_hurling_fixturesJSON.get(childPosition).toString());
                            startActivity(i);
                            break;
                        default: break;
                    }

                    return false;
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
}
