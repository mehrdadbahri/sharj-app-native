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

import java.util.ArrayList;
import java.util.List;

public class TopupFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private RadioButton saman, mellat, zarinpal;
    private View view;
    private AppCompatButton buyBtn;
    private ImageView searchContact;
    private EditText editTextPhone;

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

        buyBtn = (AppCompatButton) view.findViewById(R.id.buy_topup_btn);
        buyBtn.setOnClickListener(this);

        searchContact = (ImageView) view.findViewById(R.id.btn_search_topup);
        searchContact.setOnClickListener(this);

        editTextPhone = (EditText) view.findViewById(R.id.phone_number_topup);

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
                break;

            case R.id.btn_search_topup:
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
