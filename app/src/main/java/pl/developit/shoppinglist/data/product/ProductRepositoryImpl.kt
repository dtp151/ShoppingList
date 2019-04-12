package pl.developit.shoppinglist.data.product

import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.data.product.cloud.CloudInterface
import pl.developit.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.developit.shoppinglist.domain.product.ProductRepository
import pl.developit.shoppinglist.domain.user.UserRepository
import pl.developit.shoppinglist.presentation.utils.getTimeStamp

class ProductRepositoryImpl(
		private val userRepository: UserRepository,
		private val localDatabase: LocalDatabaseDAO,
		private val cloudInterface: CloudInterface)
	: ProductRepository {

	private val disposables = CompositeDisposable()

	override val productList = MutableLiveData<List<Product>>()
	override val isSyncing = MutableLiveData<Boolean>()

	init {
		observeLocalTable()
	}

	override fun sync() {
		syncDatabases()
	}

	override fun insert(name: String) {
		val product = Product(getTimeStamp(), name, Product.Status.ADDED)
		insertLocallyAndRemotely(product)
	}

	override fun markAsDeleted(product: Product) {
		product.status = Product.Status.DELETED
		disposables.add(
				localDatabase.update(product)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(onComplete = { deleteRemotelyAndLocally(product) }))
	}

	override fun clearLocalDatabase() {
		disposables.add(
				Completable.fromAction { localDatabase.clearTable() }
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

	override fun clearDisposables() {
		disposables.clear()
	}


	private fun observeLocalTable() {
		disposables.add(
				localDatabase.selectAll()
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(onNext = { list -> productList.postValue(list) }))
	}

	private fun syncDatabases() {
		val productList = productList.value
		disposables.add(
				cloudInterface.synchronizeProducts(userRepository.getUid(), productList)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.doOnSubscribe { isSyncing.postValue(true) }
						.doFinally { isSyncing.postValue(false) }
						.subscribeBy(
								onSuccess = { newProductList -> replaceLocally(productList, newProductList) },
								onError = { Crashlytics.logException(it) }))
	}

	private fun insertLocallyAndRemotely(product: Product) {
		disposables.add(
				localDatabase.insert(product)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(onComplete = { insertRemotelyAndMarkSyncedLocally(product) }))
	}

	private fun deleteRemotelyAndLocally(product: Product) {
		disposables.add(
				cloudInterface.deleteProduct(userRepository.getUid(), product.id)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(
								onSuccess = { deleteLocally(product) },
								onError = { Crashlytics.logException(it) }))
	}

	private fun insertRemotelyAndMarkSyncedLocally(product: Product) {
		disposables.add(
				cloudInterface.addProduct(userRepository.getUid(), product.id, product.name)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(
								onSuccess = {
									product.status = Product.Status.SYNCED
									updateLocally(product)
								},
								onError = { Crashlytics.logException(it) }))
	}

	private fun replaceLocally(oldProducts: List<Product>?, newProducts: List<Product>) {
		disposables.add(
				localDatabase.delete(oldProducts)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribe {
							newProducts.map { it.status = Product.Status.SYNCED }
							insertLocally(newProducts)
						})
	}

	private fun insertLocally(products: List<Product>) {
		disposables.add(
				localDatabase.insert(products)
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

	private fun updateLocally(product: Product) {
		disposables.add(
				localDatabase.update(product)
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

	private fun deleteLocally(product: Product) {
		disposables.add(
				localDatabase.delete(product)
						.subscribeOn(Schedulers.io())
						.subscribe())
	}

}