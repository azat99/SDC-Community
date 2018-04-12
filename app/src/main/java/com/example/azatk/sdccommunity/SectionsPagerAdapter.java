package com.example.azatk.sdccommunity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by azatk on 04.10.2017.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter{

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatsFragment chatsFragment=new ChatsFragment();
                return chatsFragment;
            case 1:
                NewsFragment newsFragment =new NewsFragment();
                return newsFragment;
            case 2:
                FriendsFragment friendsFragment=new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "CHATS";
            case 1:
                return "NEWS";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }
    }
}
