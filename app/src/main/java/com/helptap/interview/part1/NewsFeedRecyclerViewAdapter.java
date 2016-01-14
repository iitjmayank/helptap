package com.helptap.interview.part1;

/**
 * Created by maagarwa on 1/14/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.helptap.interview.R;

public class NewsFeedRecyclerViewAdapter extends RecyclerView.Adapter<NewsFeedRecyclerViewAdapter.NewsFeedViewHolder> {

    private Context mContext;
    NewsFeedDbHelper dbHelper;

    public NewsFeedRecyclerViewAdapter(Context context) {
        mContext = context;
        dbHelper = new NewsFeedDbHelper(context);
    }

    public class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        TextView mNewsFeedTitleView, mNewsFeedContentView;

        public NewsFeedViewHolder(View itemView) {
            super(itemView);
            mNewsFeedTitleView = (TextView) itemView.findViewById(R.id.news_feed_title);
            mNewsFeedContentView = (TextView) itemView.findViewById(R.id.news_feed_content);
        }
    }

    @Override
    public NewsFeedViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflatedView = LayoutInflater.from(mContext).inflate(R.layout.news_feed_view, viewGroup, false);
        return new NewsFeedViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(NewsFeedViewHolder viewHolder, int i) {
        final NewsFeedModel model = dbHelper.getNewsFeed(i+1);
        viewHolder.mNewsFeedTitleView.setText(Html.fromHtml(model.title));
        viewHolder.mNewsFeedContentView.setText(Html.fromHtml(model.contentSnippet));
        viewHolder.mNewsFeedContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(model.link));
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (int)dbHelper.getTableSize();
    }
}
