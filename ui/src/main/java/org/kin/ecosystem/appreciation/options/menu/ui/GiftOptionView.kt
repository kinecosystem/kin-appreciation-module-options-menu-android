package org.kin.ecosystem.appreciation.options.menu.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.support.annotation.ColorInt
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatDelegate
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.kin.ecosystem.base.ThemeUtil
import com.kin.ecosystem.base.widget.withEndAction


internal class GiftOptionView @JvmOverloads constructor(context: Context,
                                                        attrs: AttributeSet? = null,
                                                        defStyleAttr: Int = 0) :
	ConstraintLayout(context, attrs, defStyleAttr) {

	private val titleView: TextView
	private val amountView: TextView
	private val itemBackgroundView: ImageView
	private val confettiView: ImageView
	private val kinLogoView: ImageView
	private var animationListener: AnimationListener? = null

	@ColorInt
	private val colorDisabled: Int
	private var isAnimatingThankYou = false

	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
		LayoutInflater.from(context).inflate(R.layout.kingifting_option_item, this, true)
		titleView = findViewById(R.id.text)
		amountView = findViewById(R.id.amount)
		itemBackgroundView = findViewById(R.id.item_background)
		confettiView = findViewById(R.id.confetti)
		kinLogoView = findViewById(R.id.kin_logo)
		colorDisabled = ThemeUtil.themeAttributeToColor(
			context, R.attr.disabledColor, R.color.kinecosystem_light_red
		)
		clipToPadding = false
		clipChildren = false
	}

	fun setTitle(text: String) {
		titleView.text = text
	}

	fun setAmount(amount: Int) {
		amountView.text = amount.toString()
	}

	fun setAnimationListener(onSliderCompleted: () -> Unit, onAnimationEnded: () -> Unit) {
		setAnimationListener(object : AnimationListener {
			override fun onSliderCompleted() {
				onSliderCompleted.invoke()
			}

			override fun onAnimationEnded() {
				onAnimationEnded.invoke()
			}
		})
	}

	private fun setAnimationListener(listener: AnimationListener) {
		animationListener = listener
	}

	fun startOnSelectedAnimation() {
		disableClicks()
		itemBackgroundView.visibility = View.VISIBLE
		val halfWidth = titleView.width / 2
		val thankYouText = context.getString(R.string.kingifting_thank_you)
		val textWidth = Paint().measureText(thankYouText)
		ValueAnimator.ofInt(0, titleView.width).apply {
			duration = ANIM_DURATION_SLIDE_LOAD
			interpolator = AnticipateInterpolator()
			addUpdateListener { valueAnimator ->
				val newWidth = valueAnimator.animatedValue as Int
				val params = itemBackgroundView.layoutParams
				params.width = newWidth
				itemBackgroundView.layoutParams = params
				if ((newWidth >= halfWidth - (textWidth * 2)) && !isAnimatingThankYou) {
					isAnimatingThankYou = true
					startThankYouAnim()
				}
			}
			withEndAction {
				amountView.visibility = GONE
				kinLogoView.visibility = GONE
				showConfetti()
				animationListener?.onSliderCompleted()

			}
			start()
		}
	}

	private fun startThankYouAnim() {
		AlphaAnimation(1F, 0F).apply {
			duration = ANIM_DURATION_FADE_OUT_TEXT
			fillAfter = false
			titleView.startAnimation(this)
		}
		val thankYouText = context.getString(R.string.kingifting_thank_you)
		ValueAnimator.ofFloat(0F, 0F).apply {
			duration = ((thankYouText.length * ANIM_DURATION_FADE_IN_LETTER).toLong())
			addUpdateListener {
				val textViewString = SpannableString(thankYouText)
				thankYouText.forEachIndexed { index, _ ->
					val delta = Math.max(Math.min((currentPlayTime - index * ANIM_DURATION_FADE_IN_LETTER), ANIM_DURATION_FADE_IN_LETTER), 0F)
					val alpha = delta / ANIM_DURATION_FADE_IN_LETTER
					val color = getColor(alpha, Color.WHITE)
					textViewString.setSpan(ForegroundColorSpan(color), index, if (index < thankYouText.length) index + 1 else index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
				}
				titleView.text = textViewString
			}
			start()
		}
	}

	private fun getColor(alpha: Float, realColor: Int) = ((0xFF * alpha).toInt() shl 24) or (realColor and 0x00FFFFFF)

	private fun showConfetti() {
		confettiView.visibility = View.VISIBLE
		val scaleX = ObjectAnimator.ofFloat(confettiView, "scaleX", 0f, 1f).apply {
			withEndAction { animationListener?.onAnimationEnded() }
		}
		val scaleY = ObjectAnimator.ofFloat(confettiView, "scaleY", 0f, 1f)
		AnimatorSet().apply {
			duration = ANIM_DURATION_SCALE_CONFETTI
			interpolator = AnticipateOvershootInterpolator()
			playTogether(scaleX, scaleY)
			start()
		}
	}

	fun disable() {
		disableClicks()
		amountView.setTextColor(colorDisabled)
		titleView.setTextColor(colorDisabled)
		titleView.apply {
			background.setColorFilter(colorDisabled, PorterDuff.Mode.SRC_ATOP)
		}
		findViewById<ImageView>(R.id.kin_logo).apply {
			setImageResource(R.drawable.kingifting_ic_kin_logo_disabled)
		}
	}

	private fun disableClicks() {
		isEnabled = false
		isClickable = false
	}

	companion object {
		private const val ANIM_DURATION_FADE_IN_LETTER= 50F
		private const val ANIM_DURATION_FADE_OUT_TEXT = 100L
		private const val ANIM_DURATION_SLIDE_LOAD = 700L
		private const val ANIM_DURATION_SCALE_CONFETTI = 600L
	}

	interface AnimationListener {
		fun onSliderCompleted()

		fun onAnimationEnded()
	}
}
