package cmcmanus.kickr;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

public class CountiesSelection extends AppCompatActivity {

    String[] data = { "Laois", "Cork", "Carlow", "Waterford"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counties_selection);

        ArrayList<String> lst = new ArrayList<String>();
        GridView gvMain;

        ArrayAdapter<String> adapter;

        lst.addAll(Arrays.asList(data));
        adapter = new ArrayAdapter<String>(this, R.layout.county_list_row, R.id.profileText, lst);

        GridView gridView = (GridView) findViewById(R.id.countyGrid);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(isOnline())
                {
                    //send the selected county to the fixture retrieval class
                    Intent i = new Intent(CountiesSelection.this,Fixtures.class);
                    i.putExtra("county",((TextView) view).getText());
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Network Connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //check if there is a connection to the internet or not
    public boolean isOnline()
    {
        try
        {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            return  cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();

        } catch (Exception e) { return false; }
    }
}
