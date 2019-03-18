package pl.developit.shoppinglist.presentation.screens.main

import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.presentation.screens.login.LoginFragment
import pl.developit.shoppinglist.presentation.screens.shopping.ShoppingFragment

interface MainCommunicator {

	fun provideFragmentManager(): FragmentManager

	fun onStartSupportActionMode(callback: ActionMode.Callback): ActionMode?

	fun showLoginFragment() {
		showFragment(LoginFragment())
	}

	fun showShoppingFragment() {
		showFragment(ShoppingFragment())
	}

	private fun showFragment(fragment: Fragment) {
		val fragmentTransaction = provideFragmentManager().beginTransaction()
		fragmentTransaction.replace(R.id.fragmentContainer, fragment)
		fragmentTransaction.commitAllowingStateLoss()
	}
}