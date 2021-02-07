package com.keepseung.roomdemo.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubscriberDAO {

    // 더해진 row의 primary key를 반환한다.
    // 에러 발생시 -1을 반환함
    @Insert
    suspend fun insertSubscriber(subscriber: Subscriber): Long

    // update된 row의 수를 반환
    @Update
    suspend fun updateSubscriber(subscriber: Subscriber):Int

    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber):Int

    @Query("DELETE FROM subscriber_data_table")
    suspend fun deleteAll():Int

    @Query("SELECT * FROM subscriber_data_table")
    fun getAllSubscribers():LiveData<List<Subscriber>>
}