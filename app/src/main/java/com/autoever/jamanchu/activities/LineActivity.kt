package com.autoever.jamanchu.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.jamanchu.R

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.datingapp.R
import com.example.datingapp.api.RetrofitInstance
import com.example.datingapp.models.Line
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LineActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var lineId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line)

        val editText = findViewById<EditText>(R.id.editText)
        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)

        lineId = intent.getStringExtra("lineId")
        lineId?.let {
            editText.setText(intent.getStringExtra("lineContent")) // 수정 시 기존 내용 표시
        }

        textViewComplete.setOnClickListener {
            val content = editText.text.toString()
            if (lineId.isNullOrEmpty()) {
                addLine(content)
            } else {
                updateLine(lineId!!, content)
            }
        }
    }

    private fun addLine(text: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val newLine = Line(id = "", user = userId, line = text)

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.createLine(newLine)
                if (response.isSuccessful && response.body() != null) {
                    setResult(RESULT_OK) // 작성 완료 결과 설정
                    finish() // 액티비티 종료
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateLine(id: String, text: String) {
        val updatedLine = Line(id = id, user = firebaseAuth.currentUser?.uid!!, line = text)

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.updateLine(id, updatedLine)
                if (response.isSuccessful) {
                    setResult(RESULT_OK) // 수정 완료 결과 설정
                    finish() // 액티비티 종료
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}