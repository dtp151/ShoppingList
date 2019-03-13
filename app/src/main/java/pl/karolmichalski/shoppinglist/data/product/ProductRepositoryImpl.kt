package pl.karolmichalski.shoppinglist.data.product

import android.util.Log
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.karolmichalski.shoppinglist.data.models.Product
import pl.karolmichalski.shoppinglist.data.product.cloud.CloudInterfaceWrapper
import pl.karolmichalski.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.karolmichalski.shoppinglist.domain.product.ProductRepository
import pl.karolmichalski.shoppinglist.domain.user.UserRepository
import pl.karolmichalski.shoppinglist.presentation.utils.getTimeStamp

class ProductRepositoryImpl(
		private val userRepository: UserRepository,
		private val localDatabase: LocalDatabaseDAO,
		private val cloudInterfaceWrapper: CloudInterfaceWrapper)
	: ProductRepository {

	override fun getAll(): LiveData<List<Product>> {
		return localDatabase.getAll()
	}

	override fun insert(name: String) {
		val product = Product(getTimeStamp(), name, Product.Status.ADDED)
		localDatabase.insert(product)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onSuccess = { productId ->
							cloudInterfaceWrapper.addProduct(userRepository.getUid(), productId, product.name)
									.subscribeBy(
											onSuccess = {
												product.status = Product.Status.SYNCED
												localDatabase.update(product).subscribe()
											},
											onError = {

											})


						},
						onError = {
							Log.d("adaw", "wadawdaw")
						}
				)

	}

	override fun update(product: Product) {
		localDatabase.update(product)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe()
	}

	override fun delete(product: Product) {
		product.status = Product.Status.DELETED
		localDatabase.update(product)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onComplete = {
							cloudInterfaceWrapper.deleteProduct(userRepository.getUid(), product.id)
									.subscribeBy(
											onSuccess = {
												localDatabase.delete(product)
														.subscribe()

											},
											onError = {
												Log.d("awdaw", "awdawdaw")
											}
									)
						},
						onError = {
							Log.d("awdaw", "awdawdaw")
						}
				)

	}

	override fun clearDatabase() {
		Completable.fromAction { localDatabase.deleteAll() }
				.subscribeOn(Schedulers.io())
				.subscribe()
	}

	override fun synchronize(productList: List<Product>?, onSynchronized: () -> Unit) {
		cloudInterfaceWrapper.synchronizeProducts(userRepository.getUid(), productList)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.doFinally { onSynchronized() }
				.subscribeBy(
						onSuccess = { products ->
							clearDatabase()
							products.map { it.status = Product.Status.SYNCED }
							localDatabase.insert(products).subscribe()
						},
						onError = {

						}
				)
	}

}