package org.kin.ecosystem.appreciation.options.menu.ui

import android.os.Handler


internal class GiftingPresenterImpl(private var balance: Long,
                                    private var optionsMap: Map<GiftingView.ItemIndex, GiftingView.GiftOption>,
                                    private val eventsListener: EventsListener?,
                                    private val mainThread: Handler) : GiftingPresenter {

	private var giftingView: GiftingView? = null
	private var isClosing = false
	private var selectedItem: GiftingView.ItemIndex? = null

	override fun onAttach(view: GiftingView) {
		isClosing = false;
		giftingView = view
		setupOptions()
		giftingView?.updateBalance(balance.toString())
		disableUnaffordableOptions(balance)
		eventsListener?.onDialogOpened()
	}

	private fun setupOptions() {
		optionsMap.forEach {
			giftingView?.setItem(it.key, it.value.amount, it.value.title)
		}
	}

	private fun disableUnaffordableOptions(balance: Long) {
		optionsMap.filter { it.value.amount > balance }.forEach {
			giftingView?.disableItem(it.key)
		}
	}

	override fun onItemSelected(itemIndex: GiftingView.ItemIndex) {
		isClosing = true
		selectedItem = itemIndex
		giftingView?.apply {
			showSelectedAnimation(itemIndex)
			for (runningIndex in GiftingView.ItemIndex.values()) {
				if(runningIndex != itemIndex) {
					disableItem(runningIndex)
				}
			}
		}
		eventsListener?.onItemSelected(itemIndex.ordinal, optionsMap.getValue(itemIndex).amount)
	}

	override fun onSliderCompleted() {
		selectedItem?.let {
			val updatedAmount = balance - optionsMap.getValue(it).amount
			giftingView?.updateBalance(updatedAmount.toString())
		}
	}

	override fun onAnimationEnded() {
		mainThread.postDelayed({
			eventsListener?.onDialogClosed(CloseType.ITEM_SELECTED)
			giftingView?.close()
		}, CLOSE_DELAY)
	}

	override fun onClose(closeType: CloseType) {
		if (!isClosing) {
			isClosing = true
			eventsListener?.onDialogClosed(closeType)
			giftingView?.close()
		}
	}

	override fun onDetach() {
		giftingView = null
	}

	companion object {
		private const val CLOSE_DELAY = 800L
	}
}