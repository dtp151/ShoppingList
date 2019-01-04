package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product

interface CloudInterfaceWrapper {

	fun addProduct(uid: String,
				   id: Int,
				   name: String)
			: Single<String>

	fun deleteProduct(uid: String,
					  id: Int)
			: Single<Boolean>

	fun synchronizeProducts(uid: String,
							products: List<Product>?)
			: Single<Boolean>
}