package ru.lifelaboratory.finopolis

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.google.gson.Gson
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ArrayAdapter
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKApiPhoto
import com.vk.sdk.api.model.VKPhotoArray
import com.vk.sdk.api.model.VKWallPostResult
import com.vk.sdk.api.photo.VKImageParameters
import com.vk.sdk.api.photo.VKUploadImage
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_post.btn_to_items
import kotlinx.android.synthetic.main.activity_post.btn_to_posts
import kotlinx.android.synthetic.main.activity_post.btn_to_profile
import kotlinx.android.synthetic.main.activity_post.btn_to_social
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result.response
import kotlin.annotation.Target as Target1


class OnePostActivity : AppCompatActivity() {

    var idItem: Int? = null
    var priceItem: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        btn_to_items.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OnePostActivity, ItemsActivity::class.java))
        })
        btn_to_posts.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OnePostActivity, PostsActivity::class.java))
        })
        btn_to_social.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OnePostActivity, SocialActivity::class.java))
        })
        btn_to_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@OnePostActivity, MainActivity::class.java))
        })

        val gson = Gson()
        val json = this.getSharedPreferences(MEMORY, Context.MODE_PRIVATE).getString(MEMORY_USER, "")
        val user: Profile = gson.fromJson(json, Profile::class.java)

        btn_add_post.setOnClickListener(View.OnClickListener {
            run {
                val service = ServerAPI.create()
                service.createPost(Post(title = item_title.text.toString(), itemId = idItem, userId = user.id, text = item_description.text.toString(), photo = item_photo.text.toString()))
                    .enqueue(object: Callback<Post> {
                        override fun onFailure(call: Call<Post>?, t: Throwable?) {
                            Log.e(LOG_TAG, t?.message)
                        }
                        override fun onResponse(call: Call<Post>?, response: Response<Post>?) {
                            Log.e(LOG_TAG, response?.body().toString())
                            run {
                                val service = ServerAPI.create()
                                service.profile(user.id)
                                    .enqueue(object: Callback<Profile> {
                                        override fun onFailure(call: Call<Profile>?, t: Throwable?) {
                                            Log.e(LOG_TAG, t.toString())
                                            Toast.makeText(this@OnePostActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                                        }
                                        override fun onResponse(call: Call<Profile>?, response: Response<Profile>?) {
                                            if (response?.body() != null) {
                                                if (item_photo.text != null && !item_photo.text.equals("")) {
                                                    Picasso.with(this@OnePostActivity)
                                                        .load(item_photo.text.toString())
                                                        .into(object: Target {
                                                            override fun onBitmapFailed(errorDrawable: Drawable?) {}
                                                            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                                                                val request: VKRequest =
                                                                    VKApi.uploadWallPhotoRequest(
                                                                        VKUploadImage(bitmap, VKImageParameters.jpgImage(0.9f)),
                                                                        0, 187349196)
                                                                request.executeWithListener(object: VKRequest.VKRequestListener() {
                                                                    override fun onComplete(responseVK: VKResponse?) {
                                                                        super.onComplete(responseVK)
                                                                        val photoModel: VKApiPhoto = (responseVK?.parsedModel as VKPhotoArray)[0]
                                                                        val post = VKApi.wall().post(VKParameters.from(VKApiConst.OWNER_ID, "-187349196", VKApiConst.MESSAGE,
                                                                            item_title.text.toString() + "\n\n" + item_description.text.toString() + "\n\nЦена: " + priceItem.toString(),
                                                                            VKApiConst.ATTACHMENTS, "http://10.62.100.119/site/" + response.body().login + "," + VKApiConst.PHOTO + photoModel.owner_id + "_" + photoModel.id
                                                                        ))
                                                                        post.setModelClass(VKWallPostResult::class.java)
                                                                        post.executeWithListener(object : VKRequest.VKRequestListener() {
                                                                            override fun onComplete(response: VKResponse?) {
                                                                                startActivity(Intent(this@OnePostActivity, PostsActivity::class.java))
                                                                            }
                                                                            override fun onError(error: VKError?) {}
                                                                        })
                                                                    }
                                                                })
                                                            }
                                                            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                                                        })
                                                }



                                            }
                                        }
                                    })
                            }
                        }
                    })
            }
        })

        btn_add_item.setOnClickListener(View.OnClickListener {
            val builderSingle = AlertDialog.Builder(this@OnePostActivity)
            builderSingle.setTitle("Выбор товара")

            val arrayAdapter = ArrayAdapter<String>(this@OnePostActivity, android.R.layout.select_dialog_singlechoice)

            val service = ServerAPI.create()
            service.nomenclature(user.id)
                .enqueue(object: Callback<List<Item>> {
                    override fun onFailure(call: Call<List<Item>>?, t: Throwable?) {
                        Log.e(LOG_TAG, t.toString())
                        Toast.makeText(this@OnePostActivity, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<List<Item>>?, response: Response<List<Item>>?) {
                        if (response?.body() != null) {
                            response.body().forEach {
                                arrayAdapter.add(it.title)
                            }
                            Log.e(LOG_TAG, response.body().toString())
                            Log.e(LOG_TAG, user.posts.toString())
                            builderSingle.setNegativeButton("Отмена",
                                DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })

                            builderSingle.setAdapter(arrayAdapter, DialogInterface.OnClickListener { dialog, which ->
                                val strName = arrayAdapter.getItem(which)
                                response.body().forEach {
                                    if (it.title.equals(strName)) {
                                        this@OnePostActivity.idItem = it.id
                                        this@OnePostActivity.priceItem = it.price
                                        item_title.text = it.title?.toEditable()
                                        item_description.text = it.description?.toEditable()
                                        item_photo.text = it.photo?.toEditable()
                                        Picasso.with(this@OnePostActivity)
                                            .load(it.photo)
                                            .placeholder(R.drawable.tmp)
                                            .error(R.drawable.tmp)
                                            .into(item_img)
                                    }
                                }
                                dialog.dismiss()
                            })
                            builderSingle.show()
                        } else {
                            Toast.makeText(this@OnePostActivity, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        })
    }
}
