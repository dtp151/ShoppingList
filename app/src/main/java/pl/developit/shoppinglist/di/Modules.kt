package pl.developit.shoppinglist.di

import android.content.Context
import android.content.SharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.data.product.local.LocalDatabase
import pl.developit.shoppinglist.data.product.remote.RemoteProductSource
import pl.developit.shoppinglist.data.user.UserInterface
import pl.developit.shoppinglist.domain.ProductInteractor
import pl.developit.shoppinglist.domain.ProductUseCases
import pl.developit.shoppinglist.domain.UserInteractor
import pl.developit.shoppinglist.domain.UserUseCases
import pl.developit.shoppinglist.presentation.screens.login.LoginViewModel
import pl.developit.shoppinglist.presentation.screens.register.RegisterViewModel
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingFragment
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingViewModel
import pl.developit.shoppinglist.presentation.utils.ApiErrorParser

val viewModelFactoryModule = module {
	viewModel { LoginViewModel(get()) }
	viewModel { RegisterViewModel(get()) }
	viewModel { ShoppingViewModel(get(), get()) }
}

val productsModule = module {
	scope(named<ShoppingFragment>()) {
		scoped<ProductUseCases> { ProductInteractor(get<UserUseCases>().getUid(), get(), get()) }
		scoped { LocalDatabase.Builder.build(androidContext()) }
		scoped { RemoteProductSource.Builder.build() }
	}
}

val userModule = module {
	single<UserUseCases> { UserInteractor(androidContext(), get(), get(), get()) }
	single { UserInterface.Builder.build() }
	single { ApiErrorParser(androidContext()) }
	single<SharedPreferences> { androidContext().getSharedPreferences(androidContext().getString(R.string.sharedpreferences_file_key), Context.MODE_PRIVATE) }
}