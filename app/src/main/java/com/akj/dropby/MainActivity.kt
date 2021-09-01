package com.akj.dropby

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

// 글 목록을 저장하는 변수
val posts: MutableList<Post> = mutableListOf()

var drops : Any=  Any()

val itemMap = mutableMapOf<JSONObject, Post>()


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
/*
        //text1.setText(posts.toString())

        FirebaseDatabase.getInstance().getReference("/Posts").addValueEventListener(object: ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.KITKAT)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //val post = dataSnapshot.getValue()
                dataSnapshot.getValue()
                text1.setText(.toString())

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("JUNU", "loadPost:onCancelled", databaseError.toException())
            }
        })
    }


        override fun onMarkerClick(p0: Marker): Boolean {

                                }

*/

    }

