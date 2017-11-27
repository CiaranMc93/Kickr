package cmcmanus.kickr.Custom_Views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cmcmanus.kickr.Fixtures;

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


    public CustomViews(Context context)
    {
        this.context = context;
    }

    public RelativeLayout getActionBar() { return actionBar; };

    public void setActionBar()
    {

    }

    public CardView getCard() { return view; };

    public void setCustomCardView()
    {
        cardView = new CardView(context);

        // Set the CardView layoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(10,20,10,0);

        cardView.setLayoutParams(params);

        // Set CardView corner radius
        cardView.setRadius(12);

        // Set cardView content padding
        cardView.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView
        cardView.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

        // Set the CardView maximum elevation
        cardView.setMaxCardElevation(31);

        // Set CardView elevation
        cardView.setCardElevation(30);

        cardLayout = new LinearLayout(context);

        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.VERTICAL);

        for(int j=0; j < 4; j++)
        {
            // Initialize a new TextView to put in CardView
            TextView tv = new TextView(context);
            tv.setLayoutParams(params);
            tv.setText("CardView\nProgrammatically");
            tv.setTextColor(Color.RED);

            //add text view to linear layout
            cardLayout.addView(tv);
        }

        //add linear layout to cardview
        cardView.addView(cardLayout);

        this.view = cardView;
    }
}
