package pl.developit.shoppinglist.presentation.screens.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.databinding.ActivityMainBinding
import pl.developit.shoppinglist.presentation.App
import pl.developit.shoppinglist.presentation.dialogs.DecisionDialog
import pl.developit.shoppinglist.presentation.screens.login.LoginActivity
import pl.developit.shoppinglist.presentation.utils.ActionModeManager
import pl.developit.shoppinglist.presentation.utils.BundleDelegate
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainListener, ActionModeManager.Callback, SwipeRefreshLayout.OnRefreshListener {

	private var Bundle.selectedProducts by BundleDelegate.HashSet<Long>("selected_products")
	private var Bundle.newProductName by BundleDelegate.String("new_product_name")

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	private val viewModel by lazy {
		ViewModelProviders.of(this, viewModelFactory)[MainViewModel::class.java]
	}

	private val actionModeManager by lazy {
		ActionModeManager(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		(application as App).appComponent.inject(this)
		DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).also {
			it.lifecycleOwner = this
			it.viewModel = viewModel
			it.listener = this
			it.onRefreshListener = this
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
		viewModel.newProductName.value?.let { outState?.newProductName = it }
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
		super.onRestoreInstanceState(savedInstanceState)
		savedInstanceState?.let {
			viewModel.selectedProducts.addAll(it.selectedProducts)
			viewModel.newProductName.value = it.newProductName
		}
	}

	override fun onAddBtnClick() {
		viewModel.newProductName.value?.let { name ->
			viewModel.addProduct(name)
		}
		viewModel.clearNewProductName()
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

	override fun onRefresh() {
		viewModel.synchronizeProducts(this)
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