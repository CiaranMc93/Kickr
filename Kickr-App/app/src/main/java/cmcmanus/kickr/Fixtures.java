package cmcmanus.kickr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmcmanus.kickr.Async_Tasks.AsyncResponse;
import cmcmanus.kickr.Async_Tasks.DatabaseAsync;
import cmcmanus.kickr.Async_Tasks.FixtureRetrieval;
import cmcmanus.kickr.Custom_Objects.MatchObj;
import cmcmanus.kickr.Custom_Views.CustomViews;
import cmcmanus.kickr.Data_Storage.DBAdapter;
import cmcmanus.kickr.Data_Sorting.SortMatchInfo;

public class Fixtures extends AppCompatActivity implements AsyncResponse
{
    //progress view variable
    public View mProgressView;
    public View backgroundView;

    public static boolean search = false;
    public static boolean tabUsed = false;

    //define our expandable list variables
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //network variable
    FixtureRetrieval retrieveData = null;

    //class variable
    Fixtures fixtureVar;
    DatabaseAsync dbAsyncTask;

    //display the cards in a relative layout
    RelativeLayout const_action_bar = null;
    public static LinearLayout all_match_info_display = null;
    CustomViews competition_info_card = null;
    CalendarView calendar = null;

    //button variable
    Button info;

    //define variables needed
    private String countyName = "";
    private String jsonDataResult = "";
    private ArrayList<JSONObject> match = null;
    private ArrayList<MatchObj> matchObjList = null;
    private DBAdapter db = null;
    private String county = "";
    private String sortBy = "";
    private Boolean storeDataFlag = false;
    private int dbSize = 0;

    //define variables here
    LinearLayout calendarLayout = null;
    CompactCalendarView compactCalendarView = null;
    Button dateSearch;
    Button monthTitle;
    String date_str = "";
    TabLayout tabLayout;
    TabLayout searchTab;

    //layout variables
    AutoCompleteTextView textSearch;

