package pl.karolmichalski.shoppinglist.presentation.screens.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import pl.karolmichalski.shoppinglist.R
import pl.karolmichalski.shoppinglist.data.models.Product
import pl.karolmichalski.shoppinglist.databinding.ActivityMainBinding
import pl.karolmichalski.shoppinglist.presentation.dialogs.DecisionDialog
import pl.karolmichalski.shoppinglist.presentation.screens.login.LoginActivity
import pl.karolmichalski.shoppinglist.presentation.utils.ActionModeManager
import pl.karolmichalski.shoppinglist.presentation.utils.BundleDelegate

class MainActivity : AppCompatActivity(), MainListener, ActionModeManager.Callback {

	private var Bundle.selectedProducts by BundleDelegate.HashSet<String>("selected_products")

	private val viewModel by lazy {
		ViewModelProviders.of(this, MainViewModel.Factory(application)).get(MainViewModel::class.java)
	}

	private val actionModeManager by lazy {
		ActionModeManager(this)
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).apply {
			setLifecycleOwner(this@MainActivity)
			viewModel = this@MainActivity.viewModel
			listener = this@MainActivity
		}
		viewModel.getProducts(this)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.main_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.logout -> {
			showLogoutDecisionDialog()
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		outState?.selectedProducts = viewModel.selectedProducts
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
		super.onRestoreInstanceState(savedInstanceState)
		savedInstanceState?.selectedProducts?.let {
			viewModel.selectedProducts.addAll(it)
		}
	}

	override fun onAddBtnClick() {
		viewModel.productName.value?.let { name ->
			viewModel.addProduct(name)
		}
		viewModel.clearProductName()
	}

	override fun onProductClick(): (Product) -> Unit {
		return {
			viewModel.invalidateProductSelection(it)
			val checkedProductsCount = viewModel.selectedProducts.size
			actionModeManager.invalidateCount(checkedProductsCount)
		}
	}

	override fun onStartSupportActionMode(callback: ActionMode.Callback): ActionMode? {
		return startSupportActionMode(callback)
	}

	override fun onDeleteButtonClicked() {
		viewModel.removeCheckedProducts()
	}

	override fun onActionModeDestroyed() {
		viewModel.deselectAllProducts()
		viewModel.productList.value = viewModel.productList.value
	}

	private fun showLogoutDecisionDialog() {
		DecisionDialog().also {
			it.title = getString(R.string.are_you_sure_you_want_to_log_out_question)
			it.onButton1Click = { logout() }
		}.show(supportFragmentManager, DecisionDialog::class.java.simpleName)
	}

	private fun logout() {
		viewModel.logOut()
		startActivity(Intent(this, LoginActivity::class.java))
		finish()
	}
}