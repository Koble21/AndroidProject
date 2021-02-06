package com.example.restaurantproject.data
import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.net.Inet4Address


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM users ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>
    @Query("DELETE FROM users")
    fun deleteTheUsers()
    @Query("SELECT * FROM users LIMIT 1")
    fun readUserData():LiveData<User>
    @Query("UPDATE users SET name=:name, address=:address, email=:email, phone=:phone, image=:image WHERE id>0")
    fun updateUserDatas(name:String, address: String, email: String, phone:String, image:ByteArray)
}