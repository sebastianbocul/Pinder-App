package com.pinder.app.ui

import android.content.Intent
import android.content.Intent.getIntent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pinder.app.MainActivity
import com.pinder.app.R
import com.pinder.app.adapters.ImageAdapter
import com.pinder.app.models.Card
import com.pinder.app.ui.dialogs.ReportUserDialog
import com.pinder.app.util.CalculateDistance
import com.pinder.app.util.StringDateToAge
import com.pinder.app.utils.BuildVariantsHelper
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var name: String? = null
    var tags:kotlin.String? = null
    var gender:kotlin.String? = null
    var distance:kotlin.String? = null
    var location:kotlin.String? = null
    var description:kotlin.String? = null
    var profileImageUrl:kotlin.String? = null
    var userId: String? = null
    var myId:kotlin.String? = null
    var viewPager: ViewPager? = null
    var mImageDatabase: DatabaseReference? = null
    private var nameTextView: TextView? = null
    private  var tagsTextView:TextView? = null
    private  var genderTextView:TextView? = null
    private  var distanceTextView:TextView? = null
    private  var locationTextView:TextView? = null
    private  var descriptionTextView:TextView? = null
    private var mAuth: FirebaseAuth? = null
    private var mUserDatabase: DatabaseReference? = null
    private  var mUserProfileDatabase:DatabaseReference? = null
    private  var myDatabaseReference:DatabaseReference? = null
    private var dislikeButton: ImageView? = null
    private  var likeButton:android.widget.ImageView? = null
    private  var reportUserButton:android.widget.ImageView? = null
    private var unmatchButton: Button? = null
    private var userAge: String? = null
    private var mImages: ArrayList<*>? = null
    private var mutualTagsExtras: ArrayList<String>? = null
    private val myTags = ArrayList<String>()
    private var user: Card? = null
    private var progressBar: ProgressBar? = null
    private var defaultImage: ImageView? = null
    private  var backArrowImage:android.widget.ImageView? = null
    private val TAG = "UsersProfilesActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                UserProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO
//        val intent: Intent = getIntent()
//        if (intent.getParcelableExtra<Parcelable?>("user") != null) {
            //TODO
