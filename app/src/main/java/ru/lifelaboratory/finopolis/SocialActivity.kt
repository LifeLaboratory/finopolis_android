package ru.lifelaboratory.finopolis

import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.vk.sdk.api.*
import com.vk.sdk.api.model.VKWallPostResult
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_social.*
import kotlinx.android.synthetic.main.activity_social.btn_to_items
import kotlinx.android.synthetic.main.activity_social.btn_to_posts
import kotlinx.android.synthetic.main.activity_social.btn_to_profile
import kotlinx.android.synthetic.main.activity_social.btn_to_social
import com.vk.sdk.api.VKApiConst
import com.vk.sdk.api.VKParameters
import com.vk.sdk.api.VKRequest



class SocialActivity : AppCompatActivity() {

    var dialog: AlertDialog? = null
    var dialogInfo: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social)

        btn_to_items.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SocialActivity, ItemsActivity::class.java))
        })
        btn_to_posts.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SocialActivity, PostsActivity::class.java))
        })
        btn_to_social.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SocialActivity, SocialActivity::class.java))
        })
        btn_to_profile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SocialActivity, MainActivity::class.java))
        })

        vk_statistic.setOnClickListener(View.OnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@SocialActivity)
            builder.setTitle("Доступно в PRO аккаунте")
            builder.setPositiveButton("Купить") { p0, p1 ->
                run {
                    val builderInfo: AlertDialog.Builder = AlertDialog.Builder(this@SocialActivity)
                    builderInfo.setTitle("Статистика")
                    dialogInfo = builderInfo.create()
                    dialogInfo?.show()
                    val request = VKRequest("stats.get", VKParameters.from(VKApiConst.GROUP_ID, 187349196,
                        "intervals_count", 1, "stats_groups", "visitors", "v", "5.101", "extended", 0,
                        "interval", "day"))
                    Log.e(LOG_TAG, request.toString())
                    request.executeWithListener(object: VKRequest.VKRequestListener() {
                        override fun onComplete(response: VKResponse?) {
                            Log.e(LOG_TAG, response.toString())
                            val builderInfo: AlertDialog.Builder = AlertDialog.Builder(this@SocialActivity)
                            builderInfo.setTitle("Статистика")
                            dialogInfo = builderInfo.create()
                            dialogInfo?.show()
                        }
                        override fun onError(error: VKError?) {
                            Log.e(LOG_TAG, error.toString())
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
        })
    }
}
