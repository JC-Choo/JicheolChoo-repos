package dev.chu.memo.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson

class ConverterList {
    @TypeConverter
    fun listToJson(value: List<ImageData>?): String? {
        value?.let {
            return Gson().toJson(value)
        } ?: return null
    }

    @TypeConverter
    fun jsonToList(value: String?): List<ImageData>? {
        val objects = Gson().fromJson<Array<ImageData>>(value, Array<ImageData>::class.java)
        return objects?.toList()
    }
}
