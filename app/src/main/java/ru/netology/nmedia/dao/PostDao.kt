package ru.netology.nmedia.dao

import ru.netology.nmedia.entity.PostEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    //    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    @Query("SELECT * FROM PostEntity WHERE display = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>


    @Query("UPDATE PostEntity SET display = 1")
    suspend fun updatePosts()

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query(
        """
           UPDATE PostEntity SET
              id = :newId
           WHERE id = :oldId;
        """
    )
    suspend fun updatePostId(newId: Long, oldId: Long)

    @Query(
        """
           UPDATE PostEntity SET
               likes = likes + CASE WHEN likedByMe THEN -1 ELSE 1 END,
               likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
           WHERE id = :id;
        """
    )
    suspend fun likeById(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
                shares = shares + 1,
                sharedByMe = 1
           WHERE id = :id;
        """
    )
    suspend fun shareById(id: Long)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)
}
