package pl.karolmichalski.shoppinglist.di

import dagger.Component
import pl.karolmichalski.shoppinglist.di.modules.ProductModule
import pl.karolmichalski.shoppinglist.di.modules.SharedPreferencesModule
import pl.karolmichalski.shoppinglist.di.modules.UserModule
import pl.karolmichalski.shoppinglist.di.modules.ViewModelModule
import pl.karolmichalski.shoppinglist.presentation.screens.login.LoginActivity
import pl.karolmichalski.shoppinglist.presentation.screens.main.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [UserModule::class, ProductModule::class, ViewModelModule::class, SharedPreferencesModule::class])
interface AppComponent {
	fun inject(loginActivity: LoginActivity)
	fun inject(mainActivity: MainActivity)
}