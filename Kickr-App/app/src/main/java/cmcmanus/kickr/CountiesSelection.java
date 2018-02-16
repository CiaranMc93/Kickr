package cmcmanus.kickr;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cmcmanus.kickr.Information.Information;

public class CountiesSelection extends AppCompatActivity {

    //expandable list adapter variables
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    //button variable
    Button info;

    List<String> leinster = new ArrayList<String>();
    List<String> munster = new ArrayList<String>();
    List<String> ulster = new ArrayList<String>();
    List<String> connacht = new ArrayList<String>();
    List<String> inter_county = new ArrayList<String>();

    String[] provinces = { "Leinster", "Munster","Inter County"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counties_selection);

        //method to display the provinces and the counties
        displayProvinces();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        //get the button
        info = (Button)findViewById(R.id.button2);

        listAdapter = new ExpandableListAdapter(CountiesSelection.this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        Intent i = null;

        info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), Information.class);
                startActivity(intent);
            }
        });
        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id)
            {
                //check if the user is online.
                if(isOnline())
                {
                    switch (groupPosition)
                    {
                        case 0:
                            //send the selected county to the fixture retrieval class
                            Intent i = new Intent(CountiesSelection.this,Fixtures.class);
                            i.putExtra("county",leinster.get(childPosition).toString());
                            startActivity(i);
                            break;
                        case 1:
                            //send the selected county to the fixture retrieval class
                            i = new Intent(CountiesSelection.this,Fixtures.class);
                            i.putExtra("county",munster.get(childPosition).toString());
                            startActivity(i);
                            break;
                        case 2:
                            //send the selected county to the fixture retrieval class
                            i = new Intent(CountiesSelection.this,Fixtures.class);
                            i.putExtra("county","inter-county");
                            startActivity(i);
                            break;
                        default: break;
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"No Network Connection",Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
    }

    public void displayProvinces()
    {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        leinster.add(0,"Laois");
        leinster.add(1,"Carlow");

        munster.add(0,"Cork");
        munster.add(1,"Waterford");

        ulster.add(0,"");
        connacht.add(0,"");
        inter_county.add(0,"Inter-County Football");

        //all all of the headers.
        listDataHeader.add(provinces[0]);
        listDataHeader.add(provinces[1]);
        listDataHeader.add(provinces[2]);

        listDataChild.put(listDataHeader.get(0), leinster);
        listDataChild.put(listDataHeader.get(1), munster);
        listDataChild.put(listDataHeader.get(2), inter_county);
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
