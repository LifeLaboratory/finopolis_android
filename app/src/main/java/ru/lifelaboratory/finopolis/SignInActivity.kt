package ru.lifelaboratory.finopolis

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.vk.sdk.util.VKUtil
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val fingerprints = VKUtil.getCertificateFingerprint(this, this.packageName)

        fingerprints.forEach {
            Log.e(LOG_TAG, it.toString())
        }

        user_auth.setOnClickListener(View.OnClickListener {
            run {
                val service = ServerAPI.create()
                val login: String = user_login.text.toString()
                val password: String = user_password.text.toString()
                service.auth(Profile(login=login, password=password))
                    .enqueue(object: Callback<Profile> {
                        override fun onFailure(call: Call<Profile>?, t: Throwable?) {
                            Log.e(LOG_TAG, t.toString())
                            Toast.makeText(this@SignInActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<Profile>?, response: Response<Profile>?) {
                            if (response?.body() != null) {
                                val user: Gson = Gson()
                                val userStr : String = user.toJson(response.body())
                                val memory : SharedPreferences = this@SignInActivity.getSharedPreferences(MEMORY, MODE_PRIVATE)
                                memory.edit().putString(MEMORY_USER, userStr).apply()
                                this@SignInActivity.startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                            } else {
                                Toast.makeText(this@SignInActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        })

        btn_to_signup.setOnClickListener(View.OnClickListener {
            this@SignInActivity.startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        })
    }

}
