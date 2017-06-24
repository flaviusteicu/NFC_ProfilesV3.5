package com.example.flavius6.nfcprofiles;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Console;
import java.io.IOException;

public class CreateProfile extends AppCompatActivity {

    private Spinner wifiSpinner;
    private static final String[] wifi_options = {"Change your settings here", "Turn WiFi ON", "Turn WiFi OFF", "Toggle WiFi"};
    private String wifi_settings_dynamic = "";

    private Spinner blueToothSpinner;
    private static final String[] bluetooth_options = {"Change your settings here", "Turn Bluetooth ON", "Turn Bluetooth OFF", "Toggle Blutooth"};
    private String blueTooth_settings_dynamic = "";


    private Spinner brightnessSpinner;
    private static final String[] brightness_options = {"Change your settings here", "Brightness auto", "Brightness manual"};
    private String brightness_settings_dynamic = "";

    private Spinner soundsSpinner;
    private static final String[] sounds_options = {"Change your settings here", "Vibrate", "Silent", "Normal", "Set your own settings"};
    private String sounds_settings_dynamic = "";
    private String sounds_ringtone_dynamic = "";
    private String sounds_media_dynamic = "";
    private String sounds_notifications_dynamic = "";


    //need one for every activity with nfc
    NfcAdapter mNfcAdapter;

    PopupWindow popupWindow;
    View popupLayout;
    String finalSettingsForWrite = "";
    public static final String MIME_TEXT_PLAIN = "text/plain";


