/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.ui.list;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.sunshine.R;
import com.example.android.sunshine.data.database.WeatherEntry;
import com.example.android.sunshine.ui.detail.DetailActivity;
import com.example.android.sunshine.utilities.InjectorUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Displays a list of the next 14 days of forecasts
 */
public class MainActivity extends LifecycleActivity implements
        ForecastAdapter.ForecastAdapterOnItemClickHandler {

    private ForecastAdapter mForecastAdapter;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private ProgressBar mLoadingIndicator;
    private MainActivityViewModel mViewModel;
    private FloatingActionButton fabEdit;
    private List<WeatherEntry> list;
    private int day = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        MainViewModelFactory factory = InjectorUtils.provideMainActivityViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, factory).get(MainActivityViewModel.class);

        mRecyclerView = findViewById(R.id.recyclerview_forecast);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        fabEdit = findViewById(R.id.fab_edit_db);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mForecastAdapter = new ForecastAdapter(this, this);

        mRecyclerView.setAdapter(mForecastAdapter);
        showLoading();

        mViewModel.getWeatherForcastList().observe(this, weatherEntries -> {
            if(weatherEntries != null && weatherEntries.size() != 0) {
                list = new ArrayList<>(weatherEntries);
                mForecastAdapter.swapForecast(weatherEntries);
                showWeatherDataView();
            }else {
                showLoading();
            }
        });

        fabEdit.setOnClickListener(view -> {
            Date date = list.get(0).getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, day++);
            WeatherEntry weatherEntry = new WeatherEntry(300, calendar.getTime(), 22, 22, 22, 22, 22, 22);
            mViewModel.addWeatherInDb(weatherEntry);
        });
    }

    /**
     * This method is for responding to clicks from our list.
     *
     * @param date Date of forecast
     */
    @Override
    public void onItemClick(Date date) {
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        long timestamp = date.getTime();
        weatherDetailIntent.putExtra(DetailActivity.WEATHER_ID_EXTRA, timestamp);
        startActivity(weatherDetailIntent);
    }

    /**
     * This method will make the View for the weather data visible and hide the error message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showWeatherDataView() {
        // First, hide the loading indicator
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        // Finally, make sure the weather data is visible
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the weather View and error
     * message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading() {
        // Then, hide the weather data
        mRecyclerView.setVisibility(View.INVISIBLE);
        // Finally, show the loading indicator
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }
}
