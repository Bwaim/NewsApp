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

import android.util.Log;

import com.bwaim.newsapp.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                news.setPublishingDate((Date) jsonNews.get("webPublicationDate"));
                JSONArray references = jsonNews.getJSONArray("references");
                for (int j = 0; j < references.length(); j++) {
                    JSONObject reference = references.getJSONObject(j);
                    if (reference.getString("type").equals("author")) {
                        news.setAuthor(reference.getString("id"));
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error during json parsing", e);
        }

        return newsList;
    }
}
