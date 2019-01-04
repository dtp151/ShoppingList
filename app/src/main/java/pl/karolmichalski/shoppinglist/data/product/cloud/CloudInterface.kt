package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface CloudInterface {

	@POST("addProduct")
	fun addProduct(@Header("uid") uid: String,
				   @Query("id") id: Int,
				   @Query("name") name: String)
			: Single<String>

	@POST("deleteProduct")
	fun deleteProduct(@Header("uid") uid: String,
					  @Query("id") id: Int)
			: Single<Boolean>

	@POST("synchronizeProducts")
	fun synchronizeProducts(@Header("uid") uid: String,
							@Body products: List<Product>)
			: Single<Boolean>
}