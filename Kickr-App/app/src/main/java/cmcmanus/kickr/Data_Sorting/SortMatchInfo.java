package cmcmanus.kickr.Data_Sorting;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cmcmanus.kickr.Custom_Objects.MatchObj;

/**
 * Created by cmcmanus on 11/27/2017.
 */

public class SortMatchInfo
{
    //list of match objects
    private ArrayList<MatchObj> sortedMatches = null;

    public SortMatchInfo()
    {
    }

    public Map<String, List<MatchObj>> setMatchesByComp(ArrayList<MatchObj> matchList, String date,String sortByInput)
    {
        //instantiate the lists
        ArrayList<MatchObj> sortList = new ArrayList<MatchObj>();
        ArrayList<MatchObj> matchObjToBeSorted = new ArrayList<MatchObj>();
        //create list of times in ascending order
        ArrayList<String> matchTimes = new ArrayList<String>();
        ArrayList<MatchObj> dateSortedMatches = new ArrayList<MatchObj>();

        //parse date from string
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        //get current date
        Date currDate = new Date();
        //format to match our own specific date format
        String currDateStr = sdf.format(currDate);

        //for each match in the list of returned matches
        for(int j=0; j < matchList.size(); j++) {
            //check to make sure any fixtures that have already taken place are not shown to the user.
            try {
                //get date variables
                Date date1 = sdf.parse(currDateStr);
                Date date2 = sdf.parse(matchList.get(j).getDate());

                //check is after
                if (date1.after(date2))
                {

                }
                else
                {
                    dateSortedMatches.add(matchList.get(j));
                }
            } catch (ParseException e) {
                Log.e("ERROR", "Date Parse Exception: " + e);
            }
        }

        if(dateSortedMatches.size() == 0)
        {
            return null;
        }

        for(int k=0;k<dateSortedMatches.size();k++)
        {
            //make sure we sort by date ONLY if we are not searching by team
            if(sortByInput == null || !sortByInput.equals("sort") || !sortByInput.equals("team") || !sortByInput.equals("comp"))
            {
                //check to make sure we are sorting matches for todays date
                if(date.equals(matchList.get(k).getDate()))
                {
                    if(!matchTimes.contains(matchList.get(k).getTime()))
                    {
                        //put all the times of todays matches in a list
                        matchTimes.add(matchTimes.listIterator().nextIndex(),matchList.get(k).getTime());
                        matchObjToBeSorted.add(matchObjToBeSorted.listIterator().nextIndex(),matchList.get(k));
                    }
                    else
                    {
                        matchObjToBeSorted.add(matchObjToBeSorted.listIterator().nextIndex(),matchList.get(k));
                    }
                }
            }
        }

        Map<String, List<MatchObj>> map = new HashMap<String, List<MatchObj>>();

        //we cannot sort this if we have a sortByInput
        if(null == sortByInput || sortByInput.equals(""))
        {
            //sort the list by time.
            Collections.sort(matchTimes, Collections.<String>reverseOrder());

            //place the match objects into a sorted object list
            for(int k=0; k < matchTimes.size(); k++)
            {
                for(int l=0; l < matchObjToBeSorted.size(); l++)
                {
                    if(matchTimes.get(k).equals(matchObjToBeSorted.get(l).getTime()))
                    {
                        sortList.add(sortList.listIterator().nextIndex(),matchObjToBeSorted.get(l));
                    }
                }
            }
        }
        else
        {
            if(matchObjToBeSorted.size() == 0)
            {
                sortList = matchList;
            }
            else
            {
                sortList = matchObjToBeSorted;
            }
        }

        //sort by competition
        Collections.sort(sortList,new SortByComp());

        for (MatchObj match : sortList)
        {
            //create new list of matches and add them based on the map key
            List<MatchObj> list = map.get(match.getCompetition());
            if (list == null)
            {
                list = new ArrayList<MatchObj>();
                map.put(match.getCompetition(), list);
            }

            list.add(match);
        }

        //sort matches by key alphabetically
        Map<String,List<MatchObj>> sortedMap = new TreeMap<String,List<MatchObj>>(map);

        return sortedMap;
    }


    public void resetData()
    {
        this.sortedMatches = null;
    }

    class SortByComp implements Comparator<MatchObj>
    {
        // Used for sorting in ascending order of competition name
        public int compare(MatchObj a, MatchObj b)
        {
            return b.getCompetition().compareTo(a.getCompetition());
        }
    }
}
