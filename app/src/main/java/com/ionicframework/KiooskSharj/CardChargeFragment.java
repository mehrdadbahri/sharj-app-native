package com.ionicframework.KiooskSharj;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CardChargeFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RadioButton saman, mellat, zarinpal;
    private ImageView mtnLogo, mciLogo, rtlLogo;
    private int selectedSize, normalSize, marginSize;
    private Spinner spinner;
    private TextView selectedOperator;
    private AppCompatButton buyBtn;
    private SharedPreferences sharedpreferences;
    private EditText editTextPhone;
    private String selectedGateway;
    private String webserviceID = "587ceaef-4ee0-46dd-a64e-31585bef3768";

    public CardChargeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_card_charge, container, false);

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

        spinner = (Spinner) view.findViewById(R.id.card_charge_amount);
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

        mtnLogo = (ImageView) view.findViewById(R.id.mtn_logo);
        mciLogo = (ImageView) view.findViewById(R.id.mci_logo);
        rtlLogo = (ImageView) view.findViewById(R.id.rtl_logo);

        mtnLogo.setOnClickListener(this);
        mciLogo.setOnClickListener(this);
        rtlLogo.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        selectedSize = (int) (74 * dm.scaledDensity);
        normalSize = (int) (64 * dm.scaledDensity);
        marginSize = (int) (10 * dm.scaledDensity);

        selectedOperator = (TextView) view.findViewById(R.id.selectedOperator);

        ImageView searchContact = (ImageView) view.findViewById(R.id.btn_search_cardcharge);
        searchContact.setOnClickListener(this);

        buyBtn = (AppCompatButton) view.findViewById(R.id.buy_cardcharge_btn);
        buyBtn.setOnClickListener(this);

        editTextPhone = (EditText) view.findViewById(R.id.phone_number_cardcharge);
        if (sharedpreferences.contains("phoneNumber"))
            editTextPhone.setText(sharedpreferences.getString("phoneNumber", ""));

        return view;
    }

    @Override
    public void onClick(View v) {
        ViewGroup.MarginLayoutParams params;
        switch (v.getId()) {
            case (R.id.mtn_logo):
                selectedOperator.setText(R.string.irancel);

                params = (ViewGroup.MarginLayoutParams) (ViewGroup.MarginLayoutParams) mtnLogo.getLayoutParams();
                params.width = selectedSize;
                params.height = selectedSize;
                params.topMargin = 0;
                mtnLogo.setPadding(marginSize / 2, marginSize / 2, marginSize / 2, marginSize / 2);
                mtnLogo.requestLayout();

                params = (ViewGroup.MarginLayoutParams) mciLogo.getLayoutParams();
                params.width = normalSize;
                params.height = normalSize;
                params.topMargin = marginSize;
                mciLogo.setPadding(0, 0, 0, 0);
                mciLogo.requestLayout();

                params = (ViewGroup.MarginLayoutParams) rtlLogo.getLayoutParams();
                params.width = normalSize;
                params.height = normalSize;
                params.topMargin = marginSize;
                rtlLogo.setPadding(0, 0, 0, 0);
                rtlLogo.requestLayout();
                break;
            case (R.id.mci_logo):
                selectedOperator.setText(R.string.hamrah_aval);

                params = (ViewGroup.MarginLayoutParams) mtnLogo.getLayoutParams();
                params.width = normalSize;
                params.height = normalSize;
                params.topMargin = marginSize;
                mtnLogo.setPadding(0, 0, 0, 0);
                mtnLogo.requestLayout();

                params = (ViewGroup.MarginLayoutParams) mciLogo.getLayoutParams();
                params.width = selectedSize;
                params.height = selectedSize;
                params.topMargin = 0;
                mciLogo.setPadding(marginSize / 2, marginSize / 2, marginSize / 2, marginSize / 2);
                mciLogo.requestLayout();

                params = (ViewGroup.MarginLayoutParams) rtlLogo.getLayoutParams();
                params.width = normalSize;
                params.height = normalSize;
                params.topMargin = marginSize;
                rtlLogo.setPadding(0, 0, 0, 0);
                rtlLogo.requestLayout();
                break;
            case (R.id.rtl_logo):
                selectedOperator.setText(R.string.rightel);

                params = (ViewGroup.MarginLayoutParams) mtnLogo.getLayoutParams();
                params.width = normalSize;
                params.height = normalSize;
                params.topMargin = marginSize;
                mtnLogo.setPadding(0, 0, 0, 0);
                mtnLogo.requestLayout();

                params = (ViewGroup.MarginLayoutParams) mciLogo.getLayoutParams();
                params.width = normalSize;
                params.height = normalSize;
                params.topMargin = marginSize;
                mciLogo.setPadding(0, 0, 0, 0);
                mciLogo.requestLayout();

                params = (ViewGroup.MarginLayoutParams) rtlLogo.getLayoutParams();
                params.width = selectedSize;
                params.height = selectedSize;
                params.topMargin = 0;
                rtlLogo.setPadding(marginSize / 2, marginSize / 2, marginSize / 2, marginSize / 2);
                rtlLogo.requestLayout();
                break;

            case R.id.buy_cardcharge_btn:
                String phoneNumber = editTextPhone.getText().toString();
                if (isphoneNumber(phoneNumber)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("phoneNumber", phoneNumber);
                    editor.apply();
                } else {
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
                    break;
                }
                String scriptVersion = "Android";
                if (selectedOperator.getText().toString().equals(getResources().getString(R.string.rightel))
                        && spinner.getSelectedItem().toString().equals("هزار تومان")
                        ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("خطا");
                    builder.setMessage("خرید کارت شارژ ۱۰۰۰ تومانی برای اپراتور رایتل امکان پذیر نمی باشد.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                }
                String chargeCode = "CC-";
                chargeCode = chargeCode.concat(getOperatorCode(selectedOperator.getText().toString()));
                chargeCode = chargeCode.concat("-");
                chargeCode = chargeCode.concat(getAmount(spinner));
                buyCardCharge(chargeCode, phoneNumber, selectedGateway, scriptVersion);
                break;

            case R.id.btn_search_cardcharge:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 0);
                break;
        }
    }

    private void buyCardCharge(
            String chargeCode,
            String phoneNumber,
            String selectedGateway,
            String scriptVersion
    ) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("productId", chargeCode);
            jsonData.put("cellphone", phoneNumber);
            jsonData.put("issuer", selectedGateway);
            jsonData.put("scriptVersion", scriptVersion);
            jsonData.put("firstOutputType", "json");
            jsonData.put("secondOutputType", "view");
            jsonData.put("redirectToPage", "False");
            jsonData.put("webserviceId", webserviceID);
            String url = "https://chr724.ir/services/v3/EasyCharge/BuyProduct";
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
                            catch (ActivityNotFoundException e){
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

    private String getOperatorCode(String name) {
        switch (name){
            case "رایتل":
                return "RTL";
            case "ایرانسل":
                return "MTN";
            case "همراه اول":
                return "MCI";
            default:
                return "";
        }
    }

    private String getAmount(Spinner spinner) {
        switch (spinner.getSelectedItem().toString()) {
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

    private boolean isphoneNumber(String phoneNumber) {
        if (getOperator(phoneNumber) == null)
            return false;
        if (phoneNumber.length() != 11)
            return false;
        return true;
    }

    private String getOperator(String number) {
        if (number.startsWith("093") || number.startsWith("090")) {
            return "MTN";
        } else if (number.startsWith("094")) {
            return "WiMax";
        } else if (number.startsWith("091") || number.startsWith("0990")) {
            return "MCI";
        } else if (number.startsWith("0921") || number.startsWith("0922")) {
            return "RTL";
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}