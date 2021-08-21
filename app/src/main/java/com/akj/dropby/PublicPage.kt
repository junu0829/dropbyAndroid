package com.akj.dropby

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.akj.dropby.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_public_page.*
import java.security.Permissions
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_public_page.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class PublicPage : AppCompatActivity() {
    //test
    val PERMISSIONS= arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val REQUEST_PERMISSION_CODE = 1
    val DEFAULT_ZOOM_LEVEL = 17f

    val CITY_HALL = LatLng(37.5662952, 126.97794509999994)

    var googleMap: GoogleMap? = null

    fun hasPermissions():Boolean{
        for(permission in PERMISSIONS){
            if(ActivityCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }


    fun initMap() {
        mapView.getMapAsync{
            googleMap = it
            it.uiSettings.isMyLocationButtonEnabled=false
            when{
                hasPermissions()->{
                    it.isMyLocationEnabled=true
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(),DEFAULT_ZOOM_LEVEL))
                }
                else -> {
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL,DEFAULT_ZOOM_LEVEL))
                }
            }
        }
    }

    fun getMyLocation(): LatLng{
        val locationProvider: String = LocationManager.GPS_PROVIDER
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastKnownLocation : Location? = locationManager.getLastKnownLocation(locationProvider)
        if(lastKnownLocation != null){
            return LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
        }
        else{
            return CITY_HALL
        }
    }

    fun onMyLocationButtonClick(){
        when{
            hasPermissions()-> googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom
                (getMyLocation(),DEFAULT_ZOOM_LEVEL))
            else-> Toast.makeText(applicationContext,"위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_public_page)
        mapView.onCreate(savedInstanceState)


        if (hasPermissions()) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }

        myLocationButton.setOnClickListener { onMyLocationButtonClick() }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        (super.onRequestPermissionsResult(requestCode, permissions, grantResults))
        initMap()
    }



    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onPause(){
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

    val API_KEY = "7451434d54776e733731596544486f"
    var task: ToiletReadTask? = null
    var toilets= JSONArray()



    fun JSONArray.merge(anotherArray : JSONArray){
        for( i in 0 until anotherArray.length()){
            this.put(anotherArray.get(i))
        }
    }

    fun readData(startIndex: Int, lastIndex: Int): JSONObject{
        val url =
            URL("http://openAPI.seoul.go.kr:8088"+"/${API_KEY}/json/SearchPublicToiletPOIService/${startIndex}/${lastIndex}")
        val connection = url.openConnection()

        val data = connection.getInputStream().readBytes().toString(charset("UTF-8"))
        return JSONObject(data)
    }

    inner class ToiletReadTask : AsyncTask<Void, JSONArray, String>() {
        override fun onPreExecute() {
            googleMap?.clear()
            toilets = JSONArray()
        }

        override fun doInBackground(vararg params: Void?): String {
            val step = 1000
            var startIndex = 1
            var lastIndex = step
            var totalCount = 0

            do {
                if (isCancelled) break
                if (totalCount != 0) {
                    startIndex += step
                    lastIndex += step
                }

                val jsonObject = readData(startIndex, lastIndex)

                totalCount = jsonObject.getJSONObject("SearchPublicToiletPOIService")
                    .getInt("list_total_count")
                val rows =
                    jsonObject.getJSONObject("SearchPublicToiletPOIService").getJSONArray("row")
                toilets.merge(rows)
                publishProgress(rows)
            } while (lastIndex < totalCount)

            return "complete"
        }


    }

}
