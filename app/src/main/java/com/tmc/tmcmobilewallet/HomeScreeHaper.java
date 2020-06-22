package com.tmc.tmcmobilewallet;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class HomeScreeHaper extends FragmentPagerAdapter {
    public HomeScreeHaper(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
             EarnMoney_Fragement earnMoney_fragement = new EarnMoney_Fragement();
             return earnMoney_fragement;

            case 1:
                FunFragement funFragement = new FunFragement();
                return funFragement;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Earn Money";

            case 1:
                return "Earner";
        }
        return super.getPageTitle(position);
    }
}
