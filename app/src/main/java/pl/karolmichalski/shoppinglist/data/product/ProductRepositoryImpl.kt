package pl.karolmichalski.shoppinglist.data.product

import android.util.Log
import androidx.lifecycle.LiveData
import io.reactivex.Completable
import io.reactivex.Single
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
		Single.fromCallable { localDatabase.insert(product) }
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onSuccess = { productId ->
							cloudInterfaceWrapper.addProduct(userRepository.getUid(), productId, product.name)
									.subscribeBy(
											onSuccess = {
												product.status = Product.Status.SYNCED
												localDatabase.update(product)
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
		Completable.fromAction { localDatabase.update(product) }
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe()
	}

	override fun delete(product: Product) {
		product.status = Product.Status.DELETED
		Completable.fromAction { localDatabase.update(product) }
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onComplete = {
							cloudInterfaceWrapper.deleteProduct(userRepository.getUid(), product.id)
									.subscribeBy(
											onSuccess = {
												removeProductLocally(product)

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

	override fun synchronize(productList: List<Product>?, doFinally: () -> Unit) {
		cloudInterfaceWrapper.synchronizeProducts(userRepository.getUid(), productList)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.doFinally { doFinally() }
				.subscribeBy(
						onSuccess = {
							it.map { it.status = Product.Status.SYNCED }
							localDatabase.deleteAll()
							localDatabase.insertProducts(it)
						},
						onError = {

						}
				)
	}

	private fun removeProductLocally(product: Product) {
		Completable
				.fromAction { localDatabase.delete(product) }
				.subscribeOn(Schedulers.io())
				.subscribe()
	}

}