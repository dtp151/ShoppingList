package pl.developit.shoppinglist.presentation.utils

import androidx.fragment.app.Fragment
import pl.developit.shoppinglist.presentation.App
import pl.developit.shoppinglist.presentation.screens.main.MainCommunicator

abstract class BaseFragment : Fragment() {

	val mainCommunicator by lazy {
		activity as MainCommunicator
	}

	val appComponent by lazy {
		(activity?.application as App).appComponent
	}

}