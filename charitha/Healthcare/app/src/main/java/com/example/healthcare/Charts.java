//tab view for charts
package com.example.healthcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class Charts extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem tab1, tab2, tab3;
    ChartPageAdapter pageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        tabLayout = (TabLayout) findViewById(R.id.tabLayoutChart);
        tab1 = (TabItem) findViewById(R.id.tabBarChart);
        tab2 = (TabItem) findViewById(R.id.tabPieChart);
        tab3 = (TabItem) findViewById(R.id.tabOtherChart);
        viewPager = (ViewPager) findViewById(R.id.viewPagerChart);

        pageAdapter = new ChartPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition() == 0){
                    pageAdapter.notifyDataSetChanged();
                }
               else if(tab.getPosition() == 1){
                    pageAdapter.notifyDataSetChanged();
                }
               else if(tab.getPosition() == 2){
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}
