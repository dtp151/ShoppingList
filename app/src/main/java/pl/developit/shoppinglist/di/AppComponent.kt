package pl.developit.shoppinglist.di

import dagger.Component
import pl.developit.shoppinglist.di.modules.ProductModule
import pl.developit.shoppinglist.di.modules.SharedPreferencesModule
import pl.developit.shoppinglist.di.modules.UserModule
import pl.developit.shoppinglist.di.modules.ViewModelModule
import pl.developit.shoppinglist.presentation.screens.login.LoginActivity
import pl.developit.shoppinglist.presentation.screens.main.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [UserModule::class, ProductModule::class, ViewModelModule::class, SharedPreferencesModule::class])
interface AppComponent {
	fun inject(loginActivity: LoginActivity)
	fun inject(mainActivity: MainActivity)
}