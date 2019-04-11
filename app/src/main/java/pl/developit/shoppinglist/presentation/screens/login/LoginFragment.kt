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

		viewModel.loginResult.observe(this, startShoppingFragment())
		viewModel.errorMessage.observe(this, showError())
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

	override fun onLoginBtnClick() {
		viewModel.logIn()
	}

	override fun onRegisterBtnClick() {
		viewModel.register()
	}

	private fun startShoppingFragment(): Observer<Boolean> {
		return Observer { mainCommunicator.showShoppingFragment() }
	}

	private fun showError(): Observer<String> {
		return Observer { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
	}


}