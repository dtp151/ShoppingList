package pl.developit.shoppinglist.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.data.product.ProductRepositoryImpl
import pl.developit.shoppinglist.data.product.cloud.RemoteProductSource
import pl.developit.shoppinglist.data.product.local.LocalDatabase
import pl.developit.shoppinglist.data.user.UserInterface
import pl.developit.shoppinglist.data.user.UserRepositoryImpl
import pl.developit.shoppinglist.domain.product.ProductRepository
import pl.developit.shoppinglist.domain.user.UserRepository
import pl.developit.shoppinglist.presentation.screens.login.LoginViewModel
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingFragment
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingViewModel
import pl.developit.shoppinglist.presentation.utils.ApiErrorParser

val viewModelFactoryModule = module {
	viewModel { LoginViewModel(get()) }
	viewModel { ShoppingViewModel(get(), get()) }
}

val productsModule = module {
	scope(named<ShoppingFragment>()) {
		scoped { LocalDatabase.Builder.build(androidContext()) }
		scoped { RemoteProductSource.Builder.build() }
		scoped<ProductRepository> { ProductRepositoryImpl(get<UserRepository>().getUid(), get(), get()) }
	}
}

val userModule = module {
	single<SharedPreferences> { androidContext().getSharedPreferences(androidContext().getString(R.string.sharedpreferences_file_key), Context.MODE_PRIVATE) }
	single { UserInterface.Builder.build() }
	single { ApiErrorParser(androidContext()) }
	single<UserRepository> { UserRepositoryImpl(androidContext(), get(), get(), get()) }
}