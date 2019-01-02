package pl.karolmichalski.shoppinglist.data.product.cloud

import io.reactivex.Single

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
}