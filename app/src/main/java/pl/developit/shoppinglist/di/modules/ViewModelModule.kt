package pl.developit.shoppinglist.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import pl.developit.shoppinglist.presentation.screens.login.LoginViewModel
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingViewModel
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Suppress("unused")
@Module
abstract class ViewModelModule {
	@Binds
	internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

	@Binds
	@IntoMap
	@ViewModelKey(LoginViewModel::class)
	internal abstract fun postLoginViewModel(viewModel: LoginViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(ShoppingViewModel::class)
	internal abstract fun postMainViewModel(viewModel: ShoppingViewModel): ViewModel

}

@Singleton
class ViewModelFactory @Inject constructor(
		private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>)
	: ViewModelProvider.Factory {
	override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)