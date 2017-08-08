package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by mehrdad on 7/31/17.
 */

public class GiftcardCardAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Package> giftcards;
    private FragmentManager fragmentManager;

    // Constructor
    public GiftcardCardAdapter(Context c, FragmentManager fm, ArrayList<Package> giftcardsList){
        mContext = c;
        giftcards = giftcardsList;
        fragmentManager = fm;
    }

    public int getCount() {
        return giftcards.size();
    }

    public Object getItem(int position) {
        return giftcards.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.giftcard_template, parent, false);
        }

        final Package p = (Package) this.getItem(position);

        ((TextView) view.findViewById(R.id.tv_giftcard_price)).setText(p.getPrice() + " " + "تومان");

        ((TextView) view.findViewById(R.id.tv_giftcard_detail)).setText(p.getName());

        ((ImageView) view.findViewById(R.id.iv_giftcard_logo)).setImageResource(getImageId(p.getId()));

        CardView cv = (CardView) view.findViewById(R.id.cv_giftcards);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = "card" + p.getId();
            cv.setTransitionName(transitionName);
        }
        cv.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            String strPackage = new Gson().toJson(p, Package.class);
            bundle.putString("selectedGiftcard", strPackage);
            Fragment fragment = new GiftcardPurchaseFragment();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                Transition moveTransition = TransitionInflater.from(mContext).inflateTransition(android.R.transition.move);
                fragment.setSharedElementEnterTransition(moveTransition);
                fragment.setSharedElementReturnTransition(moveTransition);
                bundle.putString("transitionName", "card" + p.getId());
                fragment.setArguments(bundle);
                Fragment currentFragment = fragmentManager.findFragmentById(R.id.main_container_wrapper);
                fragmentManager.beginTransaction()
                        .hide(currentFragment)
                        .add(R.id.main_container_wrapper, fragment)
                        .addToBackStack(null)
                        .addSharedElement(cv, "card" + p.getId())
                        .commit();

            } else {
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container_wrapper, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private int getImageId(String name) {
        if (name.toLowerCase().contains("itunes"))
            return R.drawable.itunes;
        if (name.toLowerCase().contains("googleplay"))
            return R.drawable.google_play;
        if (name.toLowerCase().contains("microsoft"))
            return R.drawable.microsoft;
        if (name.toLowerCase().contains("amazon"))
            return R.drawable.amazon;
        if (name.toLowerCase().contains("xbox"))
            return R.drawable.xbox;
        if (name.toLowerCase().contains("playstationplus"))
            return R.drawable.playstaoin_plus;
        if (name.toLowerCase().contains("playstation"))
            return R.drawable.playstation;
        if (name.toLowerCase().contains("steam"))
            return R.drawable.steam;
        return R.drawable.xbox;
    }
}
