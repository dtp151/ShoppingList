package pl.developit.shoppinglist.presentation

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.developit.shoppinglist.di.productsModule
import pl.developit.shoppinglist.di.userModule
import pl.developit.shoppinglist.di.viewModelFactoryModule

class App : MultiDexApplication() {

	override fun onCreate() {
		super.onCreate()
		Fabric.with(this, Crashlytics())
		startKoin {
			androidContext(this@App)
			modules(listOf(viewModelFactoryModule, productsModule, userModule))
		}
	}
}