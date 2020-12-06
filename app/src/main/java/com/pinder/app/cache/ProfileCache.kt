package com.pinder.app.cache

import android.content.Context
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.pinder.app.util.Constants


class ProfileCache{
    private var context:Context
    private var mDatabase: Database? = null
    constructor(context: Context){
        this.context = context
        init()
    }

    private fun init(){
        // Get the database (if exists)
        val config = DatabaseConfiguration()
        mDatabase = Database(Constants.DB_NAME, config)
    }

}