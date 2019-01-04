package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product

class CloudInterfaceWrapperImpl(
		private val cloudInterface: CloudInterface)
	: CloudInterfaceWrapper {

	override fun generateProductKey(uid: String): Single<String> {
		return cloudInterface.generateProductKey(uid)
	}

	override fun addProduct(uid: String, key: String, name: String): Single<String> {
		return cloudInterface.addProduct(uid, key, name)
	}

	override fun deleteProduct(uid: String, key: String): Single<Boolean> {
		return cloudInterface.deleteProduct(uid, key)
	}

	override fun synchronizeProducts(uid: String, products: List<Product>?): Single<Boolean> {
		return when (products) {
			null -> Single.error(Exception("productList is null"))
			else -> cloudInterface.synchronizeProducts(uid, products)
		}
	}

}