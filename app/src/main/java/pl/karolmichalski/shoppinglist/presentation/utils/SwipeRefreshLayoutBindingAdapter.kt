package pl.karolmichalski.shoppinglist.presentation.utils

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("onRefreshListener")
fun SwipeRefreshLayout.setOnRefreshListener(onRefreshListener: SwipeRefreshLayout.OnRefreshListener) {
	setOnRefreshListener(onRefreshListener)
}

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.setIsRefreshing(isRefreshing: Boolean) {
	setRefreshing(isRefreshing)
}