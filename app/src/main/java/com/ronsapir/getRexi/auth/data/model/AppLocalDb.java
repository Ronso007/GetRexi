package com.ronsapir.getRexi.auth.data.model;

import androidx.room.Room;

import com.ronsapir.getRexi.AppContext;

public class AppLocalDb {
    private AppLocalDb() {}

    static public AppLocalDbRepository getAppDb() {
        return Room.databaseBuilder(AppContext.getMyContext(),
                        AppLocalDbRepository.class,
                        "dogs.db")
                .fallbackToDestructiveMigration()
                .build();
    }
}

