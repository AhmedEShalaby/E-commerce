package com.example.e_commerce;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

    public FragmentAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CategoriesFragment(); // Replace with actual Category Fragment
            case 1:
                return new ProductsFragment(); // Replace with actual Product Fragment
            case 2:
                return new AddCategoryFragment(); // Replace with actual Cart Fragment (optional)
            case 3:
                return new AddProductFragment(); // Replace with actual Profile Fragment (optional)
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4; // Number of fragments in the ViewPager
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Categories";
            case 1:
                return "Products";
            case 2:
                return "Cart"; // Optional title for Cart
            case 3:
                return "Profile"; // Optional title for Profile
            default:
                return null;
        }
    }
}
