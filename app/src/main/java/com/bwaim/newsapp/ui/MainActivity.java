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

package com.bwaim.newsapp.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bwaim.newsapp.R;
import com.bwaim.newsapp.adapters.NewsAdapter;
import com.bwaim.newsapp.loaders.NewsLoader;
import com.bwaim.newsapp.model.News;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>> {

    private static final int MAIN_ACTIVITY_LOADER = 1;

    private static final String QUERY = "QUERY";

    private static final String BASE_URL = "http://content.guardianapis.com/search";

    private static final String OR_SEPARATOR = ",";

    /**
     * Views of the layout
     */
    private RecyclerView mRecyclerView;
    private TextView mEmptyListTV;
    private ProgressBar mProgressBarPB;

    private NewsAdapter mNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get all necessary views
        mRecyclerView = findViewById(R.id.recycler_view_RV);
        mEmptyListTV = findViewById(R.id.empty_list_TV);
        mProgressBarPB = findViewById(R.id.progress_bar_PB);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mNewsAdapter = new NewsAdapter(new ArrayList<News>());
        mRecyclerView.setAdapter(mNewsAdapter);
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        String query = buildQuery();
        refreshNews(query);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings_action) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setEmptyList() {
        mEmptyListTV.setText(R.string.no_news);
        mEmptyListTV.setVisibility(View.VISIBLE);
    }

    private void setNoNetworkView() {
        mProgressBarPB.setVisibility(View.GONE);
        mEmptyListTV.setText(R.string.no_internet);
        mEmptyListTV.setVisibility(View.VISIBLE);
    }

    private String buildQuery() {
//        "http://content.guardianapis.com/search?q=sport&show-fields=trailText,thumbnail&show-tags=contributor&api-key="
//                + BuildConfig.MY_GUARDIAN_API_KEY;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String limit = preferences.getString(getString(R.string.settings_limit_key)
                , getString(R.string.settings_limit_default));

        HashSet<String> defaultSection = new HashSet<>();
        defaultSection.add(getString(R.string.settings_section_default));

        Set<String> sectionsSet = preferences.getStringSet(getString(R.string.settings_section_key)
                , defaultSection);
        StringBuilder sections = new StringBuilder();
        boolean first = true;
        for (String section : sectionsSet) {
            if (first) {
                first = false;
            } else {
                sections.append(OR_SEPARATOR);
            }
            sections.append(section);
        }

        Uri baseUri = Uri.parse(BASE_URL);

        Uri.Builder queryBuilder = baseUri.buildUpon();
        if (!sections.toString().isEmpty()
                && !sections.toString().contains(getString(R.string.settings_section_default))) {
            queryBuilder.appendQueryParameter("q", sections.toString());
        }
        queryBuilder.appendQueryParameter("show-fields", "trailText,thumbnail");
        queryBuilder.appendQueryParameter("show-tags", "contributor");
        queryBuilder.appendQueryParameter("api-key", "test"/*BuildConfig.MY_GUARDIAN_API_KEY*/);
        queryBuilder.appendQueryParameter("page-size", limit);

        return queryBuilder.toString();
    }

    private void refreshNews(String url) {
        if (isConnected()) {
            Bundle args = new Bundle();
            args.putString(QUERY, url);

            getLoaderManager().initLoader(MAIN_ACTIVITY_LOADER, args, this);
        } else {
            setNoNetworkView();
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        String query = "";
        if (args != null) {
            query = args.getString(QUERY);
        }
        return new NewsLoader(this, query);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link android.app.FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context, Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        mProgressBarPB.setVisibility(View.GONE);
        if (data == null || data.isEmpty()) {
            setEmptyList();
            mNewsAdapter.setData(new ArrayList<News>());
            return;
        }

        mNewsAdapter.setData(data);
        mNewsAdapter.notifyDataSetChanged();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mNewsAdapter.setData(new ArrayList<News>());
        setEmptyList();
    }
}
