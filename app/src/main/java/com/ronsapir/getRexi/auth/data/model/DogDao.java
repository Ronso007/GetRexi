package com.ronsapir.getRexi.auth.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;
@Dao
public interface DogDao {
    @Query("select * from Dog")
    LiveData<List<Dog>> getAll();

    @Query("select * from Dog where userId = :userId")
    LiveData<List<Dog>> getAllByUserId(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Dog... dogs);
}
