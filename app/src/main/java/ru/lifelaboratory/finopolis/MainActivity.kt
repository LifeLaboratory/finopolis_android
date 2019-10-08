package ru.lifelaboratory.finopolis

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.methods.VKApiWall
import kotlinx.android.synthetic.main.activity_main.*
import com.vk.sdk.api.VKResponse
import com.vk.sdk.api.VKRequest.VKRequestListener
import com.vk.sdk.api.model.VKWallPostResult
import com.vk.sdk.api.VKApi
import com.vk.sdk.api.VKRequest
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.activity_main.btn_to_items
import kotlinx.android.synthetic.main.activity_main.btn_to_posts
import kotlinx.android.synthetic.main.activity_main.btn_to_profile
import kotlinx.android.synthetic.main.activity_main.btn_to_social
import kotlinx.android.synthetic.main.adapter_list_items.view.*
import kotlinx.android.synthetic.main.adapter_list_statistics.view.*
import kotlinx.android.synthetic.main.dialog_statistics.*
import kotlinx.android.synthetic.main.dialog_statistics.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    var dialog: AlertDialog? = null
    var dialogInfo: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gson = Gson()
        val json = this.getSharedPreferences(MEMORY, Context.MODE_PRIVATE).getString(MEMORY_USER, "")
        val user: Profile = gson.fromJson(json, Profile::class.java)

        profile_name.text = (user.name ?: "").toEditable()
        profile_email.text = (user.email ?: "").toEditable()
        profile_phone.text = (user.phone ?: "").toEditable()
        shop_title.text = (user.shop?.title ?: "").toEditable()
        shop_description.text = (user.shop?.description ?: "").toEditable()
        btn_to_items.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MainActivity, ItemsActivity::class.java))
        })
        btn_to_posts.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MainActivity, PostsActivity::class.java))
        })
        btn_to_social.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MainActivity, SocialActivity::class.java))
        })
        btn_to_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@MainActivity, MainActivity::class.java))
        })

        // кнопка для ВКонтакте
        btn_vk.setOnClickListener {
            VKSdk.login(this, VKScope.EMAIL, VKScope.PHOTOS, VKScope.WALL, VKScope.STATS)
        }

        run {
            val service = ServerAPI.create()
            service.profile(user.id)
                .enqueue(object: Callback<Profile> {
                    override fun onFailure(call: Call<Profile>?, t: Throwable?) {
                        Log.e(LOG_TAG, t.toString())
                        Toast.makeText(this@MainActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<Profile>?, response: Response<Profile>?) {
                        if (response?.body() != null) {
                            profile_name.text = response.body().name?.toEditable()
                            shop_title.text = response.body().title?.toEditable()
                            shop_description.text = response.body().description?.toEditable()
                            profile_phone.text = response.body().phone?.toEditable()
                            profile_email.text = response.body().email?.toEditable()
                            Picasso.with(this@MainActivity)
                                .load(response.body().photo)
                                .placeholder(R.drawable.social)
                                .error(R.drawable.social)
                                .into(profile_img)
                            Log.e(LOG_TAG, response.body().toString())
                        }
                    }
                })
        }

        btn_to_statistics.setOnClickListener(View.OnClickListener {
            run {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Доступно в PRO аккаунте")
                builder.setPositiveButton("Купить") { p0, p1 ->
                    run {
                        val service = ServerAPI.create()
                        service.movements(user.id)
                            .enqueue(object: Callback<List<Move>> {
                                override fun onFailure(call: Call<List<Move>>?, t: Throwable?) {
                                    Log.e(LOG_TAG, t.toString())
                                    Toast.makeText(this@MainActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                                }
                                override fun onResponse(call: Call<List<Move>>?, response: Response<List<Move>>?) {
                                    if (response?.body() != null) {
                                        Log.e(LOG_TAG, response.body().toString())
                                        var score: String = ""
                                        val adb: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                                        adb.setTitle("История")
                                        val view: View = getLayoutInflater().inflate(R.layout.dialog_statistics, null)
                                        adb.setView(view)
                                        val tvCount: TextView = view.findViewById(R.id.test_title)
                                        response.body().forEach {
                                            if (it.type.equals("итоги")) {
                                                score = "Всего куплено: " + it.bye + " шт.\nВсего продано: " + it.sell +
                                                        " шт.\nЦена покупок: " + it.priceBye + " руб.\nЦена продаж: " + it.priceSale + " руб.\n" +
                                                        "Общая прибыль: " + it.profit + " руб."
                                                tvCount.text = score
                                            }
                                        }
                                        view.list_statistics.adapter = object: BaseAdapter() {
                                            var lInflater: LayoutInflater = this@MainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                            var items : List<Move>? = response.body()
                                            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
                                                val view: View = convertView ?: lInflater.inflate(
                                                    R.layout.adapter_list_statistics,
                                                    parent,
                                                    false
                                                )
                                                if (!items?.get(position)?.type.equals("итоги")) {
                                                    view.stat_type.text = items?.get(position)?.type
                                                    if (items?.get(position)?.priceBye == null) {
                                                        view.stat_price.text = items?.get(position)?.priceSale.toString()
                                                    } else {
                                                        view.stat_price.text = items?.get(position)?.priceBye.toString()
                                                    }
                                                    if (items?.get(position)?.bye == null) {
                                                        view.stat_change.text = items?.get(position)?.sell.toString()
                                                    } else {
                                                        view.stat_change.text = items?.get(position)?.bye.toString()
                                                    }

                                                    if (items?.get(position)?.profit != null) {
                                                        view.stat_profit.text = items?.get(position)?.profit.toString() + " руб."
                                                    } else {
                                                        view.stat_profit.text = "0 руб."
                                                    }
                                                }
                                                return view
                                            }
                                            override fun getItem(p0: Int): Any {
                                                if (items == null)
                                                    return items!!
                                                return items!![p0]
                                            }
                                            override fun getItemId(p0: Int): Long {
                                                return p0.toLong()
                                            }
                                            override fun getCount(): Int {
                                                if (items == null)
                                                    return 0
                                                return items!!.size
                                            }
                                        }
                                        val dialog: Dialog = adb.create()
                                        dialog.show()
                                    }
                                }
                            })
                    }
                }
                builder.setNegativeButton("Отмена") { p0, p1 -> dialog?.cancel() }
                dialog = builder.create()
                dialog?.show()
                dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setBackgroundResource(R.color.colorBtn)
                dialog?.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
                dialog?.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(Color.BLACK)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 10485) { // авторизация через VK
            if (!VKSdk.onActivityResult(requestCode, resultCode, data, object : VKCallback<VKAccessToken> {
                    override fun onResult(res: VKAccessToken) {
                        Log.i(LOG_TAG, "Авторизация ВКонтакте прошла успешно")
                        Log.i(LOG_TAG, res.email + " " + res.userId)
                    }

                    override fun onError(error: VKError) {
                        Log.e(LOG_TAG, "Авторизация ВКонтакте не успешна")
                    }
                })
            ) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } else {
            Log.i(LOG_TAG, "Код = $requestCode")
        }

    }
}
