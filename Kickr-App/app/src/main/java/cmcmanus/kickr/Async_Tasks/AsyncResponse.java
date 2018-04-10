package cmcmanus.kickr.Async_Tasks;

import org.json.JSONArray;

import java.util.ArrayList;

import cmcmanus.kickr.Custom_Objects.MatchObj;

/**
 * Created by cmcmanus on 11/29/2017.
 */

public interface AsyncResponse
{
    void processFinish(ArrayList<MatchObj> matchList);

    void processDBQueries(ArrayList<MatchObj> resultData);
}
