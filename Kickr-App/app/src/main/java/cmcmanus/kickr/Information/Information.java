package cmcmanus.kickr.Information;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import cmcmanus.kickr.Fixtures;
import cmcmanus.kickr.R;

/**
 * Created by cmcmanus on 11/25/2017.
 */

public class Information extends AppCompatActivity
{


    ArrayList<String> match = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_info);

        final Bundle bundle = getIntent().getExtras();

        //display the data here
        TextView title = (TextView) findViewById(R.id.match);
        TextView comp = (TextView) findViewById(R.id.comp);
        TextView date = (TextView) findViewById(R.id.date);
        TextView time = (TextView) findViewById(R.id.time);
        TextView venue = (TextView) findViewById(R.id.venue);

        title.setText(bundle.getString("title"));
        title.setTextSize(20);
        title.setTextColor(Color.WHITE);

        comp.setText("Competition: " + bundle.getString("comp"));
        date.setText("Date: " + bundle.getString("date"));
        time.setText("Time: " + bundle.getString("time"));
        venue.setText("Venue: " + bundle.getString("venue"));

        comp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //send the selected match to display the information
                Intent match = new Intent(Information.this,Fixtures.class);
                match.putExtra("sortby","competition-" + bundle.getString("comp"));
                match.putExtra("county",bundle.getString("county"));

                Information.this.startActivity(match);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
