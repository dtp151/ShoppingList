package pl.developit.shoppinglist.presentation.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.FragmentManager
import org.koin.android.ext.android.inject
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.domain.UserUseCases

class MainActivity : AppCompatActivity(), MainCommunicator {

	private val userRepository by inject<UserUseCases>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContentView(R.layout.activity_main)

		if (savedInstanceState != null) // if savedInstanceState is not null then it means that we are returning to the app and fragment has been saved = no need to show new fragment
			return

		if (userRepository.isLoggedIn())
			showShoppingFragment()
		else
			showLoginFragment()
	}

	override fun onStartSupportActionMode(callback: ActionMode.Callback): ActionMode? = startSupportActionMode(callback)

	override fun provideFragmentManager(): FragmentManager = supportFragmentManager

}