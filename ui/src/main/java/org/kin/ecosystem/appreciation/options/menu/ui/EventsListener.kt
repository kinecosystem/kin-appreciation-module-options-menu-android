package org.kin.ecosystem.appreciation.options.menu.ui

interface EventsListener {

	fun onDialogOpened()

	fun onItemSelected(itemIndex: Int, amount: String)

	fun onDialogClosed(closeType: CloseType)

}
