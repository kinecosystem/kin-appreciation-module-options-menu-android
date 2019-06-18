package org.kin.ecosystem.appreciation.options.menu.ui

internal interface GiftingPresenter {

	fun onAttach(view: GiftingView)

	fun onItemSelected(itemIndex: GiftingView.ItemIndex)

	fun onSliderCompleted()

	fun onAnimationEnded()

	fun onClose(closeType: CloseType)

	fun onDetach()
}