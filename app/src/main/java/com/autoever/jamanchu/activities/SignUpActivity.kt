package com.autoever.jamanchu.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.jamanchu.R
import com.autoever.jamanchu.models.Gender
import com.autoever.jamanchu.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 컴포넌트 선언
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPW)
        val editTextNickname = findViewById<EditText>(R.id.editTextNick)
        val editTextIntroduction = findViewById<EditText>(R.id.editTextIntroduction)
        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val spinner: Spinner = findViewById(R.id.spinner)
        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)

        // 문자열 배열 어댑터 생성
        ArrayAdapter.createFromResource(
            this,
            R.array.age_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // 드롭다운 레이아웃 설정
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // 어댑터 설정
            spinner.adapter = adapter
        }

        // "가입하기 버튼"
        textViewComplete.setOnClickListener {
            // 선택된 라디오 버튼 ID 가져오기
            val selectedGenderId = radioGroup.checkedRadioButtonId
            // 선택된 라디오 버튼 가져오기
            val selectedRadioButton = findViewById<RadioButton>(selectedGenderId)
            // 라디오 버튼의 텍스트 값으로 Gender를 결정
            val gender = when (selectedRadioButton.text.toString()) {
                "남성" -> Gender.MALE
                "여성" -> Gender.FEMALE
                else -> throw IllegalArgumentException("올바른 성별을 선택하세요.")
            }

            // Spinner에서 선택된 나이를 가져오기 (선택된 항목의 문자열을 Int로 변환)
            val selectedAge = spinner.selectedItem.toString().toInt()

            val user = User(
                "",
                editTextEmail.text.toString(),
                editTextNickname.text.toString(),
                editTextIntroduction.text.toString(),
                gender,
                selectedAge
            )

            Log.d("User", user.toString())

            signUp(user, editTextPassword.text.toString())
        }
    }

    // 회원가입 메서드
    fun signUp(user: User, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공 시, Firebase Authentication에서 생성된 UID 가져오기
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // User 객체에 ID 설정
                        user.id = userId // id만 입력해준다.
                        // Firestore에 사용자 데이터 저장
                        saveUserData(user)
                    }
                } else {
                    // 에러 처리
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Firestore에 사용자 데이터 저장
    private fun saveUserData(user: User) {
        val firestore = FirebaseFirestore.getInstance()

        // 사용자 UID를 Firestore 문서 ID로 사용하여 저장
        firestore.collection("users")
            .document(user.id) // UID를 문서 ID로 사용
            .set(user) // 사용자 객체를 저장
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User data successfully written!")

                // 메인 화면으로 이동
                val intent = Intent(this, UploadActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error writing document", e)
            }
    }
}