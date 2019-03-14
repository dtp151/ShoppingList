package pl.developit.shoppinglist.presentation.screens.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.databinding.ActivityLoginBinding
import pl.developit.shoppinglist.presentation.App
import pl.developit.shoppinglist.presentation.screens.main.MainActivity
import pl.developit.shoppinglist.presentation.utils.BundleDelegate
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), LoginListener {

	private var Bundle.email by BundleDelegate.String("email")
	private var Bundle.password by BundleDelegate.String("password")

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	private val viewModel by lazy {
		ViewModelProviders.of(this, viewModelFactory)[LoginViewModel::class.java]
	}

	private val showError = Observer<String> {
		Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		(application as App).appComponent.inject(this)
		if (viewModel.isUserLogged())
			logIn()
		else
			initScreen()
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		viewModel.email.value?.let { email -> outState?.email = email }
		viewModel.password.value?.let { password -> outState?.password = password }
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
		super.onRestoreInstanceState(savedInstanceState)
		savedInstanceState?.let {
			viewModel.email.value = it.email
			viewModel.password.value = it.password
		}
	}

	private fun initScreen() {
		DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login).also {
			it.lifecycleOwner = this
			it.listener = this
			it.viewModel = viewModel
		}
		viewModel.loginSuccess.observe(this, Observer { logIn() })
		viewModel.errorMessage.observe(this, showError)
	}

	private fun logIn() {
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}

	override fun onLoginBtnClick() {
		viewModel.logIn()
	}


	override fun onRegisterBtnClick() {
		viewModel.register()
	}

}