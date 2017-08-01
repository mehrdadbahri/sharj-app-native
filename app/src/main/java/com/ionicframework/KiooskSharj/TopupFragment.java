package com.ionicframework.KiooskSharj;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TopupFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RadioButton saman, mellat, zarinpal;
    private String selectedGateway;
    private RadioButton online_payment, offline_payment;
    private Switch awesomeSwitch;
    private ExpandableLayout gateways_layout;
    private View view;
    private AppCompatButton buyBtn;
    private ImageView searchContact;
    private EditText editTextPhone;
    Spinner spinner;
    private String webserviceID = "587ceaef-4ee0-46dd-a64e-31585bef3768";

    public TopupFragment() {
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
        view =  inflater.inflate(R.layout.fragment_topup, container, false);

        saman = (RadioButton) view.findViewById(R.id.saman_gateway);
        mellat = (RadioButton) view.findViewById(R.id.mellat_gateway);
        zarinpal = (RadioButton) view.findViewById(R.id.zarrinpal_gateway);

        saman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saman.setChecked(true);
                mellat.setChecked(false);
                zarinpal.setChecked(false);
                selectedGateway = "Saman";
            }
        });

        mellat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saman.setChecked(false);
                mellat.setChecked(true);
                zarinpal.setChecked(false);
                selectedGateway = "Mellat";
            }
        });

        zarinpal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saman.setChecked(false);
                mellat.setChecked(false);
                zarinpal.setChecked(true);
                selectedGateway = "ZarinPal";
            }
        });

        online_payment = (RadioButton) view.findViewById(R.id.rb_pay_online);
        offline_payment = (RadioButton) view.findViewById(R.id.rb_pay_offline);

        gateways_layout = (ExpandableLayout) view.findViewById(R.id.gateways_topup);

        online_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online_payment.setChecked(true);
                offline_payment.setChecked(false);
                gateways_layout.expand();
                view.findViewById(R.id.topup_last_divider).setVisibility(View.VISIBLE);
            }
        });

        offline_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online_payment.setChecked(false);
                offline_payment.setChecked(true);
                gateways_layout.collapse();
                view.findViewById(R.id.topup_last_divider).setVisibility(View.GONE);
            }
        });

        spinner = (Spinner) view.findViewById(R.id.charge_amount);
        spinner.setOnItemSelectedListener(this);

        List<String> chargeAmounts = new ArrayList<>();
        chargeAmounts.add("هزار تومان");
        chargeAmounts.add("۲ هزار تومان");
        chargeAmounts.add("۵ هزار تومان");
        chargeAmounts.add("۱۰ هزار تومان");
        chargeAmounts.add("۲۰ هزار تومان");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, chargeAmounts);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        buyBtn = (AppCompatButton) view.findViewById(R.id.buy_topup_btn);
        buyBtn.setOnClickListener(this);

        searchContact = (ImageView) view.findViewById(R.id.btn_search_topup);
        searchContact.setOnClickListener(this);

        editTextPhone = (EditText) view.findViewById(R.id.phone_number_topup);

        awesomeSwitch = (Switch) view.findViewById(R.id.awesome_charge_switch);

        AndroidNetworking.initialize(getContext());


        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy_topup_btn:
                if (online_payment.isChecked()){
                    String scriptVersion = "Android";
                    String phoneNumber = editTextPhone.getText().toString();
                    String chargeCode = getOperator(phoneNumber);
                    if (chargeCode == null){
                        //TODO
                        return;
                    }
                    if (awesomeSwitch.isChecked()){
                        chargeCode += "!";
                    }
                    String chargeAmount = getAmount(spinner);
                    onlineTopup(chargeCode, phoneNumber, chargeAmount, selectedGateway, scriptVersion);
                }
                else {
                    //TODO
                }
                break;

            case R.id.btn_search_topup:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 0);
                break;
        }
    }

    private void onlineTopup(
            String chargeCode,
            String phoneNumber,
            String chargeAmount,
            String selectedGateway,
            String scriptVersion
    ) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("type", chargeCode);
            jsonData.put("cellphone", phoneNumber);
            jsonData.put("amount", chargeAmount);
            jsonData.put("issuer", selectedGateway);
            jsonData.put("scriptVersion", scriptVersion);
            jsonData.put("firstOutputType", "json");
            jsonData.put("secondOutputType", "view");
            jsonData.put("redirectToPage", "False");
            jsonData.put("webserviceId", webserviceID);
            String url = "https://chr724.ir/services/v3/EasyCharge/topup";
            AndroidNetworking.post(url)
                    .addJSONObjectBody(jsonData)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("status").equals("Success")){
                                    String paymentUrl = response.getJSONObject("paymentInfo").getString("url");
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(paymentUrl));
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            // handle error
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getAmount(Spinner spinner) {
        switch (spinner.getSelectedItem().toString()){
            case "هزار تومان":
                return "1000";
            case "۲ هزار تومان":
                return "2000";
            case "۵ هزار تومان":
                return "5000";
            case "۱۰ هزار تومان":
                return "10000";
            case "۲۰ هزار تومان":
                return "20000";
        }
        return null;
    }

    private String getOperator(String number) {
        if(number.startsWith("093") || number.startsWith("090")) {
            return "MTN";
        }
        else if(number.startsWith("094")) {
            return "WiMax";
        }
        else if(number.startsWith("091") || number.startsWith("0990")) {
            return "MCI";
        }
        else if(number.startsWith("0921") || number.startsWith("0922")) {
            return "RTL";
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {

            if (resultCode == Activity.RESULT_OK) {

                Uri contactUri = data.getData();

                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = this.getActivity().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                editTextPhone.setText(number.replace("+98", "0"));
            }
        }

    }
}
