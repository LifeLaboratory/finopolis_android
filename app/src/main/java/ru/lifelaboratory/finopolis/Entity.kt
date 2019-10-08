package ru.lifelaboratory.finopolis

import android.text.Editable
import com.google.gson.annotations.SerializedName

data class Profile(
    @SerializedName("@лицо") val id: Int = 0,
    @SerializedName("логин") val login: String = "",
    @SerializedName("пароль") val password: String = "",
    var items: List<Item>? = null, var posts: List<Post>? = null,
    val shop: Shop? = null,
    @SerializedName("фио") val name: String? = null,
    @SerializedName("заголовок") val title: String? = null,
    @SerializedName("описание") val description: String? = null,
    @SerializedName("фото") val photo: String? = null,
    @SerializedName("почта") val email: String? = null,
    @SerializedName("номер") val phone: String? = null)
data class Shop(val id: Int, val title: String?, val description: String?)
data class Item(
    @SerializedName("номенклатура") val id: Int? = null,
    @SerializedName("наименование") val title: String? = null,
    @SerializedName("описание") val description: String? = null,
    @SerializedName("лицо") val userId: Int? = null,
    @SerializedName("фото") val photo: String? = null,
    @SerializedName("цена") val price: Double? = null,
    @SerializedName("остаток") val ost: Double? = null)
data class Post(
    @SerializedName("@пост") val id: Int? = null,
    @SerializedName("заголовок") val title: String? = null,
    @SerializedName("номенклатура") val itemId: Int? = null,
    @SerializedName("лицо") val userId: Int? = null,
    @SerializedName("товар") val item: Item? = null,
    @SerializedName("текст") val text: String? = null,
    @SerializedName("фото") val photo: String? = null)
data class Move(
    @SerializedName("наименование") val title: String? = null,
    @SerializedName("датавремя") val datetime: String? = null,
    @SerializedName("тип") val type: String? = null,
    @SerializedName("куплено") val bye: Int? = null,
    @SerializedName("продано") val sell: Int? = null,
    @SerializedName("прибыль") val profit: Double? = null,
    @SerializedName("цена_покупки") val priceBye: Double? = null,
    @SerializedName("цена_продажи") val priceSale: Double? = null)