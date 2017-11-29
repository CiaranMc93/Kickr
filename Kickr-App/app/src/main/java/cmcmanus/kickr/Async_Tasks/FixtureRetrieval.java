package cmcmanus.kickr.Async_Tasks;

import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cmcmanus.kickr.Fixtures;

/**
 * Created by cmcmanus on 11/29/2017.
 */

public class FixtureRetrieval extends AsyncTask<Void, Void, String>
{
    private String countyName = "";

    public AsyncResponse delegate = null;

    public FixtureRetrieval(AsyncResponse delegate){
        this.delegate = delegate;
    }

    public FixtureRetrieval(String county)
    {
        countyName = county.toLowerCase();
    }

    @Override
    protected String doInBackground(Void... params)
    {
        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder sb = null;

        try
        {
            url = new URL("https://kickr-api.herokuapp.com/fixtures/" + countyName);

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader br = null;
            sb = new StringBuilder();

            String line;

            try {

                br = new BufferedReader(new InputStreamReader(in));

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (br != null)
                {
                    try
                    {
                        br.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }//end if
            }//end finally
        }//end try
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        //set the response to the result string
        return sb.toString();
    }


    @Override
    protected void onPostExecute(final String success)
    {

        if (success.equals(""))
        {

        }
        else
        {
            //initialise the buttons for the menu bar
            delegate.processFinish(success);
        }
    }

    @Override
    protected void onCancelled()
    {

    }
}
