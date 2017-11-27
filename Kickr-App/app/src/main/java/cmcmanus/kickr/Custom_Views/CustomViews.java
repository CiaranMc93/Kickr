package cmcmanus.kickr.Custom_Views;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cmcmanus.kickr.Fixtures;

/**
 * Created by cmcmanus on 11/26/2017.
 */

public class CustomViews
{
    CardView view = null;

    public void customCardView(Context context)
    {
        CardView view = new CardView(context);

        // Set the CardView layoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(5,5,5,0);

        view.setLayoutParams(params);

        // Set CardView corner radius
        view.setRadius(9);

        // Set cardView content padding
        view.setContentPadding(15, 15, 15, 15);

        // Set a background color for CardView
        view.setCardBackgroundColor(Color.parseColor("#FFC6D6C3"));

        // Set the CardView maximum elevation
        view.setMaxCardElevation(15);

        // Set CardView elevation
        view.setCardElevation(5);

        this.view = view;
    }

    public CardView getCard() { return view; };
}
