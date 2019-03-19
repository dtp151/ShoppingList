package pl.developit.shoppinglist.data.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.data.product.cloud.CloudInterfaceWrapper
import pl.developit.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.developit.shoppinglist.domain.product.ProductRepository
import pl.developit.shoppinglist.domain.user.UserRepository
import pl.developit.shoppinglist.presentation.utils.getTimeStamp

class ProductRepositoryImpl(
		private val userRepository: UserRepository,
		private val localDatabase: LocalDatabaseDAO,
		private val cloudInterfaceWrapper: CloudInterfaceWrapper)
	: ProductRepository {

	private val disposables = CompositeDisposable()
	//TODO add disposing

	private val productList = MutableLiveData<List<Product>>()
	private val isSyncing = MutableLiveData<Boolean>()

	override fun getAll(): LiveData<List<Product>> {
		observeLocalTable()
		return productList
	}

	override fun syncAll(): LiveData<Boolean> {
		syncDatabases()
		return isSyncing
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

	private fun observeLocalTable() {
		disposables.add(
				localDatabase.selectAll()
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(onNext = { list -> productList.postValue(list) }))
	}

	private fun syncDatabases() {
		disposables.add(
				cloudInterfaceWrapper.synchronizeProducts(userRepository.getUid(), productList.value)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.doOnSubscribe { isSyncing.postValue(true) }
						.doFinally { isSyncing.postValue(false) }
						.subscribeBy(
								onSuccess = { products -> clearLocalTableAndInsertLocally(products) },
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
				cloudInterfaceWrapper.deleteProduct(userRepository.getUid(), product.id)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(
								onSuccess = { deleteLocally(product) },
								onError = { Crashlytics.logException(it) }))
	}

	private fun insertRemotelyAndMarkSyncedLocally(product: Product) {
		disposables.add(
				cloudInterfaceWrapper.addProduct(userRepository.getUid(), product.id, product.name)
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribeBy(
								onSuccess = {
									product.status = Product.Status.SYNCED
									updateLocally(product)
								},
								onError = { Crashlytics.logException(it) }))
	}

	private fun clearLocalTableAndInsertLocally(products: List<Product>) {
		disposables.add(
				Completable.fromAction { localDatabase.clearTable() }
						.subscribeOn(Schedulers.io())
						.observeOn(Schedulers.io())
						.subscribe {
							products.map { it.status = Product.Status.SYNCED }
							insertLocally(products)
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