    private JSONArray matches = null;
    private SortMatchInfo sortMatches = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixtures);

        //instantiate class
        fixtureVar = new Fixtures();

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        searchTab = (TabLayout) findViewById(R.id.textSearch);

        //define the current date
        //get current date
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        date_str = dateFormat.format(date).toString(); // eg. 22-01-2018

        //instantiate the views
        mProgressView = findViewById(R.id.info_progress);
        backgroundView = findViewById(R.id.lvExp);

        Bundle bundle = getIntent().getExtras();

        if(null == bundle.getString("sortby") || bundle.getString("sortby").equals(""))
        {
            county = bundle.getString("county");
        }
        else
        {
            sortBy = bundle.getString("sortby");
            county = bundle.getString("county");
        }

        const_action_bar = (RelativeLayout)findViewById(R.id.action_bar_const);

        all_match_info_display = (LinearLayout)findViewById(R.id.comp_display);

        //instantiate the database
        db = new DBAdapter(this);
        //db.onUpgrade(db.getWritableDatabase(),1,2);

        //retrieve data from database
        //if we do not have data in the database, then we retrieve from the API.
        //databaseHandler();
        retrieveData(true);
    }

    private void retrieveData(Boolean fixtures)
    {
        //show loading bar
        showProgress(true);

        retrieveData = new FixtureRetrieval(county,fixtures);
        retrieveData.execute();
        retrieveData.delegate = this;
    }

    private void dbAsync()
    {
        String CRUD = "GET";

        dbAsyncTask = new DatabaseAsync(this,"county",county);
        dbAsyncTask.execute(CRUD);
        dbAsyncTask.delegate = this;
    }

    //this override the implemented method from FixtureRetrieval
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void processFinish(ArrayList<MatchObj> matchList)
    {
        //remove the progress circle
        showProgress(false);

        matchObjList = matchList;

        //check to see if the database match count is the same as the match list size

        //insert into database
        //insertIntoDB(matchList);

        //display the data
        initMenu(matchList,tabLayout,sortBy);
    }

    @Override
    public void processDBQueries(ArrayList<MatchObj> matches)
    {
        //remove the progress circle
        showProgress(false);
    }

    private void insertIntoDB(ArrayList<MatchObj> matchList)
    {
        //insert the matches into the database when data is retrieved.
        for(int i=0; i< matchList.size(); i++)
        {
            db.insertMatch(matchList.get(i));
        }
    }

    private void databaseHandler()
    {
        showProgress(true);
        dbAsync();
    }

    /*
    //method to handle the database interaction and determinations coming from the data
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void databaseHandler()
    {
        if(null != sortBy && !(sortBy.equals("")))
        {
            dbAsync();

            ArrayList<MatchObj> matchList = db.getAllMatches(county);

            //if there is data existing, then we display that data first.
            if(null != matchList && !(matchList.size() == 0))
            {
                initMenu(matchList,tabLayout,sortBy);
            }
        }
        else
        {
            ArrayList<MatchObj> matchList = db.getAllMatches(county);

            //if there is data existing, then we display that data first.
            if(null != matchList && !(matchList.size() == 0))
            {
                //set matchlist
                matchObjList = matchList;

                //check to make sure that we are on the current date
                String currDate = db.getDate();

                //if current date is not null and not empty
                if(null != currDate && !(currDate.equals("")))
                {
                    if(!currDate.contains(date_str))
                    {
                        db.updateDate(date_str);
                        //retrieve the data from the API once per day to update the fixtures
                        //this is done in the background
                        retrieveData(true);
                    }
                    else
                    {
                        //display database data
                        initMenu(matchList,tabLayout,sortBy);
                    }
                }
                else
                {
                    //initial insert
                    db.insertDate(date_str);
                    initMenu(matchList,tabLayout,sortBy);
                }
            }
            else
            {
                //retrieve the data from the API initial
                showProgress(true);
                retrieveData(true);
            }
        }
    }*/




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initMenu(final ArrayList<MatchObj> matchList, final TabLayout tabLayout,final String sortBy)
    {
        //remove any existing views
        all_match_info_display.removeAllViews();

        displayMatchInfo(matchList,date_str,sortBy);

        //init the buttons
        dateSearch = (Button)findViewById(R.id.date);

        //tab layout logic
        final Drawable backIcon = (Drawable) getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp);
        Drawable dateIcon = (Drawable) getDrawable(R.drawable.ic_date_range_white_24dp);
        Drawable searchIcon = (Drawable) getDrawable(R.drawable.ic_magnify_white_24dp);

        if(!tabUsed)
        {
            tabLayout.addTab(tabLayout.newTab().setIcon(backIcon));
            tabLayout.addTab(tabLayout.newTab().setText("Today"));
            tabLayout.addTab(tabLayout.newTab().setIcon(dateIcon));
            tabLayout.addTab(tabLayout.newTab().setIcon(searchIcon));
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                if(tabLayout.getSelectedTabPosition() == 0)
                {
                    if(search)
                    {
                        initMenu(matchObjList,tabLayout,"");
                    }
                    else
                    {
                        //return to previous
                        finish();
                    }
                }
                else if(tabLayout.getSelectedTabPosition() == 1 && !search)
                {
                    Toast.makeText(Fixtures.this,"Today's Matches",Toast.LENGTH_SHORT).show();
                }
                else if(tabLayout.getSelectedTabPosition() == 2 && !search)
                {
                    //instantiate the sorting class
                    sortMatches = new SortMatchInfo();
                    sortMatches.resetData();
                    sortMatches.setMatchesByComp(matchList,date_str,null);
                    displayCalendar(matchList,tabLayout);
                }
                else if(tabLayout.getSelectedTabPosition() == 3)
                {
                    //remove all views and inflate auto text view
                    search = true;

                    all_match_info_display.removeAllViews();
                    //add the new auto complete text view
                    textSearch = competition_info_card.getAutoCompleteTextView();

                    List<String> list = new ArrayList<String>();

                    //add all teams to the list so it can be searched
                    for(int i=0;i<matchObjList.size();i++)
                    {
                        //make sure we only add names to the list if they are not already there
                        if(!(list.contains(matchObjList.get(i).getHomeTeam()) || list.contains(matchObjList.get(i).getAwayTeam())))
                        {
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getHomeTeam());
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getAwayTeam());
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getCompetition());
                        }
                    }

                    CustomArrayAdapter adapter = new CustomArrayAdapter(Fixtures.this,
                            android.R.layout.simple_dropdown_item_1line, list);

                    textSearch.setAdapter(adapter);

                    textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            String selection = (String)parent.getItemAtPosition(position);

                            ArrayList<MatchObj> filtered = filterMatchObjs(selection);
                            //clear the views
                            all_match_info_display.removeAllViews();
                            //set flag to be true
                            tabUsed = true;
                            search = false;

                            initMenu(filtered,tabLayout,"team");
                        }
                    });

                    all_match_info_display.addView(textSearch);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
                if(tabLayout.getSelectedTabPosition() == 0 && !search)
                {
                    //return to previous
                    finish();
                }
                else if(tabLayout.getSelectedTabPosition() == 1 && !search)
                {
                    Toast.makeText(Fixtures.this,"Today's Matches",Toast.LENGTH_SHORT).show();
                }
                else if(tabLayout.getSelectedTabPosition() == 2 && !search)
                {
                    //instantiate the sorting class
                    sortMatches = new SortMatchInfo();
                    sortMatches.resetData();
                    sortMatches.setMatchesByComp(matchList,date_str,null);
                    displayCalendar(matchList,tabLayout);
                }
                else if(tabLayout.getSelectedTabPosition() == 3 && !search)
                {
                    //remove all views and inflate auto text view
                    search = true;

                    all_match_info_display.removeAllViews();
                    //add the new auto complete text view
                    textSearch = competition_info_card.getAutoCompleteTextView();

                    List<String> list = new ArrayList<String>();

                    //add all teams to the list so it can be searched
                    for(int i=0;i<matchObjList.size();i++)
                    {
                        //make sure we only add names to the list if they are not already there
                        if(!(list.contains(matchObjList.get(i).getHomeTeam()) || list.contains(matchObjList.get(i).getAwayTeam())))
                        {
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getHomeTeam());
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getAwayTeam());
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getCompetition());
                        }
                    }

                    CustomArrayAdapter adapter = new CustomArrayAdapter(Fixtures.this,
                            android.R.layout.simple_dropdown_item_1line, list);

                    textSearch.setAdapter(adapter);

                    textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
                            String selection = (String)parent.getItemAtPosition(position);

                            ArrayList<MatchObj> filtered = filterMatchObjs(selection);
                            //clear the views
                            all_match_info_display.removeAllViews();
                            //set flag to be true
                            tabUsed = true;
                            search = false;

                            initMenu(filtered,tabLayout,"team");
                        }
                    });

                    all_match_info_display.addView(textSearch);
                }
            }
        });
    }

    private  ArrayList<MatchObj> filterMatchObjs(String selection)
    {
        ArrayList<MatchObj> filteredMatches = new ArrayList<MatchObj>();

        for(int i=0;i<matchObjList.size();i++)
        {
            if(matchObjList.get(i).getAwayTeam().equals(selection) || matchObjList.get(i).getHomeTeam().equals(selection))
            {
                filteredMatches.add(filteredMatches.listIterator().nextIndex(),matchObjList.get(i));
            }
        }

        return filteredMatches;
    }

    private void displayCalendar(final ArrayList<MatchObj> matchList, TabLayout tabLayout)
    {
        //set the calendar view
        CustomViews compact = new CustomViews(Fixtures.this);
        compact.setCustomerCalendar();

        calendarLayout = compact.getCustomCal();

        //set the button
        //month button
        monthTitle = (Button) calendarLayout.findViewById(R.id.monthTitle);

        //display the calendar
        all_match_info_display.removeAllViews();
        all_match_info_display.addView(calendarLayout);

        //get the calender view to be manipulated
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        ArrayList<String> dateList = new ArrayList<String>();

        //for each match in the match list
        for(MatchObj match : matchList)
        {
            if(!dateList.isEmpty())
            {
                //if the string array does not contain the match date, then we add it to the list for the next iteration
                if(!dateList.contains(match.getDate()))
                {
                    dateList.add(dateList.listIterator().nextIndex(),match.getDate());
                }
            }
            else
            {
                //add to first iteration
                dateList.add(dateList.listIterator().nextIndex(),match.getDate());
            }
        }

        Event ev1 = null;

        //format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        //set the current month
        DateFormat fmt = new SimpleDateFormat("MMMM");
        Date curr = new Date();
        monthTitle.setText(fmt.format(curr));

        //for each different date we add a marker on the calendar
        for(String date : dateList)
        {
            try
            {
                Date result = sdf.parse(date);
                long millis = result.getTime();

                ev1 = new Event(Color.BLUE, millis, "Match Date");
                compactCalendarView.addEvent(ev1);
            }
            catch (ParseException e)
            {
                System.out.println("Parse Exception");
            }
        }

        // define a listener to receive callbacks when certain events happen.
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener()
        {
            @Override
            public void onDayClick(Date dateClicked)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String parseDate = "";

                try
                {
                    //parse the date from the calendar clicked date
                    parseDate = sdf.format(dateClicked);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                //instantiate the sorting class
                sortMatches = new SortMatchInfo();
                sortMatches.resetData();
                all_match_info_display.removeAllViews();
                //display the data
                displayMatchInfo(matchList,parseDate.toString(),sortBy);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth)
            {
                DateFormat fmt = new SimpleDateFormat("MMMM");
                String date = fmt.format(firstDayOfNewMonth);

                monthTitle.setText(date);
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


    private void displayMatchInfo(ArrayList<MatchObj> matchList, String date,String sortBy)
    {
        sortMatches = new SortMatchInfo();
        LinearLayout cardLayout = null;
        CardView cardView = null;

        //set the different types of matches by competition
        Map<String, List<MatchObj>> compList;

        if(null != sortBy && !(sortBy.equals("")))
        {
            if(sortBy.equals("team"))
            {
                compList = sortMatches.setMatchesByComp(matchList,"",null);
            }
            else
            {
                compList = sortMatches.setMatchesByComp(matchList,date,sortBy);
            }
        }
        else
        {
            compList = sortMatches.setMatchesByComp(matchList,date,null);
        }

        //if there are no scheduled matches, we need to display this to user
        if(compList.size() == 0)
        {
            // Initialize a new custom CardView
            competition_info_card = new CustomViews(Fixtures.this);

            ArrayList<MatchObj> obj = null;

            competition_info_card.setMatchDataLayout("",null);

            //create a new card view with our custom card
            cardView = competition_info_card.getCard();

            //add cardview to linearLayout
            all_match_info_display.addView(cardView);
        }
        else
        {
            //for each entry in the map should have a key and matches relating to the key
            for (Map.Entry<String, List<MatchObj>> entry : compList.entrySet())
            {
                String key = entry.getKey();
                List<MatchObj> value = entry.getValue();

                //for each object in the json array
                // Initialize a new custom CardView
                competition_info_card = new CustomViews(Fixtures.this);

                competition_info_card.setMatchDataLayout(key,value);

                //create a new card view with our custom card
                cardView = competition_info_card.getCard();

                //add cardview to linearLayout
                all_match_info_display.addView(cardView);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if(all_match_info_display.isDirty())
        {
            //display todays matches when back button is pressed
            displayMatchInfo(matchObjList,date_str,sortBy);
        }
    }

    public List<char[]> bigram(String input)
    {
        ArrayList<char[]> bigram = new ArrayList<char[]>();
        for (int i = 0; i < input.length() - 1; i++)
        {
            char[] chars = new char[2];
            chars[0] = input.charAt(i);
            chars[1] = input.charAt(i+1);
            bigram.add(chars);
        }
        return bigram;
    }

    public double dice(List<char[]> bigram1, List<char[]> bigram2)
    {
        List<char[]> copy = new ArrayList<char[]>(bigram2);
        int matches = 0;
        for (int i = bigram1.size(); --i >= 0;)
        {
            char[] bigram = bigram1.get(i);
            for (int j = copy.size(); --j >= 0;)
            {
                char[] toMatch = copy.get(j);
                if (bigram[0] == toMatch[0] && bigram[1] == toMatch[1])
                {
                    copy.remove(j);
                    matches += 2;
                    break;
                }
            }
        }
        return (double) matches / (bigram1.size() + bigram2.size());
    }
}