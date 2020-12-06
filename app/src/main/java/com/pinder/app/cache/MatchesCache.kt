package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
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
        val mNumbersLd: MutableLiveData<ArrayList<MatchesObject>> = MutableLiveData()
        val matchesArrayList: ArrayList<MatchesObject> = arrayListOf()
        mNumbersLd.value = matchesArrayList
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
                    mNumbersLd.value = matchesArrayList
                    Log.d(TAG, "Getting from cache finished")
                    return mNumbersLd
                }
            }
        }
        Log.d(TAG, "getNumbers: numbers:$matchesArrayList")
        return mNumbersLd
    }

    fun saveMatches(matches: ArrayList<MatchesObject>) {
        Log.d(TAG, "SAVING TO DB")
        try {
            Log.d(TAG, "matches: $matches")
            val mutableDoc = MutableDocument("matches")
            for (matchObject in matches) {
                Log.d(TAG, "Saving to CACHE : ${matchObject.userId}")
                val map: Map<String, Any> = mapper.convertValue(matchObject, object : TypeReference<Map<String?, Any?>?>() {})
                Log.d(TAG,"Saving map: $map")
                mutableDoc.setValue(matchObject.userId, map)
            }
            // Save it to the database.
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

}