package com.ronsapir.getRexi.auth.data.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Dog.class}, version = 5)
public abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract DogDao dogDao();
}
