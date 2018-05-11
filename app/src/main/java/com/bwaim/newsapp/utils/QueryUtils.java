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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Fabien Boismoreau on 10/05/2018.
 * <p>
 */
public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final int READ_TIMEOUT = 10000;

    private static final int CONNECTION_TIMEOUT = 15000;

    private static final int SUCCESS_CODE = 200;

    // Make the class static
    private QueryUtils() {
    }

    public static URL createURL(String query) {
        URL url = null;
        try {
            url = new URL(query);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "URL not valid", e);
        }
        return url;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder input = new StringBuilder();
        String line = reader.readLine();

        while (line != null && !line.isEmpty()) {
            input.append(line);
            line = reader.readLine();
        }

        return input.toString();
    }

    public static String makeHttpRequest(String query) {

        if (query == null || query.isEmpty()) {
            return null;
        }

        URL url = createURL(query);

        HttpURLConnection connection = null;
        String jsonResponse = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            if (connection.getResponseCode() == SUCCESS_CODE) {
                inputStream = connection.getInputStream();
                jsonResponse = readInputStream(inputStream);
            } else {
                Log.e(LOG_TAG, "makeHttpRequest error : " + connection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving result", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing the inputStream", e);
                }
            }
        }

        return jsonResponse;
    }
}
