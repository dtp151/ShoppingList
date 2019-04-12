package pl.developit.shoppinglist.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.data.product.ProductRepositoryImpl
import pl.developit.shoppinglist.data.product.cloud.CloudInterface
import pl.developit.shoppinglist.data.product.local.LocalDatabase
import pl.developit.shoppinglist.data.product.local.LocalDatabaseDAO
import pl.developit.shoppinglist.data.user.UserInterface
import pl.developit.shoppinglist.data.user.UserRepositoryImpl
import pl.developit.shoppinglist.domain.product.ProductRepository
import pl.developit.shoppinglist.domain.user.UserRepository
import pl.developit.shoppinglist.presentation.screens.login.LoginViewModel
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingViewModel
import pl.developit.shoppinglist.presentation.utils.ApiErrorParser

val viewModelFactoryModule = module {
	viewModel { LoginViewModel(get()) }
	viewModel { ShoppingViewModel(get(), get()) }
}

val productsModule = module {
	single<LocalDatabaseDAO> { LocalDatabase.Builder.build(androidContext()) }
	single<CloudInterface> { CloudInterface.Builder.build() }
	factory<ProductRepository> { ProductRepositoryImpl(get(), get(), get()) } // TODO change to scoped life to ShoppingFragment
}

val userModule = module {
	single<SharedPreferences> {
		androidContext().getSharedPreferences(
				androidContext().getString(R.string.sharedpreferences_file_key), Context.MODE_PRIVATE)
	}
	single<UserInterface> { UserInterface.Builder.build() }
	single<ApiErrorParser> { ApiErrorParser(androidContext()) }
	single<UserRepository> { UserRepositoryImpl(androidContext(), get(), get(), get()) }
}