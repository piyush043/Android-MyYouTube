package com.pbansal.myyoutube;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.search_app_bar);
        setSupportActionBar(mToolbar);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);
        mTabs.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor));
        mTabs.setSelectedIndicatorColors(ContextCompat.getColor(getApplicationContext(), R.color.accentColor));
        mTabs.setViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("signout", true);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        int[] tabIcons = {R.drawable.ic_search_white_48dp,
                R.drawable.ic_playlist_add_white_48dp,
                R.drawable.ic_favorite_border_white_48dp};

        String[] tabText = getResources().getStringArray(R.array.tabs);

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Log.d("Piyush", "getItem method is called at position: " + position);
            if (position == 0) {
                fragment = new SearchFragment();
            }
            if (position == 1) {
                fragment = new PlaylistFragment();
            }
            if (position == 2) {
                fragment = new ListFragment();
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), tabIcons[position], null);
//            Drawable drawable = getResources().getDrawable(tabIcons[position]);
            drawable.setBounds(0, 0, 54, 54);
            ImageSpan imageSpan = new ImageSpan(drawable);
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
