package pl.developit.shoppinglist.presentation.utils

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import pl.developit.shoppinglist.R

class ActionModeManager(private val callback: Callback) {

	private var mode: ActionMode? = null
	private var isActive = false

	fun invalidateCount() {
		val checkedProductCount = callback.getCheckedProductsCount()
		if (checkedProductCount > 0 && !isActive)
			mode = callback.onStartSupportActionMode(actionModeCallback)
		else if (checkedProductCount == 0)
			mode?.finish()
		updateCountTitle(checkedProductCount)
	}

	private fun updateCountTitle(count: Int) {
		mode?.menu?.findItem(R.id.count)?.title = count.toString()
	}

	private val actionModeCallback = object : ActionMode.Callback {
		override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
			mode?.menuInflater?.inflate(R.menu.products_menu, menu)
			isActive = true
			return true
		}

		override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
			return false
		}

		override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
			if (item?.itemId == R.id.delete) {
				callback.onDeleteButtonClicked()
				mode?.finish()
				return true
			}
			return false
		}

		override fun onDestroyActionMode(mode: ActionMode?) {
			callback.onActionModeDestroyed()
			isActive = false
		}
	}

	interface Callback {
		fun getCheckedProductsCount(): Int

		fun onStartSupportActionMode(callback: ActionMode.Callback): ActionMode?

		fun onDeleteButtonClicked()

		fun onActionModeDestroyed()
	}

}