package pl.karolmichalski.shoppinglist.presentation.screens.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import pl.karolmichalski.shoppinglist.R
import pl.karolmichalski.shoppinglist.databinding.ActivityLoginBinding
import pl.karolmichalski.shoppinglist.presentation.screens.main.MainActivity
import pl.karolmichalski.shoppinglist.presentation.utils.BundleDelegate


class LoginActivity : AppCompatActivity(), LoginListener {

	private var Bundle.email by BundleDelegate.String("email")
	private var Bundle.password by BundleDelegate.String("password")

	private val viewModel by lazy {
		ViewModelProviders.of(this, LoginViewModel.Factory(application)).get(LoginViewModel::class.java)
	}

	private val onLoginSuccess = Observer<Boolean> {
		logIn()
	}

	private val showError = Observer<String> {
		Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
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
		DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login).apply {
			setLifecycleOwner(this@LoginActivity)
			listener = this@LoginActivity
			viewModel = this@LoginActivity.viewModel
		}
		viewModel.loginSuccess.observe(this@LoginActivity, onLoginSuccess)
		viewModel.errorMessage.observe(this@LoginActivity, showError)
	}

	private fun logIn() {
		startActivity(Intent(this, MainActivity::class.java))
		finish()
	}

	override fun onLoginBtnClick() {
		viewModel.logInWithEmailAndPassword()
	}


	override fun onRegisterBtnClick() {
		viewModel.registerWithEmailAndPassword()
	}

}