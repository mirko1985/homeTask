package com.example.hometask.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.hometask.data.CharacterData
import com.example.hometask.data.Location
import com.example.hometask.data.Origin

@Database(entities = [CharacterData::class], version = 1)
@TypeConverters(CharacterDataTypeConverter::class)
abstract class RickAndMortyServiceDatabase : RoomDatabase() {
    abstract fun charactersDao(): CharactersDao
}

class CharacterDataTypeConverter {
    var gson: Gson = Gson()

    @TypeConverter
    fun fromListToJSON(list: List<String>): String {
        also {  }
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromJSONtoList(json: String): List<String> {
        return gson.fromJson(json, (object : TypeToken<ArrayList<String>>() {}).type)
    }

    @TypeConverter
    fun fromLocationToJSON(location: Location): String {
        return gson.toJson(location)
    }

    @TypeConverter
    fun fromJSONtoLocation(json: String): Location {
        return gson.fromJson(json, Location::class.java)
    }

    @TypeConverter
    fun fromOriginToJSON(origin: Origin): String {
        return gson.toJson(origin)
    }

    @TypeConverter
    fun fromJSONtoOrigin(json: String): Origin {
        return gson.fromJson(json, Origin::class.java)
    }
}

@Dao
interface CharactersDao {
    @Query("SELECT * FROM characterdata")
    fun getCharacters(): List<CharacterData>

    @Query("SELECT * FROM characterdata")
    fun getCharactersPaged(): PagingSource<Int, CharacterData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(charactersData: List<CharacterData>)

    @Query("DELETE FROM characterdata WHERE id in (SELECT id FROM characterdata ORDER BY id ASC LIMIT 20 OFFSET 20 * (:page - 1))")
    fun deletePage(page: Int)
}