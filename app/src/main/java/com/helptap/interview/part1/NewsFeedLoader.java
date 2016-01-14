package com.helptap.interview.part1;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by maagarwa on 1/14/2016.
 */
public class NewsFeedLoader extends AsyncTaskLoader<String> {

    public static final String SERVER_URL = "https://ajax.googleapis.com/ajax/services/feed/find?v=1.0&q=apple";
    private Context mContext;

    String TAG = "NewsFeedLoader";

    public NewsFeedLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public String loadInBackground() {
        try {
            Log.d(TAG, "Start loading");
            URL url = new URL(SERVER_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Log.d(TAG, "Connection stablish");
                try {
                    Reader reader = new InputStreamReader(urlConnection.getInputStream());
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(NewsFeedModel[].class, new NewsFeedDeserializer());
                    Gson gson = gsonBuilder.create();
                    List<NewsFeedModel> newsFeeds;
                    newsFeeds = Arrays.asList(gson.fromJson(reader, NewsFeedModel[].class));
                    NewsFeedDbHelper dbHelper = new NewsFeedDbHelper(mContext);
                    dbHelper.insertNewsFeed(newsFeeds);
                    //TODO: update recycle view
                    return "PASS";
                }catch (Exception e) {
                    return "JSON";
                }
            }
        }catch (Exception e) {
            return "Network";
        }
        return "FAIL";
    }

    private static class NewsFeedDeserializer implements JsonDeserializer<NewsFeedModel[]> {

        @Override
        public NewsFeedModel[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final JsonObject jsonObject = json.getAsJsonObject();
            final JsonArray jsonAuthorsArray = jsonObject.getAsJsonObject("responseData").getAsJsonArray("entries");

            final NewsFeedModel[] models = new NewsFeedModel[jsonAuthorsArray.size()];

            int i = 0;
            for (JsonElement element : jsonAuthorsArray) {
                models[i] = context.deserialize(element,NewsFeedModel.class );
                i++;
            }
            return  models;
        }
    }
}
