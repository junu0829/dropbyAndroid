package com.akj.dropby

import com.google.type.LatLng

class Post {
    /**
     * 글의 ID
     */
    var postId = ""
    /**
     * 글 작성자의 ID
     */

    /**
     * 글의 메세지
     */
    var message = ""
    /**
     * 글이 쓰여진 시간
     */
    var writeTime: Any = Any()
    /**
     * 글의 배경이미지
     */
    var latitudes : Double = 0.0
    var longitudes : Double = 0.0

    // [START post_to_map]

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "postId" to postId,
            "message" to message,
            "writeTime" to writeTime,
            "latitudes" to latitudes,
            "longitudes" to longitudes

        )
    }
}