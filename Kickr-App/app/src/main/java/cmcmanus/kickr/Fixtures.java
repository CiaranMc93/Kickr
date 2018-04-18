package cmcmanus.kickr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    public static boolean calInUse = false;
    public boolean dbSucess = false;
    public boolean noFutureMatches = false;
    public boolean isLoading = false;

    //network variable
    FixtureRetrieval retrieveData = null;

    //class variable
    Fixtures fixtureVar;
    DatabaseAsync dbAsyncTask;

    //display the cards in a relative layout
    RelativeLayout const_action_bar = null;
    public static LinearLayout all_match_info_display = null;
    CustomViews competition_info_card = null;

    //define variables needed
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

        //reset tabUsed
        tabUsed = false;

        //define the current date
        //get current date
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        date_str = dateFormat.format(date).toString(); // eg. 22-01-2018

        //instantiate the views
        mProgressView = findViewById(R.id.info_progress);
        backgroundView = findViewById(R.id.lvExp);

        Bundle bundle = getIntent().getExtras();

        county = bundle.getString("county");

        const_action_bar = (RelativeLayout)findViewById(R.id.action_bar_const);

        all_match_info_display = (LinearLayout)findViewById(R.id.comp_display);

        //retrieve the data in the database before we query the API
        queryDB();
    }

    private void queryAPI(Boolean fixtures)
    {
        //show loading bar
        showProgress(true);

        retrieveData = new FixtureRetrieval(county,fixtures);
        retrieveData.execute();
        retrieveData.delegate = this;
        //calls processFinish()
    }

    private void queryDB()
    {
        String CRUD = "GET";

        dbAsyncTask = new DatabaseAsync(this,"county",county);
        dbAsyncTask.execute(CRUD);
        dbAsyncTask.delegate = this;
        //calls processDBQueries()
    }

    //this override the implemented method from FixtureRetrieval
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void processFinish(ArrayList<MatchObj> matchList)
    {
        isLoading = false;
        noFutureMatches = false;

        //remove the progress circle
        showProgress(false);

        matchObjList = matchList;

        //display the data
        initMenu(matchList,tabLayout,"");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void processDBQueries(ArrayList<MatchObj> matches)
    {
        //remove the progress circle
        showProgress(false);

        //check to see if we retrieved the data from the database
        if(null == matches || matches.size() == 0)
        {
            isLoading = true;
            //query with load
            showProgress(true);
            tabUsed = true;
            //retrieve the data from the API
            queryAPI(true);
        }
        else
        {
            dbSucess = true;
            matchObjList = matches;

            //create the match data layout
            initMenu(matchObjList,tabLayout,"");

            //tab layout already loaded so do not try again
            if(noFutureMatches)
            {
                isLoading = true;
                //query with load
                showProgress(true);
                tabUsed = true;
                queryAPI(true);
            }
            else
            {
                isLoading = false;
                tabUsed = true;
                //query in background
                queryAPI(true);
            }
        }
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
        queryDB();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void initMenu(final ArrayList<MatchObj> matchList, final TabLayout tabLayout,final String sortBy)
    {
        calInUse = false;

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
                    if(search || calInUse)
                    {
                        initMenu(matchObjList,tabLayout,"");
                    }
                    else
                    {
                        //return to previous
                        finish();
                    }
                }
                else if(tabLayout.getSelectedTabPosition() == 1 && !isLoading)
                {
                    tabUsed = true;
                    calInUse = false;
                    search = false;
                    //reset the matches to today
                    initMenu(matchObjList,tabLayout,"");
                }
                else if(tabLayout.getSelectedTabPosition() == 2 && !isLoading)
                {
                    tabUsed = true;
                    search = false;
                    //instantiate the sorting class
                    sortMatches = new SortMatchInfo();
                    sortMatches.resetData();
                    sortMatches.setMatchesByComp(matchList,date_str,null);
                    displayCalendar(matchList);
                }
                else if(tabLayout.getSelectedTabPosition() == 3 && !isLoading )
                {
                    //remove all views and inflate auto text view
                    search = true;
                    calInUse = false;

                    all_match_info_display.removeAllViews();
                    //add the new auto complete text view
                    textSearch = competition_info_card.getAutoCompleteTextView();

                    List<String> list = new ArrayList<String>();

                    //add all teams to the list so it can be searched
                    for(int i=0;i<matchObjList.size();i++)
                    {
                        //if the list is empty,
                        if(!(list.isEmpty()))
                        {
                            //make sure we only add names to the list if they are not already there
                            if(!(list.contains(matchObjList.get(i).getHomeTeam())))
                            {
                                list.add(list.listIterator().nextIndex(),matchObjList.get(i).getHomeTeam());
                            }
                            else if(!(list.contains(matchObjList.get(i).getAwayTeam())))
                            {
                                list.add(list.listIterator().nextIndex(),matchObjList.get(i).getAwayTeam());
                            }
                            else if(!(list.contains(matchObjList.get(i).getCompetition())))
                            {
                                list.add(list.listIterator().nextIndex(),matchObjList.get(i).getCompetition());
                            }
                        }
                        else
                        {
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getHomeTeam());
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

                            InputMethodManager inputManager = (InputMethodManager) Fixtures.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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
                if(tabLayout.getSelectedTabPosition() == 0)
                {
                    if(search || calInUse)
                    {
                        initMenu(matchObjList,tabLayout,"");
                    }
                    else
                    {
                        //return to previous
                        finish();
                    }
                }
                else if(tabLayout.getSelectedTabPosition() == 1 && !isLoading)
                {
                    tabUsed = true;
                    calInUse = false;
                    search = false;
                    //reset the matches to today
                    initMenu(matchObjList,tabLayout,"");
                }
                else if(tabLayout.getSelectedTabPosition() == 2 && !isLoading)
                {
                    search = false;
                    //instantiate the sorting class
                    sortMatches = new SortMatchInfo();
                    sortMatches.resetData();
                    sortMatches.setMatchesByComp(matchList,date_str,null);
                    displayCalendar(matchList);
                }
                else if(tabLayout.getSelectedTabPosition() == 3 && !isLoading)
                {
                    //remove all views and inflate auto text view
                    search = true;
                    tabUsed = true;
                    calInUse = false;

                    all_match_info_display.removeAllViews();
                    //add the new auto complete text view
                    textSearch = competition_info_card.getAutoCompleteTextView();

                    List<String> list = new ArrayList<String>();

                    //add all teams to the list so it can be searched
                    for(int i=0;i<matchObjList.size();i++)
                    {
                        //if the list is empty,
                        if(!(list.isEmpty()))
                        {
                            //make sure we only add names to the list if they are not already there
                            if(!(list.contains(matchObjList.get(i).getHomeTeam())))
                            {
                                list.add(list.listIterator().nextIndex(),matchObjList.get(i).getHomeTeam());
                            }
                            else if(!(list.contains(matchObjList.get(i).getAwayTeam())))
                            {
                                list.add(list.listIterator().nextIndex(),matchObjList.get(i).getAwayTeam());
                            }
                            else if(!(list.contains(matchObjList.get(i).getCompetition())))
                            {
                                list.add(list.listIterator().nextIndex(),matchObjList.get(i).getCompetition());
                            }
                        }
                        else
                        {
                            list.add(list.listIterator().nextIndex(),matchObjList.get(i).getHomeTeam());
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

                            InputMethodManager inputManager = (InputMethodManager) Fixtures.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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
            if(matchObjList.get(i).getAwayTeam().equals(selection) || matchObjList.get(i).getHomeTeam().equals(selection) || matchObjList.get(i).getCompetition().equals(selection))
            {
                filteredMatches.add(filteredMatches.listIterator().nextIndex(),matchObjList.get(i));
            }
        }

        return filteredMatches;
    }

    private void displayCalendar(final ArrayList<MatchObj> matchList)
    {
        //calender in use flag
        calInUse = true;
        tabUsed = true;

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

    //method to display the matches to the user
    private void displayMatchInfo(ArrayList<MatchObj> matchList, String date,String sortBy)
    {
        sortMatches = new SortMatchInfo();
        CardView cardView = null;

        //set the different types of matches by competition
        Map<String, List<MatchObj>> filterList = null;

        if(null != sortBy && !(sortBy.equals("")))
        {
            if(sortBy.equals("team") || sortBy.equals("comp") || sortBy.equals("sort"))
            {
                filterList = sortMatches.setMatchesByComp(matchList,"",sortBy);
            }
        }
        else
        {
            filterList = sortMatches.setMatchesByComp(matchList,date,null);
        }

        //if there are no scheduled matches, we need to display this to user
        if(filterList == null || filterList.size() == 0)
        {
            noFutureMatches = true;
            // Initialize a new custom CardView
            competition_info_card = new CustomViews(Fixtures.this);

            ArrayList<MatchObj> obj = null;

            competition_info_card.setMatchDataLayout("",null,sortBy);

            //create a new card view with our custom card
            cardView = competition_info_card.getCard();

            //add cardview to linearLayout
            all_match_info_display.addView(cardView);
        }
        else
        {
            if(filterList != null)
            {
                noFutureMatches = false;
                //for each entry in the map should have a key and matches relating to the key
                for (Map.Entry<String, List<MatchObj>> entry : filterList.entrySet())
                {
                    String key = entry.getKey();
                    List<MatchObj> value = entry.getValue();

                    //for each object in the json array
                    // Initialize a new custom CardView
                    competition_info_card = new CustomViews(Fixtures.this);

                    competition_info_card.setMatchDataLayout(key,value,sortBy);

                    //create a new card view with our custom card
                    cardView = competition_info_card.getCard();

                    //add cardview to linearLayout
                    all_match_info_display.addView(cardView);
                }
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
}