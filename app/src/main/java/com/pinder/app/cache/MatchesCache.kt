package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
import com.couchbase.lite.Array
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pinder.app.models.MatchesObject
import com.pinder.app.util.Constants


class MatchesCache constructor(private var context: Context) {
    private var mDatabase: Database? = null
    private var matches: ArrayList<MatchesObject>? = arrayListOf()
    private val TAG = "MatchesCache"
    val mapper = ObjectMapper()

    init {
        init()
    }

    private fun init() {
        // Get the database (if exists)
        val config = DatabaseConfiguration()
        mDatabase = Database(Constants.DB_NAME, config)
    }

    fun getMatches(): MutableLiveData<ArrayList<MatchesObject>>? {
        Log.d(TAG, "GET MATCHES")
        val matchesLD: MutableLiveData<ArrayList<MatchesObject>> = MutableLiveData()
        val matchesArrayList: ArrayList<MatchesObject> = arrayListOf()
        matchesLD.value = matchesArrayList
        Log.d(TAG,context!!.filesDir.absolutePath)
        if (Database.exists(Constants.DB_NAME, context!!.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("matches")
                if (document != null) {
                    document.forEach {
                        Log.d(TAG, "Getting from CACHE: $it")
                        val doc = document.getDictionary(it)
                        val matchObject: MatchesObject = mapper.convertValue(doc.toMap(), MatchesObject::class.java)
                        matchesArrayList.add(matchObject)
                    }
                    matchesLD.value = matchesArrayList
                    Log.d(TAG, "Getting from cache finished")
                    return matchesLD
                }
            }
        }
        Log.d(TAG, "getNumbers: numbers:$matchesArrayList")
        return matchesLD
    }

    fun saveMatches(matches: ArrayList<MatchesObject>) {
        Log.d(TAG, "SAVING TO DB")
        try {
            Log.d(TAG, "matches: $matches")
            val mutableDoc = MutableDocument("matches")
            for (matchObject in matches) {
                Log.d(TAG, "Saving to CACHE : ${matchObject.userId}")
                val map: Map<String, Any> = mapper.convertValue(matchObject, object : TypeReference<Map<String?, Any?>?>() {})
                mutableDoc.setValue(matchObject.userId, map)
            }
            // Save it to the database.
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun getTags(): MutableLiveData<ArrayList<String>>? {
        Log.d(TAG, "GET MATCHES FROM CACHE")
        val tagsLD: MutableLiveData<ArrayList<String>> = MutableLiveData()
        var tags: ArrayList<String>? = arrayListOf()
        tagsLD.value = tags
        Log.d(TAG,context!!.filesDir.absolutePath)
        if (Database.exists(Constants.DB_NAME, context!!.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("tags")
                if (document != null) {
                    val arrayCouchBase: Array? = document.getArray("mytags")
                    tags = arrayCouchBase?.toList() as ArrayList<String>?
                    tagsLD.value = tags
                    Log.d(TAG, "Getting from cache finished")
                    return tagsLD
                }
            }
        }
        Log.d(TAG, "getNumbers: numbers:$tags")
        return tagsLD
    }


    fun saveTags(tags: ArrayList<String>) {
        Log.d(TAG, "SAVING TAGS TO CACHE")
        try {
            Log.d(TAG, "tags: $tags")
            val mutableDoc = MutableDocument("tags")
            var mutablearray:MutableArray = MutableArray(tags.toList())
            mutableDoc.setArray("mytags",mutablearray)
            // Save it to the database.
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

}

