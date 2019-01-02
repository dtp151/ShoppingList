package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CloudInterface {

	@GET("generateProductKey")
	fun generateProductKey(@Header("uid") uid: String)
			: Single<String>

	@POST("addProduct")
	fun addProduct(@Header("uid") uid: String,
				   @Query("key") key: String,
				   @Query("name") name: String)
			: Single<String>

	@POST("deleteProduct")
	fun deleteProduct(@Header("uid") uid: String,
					  @Query("key") key: String)
			: Single<Boolean>
}