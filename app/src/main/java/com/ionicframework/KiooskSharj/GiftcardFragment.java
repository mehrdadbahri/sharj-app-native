package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GiftcardFragment extends Fragment {

    public GiftcardFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_giftcatd, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("خرید گیفت کارت");

        ArrayList<Package> giftcards = getGiftcardsList();
        ExpandableHeightGridView gridview = (ExpandableHeightGridView) view.findViewById(R.id.grid_view_giftcard);
        gridview.setAdapter(new GiftcardCardAdapter(getContext(), giftcards));
        gridview.setExpanded(true);

        return view;
    }

    private ArrayList<Package> getGiftcardsList(){
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("InitilizeData", Context.MODE_PRIVATE);
        Type listOfGiftcards = new TypeToken<ArrayList<Package>>() {}.getType();
        String strGiftcards = sharedpreferences.getString("giftcards", "");
        ArrayList<Package> giftcards = new Gson().fromJson(strGiftcards, listOfGiftcards);
        return giftcards;
    }
}
