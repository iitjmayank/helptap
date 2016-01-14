package com.helptap.interview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.helptap.interview.part1.NewsFeedLoader;
import com.helptap.interview.part1.NewsFeedRecyclerViewAdapter;


/**
 * Created by maagarwa on 1/14/2016.
 */
public class Part1Fragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    NewsFeedRecyclerViewAdapter mNewsFeedRecyclerViewAdapter;
    Context mContenxt;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContenxt = getActivity();
        getLoaderManager().initLoader(1, null,this).forceLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.part1_fragment_layout, null);
    }

    @Override
    public android.support.v4.content.Loader<String> onCreateLoader(int id, Bundle args) {
        return new NewsFeedLoader(mContenxt);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String> loader) {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String> loader, String data) {
        switch (data) {
            case "Network":
                Toast.makeText(mContenxt, "Check your network", Toast.LENGTH_SHORT).show();
                break;
            case "JSON" :
                Toast.makeText(mContenxt, "Problem with network data", Toast.LENGTH_SHORT).show();
                break;
            case "PASS":
                Toast.makeText(mContenxt, "Data loaded", Toast.LENGTH_SHORT).show();
                mNewsFeedRecyclerViewAdapter.notifyDataSetChanged();
                break;
            case "FAIL":
                Toast.makeText(mContenxt, "Some Internal Error, please try again", Toast.LENGTH_SHORT).show();
                break;
        }
       // mRecyclerView.setAdapter(mNewsFeedRecyclerViewAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.activity_main_listview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContenxt);
        mRecyclerView.setLayoutManager(layoutManager);

        //setupAdapter();
        mNewsFeedRecyclerViewAdapter = new NewsFeedRecyclerViewAdapter(mContenxt);
        mRecyclerView.setAdapter(mNewsFeedRecyclerViewAdapter);
       // mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //setupAdapter();
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (isAdded())getLoaderManager().initLoader(1, null, Part1Fragment.this).forceLoad();
                    }
                }, 1000);
            }
        });
    }
}