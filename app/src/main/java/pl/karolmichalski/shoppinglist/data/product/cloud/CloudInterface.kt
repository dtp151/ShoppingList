package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product
import retrofit2.http.*

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

	@POST("synchronizeProducts")
	fun synchronizeProducts(@Header("uid") uid: String,
							@Body products: List<Product>)
			: Single<Boolean>
}