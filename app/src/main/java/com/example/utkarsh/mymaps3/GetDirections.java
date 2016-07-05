package com.example.utkarsh.mymaps3;

/**
 * Created by utkarsh on 1/7/16.
 */
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class GetDirections {

    Listener listener;

    public GetDirections(Listener listener) {
        this.listener = listener;
    }

    public void startGettingDirections(String downloadUrl) {
        new DirectionsTask().execute(downloadUrl);
    }

    class DirectionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = null;
            try {
                // Fetching the data from Google Service
                data = GetDataFromUrl.getDataFromUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            // Parse JSON data received from Google
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                RoutesJsonParser parser = new RoutesJsonParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                listener.onFail();
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            super.onPostExecute(result);
            if (result != null && result.size() > 0)
                listener.onSuccessfullRouteFetch(result);
            else
                listener.onFail();
        }
    }
}