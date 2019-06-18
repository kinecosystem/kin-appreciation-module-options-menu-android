package org.kin.ecosystem.appreciation.options.menu.ui

internal interface GiftingView {

	fun setItem(itemIndex: ItemIndex, amount: Int, title: String)

	fun disableItem(itemIndex: ItemIndex)

	fun updateBalance(amount: String)

	fun showSelectedAnimation(itemIndex: ItemIndex)

	fun close()

	enum class ItemIndex {
		FIRST,
		SECOND,
		THIRD,
		FOURTH
	}

	data class GiftOption(val amount: Int, val title: String)
}
