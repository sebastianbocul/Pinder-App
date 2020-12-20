package com.pinder.app.cache

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.couchbase.lite.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pinder.app.models.Card
import com.pinder.app.util.Constants


class MainCache constructor(private var context: Context) {
    private var mDatabase: Database? = null
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

    fun getCards(): MutableLiveData<ArrayList<Card>> {
        Log.d(TAG, "GET MATCHES")
        val cardsLiveData: MutableLiveData<ArrayList<Card>> = MutableLiveData()
        val cardsArrayList: ArrayList<Card> = arrayListOf()
        cardsLiveData.value = cardsArrayList
        Log.d(TAG, context.filesDir.absolutePath)
        if (Database.exists(Constants.DB_NAME, context.filesDir)) {
            if (mDatabase != null) {
                val document: Document? = mDatabase!!.getDocument("cards")
                if (document != null) {
                    document.forEach {
                        Log.d(TAG, "Getting from CACHE: $it")
                        val doc = document.getDictionary(it)
                        val card: Card = mapper.convertValue(doc.toMap(), Card::class.java)
                        cardsArrayList.add(card)
                    }
                    cardsLiveData.value = cardsArrayList
                    Log.d(TAG, "Getting from cache finished")
                    return cardsLiveData
                }
            }
        }
        Log.d(TAG, "getNumbers: numbers:$cardsArrayList")
        return cardsLiveData
    }

    fun saveCards(cardsArrayList: ArrayList<Card>) {
        Log.d(TAG, "SAVING TO DB")
        try {
            Log.d(TAG, "cards: $cardsArrayList")
            val mutableDoc = MutableDocument("cards")
            for (cardObject in cardsArrayList) {
                Log.d(TAG, "Saving to CARDS CACHE : ${cardObject.userId}")
                val map: Map<String, Any> = mapper.convertValue(cardObject, object : TypeReference<Map<String?, Any?>?>() {})
                mutableDoc.setValue(cardObject.userId, map)
            }
            // Save it to the database.
            mDatabase!!.save(mutableDoc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}

