package ru.lifelaboratory.finopolis

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_posts.*
import kotlinx.android.synthetic.main.activity_posts.btn_to_items
import kotlinx.android.synthetic.main.activity_posts.btn_to_posts
import kotlinx.android.synthetic.main.activity_posts.btn_to_profile
import kotlinx.android.synthetic.main.activity_posts.btn_to_social
import kotlinx.android.synthetic.main.adapter_list_posts.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        btn_to_items.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PostsActivity, ItemsActivity::class.java))
        })
        btn_to_posts.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PostsActivity, PostsActivity::class.java))
        })
        btn_to_social.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PostsActivity, SocialActivity::class.java))
        })
        btn_to_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PostsActivity, MainActivity::class.java))
        })
        btn_add_posts.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@PostsActivity, OnePostActivity::class.java))
        })

        val gson = Gson()
        val json = this.getSharedPreferences(MEMORY, Context.MODE_PRIVATE).getString(MEMORY_USER, "")
        val user: Profile = gson.fromJson(json, Profile::class.java)

        run {
            val service = ServerAPI.create()
            service.post(user.id)
                .enqueue(object: Callback<List<Post>> {
                    override fun onFailure(call: Call<List<Post>>?, t: Throwable?) {
                        Log.e(LOG_TAG, t.toString())
                        Toast.makeText(this@PostsActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<List<Post>>?, response: Response<List<Post>>?) {
                        if (response?.body() != null) {
                            user.posts = response.body()
                            list_posts.adapter = object: BaseAdapter() {
                                var lInflater: LayoutInflater = this@PostsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                var items : List<Post>? = user.posts

                                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                                    val view : View = convertView ?: lInflater.inflate(R.layout.adapter_list_posts, parent, false)
                                    view.item_title.text = items?.get(position)?.title
                                    view.item_description.text = items?.get(position)?.text
                                    view.item_price.text = items?.get(position)?.item?.price.toString()
                                    view.item_title_.text = items?.get(position)?.item?.title
                                    Picasso.with(this@PostsActivity)
                                        .load(items?.get(position)?.photo)
                                        .placeholder(R.drawable.tmp)
                                        .error(R.drawable.tmp)
                                        .into(view.item_photo)
                                    return view
                                }

                                override fun getItem(p0: Int): Any {
                                    if (user.posts == null)
                                        return user
                                    return user.posts!![p0]
                                }

                                override fun getItemId(p0: Int): Long {
                                    return p0.toLong()
                                }

                                override fun getCount(): Int {
                                    if (user.posts == null)
                                        return 0
                                    return user.posts!!.size
                                }
                            }
                            Log.e(LOG_TAG, response.body().toString())
                            Log.e(LOG_TAG, user.posts.toString())
                        } else {
                            Toast.makeText(this@PostsActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }
}
