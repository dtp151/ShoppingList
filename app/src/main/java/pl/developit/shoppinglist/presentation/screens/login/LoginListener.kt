package pl.developit.shoppinglist.presentation.screens.login

interface LoginListener {
	fun onLoginBtnClick(email: String, password: String, isLoginRememberable: Boolean)

	fun onRegisterBtnClick()
}