//            user = intent.getParcelableExtra<Parcelable>("user")
//            userId = user!!.userId
//        }
//        if (intent.getStringExtra("userId") != null) {
//            userId = intent.getStringExtra("userId")
//        }
        nameTextView = view.findViewById(R.id.nameTextView)
        tagsTextView = view.findViewById(R.id.tagsTextView)
        genderTextView = view.findViewById(R.id.genderTextView)
        distanceTextView = view.findViewById(R.id.distanceTextView)
        locationTextView = view.findViewById(R.id.locationTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        unmatchButton = view.findViewById(R.id.unmatchButton)
        dislikeButton = view.findViewById(R.id.dislikeButton)
        reportUserButton = view.findViewById(R.id.reportUserImage)
        likeButton = view.findViewById(R.id.likeButton)
        progressBar = view.findViewById(R.id.user_progress_bar)
        defaultImage = view.findViewById(R.id.default_image)
        backArrowImage = view.findViewById(R.id.back_arrow)
        BuildVariantsHelper.disableButton(backArrowImage)
        mAuth = FirebaseAuth.getInstance()
        myId = mAuth!!.getCurrentUser()!!.uid
        mUserDatabase = FirebaseDatabase.getInstance().reference.child("Users")
        mUserProfileDatabase = FirebaseDatabase.getInstance().reference.child("Users")
                .child(myId!!)
                .child("connections")
                .child("matches")
        myDatabaseReference = FirebaseDatabase.getInstance().reference
        viewPager = view.findViewById(R.id.viewPager)
        mImageDatabase = mUserDatabase!!.child(userId!!).child("images")
        //TODO
//        if (getIntent().hasExtra("userId")) {
//            if (intent.getStringExtra("userId") == myId) {
//                unmatchButton!!.visibility = View.INVISIBLE
//                dislikeButton!!.visibility = View.INVISIBLE
//                likeButton!!.visibility = View.INVISIBLE
//                reportUserButton!!.isEnabled = false
//                loadTagsFirebase()
//            } else {
//                reportUserButton!!.isEnabled = true
//                mUserProfileDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        unmatchButton!!.visibility = View.INVISIBLE
//                        dislikeButton!!.visibility = View.VISIBLE
//                        likeButton!!.visibility = View.VISIBLE
//                        if (!dataSnapshot.exists()) return
//                        for (ds in dataSnapshot.children) {
//                            if (userId.equals(ds.key)) {
//                                unmatchButton!!.visibility = View.VISIBLE
//                                dislikeButton!!.visibility = View.INVISIBLE
//                                likeButton!!.visibility = View.INVISIBLE
//                            }
//                        }
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {}
//                })
//            }
//        }
        //TODO
//        dislikeButton!!.setOnClickListener {
//            val i = Intent(this@UsersProfilesActivity, MainActivity::class.java)
//            val myIntent = Intent(this@UsersProfilesActivity, MainActivity::class.java)
//            myIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
//            i.putExtra("fromUsersProfilesActivity", "dislikeButtonClicked")
//            startActivity(i)
//        }
//        likeButton!!.setOnClickListener {
//            val i = Intent(this@UsersProfilesActivity, MainActivity::class.java)
//            val myIntent = Intent(this@UsersProfilesActivity, MainActivity::class.java)
//            onBackPressed()
//            //                myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_SINGLE_TOP);
////                i.putExtra("fromUsersProfilesActivity", "likeButtonClicked");
////                startActivity(i);
//        }
        reportUserButton!!.setOnClickListener { openReportDialog() }
        unmatchButton!!.setOnClickListener {
            Log.d(TAG, "onClick")
            //TODO
//            val myIntent = Intent(this@UsersProfilesActivity, MainActivity::class.java)
//            myIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP
            //                myIntent.setFlags();
//                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(myIntent)
            Log.d(TAG, "startActivity: ")
            unmatchButton!!.text = "clicked unmatch"
            myDatabaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange: $dataSnapshot")
                    //                        try {
//                            String chatId = dataSnapshot.child("Users").child(myId).child("connections").child("matches").child(userId).child("ChatId").getValue().toString();
//                            //for chat
//                            DatabaseReference mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);
//                            mRemoveChild.removeValue();
//                            //for me
//                            mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Users").child(myId).child("connections");
//                            mRemoveChild.child("nope").child(userId).setValue(true);
//                            mRemoveChild.child("yes").child(userId).removeValue();
//                            mRemoveChild.child("matches").child(userId).removeValue();
//                            //for user
//                            mRemoveChild = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("connections");
//                            mRemoveChild.child("nope").child(myId).setValue(true);
//                            mRemoveChild.child("yes").child(myId).removeValue();
//                            mRemoveChild.child("matches").child(myId).removeValue();
//                            final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Intent myIntent = new Intent(UsersProfilesActivity.this, MainActivity.class);
//                                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(myIntent);
//                                }
//                            }, 500);
//                        } catch (NullPointerException e) {
//                            Toast.makeText(UsersProfilesActivity.this, "Unable to do that operation", Toast.LENGTH_SHORT).show();
//                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        //TODO
//        backArrowImage!!.setOnClickListener { v -> onBackPressed() }
//        if (intent.getParcelableExtra<Parcelable?>("user") == null) {
//            fillUserProfileFireBase()
//            loadImagesFireBase()
//        } else {
//            fillUserProfileBundle()
//            loadImagesBundle()
//        }
    }
    private fun loadImagesBundle() {
        //TODO
//        if (getIntent().getParcelableExtra("user") != null) {
//            val size: Int = user!!.images.size
//            if (user!!.images == null || user!!.images.size === 0) {
//                defaultImage!!.visibility = View.VISIBLE
//                viewPager!!.setBackgroundColor(Color.TRANSPARENT)
//                return
//            } else if (user!!.images.size >= 1) {
//                defaultImage!!.visibility = View.GONE
//            }
//            mImages = user!!.images as ArrayList<*>
//            val adapter = ImageAdapter(this@UsersProfilesActivity, mImages)
//            viewPager!!.adapter = adapter
//        }
    }

    private fun fillUserProfileBundle() {
        //TODO
//        if (getIntent().getParcelableExtra("user") != null) {
            distanceTextView!!.text = ", " + Math.round(user!!.distance).toString() + " km away"
            tagsTextView!!.text = user!!.tags.toString()
            val age = StringDateToAge().stringDateToAge(user!!.dateOfBirth)
            userAge = age.toString()
            name = user!!.name
            nameTextView!!.text = name.toString() + "  " + userAge
            gender = user!!.gender
            genderTextView!!.text = gender
            location = user!!.location
            locationTextView!!.text = location
            description = user!!.description
            descriptionTextView!!.text = description
//        }
    }

    private fun openReportDialog() {
        val reportUserDialog = ReportUserDialog(myId, userId)
        activity?.let { reportUserDialog.show(it.getSupportFragmentManager(), "Report User Dialog") }
    }

    private fun loadTagsFirebase() {
        mUserDatabase!!.child(myId!!).child("tags").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    if (ds.exists()) {
                        myTags.add(ds.key!!)
                    }
                }
                mutualTagsExtras = myTags
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadImagesFireBase() {
        progressBar!!.visibility = View.VISIBLE
        defaultImage!!.visibility = View.GONE
        mImageDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //TODO
                if (dataSnapshot.value != null) {
//                    val arrayList: ArrayList<*> = ArrayList<Any?>()
//                    for (ds in dataSnapshot.children) {
//                        arrayList.add(ds.child("uri").value)
//                    }
//                    mImages = arrayList
//                    val adapter = ImageAdapter(this@UsersProfilesActivity, mImages)
//                    viewPager!!.adapter = adapter
                } else {
                    defaultImage!!.visibility = View.VISIBLE
                    progressBar!!.visibility = View.GONE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun fillUserProfileFireBase() {
        mUserDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val map = dataSnapshot.child(userId!!).value as Map<String, Any>?
                    val mapLoc = map!!["location"] as Map<String, Any>?
                    val mapMyLoc = dataSnapshot.child(myId!!).child("location").value as Map<String, Any>?
                    val lat1: Double
                    val lon1: Double
                    val lat2: Double
                    val lon2: Double
                    lat1 = mapMyLoc!!["latitude"] as Double
                    lon1 = mapMyLoc["longitude"] as Double
                    lat2 = mapLoc!!["latitude"] as Double
                    lon2 = mapLoc["longitude"] as Double
                    val distance: Double = CalculateDistance.distance(lat1, lon1, lat2, lon2)
                    distanceTextView!!.text = ", " + Math.round(distance) + " km away"
                    //tags
                    val strB = StringBuilder()
                    if (mutualTagsExtras != null) {
                        for (str in mutualTagsExtras!!) {
                            strB.append("#")
                            strB.append("$str ")
                        }
                    } else if (dataSnapshot.child(myId!!).child("connections").child("matches").child(userId!!).child("mutualTags").exists()) {
                        val tags = dataSnapshot.child(myId!!).child("connections").child("matches").child(userId!!).child("mutualTags").value as Map<String?, Any>?
                        val stringList: ArrayList<String?> = ArrayList(tags!!.keys)
                        for (str in stringList) {
                            strB.append("#")
                            strB.append("$str ")
                        }
                    }
                    if (strB.length != 0) {
                        tagsTextView!!.text = strB.toString()
                    } else {
                        tagsTextView!!.text = " "
                    }
                    userAge = ""
                    if (map["dateOfBirth"] != null) {
                        val age = StringDateToAge().stringDateToAge(map["dateOfBirth"].toString())
                        userAge = age.toString()
                    }
                    //name
                    if (map["name"] != null) {
                        name = map["name"].toString()
                        nameTextView!!.text = name.toString() + "  " + userAge
                    } else descriptionTextView!!.text = "Name: "
                    //sex
                    if (map["sex"] != null) {
                        gender = map["sex"].toString()
                        genderTextView!!.text = gender
                    } else descriptionTextView!!.text = "Gender:"
                    //location
                    if (mapLoc["locality"] != null) {
                        location = mapLoc["locality"].toString()
                        locationTextView!!.text = location
                    } else locationTextView!!.text = ""
                    //description
                    if (map["description"] != null) {
                        description = map["description"].toString()
                        descriptionTextView!!.text = description
                    } else descriptionTextView!!.text = ""
                    if (map["images"] == null) {
//                        viewPager.setBackgroundColor(Color.TRANSPARENT);
//                        viewPager.setBackground(getDrawable(R.drawable.ic_profile_hq));
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}