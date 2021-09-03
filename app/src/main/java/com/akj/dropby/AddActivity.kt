package com.akj.dropby


import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.database.ServerValue
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Comment
import kotlinx.android.synthetic.main.activity_add.*

class AddActivity : AppCompatActivity() {

    // 글쓰기 모드를 저장하는 변수
    val mode = "post"

    // 댓글쓰기인 경우 글의 ID
    var postId = ""
    //


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add)

            var currentLocatio = intent.getDoubleArrayExtra("PickedLocation")



            if (currentLocatio!!.toList().toString() == "[1.0, 1.0]") {
                Toast.makeText(applicationContext, "위치 설정이 되어있지 않습니다.", Toast.LENGTH_LONG).show()
                textView.setText("지도를 탭해 드롭을 남길 위치를 정해주세요")
            } else {textView.setText("현재 위치: "+ currentLocatio!!.toList().toString())}

            backButton.setOnClickListener {
                finish()
            }


            sendButton.setOnClickListener {
                // 메세지가 없는 경우 토스트 메세지로 알림.
                if (TextUtils.isEmpty(input.text)) {
                    Toast.makeText(applicationContext, "메세지를 입력하세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }



                val latitudes = (currentLocatio!!.toMutableList())[0]
                val longitudes =(currentLocatio!!.toMutableList())[1]
                // Post 객체 생성
                val post = Post()
                // Firebase 의 Posts 참조에서 객체를 저장하기 위한 새로운 키를 생성하고 참조를 newRef 에 저장
                val newRef = FirebaseDatabase.getInstance().getReference("Posts").push()
                // 글이 쓰여진 시간은 Firebase 서버의 시간으로 설정
                post.writeTime = ServerValue.TIMESTAMP
                // 메세지는 input EditText 의 텍스트 내용을 할당
                post.message = input.text.toString()
                // 글의 ID 는 새로 생성된 파이어베이스 참조의 키로 할당
                post.postId = newRef.key!!
                //
                post.latitudes =latitudes
                post.longitudes= longitudes
                // Post 객체를 새로 생성한 참조에 저장
                newRef.setValue(post)
                // 저장성공 토스트 알림을 보여주고 Activity 종료
                Toast.makeText(applicationContext, "드롭이 공유되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }

        }



}



