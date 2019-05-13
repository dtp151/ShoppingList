package pl.developit.shoppinglist.data.product.local

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import pl.developit.shoppinglist.data.models.Product


@Dao
interface LocalDatabaseDAO {

	@Query("Select * from products")
	fun selectAll(): Observable<List<Product>>

	@Query("Select * from products")
	fun selectAllOnce(): Single<List<Product>>

	@Insert(onConflict = REPLACE)
	fun insert(product: Product): Completable

	@Insert(onConflict = REPLACE)
	fun insert(productList: List<Product>): Single<List<Long>>

	@Update
	fun update(product: Product): Completable

	@Delete
	fun delete(product: Product): Completable

	@Delete
	fun delete(productList: List<Product>?): Completable

	@Query("Delete from products")
	fun clearTable()

}