package pl.karolmichalski.shoppinglist.presentation

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import pl.karolmichalski.shoppinglist.di.AppComponent
import pl.karolmichalski.shoppinglist.di.DaggerAppComponent
import pl.karolmichalski.shoppinglist.di.modules.ProductModule
import pl.karolmichalski.shoppinglist.di.modules.SharedPreferencesModule
import pl.karolmichalski.shoppinglist.di.modules.UserModule

class App : MultiDexApplication() {

	val appComponent: AppComponent by lazy {
		DaggerAppComponent.builder()
				.userModule(UserModule(applicationContext))
				.productModule(ProductModule(applicationContext))
				.sharedPreferencesModule(SharedPreferencesModule(applicationContext))
				.build()
	}

	override fun onCreate() {
		super.onCreate()
		Fabric.with(this, Crashlytics())
	}
}