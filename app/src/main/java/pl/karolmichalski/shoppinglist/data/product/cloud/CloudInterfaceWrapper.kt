package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product

interface CloudInterfaceWrapper {

	fun generateProductKey(uid: String)
			: Single<String>

	fun addProduct(uid: String,
				   key: String,
				   name: String)
			: Single<String>

	fun deleteProduct(uid: String,
					  key: String)
			: Single<Boolean>

	fun synchronizeProducts(uid: String,
							products: List<Product>?)
			: Single<Boolean>
}