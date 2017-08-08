package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class AwesomePackageFragment extends Fragment implements View.OnClickListener {

    private View view;
    private FragmentManager fm;
    private Boolean loadingStatus = false;

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
        view = inflater.inflate(R.layout.fragment_awesome_package, container, false);

        fm = getParentFragment().getFragmentManager();
        fm.popBackStack();

        ArrayList<Package> packages = getAwesomePackagesList();
        if (packages != null) {
            setupGridView(packages);
        } else view.findViewById(R.id.ll_error_getting_packages).setVisibility(View.VISIBLE);

        view.findViewById(R.id.refresh_packages_btn).setOnClickListener(this);

        return view;
    }

    private void setupGridView(ArrayList<Package> packages) {
        view.findViewById(R.id.ll_error_getting_packages).setVisibility(View.GONE);
        ExpandableHeightGridView gridview = (ExpandableHeightGridView) view.findViewById(R.id.grid_view_awesome_packages);
        gridview.setAdapter(new PackageCardAdapter(getContext(), fm, packages));
        gridview.setExpanded(true);
    }

    private ArrayList<Package> getAwesomePackagesList() {
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("KiooskData", Context.MODE_PRIVATE);
        if (sharedpreferences.contains("packages")) {
            Type listOfPackages = new TypeToken<ArrayList<Package>>() {
            }.getType();
            String strPackages = sharedpreferences.getString("packages", "");
            ArrayList<Package> tempArray = new Gson().fromJson(strPackages, listOfPackages);
            ArrayList<Package> packages = new ArrayList<>();
            if (tempArray == null) {
                return null;
            }
            for (Package p : tempArray) {
                if (p.getName().contains("شگفت انگیز")) {
                    packages.add(p);
                }
            }
            return packages;
        }
        return null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (view != null && view.findViewById(R.id.ll_error_getting_packages).getVisibility() == View.VISIBLE){
                ArrayList<Package> packs = getAwesomePackagesList();
                if (packs != null) {
                    setupGridView(packs);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh_packages_btn:
                if (!loadingStatus) {
                    loadingStatus = true;
                    FABProgressCircle fabProgressCircle = (FABProgressCircle) view.findViewById(R.id.fabProgressCircle);
                    fabProgressCircle.show();
                    ArrayList<Package> packs = getAwesomePackagesList();
                    if (packs != null) {
                        setupGridView(packs);
                        fabProgressCircle.hide();
                        loadingStatus = false;

                    } else {
                        GetInitializeData dataGetter = new GetInitializeData(
                                (AppCompatActivity) getActivity(),
                                new OnEventListener() {
                                    @Override
                                    public void onSuccess() {
                                        fabProgressCircle.beginFinalAnimation();
                                        loadingStatus = false;
                                        fabProgressCircle.attachListener(() -> {
                                            ArrayList<Package> packs = getAwesomePackagesList();
                                            if (packs != null) {
                                                setupGridView(packs);
                                                fabProgressCircle.hide();
                                                loadingStatus = false;
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure() {
                                        Handler handler = new Handler();
                                        Runnable runnableCode = () -> {
                                            handler.postDelayed(() -> {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("خطا در اتصال به سرور");
                                                builder.setMessage("لطفا اتصال اینترنت خود را بررسی و مجددا امتحان کنید.");
                                                builder.setPositiveButton("OK", (dialog, which) -> {
                                                    fabProgressCircle.hide();
                                                    loadingStatus = false;
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }, 2000);
                                        };

                                        handler.post(runnableCode);
                                    }
                                }
                        );
                        dataGetter.execute();
                    }
                }
                break;
        }
    }
}
