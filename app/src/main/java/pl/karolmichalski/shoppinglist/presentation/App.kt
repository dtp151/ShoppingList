package pl.karolmichalski.shoppinglist.presentation

import androidx.multidex.MultiDexApplication
import pl.karolmichalski.shoppinglist.data.product.ProductModule
import pl.karolmichalski.shoppinglist.data.sharedPrefs.SharedPreferencesModule
import pl.karolmichalski.shoppinglist.data.user.UserModule
import pl.karolmichalski.shoppinglist.di.AppComponent
import pl.karolmichalski.shoppinglist.di.DaggerAppComponent

class App : MultiDexApplication() {

	val appComponent: AppComponent by lazy {
		DaggerAppComponent.builder()
				.userModule(UserModule())
				.productModule(ProductModule(applicationContext))
				.sharedPreferencesModule(SharedPreferencesModule(applicationContext))
				.build()
	}

}