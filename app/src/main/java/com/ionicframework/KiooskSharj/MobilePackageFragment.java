package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MobilePackageFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup rgPackageTime, rgPackageCustomerType;
    private ExpandableHeightGridView gridview;

    public MobilePackageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_mobile_package, container, false);

        ArrayList<Package> packages = getPackagesList();
        gridview = (ExpandableHeightGridView) view.findViewById(R.id.grid_view_packages);
        gridview.setAdapter(new PackageCardAdapter(getContext(), packages));
        gridview.setExpanded(true);
        gridview.setFocusable(false);

        rgPackageTime = (RadioGroup) view.findViewById(R.id.rg_time_period);
        rgPackageTime.setOnCheckedChangeListener(this);

        rgPackageCustomerType = (RadioGroup) view.findViewById(R.id.rg_customer_type);
        rgPackageCustomerType.setOnCheckedChangeListener(this);

        JSONObject filters = new JSONObject();
        try {
            filters.put("customerType", "prepaid");
            filters.put("timeRange", "monthly");
            String filterStr = filters.toString();
            ((PackageCardAdapter) gridview.getAdapter()).getFilter().filter(filterStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private ArrayList<Package> getPackagesList(){
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("InitilizeData", Context.MODE_PRIVATE);
        Type listOfPackages = new TypeToken<ArrayList<Package>>() {}.getType();
        String strPackages = sharedpreferences.getString("packages", "");
        ArrayList<Package> packages = new Gson().fromJson(strPackages, listOfPackages);
        for (int i = 0; i < packages.size(); i++){
            if (packages.get(i).getName().contains("شگفت انگیز"))
                packages.remove(i);
        }
        return packages;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String customerType = "";
        String timeRange = "";
        switch (rgPackageCustomerType.getCheckedRadioButtonId()){
            case R.id.rb_customer_prepaid:
                customerType = "prepaid";
                break;
            case R.id.rb_customer_postpaid:
                customerType = "postpaid";
                break;
            case R.id.rb_customer_td_lte:
                customerType = "TDLTE";
        }
        switch (rgPackageTime.getCheckedRadioButtonId()){
            case R.id.rb_hourly:
                timeRange = "hourly";
                break;
            case R.id.rb_daily:
                timeRange = "daily";
                break;
            case R.id.rb_weekly:
                timeRange = "weekly";
                break;
            case R.id.rb_monthly:
                timeRange = "monthly";
                break;
        }

        JSONObject filters = new JSONObject();
        try {
            filters.put("customerType", customerType);
            filters.put("timeRange", timeRange);
            String filterStr = filters.toString();
            ((PackageCardAdapter) gridview.getAdapter()).getFilter().filter(filterStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
