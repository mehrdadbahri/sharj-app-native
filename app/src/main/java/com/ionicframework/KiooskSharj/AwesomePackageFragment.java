package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AwesomePackageFragment extends Fragment {

    public AwesomePackageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_awesome_package, container, false);

        FragmentManager fm = getParentFragment().getFragmentManager();
        fm.popBackStack();

        ArrayList<Package> packages = getAwesomePackagesList();
        ExpandableHeightGridView gridview = (ExpandableHeightGridView) view.findViewById(R.id.grid_view_awesome_packages);
        gridview.setAdapter(new PackageCardAdapter(getContext(), fm, packages));
        gridview.setExpanded(true);

        return view;
    }

    private ArrayList<Package> getAwesomePackagesList(){
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("KiooskData", Context.MODE_PRIVATE);
        Type listOfPackages = new TypeToken<ArrayList<Package>>() {}.getType();
        String strPackages = sharedpreferences.getString("packages", "");
        ArrayList<Package> tempArray = new Gson().fromJson(strPackages, listOfPackages);
        ArrayList<Package> packages = new ArrayList<>();
        for (Package p : tempArray){
            if (p.getName().contains("شگفت انگیز")){
                packages.add(p);
            }
        }
        return packages;
    }
}
