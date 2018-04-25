package com.example.android.sunshine.ui.list;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.sunshine.data.SunshineRepository;
import com.example.android.sunshine.data.database.WeatherEntry;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private SunshineRepository mRepository;

    public LiveData<List<WeatherEntry>> getWeatherForcastList() {
        return mWeatherList;
    }

    private LiveData<List<WeatherEntry>> mWeatherList;

    public MainActivityViewModel(SunshineRepository mRepository){
        this.mRepository = mRepository;
        mWeatherList = mRepository.getWeatherForcastList();
    }

    public void addWeatherInDb(WeatherEntry weatherEntry){
        mRepository.addWeatherEntry(weatherEntry);
    }

}
