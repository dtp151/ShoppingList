package pl.developit.shoppinglist.presentation.screens.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.android.viewmodel.ext.android.viewModel
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.databinding.FragmentRegisterBinding
import pl.developit.shoppinglist.presentation.utils.BaseFragment
import pl.developit.shoppinglist.presentation.utils.BundleDelegate

class RegisterFragment : BaseFragment(), RegisterListener {

	private var Bundle.email by BundleDelegate.String("email")

	private val viewModel by viewModel<RegisterViewModel>()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val binding = DataBindingUtil.inflate<FragmentRegisterBinding>(inflater, R.layout.fragment_register, container, false).also {
			it.lifecycleOwner = this
			it.listener = this
			it.viewModel = viewModel
		}

		viewModel.liveEvent.observe(this, Observer {
			when (it) {
				is RegisterViewModel.RegisterEvent.Registration -> mainCommunicator.showShoppingFragment()
				is RegisterViewModel.RegisterEvent.Error -> showError(it.error)
			}
		})

		return binding.root
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		viewModel.email.value?.let { email -> outState.email = email }
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		savedInstanceState?.let { viewModel.email.value = it.email }
	}

	override fun onRegisterBtnClick(email: String, password: String, repeatedPassword: String) {
		viewModel.register(email, password, repeatedPassword)
	}

	override fun onGoToLoginBtnClick() {
		mainCommunicator.showLoginFragment()
	}

	private fun showError(error: String) = Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
}