    public void ButonTerminate(View view)
    {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        final SeekBar brightnessSeekBar = (SeekBar) findViewById(R.id.seekBarBrightness);
        final SeekBar ringtoneSeekBar = (SeekBar) findViewById(R.id.seekBarRingtone);
        final SeekBar mediaSeekbar = (SeekBar) findViewById(R.id.seekBarMedia);
        final SeekBar notificationsSeekbar = (SeekBar) findViewById(R.id.seekBarNotifications);
        final TextView ringtoneTextView = (TextView) findViewById(R.id.textViewRingtone);
        final TextView mediaTextView = (TextView) findViewById(R.id.textViewMedia);
        final TextView notificationsTextview = (TextView) findViewById(R.id.textViewNotifications);
        final ImageView apploggo = (ImageView) findViewById(R.id.apploggo);

        wifiSpinner = (Spinner) findViewById(R.id.spinnerWiFi);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateProfile.this, R.layout.spinner_item, wifi_options);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateProfile.this, android.R.layout.simple_spinner_item, wifi_options);

        adapter.setDropDownViewResource(R.layout.spinner_drown_items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wifiSpinner.setAdapter(adapter);
        wifiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    //no changes
                    case 0:
                        //  Snackbar.make(view, "something 0", Snackbar.LENGTH_INDEFINITE).show();
                        wifi_settings_dynamic = "";
                        break;
                    //turn wifi on
                    case 1:
                        // Snackbar.make(view, "something 1", Snackbar.LENGTH_INDEFINITE).show();
                        wifi_settings_dynamic = "";
                        wifi_settings_dynamic = NfcLogic.WIFI_TURN_ON;
                        break;
                    //wifi off
                    case 2:
                        wifi_settings_dynamic = "";
                        wifi_settings_dynamic = NfcLogic.WIFI_TURN_OFF;
                        break;
                    case 3:
                        wifi_settings_dynamic = "";
                        wifi_settings_dynamic = NfcLogic.WIFI_CONFIG_TOGGLE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        blueToothSpinner = (Spinner) findViewById(R.id.spinnerBluetooth);
        ArrayAdapter<String> bluetoothOptionsAdapter = new ArrayAdapter<String>(CreateProfile.this, R.layout.spinner_item, bluetooth_options);
//        ArrayAdapter<String> bluetoothOptionsAdapter = new ArrayAdapter<String>(CreateProfile.this, android.R.layout.simple_spinner_item, bluetooth_options);
//        bluetoothOptionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bluetoothOptionsAdapter.setDropDownViewResource(R.layout.spinner_drown_items);

        blueToothSpinner.setAdapter(bluetoothOptionsAdapter);
        blueToothSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        blueTooth_settings_dynamic = "";
                        break;
                    case 1:
                        blueTooth_settings_dynamic = "";
                        blueTooth_settings_dynamic = NfcLogic.BLUETOOTH_ON;
                        break;
                    case 2:
                        blueTooth_settings_dynamic = "";
                        blueTooth_settings_dynamic = NfcLogic.BLUETOOTH_OFF;
                        break;
                    case 3:
                        blueTooth_settings_dynamic = "";
                        blueTooth_settings_dynamic = NfcLogic.BLUETOOTH_TOGGLE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        brightnessSpinner = (Spinner) findViewById(R.id.spinnerBrightness);
//        final ArrayAdapter<String> brightnessAdapter = new ArrayAdapter<String>(CreateProfile.this, android.R.layout.simple_spinner_item, brightness_options);
//        brightnessAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<String> brightnessAdapter = new ArrayAdapter<String>(CreateProfile.this, R.layout.spinner_item, brightness_options);
        brightnessAdapter.setDropDownViewResource(R.layout.spinner_drown_items);

        brightnessSpinner.setAdapter(brightnessAdapter);
        brightnessSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        brightnessSeekBar.setVisibility(View.GONE);
                        brightness_settings_dynamic = "";
                        break;
                    case 1:
                        brightnessSeekBar.setVisibility(View.GONE);
                        brightness_settings_dynamic = "";
                        brightness_settings_dynamic = NfcLogic.SCREEN_ADAPTIVE_BRIGHTNESS;
                        break;
                    case 2:
                        brightnessSeekBar.setVisibility(View.VISIBLE);
                        brightnessSeekBar.setProgress(130);
                        brightnessSeekBar.getProgressDrawable().setColorFilter(Color.rgb(201,201,6), PorterDuff.Mode.SRC_IN);
                        brightnessSeekBar.getThumb().setColorFilter(Color.rgb(201,201,6), PorterDuff.Mode.SRC_IN);
                        brightness_settings_dynamic = "";
                        brightness_settings_dynamic = NfcLogic.SCREEN_BRIGHTNESS_CUSTOM_Default;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("MMMM", "Seekbar progress" + progress);
                //Snackbar.make(seekBar, "Valoare: " + progress, Snackbar.LENGTH_SHORT).show();
                brightness_settings_dynamic = "";
                brightness_settings_dynamic = NfcLogic.SCREEN_BRIGHTNESS_CUSTOM + progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        soundsSpinner = (Spinner) findViewById(R.id.spinnerSounds);
//        ArrayAdapter<String> soundsAdapter = new ArrayAdapter<String>(CreateProfile.this, android.R.layout.simple_spinner_item, sounds_options);
//        soundsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> soundsAdapter = new ArrayAdapter<String>(CreateProfile.this, R.layout.spinner_item, sounds_options);
        soundsAdapter.setDropDownViewResource(R.layout.spinner_drown_items);

        soundsSpinner.setAdapter(soundsAdapter);
        soundsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        apploggo.setVisibility(View.VISIBLE);
                        ringtoneSeekBar.setVisibility(View.GONE);
                        ringtoneTextView.setVisibility(View.GONE);
                        mediaSeekbar.setVisibility(View.GONE);
                        mediaTextView.setVisibility(View.GONE);
                        notificationsSeekbar.setVisibility(View.GONE);
                        notificationsTextview.setVisibility(View.GONE);

                        sounds_ringtone_dynamic = "";
                        sounds_media_dynamic = "";
                        sounds_notifications_dynamic = "";

                        sounds_settings_dynamic = "";
                        break;
                    case 1:
                        apploggo.setVisibility(View.VISIBLE);
                        ringtoneSeekBar.setVisibility(View.GONE);
                        ringtoneTextView.setVisibility(View.GONE);
                        mediaSeekbar.setVisibility(View.GONE);
                        mediaTextView.setVisibility(View.GONE);
                        notificationsSeekbar.setVisibility(View.GONE);
                        notificationsTextview.setVisibility(View.GONE);

                        sounds_ringtone_dynamic = "";
                        sounds_media_dynamic = "";
                        sounds_notifications_dynamic = "";

                        sounds_settings_dynamic = "";
                        sounds_settings_dynamic = NfcLogic.VOLUME_VIBRATE;
                        break;
                    case 2:
                        apploggo.setVisibility(View.VISIBLE);
                        ringtoneSeekBar.setVisibility(View.GONE);
                        ringtoneTextView.setVisibility(View.GONE);
                        mediaSeekbar.setVisibility(View.GONE);
                        mediaTextView.setVisibility(View.GONE);
                        notificationsSeekbar.setVisibility(View.GONE);
                        notificationsTextview.setVisibility(View.GONE);

                        sounds_ringtone_dynamic = "";
                        sounds_media_dynamic = "";
                        sounds_notifications_dynamic = "";

                        sounds_settings_dynamic = "";
                        sounds_settings_dynamic = NfcLogic.VOLUME_SILENT;
                        break;
                    case 3:
                        apploggo.setVisibility(View.VISIBLE);
                        ringtoneSeekBar.setVisibility(View.GONE);
                        ringtoneTextView.setVisibility(View.GONE);
                        mediaSeekbar.setVisibility(View.GONE);
                        mediaTextView.setVisibility(View.GONE);
                        notificationsSeekbar.setVisibility(View.GONE);
                        notificationsTextview.setVisibility(View.GONE);

                        sounds_ringtone_dynamic = "";
                        sounds_media_dynamic = "";
                        sounds_notifications_dynamic = "";

                        sounds_settings_dynamic = "";
                        sounds_settings_dynamic = NfcLogic.VOLUME_NORMAL;
                        break;
                    case 4:
                        apploggo.setVisibility(View.GONE);
                        ringtoneSeekBar.setVisibility(View.VISIBLE);
                        ringtoneTextView.setVisibility(View.VISIBLE);
                        //*** Changing seekbar color to blue (RGB 55-89-226) ***
                        ringtoneSeekBar.getProgressDrawable().setColorFilter(Color.rgb(0,153,0), PorterDuff.Mode.SRC_IN);
                        ringtoneSeekBar.getThumb().setColorFilter(Color.rgb(0,153,0), PorterDuff.Mode.SRC_IN);

                        ringtoneSeekBar.setProgress(6);

                        mediaSeekbar.setVisibility(View.VISIBLE);
                        mediaTextView.setVisibility(View.VISIBLE);

                        mediaSeekbar.getProgressDrawable().setColorFilter(Color.rgb(0,153,0), PorterDuff.Mode.SRC_IN);
                        mediaSeekbar.getThumb().setColorFilter(Color.rgb(0,153,0), PorterDuff.Mode.SRC_IN);

                        mediaSeekbar.setProgress(7);

                        notificationsSeekbar.setVisibility(View.VISIBLE);
                        notificationsTextview.setVisibility(View.VISIBLE);

                        notificationsSeekbar.getProgressDrawable().setColorFilter(Color.rgb(0,153,0), PorterDuff.Mode.SRC_IN);
                        notificationsSeekbar.getThumb().setColorFilter(Color.rgb(0,153,0), PorterDuff.Mode.SRC_IN);

                        notificationsSeekbar.setProgress(2);

                        sounds_settings_dynamic = "";
                        sounds_ringtone_dynamic = NfcLogic.VOLUME_RINGTONE_CUSTOM_Default;
                        sounds_media_dynamic = NfcLogic.VOLUME_MEDIA_CUSTOM_Default;
                        sounds_notifications_dynamic = NfcLogic.VOLUME_NOTIFICATIONS_CUSTOM_Default;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ringtoneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("MMMM", "Seekbar ring progress" + progress);
                sounds_ringtone_dynamic = "";
                sounds_ringtone_dynamic = NfcLogic.VOLUME_RINGTONE_CUSTOM + progress;
                //Snackbar.make(seekBar, "String " + sounds_ringtone_dynamic, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("MMMM", "Seekbar media progress" + progress);
                sounds_media_dynamic = "";
                sounds_media_dynamic = NfcLogic.VOLUME_MEDIA_CUSTOM + progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        notificationsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("MMMM", "Seekbar notif progress" + progress);
                sounds_notifications_dynamic = "";
                sounds_notifications_dynamic = NfcLogic.VOLUME_NOTIFICATIONS_CUSTOM + progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //pop up
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupLayout = inflater.inflate(R.layout.popupwrite, (ViewGroup) findViewById(R.id.popUp_Msg));

        //set size for popup window
        popupWindow = new PopupWindow(popupLayout, 1000, 1000, true);
//        popupWindow.setHeight(700);
//        popupWindow.setWidth(800);
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

            if (popupWindow.isShowing()) {
                Tag nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                try {
                    NfcLogic.write(finalSettingsForWrite, nfcTag);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
            }

        }
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


    public void ClickOkButton(View view) {
        finalSettingsForWrite = "";

        if (wifi_settings_dynamic != "")
            finalSettingsForWrite = finalSettingsForWrite + wifi_settings_dynamic + NfcLogic.DELIMITER;

        if (blueTooth_settings_dynamic != "")
            finalSettingsForWrite = finalSettingsForWrite + blueTooth_settings_dynamic + NfcLogic.DELIMITER;

        if (brightness_settings_dynamic != "")
            finalSettingsForWrite = finalSettingsForWrite + brightness_settings_dynamic + NfcLogic.DELIMITER;

        if (sounds_settings_dynamic != "")
            finalSettingsForWrite = finalSettingsForWrite + sounds_settings_dynamic + NfcLogic.DELIMITER;

        if (sounds_ringtone_dynamic != "")
            finalSettingsForWrite = finalSettingsForWrite + sounds_ringtone_dynamic + NfcLogic.DELIMITER;

        if (sounds_media_dynamic != "")
            finalSettingsForWrite = finalSettingsForWrite + sounds_media_dynamic + NfcLogic.DELIMITER;

        if (sounds_notifications_dynamic != "")
            finalSettingsForWrite = finalSettingsForWrite + sounds_notifications_dynamic + NfcLogic.DELIMITER;


        popupWindow.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);
        Log.i("MMMM", "Stringul de sunet este" + sounds_settings_dynamic);
        Log.i("MMMMM", "Stringul final este" + finalSettingsForWrite);
    }
}
