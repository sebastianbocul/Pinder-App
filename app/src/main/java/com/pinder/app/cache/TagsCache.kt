package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pinder.app.models.TagsObject
import com.pinder.app.util.Constants
import kotlin.math.log

class TagsCache constructor(private var context: Context) {
    private var mDatabase: Database? = null
    private val TAG = "TagsCache"
    val tagsLD: MutableLiveData<List<TagsObject>> = MutableLiveData()
    val mapper = ObjectMapper()

    init {
        init()
    }

    private fun init() {
        // Get the database (if exists)
        val config = DatabaseConfiguration()
        mDatabase = Database(Constants.DB_NAME, config)
    }

    fun getTags(): MutableLiveData<List<TagsObject>>? {
        Log.d(TAG, "GET POPULAR TAGS")
        tagsLD.value = arrayListOf()
        val tagsList: ArrayList<TagsObject>? = arrayListOf()
        Log.d(TAG, context.filesDir.absolutePath)
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            mDatabase?.let {
                Log.d(TAG, "database!=null:")
                val document: Document? = mDatabase!!.getDocument("tags")
                document?.let {
                    Log.d(TAG, "document!=null:")
                    document.forEach {
                        Log.d(TAG, "Getting from CACHE: $it")
                        val doc = document.getDictionary(it)
                        doc?.let {
                            Log.d(TAG, "getTags tag item: ${it.toMap()}")
                            val tagsObject: TagsObject = mapper.convertValue(doc.toMap(), TagsObject::class.java)
                            tagsList?.add(tagsObject)
                        }
                    }
                    Log.d(TAG, "Getting from CACHE: $tagsList")
                    tagsLD.value = tagsList
                    Log.d(TAG, "Getting from cache finished")
                }
            }
        }
        return tagsLD
    }

    fun saveTags(tags: ArrayList<TagsObject>) {
        Log.d(TAG, "SAVING TO DB")
        try {
            Log.d(TAG, "popular tags: $tags")
            val mutableDoc = MutableDocument("tags")
            for (tagObject in tags) {
                Log.d(TAG, "Saving to CACHE : ${tagObject}")
                val map: Map<String, String> = mapper.convertValue(tagObject, object : TypeReference<Map<String?, String?>?>() {})
                mutableDoc.setValue(tagObject.tagName, map.toSortedMap())
            }
            // Save it to the database.
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}

