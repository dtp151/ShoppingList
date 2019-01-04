package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product

class CloudInterfaceWrapperImpl(
		private val cloudInterface: CloudInterface)
	: CloudInterfaceWrapper {

	override fun addProduct(uid: String, id: Long, name: String): Single<String> {
		return cloudInterface.addProduct(uid, id, name)
	}

	override fun deleteProduct(uid: String, id: Long): Single<Boolean> {
		return cloudInterface.deleteProduct(uid, id)
	}

	override fun synchronizeProducts(uid: String, products: List<Product>?): Single<List<Product>> {
		return when (products) {
			null -> Single.error(Exception("productList is null"))
			else -> cloudInterface.synchronizeProducts(uid, products)
		}
	}

}