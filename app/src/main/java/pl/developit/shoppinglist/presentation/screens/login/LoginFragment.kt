package pl.developit.shoppinglist.presentation.screens.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.databinding.FragmentLoginBinding
import pl.developit.shoppinglist.presentation.utils.BaseFragment
import pl.developit.shoppinglist.presentation.utils.BundleDelegate

class LoginFragment : BaseFragment(), LoginListener {

	private var Bundle.email by BundleDelegate.String("email")
	private var Bundle.password by BundleDelegate.String("password")

	private val viewModel by viewModel<LoginViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(false)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater, R.layout.fragment_login, container, false).also {
			it.lifecycleOwner = this
			it.listener = this
			it.viewModel = viewModel
		}

		viewModel.liveEvent.observe(this, Observer {
			when (it) {
				is LoginViewModel.LoginEvent.LogIn -> mainCommunicator.showShoppingFragment()
				is LoginViewModel.LoginEvent.Error -> showError(it.error)
			}
		})
		return binding.root
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		viewModel.email.value?.let { email -> outState.email = email }
		viewModel.password.value?.let { password -> outState.password = password }
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		savedInstanceState?.let {
			viewModel.email.value = it.email
			viewModel.password.value = it.password
		}
	}

	override fun onLoginBtnClick(email: String, password: String, isLoginRememberable: Boolean) {
		viewModel.logIn(email, password, isLoginRememberable)
	}

	override fun onRegisterBtnClick() {
		mainCommunicator.showRegisterFragment()
	}

	private fun showError(error: String) = Toast.makeText(context, error, Toast.LENGTH_SHORT).show()

}