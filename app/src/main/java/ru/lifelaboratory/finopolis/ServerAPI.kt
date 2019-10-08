package ru.lifelaboratory.finopolis

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServerAPI {

    @POST("/auth")
    fun auth(@Body user: Profile): Call<Profile>

    @GET("/profile/{id}")
    fun profile(@Path("id") id: Int): Call<Profile>

    @GET("/post")
    fun post(@Query("@лицо") id: Int): Call<List<Post>>

    @GET("/nomenclature")
    fun nomenclature(@Query("@лицо") id: Int): Call<List<Item>>

    @POST("/nomenclature")
    fun addNomenclature(@Body item: Item): Call<Item>

    @POST("/post")
    fun createPost(@Body post: Post): Call<Post>

    @GET("/movements")
    fun movements(@Query("@лицо") id: Int): Call<List<Move>>

    companion object Factory {
        fun create(): ServerAPI {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(SERVER)
                .build()

            return retrofit.create(ServerAPI::class.java)
        }
    }

}
