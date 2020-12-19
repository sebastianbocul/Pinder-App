package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
import com.pinder.app.util.Constants


class SettingsCache constructor(private var context: Context) {
    private var mDatabase: Database? = null
    private val TAG = "SettingsCache"
    private val dateLiveData = MutableLiveData<String?>()
    private val showMyLocation = MutableLiveData<Boolean?>()
    private val sortByDistance = MutableLiveData<Boolean?>()
    private val mutableDocument: MutableDocument

    init {
        init()
        val document: Document? = mDatabase!!.getDocument("settings")
        mutableDocument = document?.toMutable() ?: MutableDocument("settings")
    }

    private fun init() {
        // Get the database (if exists)
        val config = DatabaseConfiguration()
        mDatabase = Database(Constants.DB_NAME, config)
    }

    fun getDate(): LiveData<String?> {
        Log.d(TAG, "GET DATE")
        dateLiveData.value = ""
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("settings")
                if (document != null) {
                    var date: String? = document.getString("date")
                    dateLiveData.value = date
                    Log.d(TAG, "getDate: date:$date")
                    Log.d(TAG, "DOCUMENT LOG:  sort " + document.getBoolean("sort_by_distance"))
                    Log.d(TAG, "DOCUMENT LOG:  show " + document.getBoolean("show_my_location"))
                    Log.d(TAG, "DOCUMENT LOG:  date " + document.getString("date"))
                }
            }
        }
        return dateLiveData
    }

    fun setDate(date: String) {
        Log.d(TAG, "SET CACHE date $date")
        try {
            mutableDocument.setString("date", date)
            mDatabase!!.save(mutableDocument)
            Log.d(TAG, "SAVED date $date")
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun getShowMyLocation(): LiveData<Boolean?> {
        Log.d(TAG, "GET SHOW MY LOCATION")
        showMyLocation.value = null
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("settings")
                if (document != null) {
                    var showMyLocationBool: Boolean = document.getBoolean("show_my_location")
                    showMyLocation.value = showMyLocationBool
                    Log.d(TAG, "getDate: SHOW MY LOCATION:$showMyLocationBool")
                    Log.d(TAG, "DOCUMENT LOG:  sort " + document.getBoolean("sort_by_distance"))
                    Log.d(TAG, "DOCUMENT LOG:  show " + document.getBoolean("show_my_location"))
                    Log.d(TAG, "DOCUMENT LOG:  date " + document.getString("date"))
                }
            }
        }
        return showMyLocation
    }

    fun setShowMyLocation(showMyLocation: Boolean) {
        Log.d(TAG, "SET CACHE SHOW MY LOCaTION $showMyLocation")
        try {
            mutableDocument.setBoolean("show_my_location", showMyLocation)
            mDatabase!!.save(mutableDocument)
            Log.d(TAG, "SAVED SHOW MY LOCATION $showMyLocation")
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun getSortByDistance(): LiveData<Boolean?> {
        Log.d(TAG, "GET SORT BY DISTANCE")
        sortByDistance.value = null
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("settings")
                if (document != null) {
                    var sortByDistanceBool: Boolean = document.getBoolean("sort_by_distance")
                    sortByDistance.value = sortByDistanceBool
                    Log.d(TAG, "getDate: SORT BY DISTANCE:$sortByDistanceBool")
                    Log.d(TAG, "DOCUMENT LOG:  sort " + document.getBoolean("sort_by_distance"))
                    Log.d(TAG, "DOCUMENT LOG:  show " + document.getBoolean("show_my_location"))
                    Log.d(TAG, "DOCUMENT LOG:  date " + document.getString("date"))
                }
            }
        }
        return sortByDistance
    }

    fun setSortByDistance(sortByDistance: Boolean) {
        Log.d(TAG, "SET CACHE SORT BY DISTANCE $sortByDistance")
        try {
            mutableDocument.setBoolean("sort_by_distance", sortByDistance)
            mDatabase!!.save(mutableDocument)
            Log.d(TAG, "SAVED SORT BY DISTANCE $sortByDistance")

        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    fun removeUserCache() {
        var query: Query? = QueryBuilder
                .select(SelectResult.expression(Meta.id), SelectResult.all())
                .from(DataSource.database(mDatabase!!))
        val rs = query!!.execute()
        for (result in rs) {
            val data = result.getDictionary("db_name")
            Log.d(TAG, "REMOVE USER document: $data")
            Log.d(TAG, "REMOVE USER id: " + result.getString(0))
            val id = result.getString(0)
            val doc: Document = mDatabase!!.getDocument(id)
            Log.d(TAG, "REMOVE USER delete doc: $doc")

            mDatabase!!.delete(doc)
        }

    }
}

