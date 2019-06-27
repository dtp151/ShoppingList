package pl.developit.shoppinglist.presentation.screens.register

interface RegisterListener {
	fun onRegisterBtnClick(email: String, password: String, repeatedPassword: String)

	fun onGoToLoginBtnClick()
}