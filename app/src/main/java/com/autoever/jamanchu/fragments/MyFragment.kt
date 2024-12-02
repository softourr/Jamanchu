package com.autoever.jamanchu.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.autoever.jamanchu.R
import com.autoever.jamanchu.activities.IntroActivity
import com.autoever.jamanchu.models.Gender
import com.autoever.jamanchu.models.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyFragment : Fragment() {
    // Firestore 인스턴스 초기화
    val db: FirebaseFirestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my, container, false)

        val imageViewUser = view.findViewById<ImageView>(R.id.imageViewUser)
        val textViewNickname = view.findViewById<TextView>(R.id.textViewNickname)
        val textViewGender = view.findViewById<TextView>(R.id.textViewGender)
        val textViewAge = view.findViewById<TextView>(R.id.textViewAge)
        val textViewIntroduction = view.findViewById<TextView>(R.id.textViewIntroduction)

        // 내 정보 불러오기
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUserId = currentUser.uid
            getUser(currentUserId) { user ->
                if (user != null) {
                    // 사용자 정보가 성공적으로 가져와졌을 때 처리
                    println("Email: ${user.email}, Nickname: ${user.nickname}")
                    Glide.with(requireContext())
                        .load(user.image) // 불러올 이미지의 URL 또는 URI
                        .placeholder(R.drawable.user)
                        .into(imageViewUser) // 이미지를 표시할 ImageView
                    textViewNickname.text = user.nickname
                    textViewGender.text = if (user.gender == Gender.MALE) "남" else "여"
                    textViewAge.text = user.age.toString()
                    textViewIntroduction.text = user.introduction
                } else {
                    // 사용자 정보를 가져오는 데 실패했을 때 처리
                    println("User not found or error occurred.")
                }
            }
        }

        // 로그아웃
        val textViewLogout = view.findViewById<TextView>(R.id.textViewLogout)
        textViewLogout.setOnClickListener {
            // Firebase 인증 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 인트로 화면으로 이동
            val intent = Intent(requireContext(), IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 이전 액티비티 제거
            startActivity(intent)
        }

        // 친구
//        val layoutFriend = view.findViewById<LinearLayout>(R.id.layoutFriend)
//        layoutFriend.setOnClickListener {
//            // 친구 화면으로 이동
//            val intent = Intent(requireContext(), FriendActivity::class.java)
//            startActivity(intent)
//        }

        return view
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        db.collection("users")
            .document(userId) // 사용자 ID로 문서 가져오기
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    callback(user) // 변환한 User 객체를 callback으로 전달
                } else {
                    callback(null) // 해당하는 문서가 없을 경우 null 전달
                }
            }
            .addOnFailureListener { exception ->
                // 에러 처리
                println("Error getting document: $exception")
                callback(null) // 에러 발생 시 null 전달
            }
    }

}