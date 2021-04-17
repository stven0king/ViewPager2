package androidx.viewpager2.integration.testapp

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

/**
 * Created by Tanzhenxing
 * Date: 2021/4/12 11:44 上午
 * Description: 测试吸顶+Fragment左右滑动+（页面内部横滑和竖滑列表）
 */
class CollapsingParallelNestedScrollingActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_layout)
        supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(R.id.flFragment, MyFragment())
            commitAllowingStateLoss()
        }
    }
}

