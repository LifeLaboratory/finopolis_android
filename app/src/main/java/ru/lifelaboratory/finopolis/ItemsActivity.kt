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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.adapter_list_items.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        btn_to_items.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ItemsActivity, ItemsActivity::class.java))
        })
        btn_to_posts.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ItemsActivity, PostsActivity::class.java))
        })
        btn_to_social.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ItemsActivity, SocialActivity::class.java))
        })
        btn_to_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ItemsActivity, MainActivity::class.java))
        })
        btn_add_item.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ItemsActivity, OneItemActivity::class.java))
        })

        val gson = Gson()
        val json = this.getSharedPreferences(MEMORY, Context.MODE_PRIVATE).getString(MEMORY_USER, "")
        val user: Profile = gson.fromJson(json, Profile::class.java)

        run {
            val service = ServerAPI.create()
            service.nomenclature(user.id)
                .enqueue(object: Callback<List<Item>> {
                    override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                        Log.e(LOG_TAG, t.toString())
                        Toast.makeText(this@ItemsActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                        if (response?.body() != null) {
                            user.items = response.body()
                            list_items.adapter = object: BaseAdapter() {
                                var lInflater: LayoutInflater = this@ItemsActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                var items : List<Item>? = user.items

                                override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                                    val view : View = convertView ?: lInflater.inflate(R.layout.adapter_list_items, parent, false)
                                    view.item_title.text = items?.get(position)?.title
                                    view.item_price.text = items?.get(position)?.price.toString()
                                    view.item_ost.text = items?.get(position)?.ost.toString()
                                    view.item_description.text = items?.get(position)?.description
                                    Picasso.with(this@ItemsActivity)
                                        .load(items?.get(position)?.photo)
                                        .placeholder(R.drawable.tmp)
                                        .error(R.drawable.tmp)
                                        .into(view.item_photo)
                                    return view
                                }

                                override fun getItem(p0: Int): Any {
                                    if (user.items == null)
                                        return user
                                    return user.items!![p0]
                                }

                                override fun getItemId(p0: Int): Long {
                                    return p0.toLong()
                                }

                                override fun getCount(): Int {
                                    if (user.items == null)
                                        return 0
                                    return user.items!!.size
                                }
                            }
                            Log.e(LOG_TAG, response.body().toString())
                            Log.e(LOG_TAG, user.posts.toString())
                        } else {
                            Toast.makeText(this@ItemsActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }
}
