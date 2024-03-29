package pl.developit.shoppinglist.presentation.utils

import android.widget.CheckedTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("checked")
fun CheckedTextView.setChecked(isChecked: Boolean) {
	setChecked(isChecked)
}

@InverseBindingAdapter(attribute = "checked")
fun CheckedTextView.getChecked(): Boolean {
	return isChecked
}

@BindingAdapter("app:checkedAttrChanged")
fun CheckedTextView.setCheckedListener(attrChange: InverseBindingListener) {
	setOnClickListener {
		isChecked = isChecked.not()
		attrChange.onChange()
	}
}

@BindingAdapter("onRefreshListener")
fun SwipeRefreshLayout.setOnRefreshListener(onRefreshListener: SwipeRefreshLayout.OnRefreshListener) {
	setOnRefreshListener(onRefreshListener)
}

@BindingAdapter("isRefreshing")
fun SwipeRefreshLayout.setIsRefreshing(isRefreshing: Boolean) {
	setRefreshing(isRefreshing)
}