package pl.developit.shoppinglist.data.product.local

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import io.reactivex.Completable
import io.reactivex.Single
import pl.developit.shoppinglist.data.models.Product


@Dao
interface LocalDatabaseDAO {

	@Query("Select * from products")
	fun selectAll(): LiveData<List<Product>>

	@Insert(onConflict = REPLACE)
	fun insert(product: Product): Single<Long>

	@Insert(onConflict = REPLACE)
	fun insert(productList: List<Product>): Single<List<Long>>

	@Update
	fun update(product: Product): Completable

	@Delete
	fun delete(product: Product): Completable

	@Query("Delete from products")
	fun clearTable()

}