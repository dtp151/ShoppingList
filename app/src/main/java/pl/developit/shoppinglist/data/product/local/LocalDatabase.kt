package pl.developit.shoppinglist.data.product.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.developit.shoppinglist.data.models.Product

@Database(entities = [(Product::class)], version = 2)
abstract class LocalDatabase : RoomDatabase() {

	object Builder {
		fun build(context: Context): LocalDatabaseDAO {
			val database = Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, "shopping.db")
					.fallbackToDestructiveMigration()
					.build()
			return database.productsDao()
		}

	}

	abstract fun productsDao(): LocalDatabaseDAO

}