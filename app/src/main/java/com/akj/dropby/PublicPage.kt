package com.akj.dropby


import kotlinx.android.synthetic.main.map.*
import kotlinx.android.synthetic.main.publicpage.*
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

import com.google.common.reflect.TypeToken
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.NullPointerException


import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

import org.joda.time.DateTime
import org.joda.time.Days
import org.joda.time.Hours
import org.joda.time.Minutes
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*


/** intentmaker 시도 1
private fun intentmaker( Object: LatLng): MutableList<Double> {
var mywhich = mutableListOf<Double>()
val lat =Object.latitude
val long = Object.longitude

mywhich.set(0, lat)
mywhich.set(1, long)

return mywhich
}
 */

class PublicPage : AppCompatActivity(){

    // 변수선언쓰
    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val REQUEST_PERMISSION_CODE = 1
    val DEFAULT_ZOOM_LEVEL = 17f
    val ANAM = com.google.android.gms.maps.model.LatLng(37.58651702915194, 127.0290833537988)
    var mywheechee : DoubleArray = doubleArrayOf(1.0,1.0)
    var googleMap: GoogleMap? = null

    /*
   val bitmap by lazy {
        val drawable = resources.getDrawable(R.drawable.mylocation) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 35, 35, false)
    }

    */
    val otherbitmap by lazy{
        val drawable = resources.getDrawable(R.drawable.adddrop) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 150, 200
            , false)
    }
    var latitudel = 0.0
    var longitudel = 0.0

    // 글 목록을 저장하는 변수
    val posts: MutableList<Post> = mutableListOf()
    var currentMarker: Marker? = null


    val anotherbitmap by lazy {
        val drawable = resources.getDrawable(R.drawable.dropp) as BitmapDrawable
        Bitmap.createScaledBitmap(drawable.bitmap, 120, 170, false)
    }



         override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.map)

             drop()

             //버튼정리 가실게요
        addd.setOnClickListener{
            if (doubleArrayOf(mywheechee.get(0), mywheechee.get(1)) != doubleArrayOf(1.0,1.0)) {
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra(
                    "PickedLocation",
                    doubleArrayOf(mywheechee.get(0), mywheechee.get(1))
                )

                startActivity(intent)
            }
            else {
                Toast.makeText(applicationContext, "위치를 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
        }
        }


        // 맵뷰에 onCreate 함수 호출
        mapView.onCreate(savedInstanceState)
        // 앱이 실행될때 런타임에서 위치 서비스 관련 권한체크
        if (hasPermissions()) {
            // 권한이 있는 경우 맵 초기화
            initMap()
        } else {
            // 권한 요청
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }
        // 현재 위치 버튼 클릭 이벤트 리스너 설정
        myLocationButton.setOnClickListener { onMyLocationButtonClick() }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 맵 초기화
        initMap()
    }
    // 앱에서 사용하는 권한이 있는지 체크하는 함수
    fun hasPermissions(): Boolean {
        // 퍼미션목록중 하나라도 권한이 없으면 false 반환
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }



    // 맵 초기화하는 함수
    @SuppressLint("MissingPermission")
    fun initMap(){

        // 맵뷰에서 구글 맵을 불러오는 함수. 컬백함수에서 구글 맵 객체가 전달됨
        mapView.getMapAsync {
            /*
            val markerOptions = MarkerOptions()
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            markerOptions.position(getMyLocation())
            markerOptions.title("MyLocation")
*/
            googleMap = it
            // 현재위치로 이동 버튼 비활성화
            it.uiSettings.isMyLocationButtonEnabled = false
            // 위치 사용 권한이 있는 경우
            when {
                hasPermissions() -> {
                    // 현재위치 표시 활성화
                    it.isMyLocationEnabled = true
                    // 현재위치로 카메라 이동
                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            getMyLocation(),
                            DEFAULT_ZOOM_LEVEL
                        )
                    )

                   //마커올리기
                   // googleMap?.addMarker(markerOptions)

                    //클릭시 핀 생성

                    googleMap!!.setOnMapClickListener (object : GoogleMap.OnMapClickListener {
                        override fun onMapClick(point: LatLng) {
                            latitudel = point.latitude; // 위도
                            longitudel = point.longitude; // 경도



                            if (currentMarker != null) {
                                currentMarker!!.remove()
                                currentMarker = null
                            }
                            else  {
                                currentMarker = googleMap!!.addMarker(
                                    MarkerOptions().position(LatLng(latitudel, longitudel))
                                        .title("이곳에 드롭 추가")
                                        .snippet(latitudel.toString() + ", " + longitudel.toString())
                                        .icon(BitmapDescriptorFactory.fromBitmap(otherbitmap))
                                )
                            }




                           // googleMap!!.setOnMarkerClickListener(this@PublicPage)
                            mywheechee = doubleArrayOf(latitudel, longitudel)

                        }
                    })






                }
                else -> {
                    // 권한이 없으면 서울시청의 위치로 이동
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(ANAM, DEFAULT_ZOOM_LEVEL))

                    googleMap!!.setOnMapClickListener (object : GoogleMap.OnMapClickListener {
                        override fun onMapClick(point: LatLng) {

                            latitudel = point.latitude; // 위도
                            longitudel = point.longitude; // 경도



                            if (currentMarker != null) {
                                currentMarker!!.remove()
                                currentMarker = null
                            }
                            else {
                                currentMarker = googleMap!!.addMarker(
                                    MarkerOptions().position(LatLng(latitudel, longitudel))
                                        .title("이곳에 드롭 추가")
                                        .snippet(latitudel.toString() + ", " + longitudel.toString())
                                        .icon(BitmapDescriptorFactory.fromBitmap(otherbitmap))



                                        )
                            }


                            //googleMap!!.setOnMarkerClickListener(this@PublicPage)
                            mywheechee = doubleArrayOf(latitudel, longitudel)


                        }
                    })
                }
            }
        }
    }









    @SuppressLint("MissingPermission")
    fun getMyLocation(): com.google.android.gms.maps.model.LatLng {
        // 위치를 측정하는 프로바이더를 GPS 센서로 지정
        val locationProvider: String = LocationManager.GPS_PROVIDER
        // 위치 서비스 객체를 불러옴
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 마지막으로 업데이트된 위치를 가져옴
        val lastKnownLocation: Location? = locationManager.getLastKnownLocation(locationProvider)
        // 위도 경도 객체로 반환
        if (lastKnownLocation != null) {
            // 위도 경도 객체로 반환
            return com.google.android.gms.maps.model.LatLng(
                lastKnownLocation.latitude,
                lastKnownLocation.longitude
            )
        } else {
            // 위치를 구하지 못한경우 기본값 반환
            return ANAM

        }
    }
    // 현재 위치 버튼 클릭한 경우
    fun onMyLocationButtonClick() {
        when {
            hasPermissions() -> googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL)
            )
            else -> Toast.makeText(applicationContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG)
                .show()
        }
    }




    // 하단부터 맵뷰의 라이프사이클 함수 호출을 위한 코드들
    override fun onResume() {
        super.onResume()
        mapView.onResume()

    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    // 마커를 추가하는 함수
    fun addMarkerr(post: Post?) {
        var jsonversion = JSONObject(post?.toMap())
        var latitude = jsonversion.getDouble("latitudes")
        var longitude = jsonversion.getDouble("longitudes")

        googleMap?.addMarker(MarkerOptions()
            .position(LatLng(latitude,longitude))
            //.title(jsonversion.getString("message"))
            .icon(BitmapDescriptorFactory.fromBitmap(anotherbitmap))
            .title(jsonversion.getString("message"))
            )
    }




    fun drop() {
        FirebaseDatabase.getInstance().getReference("/Posts")
            .orderByChild("writeTime").addChildEventListener(object : ChildEventListener {
                // 글이 추가된 경우
                override fun onChildAdded(snapshot: DataSnapshot, prevChildKey: String?) {
                    snapshot?.let { snapshot ->
                        // snapshop 의 데이터를 Post 객체로 가져옴
                        val post = snapshot.getValue(Post::class.java)
                        addMarkerr(post)
                        post?.let {
                            // 새 글이 마지막 부분에 추가된 경우
                            if (prevChildKey == null) {
                                //글 목록을 저장하는 변수에 post 객체 추가
                                posts.add(it)
                                // RecyclerView 의 adapter 에 글이 추가된 것을 알림


                            } else {
                                // 글이 중간에 삽입된 경우 prevChildKey 로 한단계 앞의 데이터의 위치를 찾은 뒤 데이터를 추가한다.
                                val prevIndex =
                                    posts.map { it.postId }.indexOf(prevChildKey)
                                posts.add(prevIndex + 1, post)
                                // RecyclerView 의 adapter 에 글이 추가된 것을 알림

                            }
                        }
                    }
                }

                // 글이 변경된 경우
                override fun onChildChanged(snapshot: DataSnapshot, prevChildKey: String?) {

                }

                // 글의 순서가 이동한 경우
                override fun onChildMoved(snapshot: DataSnapshot, prevChildKey: String?) {

                }

                // 글이 삭제된 경우
                override fun onChildRemoved(snapshot: DataSnapshot) {


                }

                // 취소된 경우
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }
        }


        /*
       val drop by lazy{
           val drawable = resources.getDrawable(R.drawable.back) as BitmapDrawable
           Bitmap.createScaledBitmap(drawable.bitmap, 750, 450, false)
       }


       override fun onMarkerClick(clickedmarker: Marker): Boolean {

           clickedmarker.setIcon(BitmapDescriptorFactory.fromResource())
           clickedmarker.setAnchor(0.5f,0.34f)
           latitudu = clickedmarker.position.latitude
           longitudu = clickedmarker.position.longitude

           googleMap!!.setOnMapClickListener(){clickedmarker.remove()}


           return true
       }

   */







