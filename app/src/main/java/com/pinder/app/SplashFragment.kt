package com.pinder.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pinder.app.util.Resource.Status.*
import com.pinder.app.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
@AndroidEntryPoint
class SplashFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var logo: ImageView? = null
    private var logoLayout: LinearLayout? = null
    private var firebaseAuthStateListener: AuthStateListener? = null
    private var mAuth: FirebaseAuth? = null
    private val TAG = "SplashFragment"

    @Inject
    lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObjectsById()
        mAuth = FirebaseAuth.getInstance()
        authStateListener()
        setObjectsById()
        val anim = AnimationUtils.loadAnimation(context, R.anim.rotate)
        anim.repeatCount = Animation.INFINITE
        logo!!.startAnimation(anim)
    }

    private fun authStateListener() {
        firebaseAuthStateListener = AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            Log.d(TAG, "USER : $user");
            if (user != null) {
                Log.d(TAG, "USER!=NULL");
                authViewModel.fetchUserData().observe(viewLifecycleOwner, Observer {
                    if (it != null) {
                        Log.d(TAG, "HANDLER");
                        Log.d(TAG, "OBSERVER HANDLER it: ${it.status}")
                        Log.d(TAG, "OBSERVER HANDLER it: ${it.data}")
                        when (it.status) {
                            SUCCESS -> {
                                val animScale = AnimationUtils.loadAnimation(context, R.anim.scale)
                                logoLayout!!.startAnimation(animScale)
                                val handler = Handler()
                                handler.postDelayed({
                                    logoLayout!!.visibility = View.GONE
                                    logoLayout!!.clearAnimation()
                                    Log.d(TAG, "SUCCESS");
                                    if (it.data!!) {
                                        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                            val intent = Intent(activity, MainFragmentManager::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(intent)
                                        } else {
                                            val requestLocationActivity = Intent(activity, RequestLocationPermissionActivity::class.java)
                                            requestLocationActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            startActivity(requestLocationActivity)
                                        }
                                    } else {
                                        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.start_fragment_container, LoginFragment()).commit()
                                    }
                                }, 300)
                            }
                            ERROR -> {
                                Log.d(TAG, "ERROR");
                            }
                            LOADING -> {
                                Log.d(TAG, "LOADING");
                            }
                        }

                    }
                })

            }
        }
    }

    private fun setObjectsById() {
        logo = requireView().findViewById(R.id.bigLogo)
        logoLayout = requireView().findViewById(R.id.logoLayout)
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(firebaseAuthStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        mAuth!!.removeAuthStateListener(firebaseAuthStateListener!!)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String?, param2: String?): SplashFragment {
            val fragment = SplashFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}