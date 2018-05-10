/*
 *    Copyright 2018 Fabien Boismoreau
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.bwaim.newsapp.utils;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bwaim.newsapp.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fabien Boismoreau on 10/05/2018.
 * <p>
 */
public class NewsUtils {

    private static final String LOG_TAG = NewsUtils.class.getSimpleName();

    // Make the class static
    private NewsUtils() {
    }

    public static List<News> createNewsFromJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        List<News> newsList = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject jsonNews = results.getJSONObject(i);
                News news = new News(jsonNews.getString("webTitle")
                        , jsonNews.getString("sectionName"));
                news.setUrl(jsonNews.getString("webUrl"));

                String date = formatDate(jsonNews.getString("webPublicationDate"));
                news.setPublishingDate(date);

                JSONArray tags = jsonNews.getJSONArray("tags");
                for (int j = 0; j < tags.length(); j++) {
                    JSONObject tag = tags.getJSONObject(j);
                    if (tag.getString("type").equals("contributor")) {
                        news.setAuthor(tag.getString("webTitle"));
                    }
                }

                JSONObject fields = jsonNews.getJSONObject("fields");
                news.setTrailText(fields.getString("trailText"));

                Drawable img = getImage(fields.getString("thumbnail"));
                news.setImg(img);

                newsList.add(news);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error during json parsing", e);
        }

        return newsList;
    }

    private static String formatDate(String input) {
        SimpleDateFormat formatInput =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        SimpleDateFormat formatOutput =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String output = null;
        try {
            Date dt = formatInput.parse(input);
            output = formatOutput.format(dt);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error during formatting date", e);
        }
        return output;
    }

    private static Drawable getImage(String urlString) {
        URL url = QueryUtils.createURL(urlString);

        if (url == null) {
            return null;
        }

        Drawable img = null;
        try {
            InputStream inputStream = (InputStream) url.getContent();
            img = Drawable.createFromStream(inputStream, urlString);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error during image retrieving", e);
        }
        return img;
    }
}
