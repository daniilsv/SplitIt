package ru.daniils.splitit.ui.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.daniils.splitit.R;
import ru.daniils.splitit.ui.fragments.PrivateRoomsFragment;
import ru.daniils.splitit.ui.fragments.PublicRoomsFragment;

public class MainActivity extends AppCompatActivity {

    @BindString(R.string.public_rooms)
    String s_public_room;
    @BindString(R.string.private_room)
    String s_private_room;
    @BindString(R.string.protected_room)
    String s_protected_room;
    @BindView(R.id.toolbar)
    Toolbar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        setSupportActionBar(mActionBar);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> arr = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            arr.add(new PublicRoomsFragment());
            arr.add(new PrivateRoomsFragment());
            //arr.add(new ProtectedRoomsFragment());
        }

        @Override
        public Fragment getItem(int position) {
            return arr.get(position);
        }

        @Override
        public int getCount() {
            return arr.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return s_public_room;
                case 1:
                    return s_private_room;
                //case 2:
                //    return s_protected_room;
            }
            return null;
        }
    }
}
