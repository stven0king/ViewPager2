package androidx.viewpager2.integration.testapp.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Tanzhenxing
 * Date: 2021/4/7 7:04 下午
 * Description:解决 [RecyclerView] 嵌套到 [androidx.viewpager2.widget.ViewPager2] 左右滑动冲突
 * 目前只解决了左右滑动冲突
 */
class RecyclerViewAtViewPager2 : RecyclerView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var x1 = 0f
    var x2 = 0f
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if(event!!.action == MotionEvent.ACTION_DOWN) {
            x1 = event.x
        } else if(event.action == MotionEvent.ACTION_MOVE) {
            x2 = event.x
        } else if (event.action == MotionEvent.ACTION_CANCEL
            || event.action == MotionEvent.ACTION_UP) {
            x2 = 0f
            x1 = 0f
        }
        val xOffset= x2-x1
        if (layoutManager is LinearLayoutManager) {
            val linearLayoutManager = layoutManager as LinearLayoutManager
            if (linearLayoutManager.orientation == HORIZONTAL) {
                if ((xOffset <= 0 && canScrollHorizontally(1))
                    || (xOffset >= 0 && canScrollHorizontally(-1))) {
                    this.parent?.requestDisallowInterceptTouchEvent(true)
                } else {
                    this.parent?.requestDisallowInterceptTouchEvent(false)
                }
            } else {
                // TODO: 2021/4/8 目前没有实现上下滑动和 [androidx.viewpager2.widget.ViewPager2]上下滑动的冲突
            }
        } else {
            handleDefaultScroll()
        }

        return super.dispatchTouchEvent(event)
    }

    fun handleDefaultScroll() {
        val canScrollHorizontally = canScrollHorizontally(-1) || canScrollHorizontally(1)
        val canScrollVertically = canScrollVertically(-1) || canScrollVertically(1)
        if (canScrollHorizontally || canScrollVertically) {
            this.parent?.requestDisallowInterceptTouchEvent(true)
        } else {
            this.parent?.requestDisallowInterceptTouchEvent(false)
        }
    }
}