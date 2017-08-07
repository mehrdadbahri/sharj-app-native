package com.ionicframework.KiooskSharj;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GiftcardPurchaseFragment extends Fragment implements View.OnClickListener {

    private RadioButton saman, mellat, zarinpal;
    private String selectedGateway;
    private EditText editTextPhone;
    private SharedPreferences sharedpreferences;
    private String selectedGiftcardId;

    public GiftcardPurchaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("خرید گیفت کارت");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_giftcard_purchase, container, false);

        sharedpreferences = getContext().getSharedPreferences("KiooskData", Context.MODE_PRIVATE);

        Bundle bundle = this.getArguments();
        String strPackage = bundle.getString("selectedGiftcard");
        Package p = new Gson().fromJson(strPackage, Package.class);
        selectedGiftcardId = p.getId();

        ((TextView) view.findViewById(R.id.tv_selected_giftcard_price)).setText(p.getPrice() + " " + "تومان");

        ((TextView) view.findViewById(R.id.tv_selected_giftcard_detail)).setText(p.getName());

        ((ImageView) view.findViewById(R.id.iv_selected_giftcard_logo)).setImageResource(getImageId(p.getId()));

        saman = (RadioButton) view.findViewById(R.id.saman_gateway);
        mellat = (RadioButton) view.findViewById(R.id.mellat_gateway);
        zarinpal = (RadioButton) view.findViewById(R.id.zarrinpal_gateway);

        saman.setOnClickListener(v -> {
            saman.setChecked(true);
            mellat.setChecked(false);
            zarinpal.setChecked(false);
            selectedGateway = "Saman";
        });

        mellat.setOnClickListener(v -> {
            saman.setChecked(false);
            mellat.setChecked(true);
            zarinpal.setChecked(false);
            selectedGateway = "Mellat";
        });

        zarinpal.setOnClickListener(v -> {
            saman.setChecked(false);
            mellat.setChecked(false);
            zarinpal.setChecked(true);
            selectedGateway = "ZarinPal";
        });

        AppCompatButton buyBtn = (AppCompatButton) view.findViewById(R.id.buy_selected_giftcard_btn);
        buyBtn.setOnClickListener(this);

        ImageView searchContact = (ImageView) view.findViewById(R.id.btn_search__selected_giftcard);
        searchContact.setOnClickListener(this);

        editTextPhone = (EditText) view.findViewById(R.id.phone_number_selected_giftcard);
        if (sharedpreferences.contains("phoneNumber"))
            editTextPhone.setText(sharedpreferences.getString("phoneNumber", ""));

        AndroidNetworking.initialize(getContext());

        return view;
    }

    private int getImageId(String name) {
        if (name.toLowerCase().contains("itunes"))
            return R.drawable.itunes;
        if (name.toLowerCase().contains("googleplay"))
            return R.drawable.google_play;
        if (name.toLowerCase().contains("microsoft"))
            return R.drawable.microsoft;
        if (name.toLowerCase().contains("amazon"))
            return R.drawable.amazon;
        if (name.toLowerCase().contains("xbox"))
            return R.drawable.xbox;
        if (name.toLowerCase().contains("playstationplus"))
            return R.drawable.playstaoin_plus;
        if (name.toLowerCase().contains("playstation"))
            return R.drawable.playstation;
        if (name.toLowerCase().contains("steam"))
            return R.drawable.steam;
        return R.drawable.xbox;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_selected_giftcard_btn:
                String phoneNumber = editTextPhone.getText().toString();
                if (isphoneNumber(phoneNumber)) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("phoneNumber", phoneNumber);
                    editor.apply();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("خطا");
                    builder.setMessage("شماره تلفن وارد شده صحیح نمی باشد.");
                    builder.setPositiveButton("OK", (dialog, which) -> {

                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }

                String scriptVersion = "Android";

                buyGiftcard(selectedGiftcardId, phoneNumber, selectedGateway, scriptVersion);
                break;

            case R.id.btn_search_topup:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 0);
                break;
        }
    }

    private void buyGiftcard(String selectedGiftcardId, String phoneNumber, String selectedGateway, String scriptVersion) {
        JSONObject jsonData = new JSONObject();
        try {
            jsonData.put("productId", selectedGiftcardId);
            jsonData.put("cellphone", phoneNumber);
            jsonData.put("issuer", selectedGateway);
            jsonData.put("scriptVersion", scriptVersion);
            jsonData.put("firstOutputType", "json");
            jsonData.put("secondOutputType", "view");
            jsonData.put("redirectToPage", "False");
            String webserviceID = "587ceaef-4ee0-46dd-a64e-31585bef3768";
            jsonData.put("webserviceId", webserviceID);
            String url = "https://chr724.ir/services/v3/EasyCharge/BuyProduct";
            AndroidNetworking.post(url)
                    .addJSONObjectBody(jsonData)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("status").equals("Success")) {
                                    String paymentUrl = response.getJSONObject("paymentInfo").getString("url");
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(paymentUrl));
                                    startActivity(i);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("خطا");
                                    builder.setMessage(response.getString("errorMessage"));
                                    builder.setPositiveButton("OK", (dialog, which) -> {

                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            } catch (JSONException | ActivityNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("خطا");
                            builder.setMessage("خطا در اتصال به سرور! لطفا از اتصال به اینترنت اطمینال حاصل نمایید سپس مجددا امتحان کنید.");
                            builder.setPositiveButton("OK", (dialog, which) -> {

                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
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
                assert cursor != null;
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                editTextPhone.setText(number.replace("+98", "0"));
                cursor.close();
            }
        }

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

    private boolean isphoneNumber(String phoneNumber) {
        return getOperator(phoneNumber) != null && phoneNumber.length() == 11;
    }
}
