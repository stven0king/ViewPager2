package androidx.viewpager2.integration.testapp.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;

/**
 * Created by Tanzhenxing
 * Date: 2021/4/17 10:36 上午
 * Description:copy {@link com.google.android.material.tabs.TabLayoutMediator}
 * 为了查找当前展现0tab，点击1tab的时候，2tab也进行了onStart的问题
 */
public final class TabLayoutMediator {
    @NonNull
    private final TabLayout tabLayout;
    @NonNull
    private final ViewPager2 viewPager;
    private final boolean autoRefresh;
    private final TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy;
    @Nullable
    private RecyclerView.Adapter<?> adapter;
    private boolean attached;
    @Nullable
    private TabLayoutMediator.TabLayoutOnPageChangeCallback onPageChangeCallback;
    @Nullable
    private TabLayout.OnTabSelectedListener onTabSelectedListener;
    @Nullable
    private RecyclerView.AdapterDataObserver pagerAdapterObserver;

    public TabLayoutMediator(@NonNull TabLayout tabLayout, @NonNull ViewPager2 viewPager, @NonNull TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy) {
        this(tabLayout, viewPager, true, tabConfigurationStrategy);
    }

    public TabLayoutMediator(@NonNull TabLayout tabLayout, @NonNull ViewPager2 viewPager, boolean autoRefresh, @NonNull TabLayoutMediator.TabConfigurationStrategy tabConfigurationStrategy) {
        this.tabLayout = tabLayout;
        this.viewPager = viewPager;
        this.autoRefresh = autoRefresh;
        this.tabConfigurationStrategy = tabConfigurationStrategy;
    }

    public void attach() {
        if (this.attached) {
            throw new IllegalStateException("TabLayoutMediator is already attached");
        } else {
            this.adapter = this.viewPager.getAdapter();
            if (this.adapter == null) {
                throw new IllegalStateException("TabLayoutMediator attached before ViewPager2 has an adapter");
            } else {
                this.attached = true;
                this.onPageChangeCallback = new TabLayoutMediator.TabLayoutOnPageChangeCallback(this.tabLayout);
                this.viewPager.registerOnPageChangeCallback(this.onPageChangeCallback);
                this.onTabSelectedListener = new TabLayoutMediator.ViewPagerOnTabSelectedListener(this.viewPager);
                this.tabLayout.addOnTabSelectedListener(this.onTabSelectedListener);
                if (this.autoRefresh) {
                    this.pagerAdapterObserver = new TabLayoutMediator.PagerAdapterObserver();
                    this.adapter.registerAdapterDataObserver(this.pagerAdapterObserver);
                }

                this.populateTabsFromPagerAdapter();
                this.tabLayout.setScrollPosition(this.viewPager.getCurrentItem(), 0.0F, true);
            }
        }
    }

    public void detach() {
        this.adapter.unregisterAdapterDataObserver(this.pagerAdapterObserver);
        this.tabLayout.removeOnTabSelectedListener(this.onTabSelectedListener);
        this.viewPager.unregisterOnPageChangeCallback(this.onPageChangeCallback);
        this.pagerAdapterObserver = null;
        this.onTabSelectedListener = null;
        this.onPageChangeCallback = null;
        this.adapter = null;
        this.attached = false;
    }

    void populateTabsFromPagerAdapter() {
        this.tabLayout.removeAllTabs();
        if (this.adapter != null) {
            int adapterCount = this.adapter.getItemCount();

            int lastItem;
            for(lastItem = 0; lastItem < adapterCount; ++lastItem) {
                TabLayout.Tab tab = this.tabLayout.newTab();
                this.tabConfigurationStrategy.onConfigureTab(tab, lastItem);
                this.tabLayout.addTab(tab, false);
            }

            if (adapterCount > 0) {
                lastItem = this.tabLayout.getTabCount() - 1;
                int currItem = Math.min(this.viewPager.getCurrentItem(), lastItem);
                if (currItem != this.tabLayout.getSelectedTabPosition()) {
                    this.tabLayout.selectTab(this.tabLayout.getTabAt(currItem));
                }
            }
        }

    }

    private class PagerAdapterObserver extends RecyclerView.AdapterDataObserver {
        PagerAdapterObserver() {
        }

        public void onChanged() {
            TabLayoutMediator.this.populateTabsFromPagerAdapter();
        }

        public void onItemRangeChanged(int positionStart, int itemCount) {
            TabLayoutMediator.this.populateTabsFromPagerAdapter();
        }

        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            TabLayoutMediator.this.populateTabsFromPagerAdapter();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            TabLayoutMediator.this.populateTabsFromPagerAdapter();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            TabLayoutMediator.this.populateTabsFromPagerAdapter();
        }

        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            TabLayoutMediator.this.populateTabsFromPagerAdapter();
        }
    }

    private static class ViewPagerOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
        private final ViewPager2 viewPager;

        ViewPagerOnTabSelectedListener(ViewPager2 viewPager) {
            this.viewPager = viewPager;
        }

        public void onTabSelected(@NonNull TabLayout.Tab tab) {
            //改为this.viewPager.setCurrentItem(tab.getPosition(), false);
            //可以避免当前展现0tab，点击1tab的时候，2tab也进行了onStart
            this.viewPager.setCurrentItem(tab.getPosition(), true);
        }

        public void onTabUnselected(TabLayout.Tab tab) {
        }

        public void onTabReselected(TabLayout.Tab tab) {
        }
    }

    private static class TabLayoutOnPageChangeCallback extends ViewPager2.OnPageChangeCallback {
        @NonNull
        private final WeakReference<TabLayout> tabLayoutRef;
        private int previousScrollState;
        private int scrollState;

        TabLayoutOnPageChangeCallback(TabLayout tabLayout) {
            this.tabLayoutRef = new WeakReference(tabLayout);
            this.reset();
        }

        public void onPageScrollStateChanged(int state) {
            this.previousScrollState = this.scrollState;
            this.scrollState = state;
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            TabLayout tabLayout = (TabLayout)this.tabLayoutRef.get();
            if (tabLayout != null) {
                boolean updateText = this.scrollState != 2 || this.previousScrollState == 1;
                boolean updateIndicator = this.scrollState != 2 || this.previousScrollState != 0;
                tabLayout.setScrollPosition(position, positionOffset, updateText, updateIndicator);
            }

        }

        public void onPageSelected(int position) {
            TabLayout tabLayout = (TabLayout)this.tabLayoutRef.get();
            if (tabLayout != null && tabLayout.getSelectedTabPosition() != position && position < tabLayout.getTabCount()) {
                boolean updateIndicator = this.scrollState == 0 || this.scrollState == 2 && this.previousScrollState == 0;
                tabLayout.selectTab(tabLayout.getTabAt(position), updateIndicator);
            }

        }

        void reset() {
            this.previousScrollState = this.scrollState = 0;
        }
    }

    public interface TabConfigurationStrategy {
        void onConfigureTab(@NonNull TabLayout.Tab var1, int var2);
    }
}