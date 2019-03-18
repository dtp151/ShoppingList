package pl.developit.shoppinglist.di

import dagger.Component
import pl.developit.shoppinglist.di.modules.ProductModule
import pl.developit.shoppinglist.di.modules.SharedPreferencesModule
import pl.developit.shoppinglist.di.modules.UserModule
import pl.developit.shoppinglist.di.modules.ViewModelModule
import pl.developit.shoppinglist.presentation.screens.login.LoginFragment
import pl.developit.shoppinglist.presentation.screens.main.MainActivity
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [UserModule::class, ProductModule::class, ViewModelModule::class, SharedPreferencesModule::class])
interface AppComponent {
	fun inject(mainActivity: MainActivity)
	fun inject(loginFragment: LoginFragment)
	fun inject(shoppingFragment: ShoppingFragment)
}