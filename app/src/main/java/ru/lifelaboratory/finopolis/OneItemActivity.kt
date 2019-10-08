package ru.lifelaboratory.finopolis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.android.synthetic.main.activity_item.btn_add_item
import kotlinx.android.synthetic.main.activity_item.btn_to_items
import kotlinx.android.synthetic.main.activity_item.btn_to_posts
import kotlinx.android.synthetic.main.activity_item.btn_to_profile
import kotlinx.android.synthetic.main.activity_item.btn_to_social
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.adapter_list_items.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OneItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        btn_to_items.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OneItemActivity, ItemsActivity::class.java))
        })
        btn_to_posts.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OneItemActivity, PostsActivity::class.java))
        })
        btn_to_social.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OneItemActivity, SocialActivity::class.java))
        })
        btn_to_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OneItemActivity, MainActivity::class.java))
        })

        val gson = Gson()
        val json = this.getSharedPreferences(MEMORY, Context.MODE_PRIVATE).getString(MEMORY_USER, "")
        val user: Profile = gson.fromJson(json, Profile::class.java)

        btn_add_item.setOnClickListener(View.OnClickListener {
            run {
                val service = ServerAPI.create()
                service.addNomenclature(Item(title = item_title.text.toString(), userId = user.id, description = item_description.text.toString(), photo = item_photo.text.toString()))
                    .enqueue(object: Callback<Item> {
                        override fun onFailure(call: Call<Item>?, t: Throwable?) {
                            Log.e(LOG_TAG, t?.message)
                        }
                        override fun onResponse(call: Call<Item>?, response: Response<Item>?) {
                            startActivity(Intent(this@OneItemActivity, ItemsActivity::class.java))
                        }
                    })
            }
        })
    }
}
