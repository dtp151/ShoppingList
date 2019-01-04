package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product

interface CloudInterfaceWrapper {

	fun addProduct(uid: String,
				   id: Long,
				   name: String)
			: Single<String>

	fun deleteProduct(uid: String,
					  id: Long)
			: Single<Boolean>

	fun synchronizeProducts(uid: String,
							products: List<Product>?)
			: Single<List<Product>>
}