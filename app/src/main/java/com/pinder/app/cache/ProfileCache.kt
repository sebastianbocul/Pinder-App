package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
import com.couchbase.lite.Array
import com.pinder.app.util.Constants


class ProfileCache constructor(private var context: Context) : ProfileCacheDao {
    private var mDatabase: Database? = null
    private val TAG = "ProfileCache"

    private val nameLiveData: MutableLiveData<String> = MutableLiveData()
    private val descriptionLiveData: MutableLiveData<String> = MutableLiveData()
    private val imagesLiveData: MutableLiveData<ArrayList<String>> = MutableLiveData()


    init {
        init()
    }

    private fun init() {
        // Get the database (if exists)
        val config = DatabaseConfiguration()
        mDatabase = Database(Constants.DB_NAME, config)
    }

    override fun getName(): LiveData<String> {
        Log.d(TAG, "GET NAME")
        Log.d(TAG, context.filesDir.absolutePath)
        nameLiveData.value=""
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("userName")
                if (document != null) {
                    var name: String? = document.getString("name")
                    nameLiveData.value = name
                    Log.d(TAG, "getName: name:$name")
                    Log.d(TAG, "Getting name from cache finished")
                }
            }
        }
        return nameLiveData
    }

    override fun getDescription(): LiveData<String> {
        Log.d(TAG, "GET DESCRIPTION")
        Log.d(TAG, context.filesDir.absolutePath)
        descriptionLiveData.value=""
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("userDescription")
                if (document != null) {
                    var description: String? = document.getString("description")
                    descriptionLiveData.value = description
                    Log.d(TAG, "getDescription: description:$description")
                }
            }
        }
        return descriptionLiveData
    }

    override fun getImages(): LiveData<java.util.ArrayList<String>> {
        Log.d(TAG, "GET IMAGES FROM CACHE")
        var images: ArrayList<String>? = arrayListOf()
        imagesLiveData.value = images
        Log.d(TAG, context.filesDir.absolutePath)
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("userImages")
                if (document != null) {
                    val arrayCouchBase: Array? = document.getArray("images")
                    images = arrayCouchBase?.toList() as ArrayList<String>?
                    imagesLiveData.value = images
                    Log.d(TAG, "getImages: images:$images")
                    return imagesLiveData
                }
            }
        }
        return imagesLiveData    }

    override fun setName(name: String) {
        Log.d(TAG, "SET CACHE name $name")
        try {
            val mutableDoc = MutableDocument("userName")
            mutableDoc.setValue("name", name)
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun setDescription(description: String) {
        Log.d(TAG, "SET CACHE description $description")
        try {
            val mutableDoc = MutableDocument("userDescription")
            mutableDoc.setValue("description", description)
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun setImages(imagesUrls: ArrayList<String>) {
        Log.d(TAG, "SET IMAGES URLS TO CACHE")
        try {
            Log.d(TAG, "SET imagesUrls: $imagesUrls")
            val mutableDoc = MutableDocument("userImages")
            var mutablearray = MutableArray(imagesUrls.toList())
            mutableDoc.setArray("images", mutablearray)
            // Save it to the database.
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}

