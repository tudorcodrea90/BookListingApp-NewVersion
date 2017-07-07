package com.example.android.booklistingapp_newversion;

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

import static com.example.android.booklistingapp_newversion.BookActivity.LOG_TAG;

/**
 * Created by Tudor Cristian on 07.07.2017.
 */

public class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google Books data set and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl){

        //create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try{
            jsonResponse = makeHttpRequest(url);
        }
        catch (IOException e){
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
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

            // For each book in the booklist, create an {@link booklist} object
            for (int i = 0; i < bookArray.length(); i++) {

                // Get a single earthquake at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all properties
                // for that book.
                JSONObject properties = currentBook.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                String title  = "";
                if(properties.has("title")){
                    title = properties.getString("title");
                }


                // Extract the array of authors
                String [] authors = new String[]{};

                if(properties.has("authors")) {
                    JSONArray authorJsonArray = properties.getJSONArray("authors");
                    ArrayList<String> authorList = new ArrayList<String>();
                    for (int j = 0; j < authorJsonArray.length(); j++) {
                        authorList.add(authorJsonArray.get(j).toString());
                    }
                    authors = authorList.toArray(new String[authorList.size()]);
                }


                //Extract value for the key called "description"
                String description = "";
                if(properties.has("description"))
                    description = properties.getString("description");
                else{
                    description = "No description available.";
                }

                //Extract value for images from key called "imageLinks"
                String thumbnailLink = "";
                if(properties.has("imageLinks")){
                    thumbnailLink = properties.getJSONObject("imageLinks").getString("thumbnail");
                }


                // Extract the value for the key called "url"
                String url = "";
                if(properties.has("infoLink")){
                    url = properties.getString("infoLink");
                }


                // Create a new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                Book book = new Book(title, authors, description, thumbnailLink, url);

                // Add the new {@link Earthquake} to the list of earthquakes.
                books.add(book);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of earthquakes
        return books;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
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
}
