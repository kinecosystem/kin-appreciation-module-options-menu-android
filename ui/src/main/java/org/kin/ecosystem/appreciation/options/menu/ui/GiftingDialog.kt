package org.kin.ecosystem.appreciation.options.menu.ui


import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.constraint.ConstraintLayout
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextSwitcher
import android.widget.TextView
import com.kin.ecosystem.base.FontUtil
import java.util.*


class GiftingDialog private constructor(context: Context, themeResId: Int, balance: Long,
                                        eventsListener: EventsListener?) :
	Dialog(context, themeResId) {

	private val mainHandler = Handler(Looper.getMainLooper())
	private val presenter: GiftingPresenter
	private var balanceText: TextSwitcher? = null
	private var container: ConstraintLayout? = null

	private val softButtonsBarHeight: Int
	private val optionsArray = ArrayList<GiftOptionView>(4)
	private val optionsMap = mapOf(
		Pair(GiftingView.ItemIndex.FIRST, GiftingView.GiftOption(1, context.getString(R.string.kingifting_love_it))),
		Pair(GiftingView.ItemIndex.SECOND, GiftingView.GiftOption(5, context.getString(R.string.kingifting_you_rule))),
		Pair(GiftingView.ItemIndex.THIRD, GiftingView.GiftOption(10, context.getString(R.string.kingifting_amazing_stuff))),
		Pair(GiftingView.ItemIndex.FOURTH, GiftingView.GiftOption(20, context.getString(R.string.kingifting_you_made_my_day)))
	)

	init {
		FontUtil.init(assetsManager = context.assets)
		setUpWindowLayout()
		presenter = GiftingPresenterImpl(balance, optionsMap, eventsListener, mainHandler)
		softButtonsBarHeight = getSoftButtonsBarHeight()
	}

	private fun initViews() {
		container = findViewById(R.id.screen_content)
		balanceText = findViewById<TextSwitcher>(R.id.balance_text).apply {
			setFactory {
				val balanceText = TextView(context)
				balanceText.setTextAppearance(context, R.style.KinGiftingBalanceText)
				balanceText.typeface = FontUtil.SAILEC_MEDIUM
				balanceText
			}
		}
		findViewById<ImageView>(R.id.close_btn).apply {
			setOnClickListener { presenter.onClose(CloseType.CLOSE_BUTTON) }
		}

		val itemSelectedListener = View.OnClickListener {
			val itemIndex = GiftingView.ItemIndex.valueOf(it.tag.toString())
			presenter.onItemSelected(itemIndex)
		}

		optionsArray.addAll(
			arrayOf(
				findViewById(R.id.option_1),
				findViewById(R.id.option_2),
				findViewById(R.id.option_3),
				findViewById(R.id.option_4)
			)
		)
		for (optionView in optionsArray) {
			optionView.setOnClickListener(itemSelectedListener)
			optionView.setAnimationListener(onSliderCompleted = { presenter.onSliderCompleted() },
											onAnimationEnded = { presenter.onAnimationEnded() })
		}
	}

	private val viewActions = object : GiftingView {
		override fun setItem(itemIndex: GiftingView.ItemIndex, amount: Int, title: String) {
			optionsArray[itemIndex.ordinal].apply {
				setTitle(title)
				setAmount(amount)
			}
		}

		override fun disableItem(itemIndex: GiftingView.ItemIndex) {
			optionsArray[itemIndex.ordinal].disable()
		}

		override fun updateBalance(amount: String) {
			balanceText?.setText(amount)
		}

		override fun showSelectedAnimation(itemIndex: GiftingView.ItemIndex) {
			optionsArray[itemIndex.ordinal].startOnSelectedAnimation()
		}

		override fun close() {
			this@GiftingDialog.dismiss()
		}
	}

	private fun setUpWindowLayout() {
		val newParams = WindowManager.LayoutParams().apply {
			copyFrom(this@GiftingDialog.window!!.attributes)
			width = WindowManager.LayoutParams.MATCH_PARENT
			height = WindowManager.LayoutParams.MATCH_PARENT
			gravity = Gravity.BOTTOM
		}
		this@GiftingDialog.window!!.attributes = newParams
	}

	override fun onBackPressed() {
		presenter.onClose(CloseType.BACK_NAV_BUTTON)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.kingifting_bottom_dialog_main)
		initViews()
		setCancelable(true)
		setCanceledOnTouchOutside(true)
		presenter.onAttach(viewActions)

	}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (event.action == MotionEvent.ACTION_UP) {
			container?.let { container ->
				if (event.rawY - softButtonsBarHeight < container.top) {
					presenter.onClose(CloseType.TOUCH_OUTSIDE)
					return false
				}
			}
		}

		return super.onTouchEvent(event)
	}

	private fun getSoftButtonsBarHeight(): Int {
		val hasHardKeys = ViewConfiguration.get(context).hasPermanentMenuKey()
		if (!hasHardKeys) {
			// getRealMetrics is only available with API 17 and +
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				window.windowManager.defaultDisplay.apply {
					val metrics = DisplayMetrics()
					val realMetrics = DisplayMetrics()
					getMetrics(metrics)
					getRealMetrics(realMetrics)
					return if (realMetrics.heightPixels > metrics.heightPixels) {
						realMetrics.heightPixels - metrics.heightPixels
					} else 0
				}
			} else {
				val tv = TypedValue()
				if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
					return TypedValue.complexToDimensionPixelSize(
						tv.data, context.resources.displayMetrics
					)
				}
			}
		}
		return 0
	}

	data class Builder(private val context: Context) {
		private var balance: Long = -1
		private var eventsListener: EventsListener? = null
		private var themeResId: Int = R.style.KinGiftingDialog_Light

		fun balance(balance: Long) = apply { this.balance = balance }
		fun theme(dialogTheme: DialogTheme) = apply {
			this.themeResId =
				if (dialogTheme == DialogTheme.LIGHT) R.style.KinGiftingDialog_Light else R.style.KinGiftingDialog_Dark
		}

		fun eventsListener(eventsListener: EventsListener) =
			apply { this.eventsListener = eventsListener }

		@Throws(exceptionClasses = [IllegalArgumentException::class])
		fun build(): GiftingDialog {
			if (balance == -1L) throw IllegalArgumentException("Please provide balance")

			return GiftingDialog(context, themeResId, balance, eventsListener)
		}
	}
}
