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

package com.bwaim.newsapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bwaim.newsapp.R;
import com.bwaim.newsapp.model.News;

import java.util.List;

/**
 * Created by Fabien Boismoreau on 10/05/2018.
 * <p>
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private List<News> mData;

    public NewsAdapter(List<News> data) {
        this.mData = data;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link RecyclerView.Adapter<>.onBindViewHolder(ViewHolder, int, List)}. Since it will
     * be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_item, parent, false);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return new ViewHolder(cardView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link RecyclerView.Adapter<>.onBindViewHolder(ViewHolder, int, List)} instead
     * if Adapter can handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News news = mData.get(position);
        holder.mTitle.setText(news.getTitle());
        holder.mSection.setText(news.getSectionName());
        holder.mPublicationDate.setText(news.getPublishingDate());
        holder.mAuthor.setText(news.getAuthor());
        holder.mTrailText.setText(news.getTrailText());
        holder.mImage.setImageDrawable(news.getImg());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<News> getData() {
        return mData;
    }

    public void setData(List<News> mData) {
        this.mData = mData;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTitle;
        private TextView mSection;
        private TextView mPublicationDate;
        private TextView mAuthor;
        private TextView mTrailText;
        private ImageView mImage;

        ViewHolder(View v) {
            super(v);

            mTitle = v.findViewById(R.id.title_TV);
            mSection = v.findViewById(R.id.section_TV);
            mPublicationDate = v.findViewById(R.id.publication_date_TV);
            mAuthor = v.findViewById(R.id.author_TV);
            mTrailText = v.findViewById(R.id.trail_text_TV);
            mImage = v.findViewById(R.id.image_IV);
        }
    }
}
