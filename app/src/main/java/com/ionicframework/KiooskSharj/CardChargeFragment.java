package com.ionicframework.KiooskSharj;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;
import java.util.List;


public class CardChargeFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RadioButton saman, mellat, zarinpal;
    ImageView mtnLogo, mciLogo, rtlLogo;
    int selectedSize, normalSize, marginSize;
    TextView selectedOperator;
    private AppCompatButton buyBtn;
    private ImageView searchContact;
    private EditText editTextPhone;

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

        saman = (RadioButton) view.findViewById(R.id.saman_gateway);
        mellat = (RadioButton) view.findViewById(R.id.mellat_gateway);
        zarinpal = (RadioButton) view.findViewById(R.id.zarrinpal_gateway);

        saman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saman.setChecked(true);
                mellat.setChecked(false);
                zarinpal.setChecked(false);
            }
        });

        mellat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saman.setChecked(false);
                mellat.setChecked(true);
                zarinpal.setChecked(false);
            }
        });

        zarinpal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saman.setChecked(false);
                mellat.setChecked(false);
                zarinpal.setChecked(true);
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.charge_amount);
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

        searchContact = (ImageView) view.findViewById(R.id.btn_search_cardcharge);
        searchContact.setOnClickListener(this);

        buyBtn = (AppCompatButton) view.findViewById(R.id.buy_cardcharge_btn);
        buyBtn.setOnClickListener(this);

        editTextPhone = (EditText) view.findViewById(R.id.phone_number_cardcharge);

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

            case R.id.buy_topup_btn:
                break;

            case R.id.btn_search_cardcharge:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 0);
                break;
        }
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