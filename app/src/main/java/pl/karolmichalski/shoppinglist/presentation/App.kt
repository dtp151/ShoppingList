package pl.karolmichalski.shoppinglist.presentation

import androidx.multidex.MultiDexApplication
import pl.karolmichalski.shoppinglist.di.AppComponent
import pl.karolmichalski.shoppinglist.di.DaggerAppComponent
import pl.karolmichalski.shoppinglist.di.modules.ProductModule
import pl.karolmichalski.shoppinglist.di.modules.SharedPreferencesModule
import pl.karolmichalski.shoppinglist.di.modules.UserModule

class App : MultiDexApplication() {

	val appComponent: AppComponent by lazy {
		DaggerAppComponent.builder()
				.userModule(UserModule())
				.productModule(ProductModule(applicationContext))
				.sharedPreferencesModule(SharedPreferencesModule(applicationContext))
				.build()
	}

}