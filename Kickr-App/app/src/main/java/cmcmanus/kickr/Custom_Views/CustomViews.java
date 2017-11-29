package cmcmanus.kickr.Custom_Views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import cmcmanus.kickr.Fixtures;
import cmcmanus.kickr.R;

/**
 * Created by cmcmanus on 11/26/2017.
 */

public class CustomViews
{
    Context context = null;

    //views
    CardView view = null;
    LinearLayout cardLayout = null;

    //constructed views
    CardView cardView = null;
    RelativeLayout actionBar = null;
    RelativeLayout match_card_layout = null;
    CalendarView calendar = null;


    public CustomViews(Context context)
    {
        this.context = context;
    }

    public RelativeLayout getActionBar() { return actionBar; };

    public void setActionBar()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        RelativeLayout layoutActionBar = (RelativeLayout) inflater.inflate(R.layout.card_title_bar, null);

        this.actionBar = layoutActionBar;
    }

    public void setCalendarView()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        CalendarView calendarView = (CalendarView) inflater.inflate(R.layout.calendar_layout, null);

        this.calendar = calendarView;
    }

    public CalendarView getCalendarView() { return calendar; }

    public void setMatchCardLayout()
    {
        LayoutInflater inflater = LayoutInflater.from(context);

        RelativeLayout layoutActionBar = (RelativeLayout) inflater.inflate(R.layout.match_card_layout, null);

        this.match_card_layout = layoutActionBar;
    }

    public CardView getCard() { return view; };

    public void setCustomCardView(JSONArray matches, String compTitle)
    {
        cardView = new CardView(context);

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
        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));

        // Set the CardView maximum elevation
        cardView.setMaxCardElevation(31);

        // Set CardView elevation
        cardView.setCardElevation(30);

        cardLayout = new LinearLayout(context);

        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.VERTICAL);

        //set the action bar to be displayed in the card
        setActionBar();

        //set the comp title to be what has been passed in
        TextView title = (TextView)this.actionBar.findViewById(R.id.compTitle);
        title.setText(compTitle);

        cardLayout.addView(this.actionBar);

        //display the matches inside the card layout
        for(int j=0; j < matches.length(); j++)
        {
            //match variables
            TextView score1;
            TextView score2;

            setMatchCardLayout();

            try
            {
                //display the match data
                TextView time = (TextView)this.match_card_layout.findViewById(R.id.time);
                time.setText(matches.getJSONObject(j).get("time").toString());
                time.setTextSize(16);

                if(!matches.getJSONObject(j).get("homeTeamScore").equals(""))
                {
                    score1 = (TextView)this.match_card_layout.findViewById(R.id.score);
                    score1.setText(matches.getJSONObject(j).get("homeTeamScore").toString());
                    score1.setTextSize(16);
                }
                else
                {
                    score1 = (TextView)this.match_card_layout.findViewById(R.id.score);
                    score1.setText("0-00");
                    score1.setTextSize(16);
                }

                if(!matches.getJSONObject(j).get("awayTeamScore").equals(""))
                {
                    score2 = (TextView)this.match_card_layout.findViewById(R.id.score2);
                    score2.setText(matches.getJSONObject(j).get("awayTeamScore").toString());
                    score2.setTextSize(16);
                }
                else
                {
                    score2 = (TextView)this.match_card_layout.findViewById(R.id.score2);
                    score2.setText("0-00");
                    score2.setTextSize(16);
                }

                TextView home = (TextView)this.match_card_layout.findViewById(R.id.team1);
                home.setText(matches.getJSONObject(j).get("homeTeam").toString());
                home.setTextSize(16);

                TextView away = (TextView)this.match_card_layout.findViewById(R.id.team2);
                away.setText(matches.getJSONObject(j).get("awayTeam").toString());
                away.setTextSize(16);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            //add text view to linear layout
            cardLayout.addView(this.match_card_layout);
        }

        //add linear layout to cardview
        cardView.addView(cardLayout);

        this.view = cardView;
    }

    public void removeViews()
    {
        cardView.removeAllViews();
    }
}
