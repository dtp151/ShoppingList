package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single
import pl.karolmichalski.shoppinglist.data.models.Product

class CloudInterfaceWrapperImpl(
		private val cloudInterface: CloudInterface)
	: CloudInterfaceWrapper {

	override fun addProduct(uid: String, id: Int, name: String): Single<String> {
		return cloudInterface.addProduct(uid, id, name)
	}

	override fun deleteProduct(uid: String, id: Int): Single<Boolean> {
		return cloudInterface.deleteProduct(uid, id)
	}

	override fun synchronizeProducts(uid: String, products: List<Product>?): Single<Boolean> {
		return when (products) {
			null -> Single.error(Exception("productList is null"))
			else -> cloudInterface.synchronizeProducts(uid, products)
		}
	}

}