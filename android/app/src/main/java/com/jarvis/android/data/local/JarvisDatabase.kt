package com.jarvis.android.data.local

import androidx.room.*

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :cid ORDER BY timestamp ASC")
    suspend fun getMessages(cid: String): List<MessageEntity>

    @Insert
    suspend fun insert(msg: MessageEntity)

    @Query("DELETE FROM messages WHERE conversationId = :cid")
    suspend fun clear(cid: String)
}

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
