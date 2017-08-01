package com.ionicframework.KiooskSharj;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopupFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RadioButton saman, mellat, zarinpal;
    private String selectedGateway;
    private RadioButton online_payment, offline_payment;
    private Switch awesomeSwitch;
    private ExpandableLayout gateways_layout, creditcard_layout;
    SharedPreferences sharedpreferences;
    AppCompatButton buyBtn;
    private View view;
    private EditText editTextPhone;
    private Spinner spinner;
    private EditText credit1, credit2, credit3, credit4;
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

        sharedpreferences = getContext().getSharedPreferences("KiooskData", Context.MODE_PRIVATE);

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
        creditcard_layout = (ExpandableLayout) view.findViewById(R.id.creditcard_layout_topup);

        online_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online_payment.setChecked(true);
                offline_payment.setChecked(false);
                gateways_layout.expand();
                creditcard_layout.collapse();
//                view.findViewById(R.id.topup_last_divider).setVisibility(View.VISIBLE);
            }
        });

        offline_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online_payment.setChecked(false);
                offline_payment.setChecked(true);
                gateways_layout.collapse();
                creditcard_layout.expand();
//                view.findViewById(R.id.topup_last_divider).setVisibility(View.GONE);
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, chargeAmounts);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        buyBtn = (AppCompatButton) view.findViewById(R.id.buy_topup_btn);
        buyBtn.setOnClickListener(this);

        ImageView searchContact = (ImageView) view.findViewById(R.id.btn_search_topup);
        searchContact.setOnClickListener(this);

        editTextPhone = (EditText) view.findViewById(R.id.phone_number_topup);
        if (sharedpreferences.contains("phoneNumber"))
            editTextPhone.setText(sharedpreferences.getString("phoneNumber", ""));

        awesomeSwitch = (Switch) view.findViewById(R.id.awesome_charge_switch);

        setupCreditcardInputs();

        AndroidNetworking.initialize(getContext());

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy_topup_btn:
                String phoneNumber = editTextPhone.getText().toString();
                if (isphoneNumber(phoneNumber)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("phoneNumber", phoneNumber);
                    editor.apply();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("خطا");
                    builder.setMessage("شماره تلفن وارد شده صحیح نمی باشد.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                if (online_payment.isChecked()){
                    String scriptVersion = "Android";
                    String chargeCode = getOperator(phoneNumber);
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
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("خطا");
                                    builder.setMessage(response.getString("errorMessage"));
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("خطا");
                            builder.setMessage("خطا در اتصال به سرور! لطفا از اتصال به اینترنت اطمینال حاصل نمایید سپس مجددا امتحان کنید.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
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

    private boolean isphoneNumber(String phoneNumber) {
        if (getOperator(phoneNumber) == null)
            return false;
        if (phoneNumber.length() != 11)
            return false;
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {

            if (resultCode == Activity.RESULT_OK) {

                Uri contactUri = data.getData();

                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                Cursor cursor = this.getActivity().getContentResolver()
                        .query(contactUri, projection, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                editTextPhone.setText(number.replace("+98", "0"));
                cursor.close();
            }
        }

    }

    private void setupCreditcardInputs() {
        credit1 = (EditText) view.findViewById(R.id.et_credit1);
        credit2 = (EditText) view.findViewById(R.id.et_credit2);
        credit3 = (EditText) view.findViewById(R.id.et_credit3);
        credit4 = (EditText) view.findViewById(R.id.et_credit4);

        credit1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (credit1.getText().toString().length() >= 4) {
                    credit2.requestFocus();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        credit2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (credit2.getText().toString().length() >= 4) {
                    credit3.requestFocus();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        credit3.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (credit3.getText().toString().length() >= 4) {
                    credit4.requestFocus();
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        credit4.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (credit4.getText().toString().length() >= 4) {
                    buyBtn.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(credit4.getWindowToken(), 0);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
