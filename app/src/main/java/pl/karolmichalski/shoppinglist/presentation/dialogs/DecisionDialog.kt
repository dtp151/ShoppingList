package pl.karolmichalski.shoppinglist.presentation.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import pl.karolmichalski.shoppinglist.R
import pl.karolmichalski.shoppinglist.databinding.DialogDecisionBinding

class DecisionDialog : DialogFragment() {

	var title: String? = null
	var button1text: String? = null
	var button2text: String? = null

	var onButton1Click: () -> Unit = { dismiss() }
	var onButton2Click: () -> Unit = { dismiss() }

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		attemptSettingDefaultTexts()
		return DataBindingUtil.inflate<DialogDecisionBinding>(inflater, R.layout.dialog_decision, container, false).also {
			it.lifecycleOwner = this
			it.title = title
			it.button1text = button1text
			it.button2text = button2text
			it.onButton1Click = onButton1Click
			it.onButton2Click = onButton2Click
		}.root
	}

	private fun attemptSettingDefaultTexts() {
		if (title == null) title = getString(R.string.are_you_sure_question)
		if (button1text == null) button1text = getString(R.string.yes)
		if (button2text == null) button2text = getString(R.string.no)
	}
}