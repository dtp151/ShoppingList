package pl.developit.shoppinglist.presentation.screens.shopping

import android.os.Bundle
import android.view.*
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import pl.developit.shoppinglist.R
import pl.developit.shoppinglist.data.models.Product
import pl.developit.shoppinglist.databinding.FragmentShoppingBinding
import pl.developit.shoppinglist.presentation.dialogs.DecisionDialog
import pl.developit.shoppinglist.presentation.utils.ActionModeManager
import pl.developit.shoppinglist.presentation.utils.BaseFragment
import pl.developit.shoppinglist.presentation.utils.BundleDelegate
import javax.inject.Inject

class ShoppingFragment : BaseFragment(), ShoppingListener, SwipeRefreshLayout.OnRefreshListener, ActionModeManager.Callback {

	private var Bundle.selectedProducts by BundleDelegate.HashSet<Long>("selected_products")
	private var Bundle.newProductName by BundleDelegate.String("new_product_name")

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	private val viewModel by lazy {
		ViewModelProviders.of(this, viewModelFactory)[ShoppingViewModel::class.java]
	}

	private val actionModeManager by lazy {
		ActionModeManager(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		appComponent.inject(this)
		val binding = DataBindingUtil.inflate<FragmentShoppingBinding>(inflater, R.layout.fragment_shopping, container, false).also {
			it.lifecycleOwner = this
			it.viewModel = viewModel
			it.listener = this
			it.onRefreshListener = this
		}

		viewModel.getProducts(this)

		return binding.root
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.selectedProducts = viewModel.selectedProducts
		viewModel.newProductName.value?.let { outState.newProductName = it }
	}

	override fun onViewStateRestored(savedInstanceState: Bundle?) {
		super.onViewStateRestored(savedInstanceState)
		savedInstanceState?.let {
			viewModel.selectedProducts.addAll(it.selectedProducts)
			viewModel.newProductName.value = it.newProductName

			actionModeManager.invalidateCount()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater?.inflate(R.menu.main_menu, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
		R.id.logout -> {
			showLogoutDecisionDialog()
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onRefresh() {
		viewModel.synchronizeProducts(this)
	}

	override fun getCheckedProductsCount(): Int {
		return viewModel.selectedProducts.count()
	}

	override fun onStartSupportActionMode(callback: ActionMode.Callback): ActionMode? {
		return mainCommunicator.onStartSupportActionMode(callback)
	}

	override fun onDeleteButtonClicked() {
		viewModel.removeCheckedProducts()
	}

	override fun onActionModeDestroyed() {
		viewModel.deselectAllProducts()
	}

	override fun onAddBtnClick() {
		viewModel.newProductName.value?.let { name ->
			viewModel.addProduct(name)
		}
		viewModel.clearNewProductName()
	}

	override fun onProductClick(): (Product) -> Unit {
		return { product ->
			viewModel.invalidateSelectionFor(product)
			actionModeManager.invalidateCount()
		}
	}

	private fun showLogoutDecisionDialog() {
		DecisionDialog().also {
			it.title = getString(R.string.are_you_sure_you_want_to_log_out_question)
			it.onButton1Click = { logout() }
		}.show(childFragmentManager, DecisionDialog::class.java.simpleName)
	}

	private fun logout() {
		viewModel.logOut()
		mainCommunicator.showLoginFragment()
	}

}