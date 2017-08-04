package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mehrdad on 7/31/17.
 */

public class GiftcardCardAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Package> giftcards;

    // Constructor
    public GiftcardCardAdapter(Context c, ArrayList<Package> giftcardsList){
        mContext = c;
        giftcards = giftcardsList;
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
        cv.setOnClickListener(v -> {
            // TODO
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
