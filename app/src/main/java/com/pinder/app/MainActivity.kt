package com.pinder.app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.pinder.app.ui.MainFragmentManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var mFullScreenAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.main_view, MainFragmentManager()).commit()
        initAdmob()
        initFullScreenAdd()

    }

    private fun initAdmob() {
        MobileAds.initialize(this) { Log.d("ADS INIT SUCCES", "onSuccess: ") }
    }

    private fun initFullScreenAdd() {
        mFullScreenAd = InterstitialAd(this)
        mFullScreenAd!!.setAdUnitId(getString(R.string.full_screen_ad))
        mFullScreenAd!!.loadAd(AdRequest.Builder().build())
        mFullScreenAd!!.setAdListener(object : AdListener() {
            override fun onAdClosed() {
                // Load the next interstitial.
                mFullScreenAd!!.loadAd(AdRequest.Builder().build())
            }
        })
    }

    fun getmFullScreenAd(): InterstitialAd? {
        return mFullScreenAd
    }

    fun showFullScreenAd() {
        mFullScreenAd?.show()
    }

}