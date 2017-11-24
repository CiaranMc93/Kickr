package cmcmanus.kickr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cmcmanus on 8/31/2017.
 */

public class Pop extends AppCompatActivity
{
    private TextView home = null;
    private TextView away = null;
    private TextView competition = null;
    private TextView time = null;
    private TextView venue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popup_window);

        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * 0.65),(int)(height * 0.65));

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("county");

        try
        {
            if(message != null)
            {
                JSONObject jsonObj = new JSONObject(message);

                home = (TextView) findViewById(R.id.homeTeam);
                home.setText(jsonObj.get("homeTeam").toString());

                away = (TextView) findViewById(R.id.awayTeam);
                away.setText(jsonObj.getString("awayTeam").toString());

                competition = (TextView) findViewById(R.id.textView3);
                competition.setText(jsonObj.get("competition").toString());

                time = (TextView) findViewById(R.id.textView4);
                time.setText(jsonObj.get("time").toString());

                venue = (TextView) findViewById(R.id.textView5);
                venue.setText(jsonObj.get("venue").toString());
            }
        }
        catch(JSONException e)
        {

        }

    }
}
