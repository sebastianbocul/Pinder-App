package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.pinder.app.models.PopularTagsObject
import com.pinder.app.util.Constants
import com.pinder.app.util.Resource
import com.pinder.app.util.Resource.success


class PopularTagsCache constructor(private var context: Context) {
    private var mDatabase: Database? = null
    private val TAG = "PopularTagsCache"
    val popularTagsLD: MutableLiveData<List<PopularTagsObject>> = MutableLiveData()
    val mapper = ObjectMapper()

    init {
        init()
    }

    private fun init() {
        // Get the database (if exists)
        val config = DatabaseConfiguration()
        mDatabase = Database(Constants.DB_NAME, config)
    }

    fun getPopularTags(): MutableLiveData<List<PopularTagsObject>>? {
        popularTagsLD.value= arrayListOf()
        Log.d(TAG, "GET POPULAR TAGS")
        val popularTagsList: ArrayList<PopularTagsObject>? = arrayListOf()
        Log.d(TAG, context.filesDir.absolutePath)
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            mDatabase?.let {
                Log.d(TAG, "database!=null:")
                val document: Document? = mDatabase!!.getDocument("popular_tags")
                document?.let {
                    Log.d(TAG, "document!=null:")
                    document.forEach {tagName->
                        Log.d(TAG, "Getting from CACHE: $it")
                        val popularity = document.getInt(tagName)
                        val popularTagsObject: PopularTagsObject = PopularTagsObject(tagName,popularity)
                        popularTagsList?.add(popularTagsObject)
                    }
                    Log.d(TAG, "Getting from CACHE: $popularTagsList")
                    popularTagsLD.value = popularTagsList
                    Log.d(TAG, "Getting from cache finished")
                }
            }
        }
        return popularTagsLD
    }

    fun savePopularTags(popularTags: ArrayList<PopularTagsObject>) {
        Log.d(TAG, "SAVING TO DB")
        try {
            Log.d(TAG, "popular tags: $popularTags")
            val mutableDoc = MutableDocument("popular_tags")
            for (popularTagObject in popularTags) {
                Log.d(TAG, "Saving to CACHE :")
                mutableDoc.setValue(popularTagObject.tagName, popularTagObject.tagPopularity)
            }
            // Save it to the database.
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}

