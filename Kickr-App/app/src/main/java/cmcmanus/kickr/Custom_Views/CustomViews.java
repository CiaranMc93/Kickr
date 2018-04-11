package cmcmanus.kickr.Custom_Views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cmcmanus.kickr.CountiesSelection;
import cmcmanus.kickr.Custom_Objects.MatchObj;
import cmcmanus.kickr.Fixtures;
import cmcmanus.kickr.Information.Information;
import cmcmanus.kickr.R;

import static cmcmanus.kickr.Fixtures.all_match_info_display;

/**
 * Created by cmcmanus on 11/26/2017.
 */

public class CustomViews extends Activity
{
    Context context = null;

    //views
    CardView view = null;
    LinearLayout cardLayout = null;

    //constructed views
    CardView cardView = null;
    RelativeLayout comp_title_layout = null;
    RelativeLayout match_info_layout = null;
    LinearLayout match_data = null;
    CalendarView calendar = null;
    LinearLayout calendarCustom = null;
    LinearLayout textSearch = null;

    ArrayList<RelativeLayout> cardList;

    //match info list
    List<MatchObj> matchList;

    public CustomViews(Context context)
    {
        this.context = context;
    }

    public RelativeLayout getCompTitleLayout() { return comp_title_layout; };

    public void setActionBar()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        RelativeLayout layoutActionBar = (RelativeLayout) inflater.inflate(R.layout.card_title_bar, null);

        this.comp_title_layout = layoutActionBar;
    }

    public void setMonthButton()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        CalendarView calendarView = (CalendarView) inflater.inflate(R.layout.calendar_layout, null);

        this.calendar = calendarView;
    }

    public void setCustomerCalendar()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        LinearLayout calendarView = (LinearLayout) inflater.inflate(R.layout.customer_cal, null);

        this.calendarCustom = calendarView;
    }

    public AutoCompleteTextView getAutoCompleteTextView()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        AutoCompleteTextView textSearch = (AutoCompleteTextView) inflater.inflate(R.layout.auto_complete_text_view, null);

        return textSearch;
    }

    public LinearLayout getCustomCal() { return calendarCustom; }

    public CalendarView getCalendarView() { return calendar; }

    public void setMatchInfoLayout()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        RelativeLayout layoutActionBar = (RelativeLayout) inflater.inflate(R.layout.match_card_layout, null);

        layoutActionBar.setClickable(true);//make your TextView Clickable
        layoutActionBar.setOnClickListener(btnClickListener);

        this.match_info_layout = layoutActionBar;
    }

    public CardView getCard() { return view; };

    public void setMatchDataLayout(String league, List<MatchObj> matches)
    {
        matchList = matches;
        cardView = new CardView(context);
        cardList = new ArrayList<RelativeLayout>();

        // Set the CardView layoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );

        // Set the CardView layoutParams
        RelativeLayout.LayoutParams relLayout = new RelativeLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(10,20,10,10);

        cardView.setLayoutParams(params);

        // Set CardView corner radius
        cardView.setRadius(12);

        // Set cardView content padding
        cardView.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView
        cardView.setCardBackgroundColor(Color.parseColor("#d0d0d0"));

        // Set the CardView maximum elevation
        cardView.setMaxCardElevation(31);

        // Set CardView elevation
        cardView.setCardElevation(30);

        cardLayout = new LinearLayout(context);

        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.VERTICAL);

        if(!(null == matches))
        {
            //set the action bar to be displayed in the card
            setActionBar();

            //set the comp title to be what has been passed in
            TextView title = (TextView)this.comp_title_layout.findViewById(R.id.compTitle);
            title.setText(league);

            cardLayout.addView(this.comp_title_layout);

            //display the matches inside the card layout
            for(int j=0; j < matches.size(); j++)
            {
                //match variables
                TextView score1;
                TextView score2;
                TextView id;

                setMatchInfoLayout();

                //display the match data
                TextView time = (TextView)this.match_info_layout.findViewById(R.id.time);
                time.setText(matches.get(j).getTime());
                time.setTextSize(16);

                if(!(matches.get(j).getHomeTeamScore().equals("0-00") && matches.get(j).getAwayTeamScore().equals("0-00")))
                {
                    score1 = (TextView)this.match_info_layout.findViewById(R.id.score);
                    score1.setText(matches.get(j).getHomeTeamScore());
                    score1.setTextSize(16);

                    score2 = (TextView)this.match_info_layout.findViewById(R.id.score2);
                    score2.setText(matches.get(j).getAwayTeamScore());
                    score2.setTextSize(16);
                }
                else
                {
                    score1 = (TextView)this.match_info_layout.findViewById(R.id.score);
                    score1.setText("");
                    score2 = (TextView)this.match_info_layout.findViewById(R.id.score2);
                    score2.setText("");
                }

                TextView home = (TextView)this.match_info_layout.findViewById(R.id.team1);
                home.setText(matches.get(j).getHomeTeam());
                home.setTextSize(16);

                TextView away = (TextView)this.match_info_layout.findViewById(R.id.team2);
                away.setText(matches.get(j).getAwayTeam());
                away.setTextSize(16);

                //set the layout id to be the match id
                this.match_info_layout.setId(matches.get(j).getId());

                //add text view to linear layout
                cardLayout.addView(this.match_info_layout);
            }

            //add linear layout to cardview
            cardView.addView(cardLayout);

            this.view = cardView;
        }
        else
        {
            //set the action bar to be displayed in the card
            setActionBar();

            //set the comp title to be what has been passed in
            TextView title = (TextView)this.comp_title_layout.findViewById(R.id.compTitle);

            title.setText("No Fixtures Available");

            //set the width to match the parent
            cardLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            cardLayout.addView(this.comp_title_layout);

            //add linear layout to cardview
            cardView.addView(cardLayout);

            this.view = cardView;
        }
    }

    public void removeViews()
    {
        cardView.removeAllViews();
    }

    public ArrayList<RelativeLayout> getCardList() { return cardList; }

    View.OnClickListener btnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            for(int i=0; i<matchList.size();i++)
            {
                //make sure we get the match that was selected
                if(v.getId() == matchList.get(i).getId())
                {
                    //check if it is a match fixture or result.

                    //create bundle and add string array list
                    Bundle matchBundle = new Bundle();
                    matchBundle.putString("title",matchList.get(i).getHomeTeam() + " vs. " + matchList.get(i).getAwayTeam());
                    matchBundle.putString("comp",matchList.get(i).getCompetition());
                    matchBundle.putString("date",matchList.get(i).getDate());
                    matchBundle.putString("time",matchList.get(i).getTime());
                    matchBundle.putString("venue",matchList.get(i).getVenue());
                    matchBundle.putString("county",matchList.get(i).getCounty());

                    //send the selected match to display the information
                    Intent match = new Intent(context,Information.class);
                    match.putExtras(matchBundle);
                    context.startActivity(match);
                }
            }
        }
    };
}
