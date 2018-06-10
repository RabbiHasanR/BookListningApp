package com.example.diu.booklistingapp;

import android.text.TextUtils;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    /** tag for the log message **/
    public static final String LOG_TAG=QueryUtils.class.getSimpleName();

    /**
     * Query the BOOK API dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl){
        //force the background thread sleep for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHTTPRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<Book> books = bookDetailsFromJsonForInfo(jsonResponse);
        Log.v(LOG_TAG,"fetchBookData");

        // Return the list of {@link Earthquake}s
        return books;

    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl){
        URL url=null;
        try{
            if(stringUrl!=null){
                url=new URL(stringUrl);
            }
        }
        catch (MalformedURLException e){
            Log.e(LOG_TAG,"Error with creating url",e);
        }
        return url;

    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */

    public static String makeHTTPRequest(URL url)throws IOException{
        String jasonResponse="";
        if(url==null){
            return jasonResponse;
        }
        HttpURLConnection httpURLConnection=null;
        InputStream inputStream=null;
        try{
            httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(15000 /* milliseconds */);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode()==200){
                inputStream=httpURLConnection.getInputStream();
                jasonResponse=readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }

        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        }
        finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jasonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream)throws IOException{
        StringBuilder output=new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();

    }

    /**
     * Return an {@link Book} object by parsing out information
     * about the first earthquake from the input earthquakeJSON string.
     */

    public static List<Book> bookDetailsFromJsonForInfo(String bookJSON){
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)){
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<Book> books = new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < bookArray.length(); i++) {
                JSONObject items_object = bookArray.getJSONObject(i);
                JSONObject volumeInfo = items_object.getJSONObject("volumeInfo");
                String previewLink = volumeInfo.getString("previewLink");
                String name = volumeInfo.getString("title");
                JSONArray authors;
                String author = "";
                JSONObject imageLinks;
                String imageUrl = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcR2Mlfd3Qo223fIgVdA1Fg9psRSNkZqmErDInIGz6c7yCcrwV5WYA";
                if (volumeInfo.has("imageLinks")) {
                    imageLinks = volumeInfo.getJSONObject("imageLinks");
                    imageUrl = imageLinks.getString("smallThumbnail");
                }
                if (volumeInfo.has("authors")) {
                    authors = volumeInfo.getJSONArray("authors");
                    author = authors.getString(0);
                }
                books.add(new Book(name, author, imageUrl, previewLink));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return books;
    }

}
