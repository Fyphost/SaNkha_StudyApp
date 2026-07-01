package com.fyphost.sankhadip.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeDao {

    @Query("SELECT * FROM nodes")
    fun observeAll(): Flow<List<NodeEntity>>

    @Query("SELECT * FROM nodes")
    suspend fun getAll(): List<NodeEntity>

    @Query("SELECT * FROM nodes WHERE id = :id")
    suspend fun getById(id: String): NodeEntity?

    @Query("SELECT COUNT(*) FROM nodes WHERE isSubject = 1")
    suspend fun subjectCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(node: NodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(nodes: List<NodeEntity>)

    @Query("DELETE FROM nodes WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<String>)
}
