package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PackageFragment extends Fragment {

    private FragmentManager fragmentManager;

    public PackageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_package, container, false);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("بسته های اینترنتی ایرانسل");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        fragmentManager = getChildFragmentManager();

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_package);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs_package);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager);
        adapter.addFragment(new AwesomePackageFragment(), "شگفت انگیز");
        adapter.addFragment(new MobilePackageFragment(), "بسته های همراه");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);
    }

    @Override
    public void onAttach(Context context) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        super.onAttach(context);
    }
}
