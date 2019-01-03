package pl.karolmichalski.shoppinglist.data.product

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.karolmichalski.shoppinglist.data.models.Product
import pl.karolmichalski.shoppinglist.data.product.cloud.CloudInterfaceWrapper
import pl.karolmichalski.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.karolmichalski.shoppinglist.domain.product.ProductRepository

class ProductRepositoryImpl(
		private val localDatabase: LocalDatabaseDAO,
		private val cloudInterfaceWrapper: CloudInterfaceWrapper)
	: ProductRepository {

	override fun getAll(): LiveData<List<Product>> {
		return localDatabase.getAll()
	}

	override fun insert(name: String) {
		cloudInterfaceWrapper.generateProductKey(FirebaseAuth.getInstance().currentUser!!.uid)
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.io())
				.subscribeBy(
						onSuccess = {key ->
							val product = Product(key, name, Product.Status.ADDED)
							Completable.fromAction { localDatabase.insert(product) }
									.andThen(cloudInterfaceWrapper.addProduct(FirebaseAuth.getInstance().currentUser!!.uid, product.key, product.name))
									.subscribeBy(
											onSuccess = {
												product.status = Product.Status.SYNCED
												localDatabase.update(product)
											},
											onError = {
												Log.d("adaw", "wadawdaw")
											}
									)

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
							cloudInterfaceWrapper.deleteProduct(FirebaseAuth.getInstance().currentUser!!.uid, product.key)
									.subscribeBy(
											onSuccess = {
												Completable.fromAction { localDatabase.delete(product) }
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
}