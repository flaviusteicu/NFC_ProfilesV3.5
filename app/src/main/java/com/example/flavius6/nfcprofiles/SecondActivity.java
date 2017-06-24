package com.example.flavius6.nfcprofiles;

import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {
    NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private boolean wifiBtnEnable = false;
    private boolean blueToothBtnEnable = false;
    private boolean screenBtnEnable = false;
    private boolean volumeBtnEnable = false;

    private List<String> configToBeWrite = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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


    public void ButonBluetooth(View view) {
        Snackbar.make(view, "NFC Content: ", Snackbar.LENGTH_LONG);
        blueToothBtnEnable = !blueToothBtnEnable;

        if (blueToothBtnEnable)
            configToBeWrite.add(NfcLogic.BLUETOOTH_TOGGLE);
        else
            configToBeWrite.remove(NfcLogic.BLUETOOTH_TOGGLE);
        Button altbuton = (Button) view;
        ChangeButtonColorOnClick(altbuton);
    }

    public void ButonWifi(View view) {
        wifiBtnEnable = !wifiBtnEnable;
        if (wifiBtnEnable)
            configToBeWrite.add(NfcLogic.WIFI_CONFIG_TOGGLE);
        else
            configToBeWrite.remove(NfcLogic.WIFI_CONFIG_TOGGLE);

        Button wifibutton = (Button) view;
        ChangeButtonColorOnClick(wifibutton);
    }

//    public void ButonLuminozitate(View view) {
//        screenBtnEnable = !screenBtnEnable;
//        if (screenBtnEnable)
//            configToBeWrite.add(NfcLogic.SCREEN_TOGGLE);
//        else
//            configToBeWrite.remove(NfcLogic.SCREEN_TOGGLE);
//        Button luminozitate = (Button) view;
//        ChangeButtonColorOnClick(luminozitate);
//    }

    public void ButonVolume(View view) {
        volumeBtnEnable = !volumeBtnEnable;
        if (volumeBtnEnable)
            configToBeWrite.add((NfcLogic.VOLUME_RINGTONE_PROFILES));
        else
            configToBeWrite.remove(NfcLogic.VOLUME_RINGTONE_PROFILES);
        Button airplane = (Button) view;
        ChangeButtonColorOnClick(airplane);
    }


    public void ChangeButtonColorOnClick(Button buton) {
        ColorDrawable buttonColor = (ColorDrawable) buton.getBackground();
        int colorID = buttonColor.getColor();
        if (colorID == getResources().getColor(R.color.red))
            buton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        else if (colorID == getResources().getColor(R.color.green))
            buton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Switch switchNfc = (Switch) findViewById(R.id.nfcToggleAction);
            if (switchNfc.isChecked()) {
                //write to a tag nfc
                Tag nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                try {
                    String textForNfc = "";
                    for (String item : configToBeWrite) {
                        textForNfc += item + NfcLogic.DELIMITER;
                    }
                    NfcLogic.write(textForNfc, nfcTag);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
            } else {

                ///read from a tag
                Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                NdefMessage[] msgs = null;
                if (rawMsgs != null) {
                    msgs = new NdefMessage[rawMsgs.length];
                    for (int i = 0; i < rawMsgs.length; i++) {
                        msgs[i] = (NdefMessage) rawMsgs[i];
                        Toast.makeText(getApplicationContext(), "mesaj from nfc: " + msgs[i], Toast.LENGTH_LONG);

                    }
                    String textFromNfc = NfcLogic.buildTagViews(msgs);
                    ToggleSettings(textFromNfc);
                }
            }
        }
    }


    private void ToggleSettings(String text) {

        String[] parts = text.split(NfcLogic.DELIMITER);
        if (parts.length >= 1) {
            for (int i = 0; i < parts.length; i++)
            {
                String parte = parts[i];

                //Wifi Toggle;
                if (parte.startsWith(NfcLogic.WIFI_CONFIG_TOGGLE))
                {
                    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    if (wifi.isWifiEnabled())
                        wifi.setWifiEnabled(false);
                    else
                        wifi.setWifiEnabled(true);
                }

                //Wifi Off;
                if (parte.startsWith(NfcLogic.WIFI_TURN_OFF))
                {
                    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    boolean wifiEnabled = wifi.isWifiEnabled();
                    if (wifiEnabled = true)
                        wifi.setWifiEnabled(false);
                }

                //Wifi On;
                if (parte.startsWith(NfcLogic.WIFI_TURN_ON))
                {
                    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    boolean wifiEnabled = wifi.isWifiEnabled();
                    if(wifiEnabled = false);
                        wifi.setWifiEnabled(true);
                }

                //Enabling Screen Adaptive Brightness
                if (parte.startsWith(NfcLogic.SCREEN_ADAPTIVE_BRIGHTNESS))
                {
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                }

                //Changing screen brightness on manual so we can change the brightness to desired value (0-255) - this is for night mode predifined profile
                if (parte.startsWith(NfcLogic.SCREEN_BRIGHTNESS_NIGHT))
                {
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 30);
                }

                //Same as above
                if (parte.startsWith(NfcLogic.SCREEN_BRIGHTNESS_WORK))
                {
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 165);
                }

                if (parte.startsWith(NfcLogic.SCREEN_BRIGHTNESS_SPORT))
                {
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 200);
                }

                //Bluetooth toggle
                if (parte.startsWith(NfcLogic.BLUETOOTH_TOGGLE))
                {
                    BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!blueToothAdapter.isEnabled()) {
                        blueToothAdapter.enable();
                    } else {
                        blueToothAdapter.disable();
                    }
                }

                //Bluetooth off
                if (parte.startsWith(NfcLogic.BLUETOOTH_OFF))
                {
                    BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (blueToothAdapter.isEnabled())
                        blueToothAdapter.disable();
                }

                //Bluetooth on
                if (parte.startsWith(NfcLogic.BLUETOOTH_ON))
                {
                    BluetoothAdapter blueToothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!blueToothAdapter.isEnabled())
                        blueToothAdapter.enable();
                }


                if(parte.startsWith(NfcLogic.VOLUME_RINGTONE_PROFILES))
                {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 6,0);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 5,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_NOTIFICATIONS_PROFILES))
                {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 3,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_MEDIA_PROFILES))
                {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_VIBRATE))
                {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }

                if(parte.startsWith(NfcLogic.VOLUME_MEDIA_NIGHTMODE))
                {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3,0);
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 2,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_NOTIFICATIONS_WORK))
                {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 1,0);
                }

//                if (parte.startsWith(NfcLogic.VOLUME_RINGTONE_PROFILE))
//                {
//                   AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//                    //** Ringtone 0-7
//                    //** Media 0-15
//                    //** Alarm 0-7
//                    //** System 0-7
//                    //** Notification 0-7
//                    //** Phone Calls 0-5
//                    //** DTMF 0-15 [Keyvolume]
//
//
//                    int ringstate = audioManager.getRingerMode();
//                    if (ringstate != AudioManager.RINGER_MODE_VIBRATE)
//                    {
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
//                    } else
//                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

//                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 10, 0);
//                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 3, 0);
//                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 7, 0);

            }
        }
    }
}

