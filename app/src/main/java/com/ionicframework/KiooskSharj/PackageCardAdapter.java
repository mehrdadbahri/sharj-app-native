package com.ionicframework.KiooskSharj;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PackageCardAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    ArrayList<Package> packages;
    ArrayList<Package> filteredPackages;

    // Constructor
    public PackageCardAdapter(Context c, ArrayList<Package> packagesList) {
        mContext = c;
        packages = packagesList;
        filteredPackages = new ArrayList<Package>(packages);
    }

    public int getCount() {
        return filteredPackages.size();
    }

    public Object getItem(int position) {
        return filteredPackages.get(position);
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
            view = LayoutInflater.from(mContext).inflate(R.layout.card_view_template, parent, false);
        }

        final Package p = (Package) this.getItem(position);

        ((TextView) view.findViewById(R.id.tv_pacakge_price)).setText(p.getPrice() + " " + "تومان");

        String customerType = getCustomerType(p.getName());
        ((TextView) view.findViewById(R.id.tv_package_customer_type)).setText(customerType);

        String traffic = getTraffic(p.getName());
        ((TextView) view.findViewById(R.id.tv_package_traffic)).setText(traffic);

        String time = getTime(p.getName());
        ((TextView) view.findViewById(R.id.tv_package_time)).setText(time);

        String detail = getDetail(p.getName());
        ((TextView) view.findViewById(R.id.tv_pacakge_detail)).setText(detail);

        CardView cv = (CardView) view.findViewById(R.id.cv_package_card);
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        return view;
    }

    private String getCustomerType(String name) {
        if(name.contains("دائمی")) {
            return "ویژه مشترکین دائمی";
        }
        if(name.contains("TDLTE")) {
            return "ویژه مشترکین TDLTE";
        }
        if (name.contains("شگفت انگیز")) {
            return "ویژه مشترکین اعتباری و دائمی";
        }
        return "ویژه مشترکین اعتباری";
    }

    private String getDetail(String name) {
        if (name.contains("ظهر")) {
            return "6 صبح تا 12 ظهر";
        }
        name = name.replaceAll("اینترنت ایرانسل ((ساعتی)|(روزانه)|(هفتگی)|(ماهانه)) - ", "");
        name = name.replace("اینترنت ثابت TDLTE - ", "");
        name = name.replace("(مشترکین دائمی)", "");
        name = name.replaceAll("\\(|\\)", "");
        name = name.replaceAll("\\d+ تومانی", "");
        name = name.replace("مشترکین اعتباری", "");
        name = name.replace("یک", "1");
        name = name.replace("دو", "2");
        name = name.replace("سه", "3");
        name = name.replace("شش", "6");
        name = name.replaceAll("(\\d+)\\s*((روزه)|(ماهه)|(ساعته))", "");
        return name;
    }

    private String getTime(String name) {
        if (name.contains("روزانه")) {
            if (!name.contains("روزه")) {
                return "یک روزه";
            }
        }

        if (name.contains("ماهانه")) {
            if (!name.contains("ماهه")) {
                return "یک ماهه";
            }
        }

        if (name.contains("هفتگی")) {
            return "یک هفته‌ای";
        }

        name = name.replace("یک", "1");
        name = name.replace("دو", "2");
        name = name.replace("سه", "3");
        name = name.replace("شش", "6");

        final Pattern pattern = Pattern.compile("(\\d+)\\s*((روزه)|(ماهه)|(ساعته))");
        Matcher m = pattern.matcher(name);
        if (m.find()) {
            return m.group(1) + " " + m.group(2);
        } else
            return "";

    }

    private String getTraffic(String name) {
        final Pattern pattern = Pattern.compile("(\\d+\\.*\\d*)\\s*((مگابایت)|(گیگابایت)|(گیگ))");
        Matcher m = pattern.matcher(name);
        double traffic = 0.;
        String unit = "", trafficStr;
        while (m.find()) {
            traffic += Double.parseDouble(m.group(1));
            unit = m.group(2);
        }
        if (traffic != 0) {
            trafficStr = String.valueOf(traffic).replace(".0", "") + " " + unit;
        } else {
            trafficStr = "نامحدود";
        }
        return trafficStr;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String customerType = null;
                String timeRange = null;
                try {
                    JSONObject jsonObj = new JSONObject((String) constraint);
                    customerType = jsonObj.getString("customerType");
                    timeRange = jsonObj.getString("timeRange");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (customerType == null || timeRange == null){

                    return results;
                }
                filteredPackages.clear();
                ArrayList<Package> tempArray = new ArrayList<Package>();
                for (Package p : packages) {
                    switch (customerType) {
                        case "prepaid":
                            if (!p.getName().contains("دائمی") && !p.getName().contains("TDLTE"))
                                tempArray.add(p);
                            break;
                        case "TDLTE":
                            if (p.getName().contains(customerType))
                                tempArray.add(p);
                            break;
                        case "postpaid":
                            if (p.getName().contains("دائمی"))
                                tempArray.add(p);
                            break;
                    }
                }
                for (Package p : tempArray){
                    switch (timeRange){
                        case "hourly":
                            if (p.getName().contains("ساعت"))
                                filteredPackages.add(p);
                            break;
                        case "daily":
                            if (p.getName().contains("روز"))
                                filteredPackages.add(p);
                            break;
                        case "weekly":
                            if (p.getName().contains("هفتگی"))
                                filteredPackages.add(p);
                            break;
                        case "monthly":
                            if (p.getName().contains("ماه"))
                                filteredPackages.add(p);
                            break;
                    }
                }
                results.count = filteredPackages.size();
                results.values = filteredPackages;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}