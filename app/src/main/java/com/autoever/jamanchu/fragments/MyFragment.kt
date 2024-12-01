package com.autoever.jamanchu.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.autoever.jamanchu.R
import com.autoever.jamanchu.activities.IntroActivity
import com.google.firebase.auth.FirebaseAuth

class MyFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my, container, false)

        val textViewLogout = view.findViewById<TextView>(R.id.textViewLogout)
        textViewLogout.setOnClickListener {
            // Firebase 인증 로그아웃
            FirebaseAuth.getInstance().signOut()

            // 인트로 화면으로 이동
            val intent = Intent(requireContext(), IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 이전 액티비티 제거
            startActivity(intent)
        }



        return view
    }
}