package com.example.flavius6.nfcprofiles;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

public class ProfilePredefinedActivity extends AppCompatActivity {
    //need one for every activity with nfc
    NfcAdapter mNfcAdapter;

    PopupWindow popupWindow;
    View popupLayout;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private String textToWriteOnNfc;




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_predefined);

        //pop up
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupLayout = inflater.inflate(R.layout.popupwrite, (ViewGroup) findViewById(R.id.popUp_Msg));

        //set size for popup window
        popupWindow = new PopupWindow(popupLayout, 1000, 1000, true);
//        popupWindow.setHeight(750);
//        popupWindow.setWidth(1000);
//        popupWindow.setBackgroundDrawable(getDrawable(R.drawable.popup));


        //this is current activity
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();

        //verify intent is an nfc tag
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            if( popupWindow.isShowing()){
                Tag nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                try {
                    NfcLogic.write(textToWriteOnNfc, nfcTag);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
            }

        }
    }




    public void ClickOnHome(View view)
    {
        //set phone settings option for the nfc tag
        //make it empty before set
        textToWriteOnNfc = " ";
        textToWriteOnNfc = NfcLogic.BLUETOOTH_OFF + NfcLogic.DELIMITER + NfcLogic.WIFI_TURN_ON + NfcLogic.DELIMITER + NfcLogic.SCREEN_ADAPTIVE_BRIGHTNESS + NfcLogic.DELIMITER + NfcLogic.VOLUME_RINGTONE_PROFILES
                         + NfcLogic.DELIMITER + NfcLogic.VOLUME_MEDIA_PROFILES + NfcLogic.DELIMITER + NfcLogic.VOLUME_NOTIFICATIONS_PROFILES + NfcLogic.DELIMITER;
        popupWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
    }


    public void ClickOnNightMode(View view)
    {
        textToWriteOnNfc = " ";
        textToWriteOnNfc = NfcLogic.WIFI_TURN_OFF + NfcLogic.DELIMITER + NfcLogic.SCREEN_BRIGHTNESS_NIGHT + NfcLogic.DELIMITER + NfcLogic.VOLUME_VIBRATE + NfcLogic.DELIMITER + NfcLogic.VOLUME_MEDIA_NIGHTMODE
                            +NfcLogic.DELIMITER;
        popupWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
    }


    public void ClickOnWork(View view)
    {
        textToWriteOnNfc = " ";
        textToWriteOnNfc = NfcLogic.WIFI_TURN_ON + NfcLogic.DELIMITER + NfcLogic.SCREEN_ADAPTIVE_BRIGHTNESS + NfcLogic.DELIMITER + NfcLogic.BLUETOOTH_OFF + NfcLogic.DELIMITER + NfcLogic.VOLUME_RINGTONE_PROFILES
                          + NfcLogic.DELIMITER + NfcLogic.VOLUME_MEDIA_NIGHTMODE + NfcLogic.DELIMITER + NfcLogic.VOLUME_NOTIFICATIONS_WORK + NfcLogic.DELIMITER;
        popupWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
    }


    public void ClickOnSport(View view)
    {
        textToWriteOnNfc = " ";
        textToWriteOnNfc = NfcLogic.WIFI_TURN_OFF + NfcLogic.DELIMITER + NfcLogic.SCREEN_BRIGHTNESS_SPORT + NfcLogic.DELIMITER + NfcLogic.BLUETOOTH_ON + NfcLogic.DELIMITER + NfcLogic.VOLUME_RINGTONE_PROFILES
                + NfcLogic.DELIMITER + NfcLogic.VOLUME_MEDIA_PROFILES + NfcLogic.DELIMITER + NfcLogic.VOLUME_NOTIFICATIONS_PROFILES + NfcLogic.DELIMITER;
        popupWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }


//    //this modify settings based on the text from the nfc tag
//    private void ToggleSettings(String text) {
//        String[] parts = text.split(NfcLogic.DELIMITER);
//        if (parts.length >= 1) {
//            for (int i = 0; i < parts.length; i++) {
//                String parte = parts[i];
//                if (parte.startsWith(NfcLogic.WIFI_CONFIG_TOGGLE)) {
//                    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//                    if (wifi.isWifiEnabled())
//                        wifi.setWifiEnabled(false);
//                    else
//                        wifi.setWifiEnabled(true);
//                    Log.i("textul poate este", parte);
//                }
//                if (parte.startsWith(NfcLogic.SCREEN_TOGGLE)) {
//                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
//                            android.provider.Settings.System.SCREEN_BRIGHTNESS, 200);
//                    Log.i("textul este", parte);
//                }
//
//                if (parte.startsWith(NfcLogic.BLUETOOTH_TOGGLE)) {
//                    BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
//                    if (!blueToothAdapter.isEnabled()) {
//                        blueToothAdapter.enable();
//                    } else {
//                        blueToothAdapter.disable();
//                    }
//                }
//            }
//        }
//
//    }



}