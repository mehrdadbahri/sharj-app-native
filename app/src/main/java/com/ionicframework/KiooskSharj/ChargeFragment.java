package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ChargeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FragmentManager fragmentManager;

    public ChargeFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_charge, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("خرید شارژ");

        fragmentManager = getChildFragmentManager();

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(fragmentManager);
        adapter.addFragment(new CardChargeFragment(), "کارت شارژ");
        adapter.addFragment(new TopupFragment(), "شارژ مستقیم");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1, false);
    }
}
