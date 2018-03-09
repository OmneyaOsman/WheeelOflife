package com.omni.wheeeloflife.utils;


import android.util.Log;

import com.omni.wheeeloflife.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static android.content.ContentValues.TAG;

public class AppConfig {
    public static final int REQUEST_CODE_ASK_PERMISSIONS_LOCATION = 123;
    public static final  String DATE_KEY="date";
    public static final  String TIME_KEY="time";
    public static final  int DATE_REQUEST_CODE= 4;
    public static final  int Time_REQUEST_CODE=5;
    public static final  int REMINDER_RQUEST_CODE=8;

    public static final  String FAMILY_CAT="Family";
    public static final  String FUN_CAT="Fun";
    public static final  String FINANCIAL_CAT="Financial";
    public static final  String EDUCATION_CAT="Education";
    public static final  String RELIGION_CAT="Religion";
    public static final  String TRAVEL_CAT="Travel";
    public static final  String CAREER_CAT="Career";
    public static final  String SOCIAL_LIFE_CAT="Social Life";
    public static final  String HEALTH_CAT="Health";
    public static final  String LOVE_CAT="Love";
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private static  final String BASE_URL_PART_ONE ="https://api.unsplash.com/search/photos/?page=1&per_page=1&query=";
    private static  final String APPLICATION_ID ="&client_id=fd54017ac3a101131fdefd61036782207bf1e9f5768783f3ce9dbebf9065c199";




    public static String fetchPhotoURL(String query) {
        URL url = createURL(BASE_URL_PART_ONE.concat(query).concat(APPLICATION_ID));
        String jsonString = "";
        try {
            jsonString = makeHttpRequest(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractPhotoURL(jsonString);

    }

    private static String extractPhotoURL(String splashJSON) {


        String poster = "";
        try {
            JSONObject jsonObject = new JSONObject(splashJSON);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);
                JSONObject urls = resultObject.getJSONObject("urls");
                 poster = urls.getString("regular");
                Log.d(TAG, "extractPhotoURL: " +poster);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return poster;
    }

    // ... create URL from url String
    private static URL createURL(String stringURL) {
        URL url = null;

        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;


        //...establish httpURLConnection
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(1000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //...check if connection successful to read inputStream
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }


    public static int toolbarColor(String category) {
        int mColor = R.color.seashell;
        switch (category) {
            case AppConfig.FAMILY_CAT:
                mColor = R.color.pink;
                break;
            case AppConfig.FUN_CAT:
                mColor = R.color.deep_orange;
                break;
            case AppConfig.FINANCIAL_CAT:
                mColor = R.color.green;
                break;
            case AppConfig.EDUCATION_CAT:
                mColor = R.color.brawn;
                break;
            case AppConfig.TRAVEL_CAT:
                mColor = R.color.orange;
                break;
            case AppConfig.SOCIAL_LIFE_CAT:
                mColor = R.color.purple;
                break;
            case AppConfig.LOVE_CAT:
                mColor = R.color.red;
                break;
            case AppConfig.RELIGION_CAT:
                mColor = R.color.light_green;
                break;
            case AppConfig.HEALTH_CAT:
                mColor = R.color.blue;
                break;
            case AppConfig.CAREER_CAT:
                mColor = R.color.turbo;
                break;
        }
        return  mColor ;
    }
}
