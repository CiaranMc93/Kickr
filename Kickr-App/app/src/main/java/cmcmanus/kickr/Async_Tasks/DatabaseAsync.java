package cmcmanus.kickr.Async_Tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import cmcmanus.kickr.Custom_Objects.MatchObj;
import cmcmanus.kickr.Data_Storage.DBAdapter;

/**
 * Created by cmcmanus on 2/26/2018.
 */

public class DatabaseAsync extends AsyncTask<String, Void, ArrayList<MatchObj>>
{
    //db adapter
    private DBAdapter db;

    String retrieval;
    String typeRetrieve;
    ArrayList<MatchObj> matchList;

    public AsyncResponse delegate = null;

    public DatabaseAsync(Context context, String typeRetrieve, String retrieve)
    {
        this.retrieval = retrieve.toLowerCase();
        this.typeRetrieve = typeRetrieve;
        this.db = new DBAdapter(context);
    }

    protected ArrayList<MatchObj> doInBackground(String... CRUD)
    {
        matchList = new ArrayList<MatchObj>();

        String crud = CRUD[0];

        switch (crud)
        {
            case "GET":
                //get all from database where county is county
                matchList = db.getAllMatches(retrieval);
                break;
        }
        delegate.processDBQueries(matchList);

        return matchList;
    }

    @Override
    protected void onCancelled()
    {

    }
}
