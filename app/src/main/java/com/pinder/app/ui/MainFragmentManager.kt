package com.pinder.app.ui

import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.gms.ads.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pinder.app.BaseApplication
import com.pinder.app.BaseApplication.LoginEnum
import com.pinder.app.R
import com.pinder.app.adapters.MainFragmentManagerPagerAdapter
import com.pinder.app.util.ExpandCollapseView
import com.pinder.app.util.HideSoftKeyboard

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainViewFragmentManager.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragmentManager : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val TAG = "MainFragmentManagerTAG"
    private var bottomNavigationView: BottomNavigationView? = null
    private var fromActivity = ""
    private var adContainerView: FrameLayout? = null
    private var adView: AdView? = null
    var mKeyboardVisible = false
    var adapter: MainFragmentManagerPagerAdapter? = null
    private var extras = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fromActivity = it.getString("fromActivity", "")
            var ext = it.getString("fromUsersProfilesActivity")
            extras.putString("fromActivity", fromActivity)
            extras.putString("fromUsersProfilesActivity", it.getString("fromUsersProfilesActivity", null))
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            it.clear()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_fragment_manager, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainViewFragmentManager.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MainFragmentManager().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onCreate: ON CREATE")
        BaseApplication.UserStatus = LoginEnum.LOGGED
        bottomNavigationView = view?.findViewById(R.id.bottom_navigation)
        bottomNavigationView?.selectedItemId = R.id.nav_main

        val viewPager: ViewPager = view.findViewById(R.id.pager)
        adapter = activity?.let {
            MainFragmentManagerPagerAdapter(childFragmentManager, bottomNavigationView!!.maxItemCount, extras)
        }
        viewPager.adapter = adapter
        if (fromActivity.equals("chatActivity") || fromActivity.equals("unmatchButtonClicked")) {
            viewPager.currentItem = 3
            bottomNavigationView!!.selectedItemId = R.id.nav_matches
        } else {
            viewPager.currentItem = 2
        }
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                HideSoftKeyboard.hideKeyboard(activity)
                when (position) {
                    0 -> bottomNavigationView!!.menu.findItem(R.id.nav_settings).isChecked = true
                    1 -> {
                        Log.d("MainFragment", "onPageSelected:  tags")
                        bottomNavigationView!!.menu.findItem(R.id.nav_profile).isChecked = true
                    }
                    2 -> {
                        Log.d("MainFragment", "onPageSelected:  main")
                        bottomNavigationView!!.menu.findItem(R.id.nav_main).isChecked = true
                    }
                    3 -> bottomNavigationView!!.menu.findItem(R.id.nav_matches).isChecked = true
                    4 -> bottomNavigationView!!.menu.findItem(R.id.nav_tags).isChecked = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> viewPager.currentItem = 0
                R.id.nav_profile -> viewPager.currentItem = 1
                R.id.nav_main -> viewPager.currentItem = 2
                R.id.nav_matches -> viewPager.currentItem = 3
                R.id.nav_tags -> viewPager.currentItem = 4
            }
            true
        }
        initAdaptiveAd()
    }

    fun replaceTabPage(tabPage: Int) {
        bottomNavigationView!!.selectedItemId = tabPage
    }

    private fun initAdaptiveAd() {
        adContainerView = view?.findViewById(R.id.ad_frame)
        // Step 1 - Create an AdView and set the ad unit ID on it.
        adView = AdView(activity)
        adView!!.setAdUnitId(getString(R.string.banner_ad))
        adContainerView!!.addView(adView)
        loadBanner()
    }

    private fun loadBanner() {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        val adSize = getAdSize()
        // Step 4 - Set the adaptive ad size on the ad view.
        adView!!.adSize = adSize
        // Step 5 - Start loading the ad in the background.
        adView!!.loadAd(adRequest)
    }

    private fun getAdSize(): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val display: Display? = activity?.getWindowManager()?.getDefaultDisplay()
        val outMetrics = DisplayMetrics()
        display?.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }

    override fun onResume() {
        super.onResume()
        getContentView()?.viewTreeObserver
                ?.addOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener)
    }

    override fun onPause() {
        super.onPause()
        getContentView()?.viewTreeObserver
                ?.removeOnGlobalLayoutListener(mLayoutKeyboardVisibilityListener)
    }

    private val mLayoutKeyboardVisibilityListener = OnGlobalLayoutListener {
        val rectangle = Rect()
        val contentView = getContentView()
        contentView?.getWindowVisibleDisplayFrame(rectangle)
        val screenHeight = contentView?.rootView?.height
        // r.bottom is the position above soft keypad or device button.
        // If keypad is shown, the rectangle.bottom is smaller than that before.
        val keypadHeight = screenHeight?.minus(rectangle.bottom)
        // 0.15 ratio is perhaps enough to determine keypad height.
        val isKeyboardNowVisible = keypadHeight!! > screenHeight * 0.15
        if (mKeyboardVisible !== isKeyboardNowVisible) {
            if (isKeyboardNowVisible) {
                onKeyboardShown()
            } else {
                onKeyboardHidden()
            }
        }
        mKeyboardVisible = isKeyboardNowVisible
    }

    private fun onKeyboardShown() {
        ExpandCollapseView.collapseAdBanner(adContainerView)
    }

    private fun onKeyboardHidden() {
        ExpandCollapseView.expandAdBanner(adContainerView)
    }

    private fun getContentView(): View? {
        return view?.findViewById(R.id.mainFragmentManager)
    }
}