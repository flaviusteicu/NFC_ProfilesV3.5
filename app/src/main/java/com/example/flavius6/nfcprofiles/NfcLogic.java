package com.example.flavius6.nfcprofiles;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.nfc.NdefRecord.createApplicationRecord;
import static android.nfc.NdefRecord.createMime;

/**
 * Created by flavius6 on 05.06.2017.
 */

public class NfcLogic {

    public static final String WIFI_CONFIG_TOGGLE = "WIFI_TOG";
    public static final String WIFI_TURN_OFF = "WIFI_OFF";
    public static final String WIFI_TURN_ON = "WIFI_ON";
    public static final String BLUETOOTH_TOGGLE = "BLT_TOG";
    public static final String BLUETOOTH_ON = "BLT_ON";
    public static final String BLUETOOTH_OFF = "BLT_OFF";
    public static final String SCREEN_ADAPTIVE_BRIGHTNESS = "SCR_AD";
    public static final String SCREEN_BRIGHTNESS_NIGHT = "SCR_NGT";
    public static final String SCREEN_BRIGHTNESS_WORK = "SCR_WRK";
    public static final String SCREEN_BRIGHTNESS_SPORT = "SCR_SP";
    public static final String SCREEN_BRIGHTNESS_CUSTOM = "SCR_CST_";
    public static final String SCREEN_BRIGHTNESS_CUSTOM_Default = "SCR_CST_130";
    public static final String DELIMITER = "&&";
    public static final String VOLUME_RINGTONE_PROFILES ="VOL_RI_PRF";
    public static final String VOLUME_NOTIFICATIONS_PROFILES = "VOL_NOT_PRF";
    public static final String VOLUME_MEDIA_PROFILES = "VOL_ME_PRF";
    public static final String VOLUME_VIBRATE = "VOL_VBR";
    public static final String VOLUME_MEDIA_NIGHTMODE = "VOL_ME_NGT";
    public static final String VOLUME_NOTIFICATIONS_WORK = "VOL_NOT_WRK";
    public static final String VOLUME_SILENT = "VOL_SIL";
    public static final String VOLUME_NORMAL = "VOL_NOR";
    public static final String VOLUME_RINGTONE_CUSTOM = "VOL_RI_CST_";
    public static final String VOLUME_MEDIA_CUSTOM = "VOL_ME_CST_";
    public static final String VOLUME_NOTIFICATIONS_CUSTOM = "VOL_NOT_CST_";
    public static final String VOLUME_NOTIFICATIONS_CUSTOM_Default = "VOL_NOT_CST_2";
    public static final String VOLUME_MEDIA_CUSTOM_Default = "VOL_ME_CST_7";
    public static final String VOLUME_RINGTONE_CUSTOM_Default = "VOL_RI_CST_6";



    public static void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] recordsToMessage = new NdefRecord[2];
        ArrayList<NdefRecord> records = new ArrayList<NdefRecord>();
        records.add(createRecord(text));
        records.add(NdefRecord.createApplicationRecord("com.example.flavius6.nfcprofiles"));

        NdefMessage message = new NdefMessage(records.toArray(recordsToMessage));
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        // Close the connection
        ndef.close();
    }

    public static NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang = "en";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payload = new byte[1 + langLength + textLength];

        // set status byte (see NDEF spec for actual bits)
        payload[0] = (byte) langLength;

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

//        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, "com.example.flavius6.nfcprofiles".getBytes(), new byte[0], payload);
        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], payload);
//        NdefRecord extRecord = new NdefRecord(
//                NdefRecord.TNF_EXTERNAL_TYPE, "com.example:externalType", new byte[0], payload);

//
//        NdefRecord Myrecord = createMime(
//                        "com.example.flavius6.nfcprofiles", text.getBytes());


//        String domain = "com.example.flavius6.nfcprofiles"; //usually your app's package name
//        String type = "externalType";
//        NdefRecord extRecord = NdefRecord.createExternal(domain, type, payload);
        return recordNFC;
    }

    public static String buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return "";

        String text = "";
//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }
        Log.i("NFC", "textul e : " + text);
        return text;
    }


    public static Context context;

    public static void ToggleSettings(String text) {

        String[] parts = text.split(NfcLogic.DELIMITER);
        if (parts.length >= 1) {
            for (int i = 0; i < parts.length; i++)
            {
                String parte = parts[i];

                //Wifi Toggle;
                if (parte.startsWith(NfcLogic.WIFI_CONFIG_TOGGLE))
                {
                    WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                    if (wifi.isWifiEnabled())
                        wifi.setWifiEnabled(false);
                    else
                        wifi.setWifiEnabled(true);
                }

                //Wifi Off;
                if (parte.startsWith(NfcLogic.WIFI_TURN_OFF))
                {
                    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    boolean wifiEnabled = wifi.isWifiEnabled();
                    if (wifiEnabled = true)
                        wifi.setWifiEnabled(false);
                }

                //Wifi On;
                if (parte.startsWith(NfcLogic.WIFI_TURN_ON))
                {
                    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    boolean wifiEnabled = wifi.isWifiEnabled();
                    if(wifiEnabled = false);
                    wifi.setWifiEnabled(true);
                }

                //Enabling Screen Adaptive Brightness
                if (parte.startsWith(NfcLogic.SCREEN_ADAPTIVE_BRIGHTNESS))
                {
                    Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                }

                //Changing screen brightness on manual so we can change the brightness to desired value (0-255) - this is for night mode predifined profile
                if (parte.startsWith(NfcLogic.SCREEN_BRIGHTNESS_NIGHT))
                {
                    Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 30);
                }

                //Same as above
                if (parte.startsWith(NfcLogic.SCREEN_BRIGHTNESS_WORK))
                {
                    Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 165);
                }

                if (parte.startsWith(NfcLogic.SCREEN_BRIGHTNESS_SPORT))
                {
                    Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 200);
                }

                if (parte.startsWith(NfcLogic.SCREEN_BRIGHTNESS_CUSTOM))
                {
                    String[] customBrightness = parte.split("_");
                    if(customBrightness.length==3)
                    {
                        String brightnessValueString = customBrightness[2];
                        Log.i("Luminozitatea este", brightnessValueString);
                        int BrightnessValue = Integer.parseInt(brightnessValueString);
                        Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                        Settings.System.putInt(context.getApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,BrightnessValue);
                    }


                }

                if (parte.startsWith(NfcLogic.VOLUME_RINGTONE_CUSTOM))
                {
                    String[] customRingtoneVolume = parte.split("_");
                    if(customRingtoneVolume.length==4)
                    {
                        String ringtoneValueString = customRingtoneVolume[3];
                        Log.i("Volumul ringtone este", ringtoneValueString);
                        int RingtoneValue = Integer.parseInt(ringtoneValueString);
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, RingtoneValue ,0);
                    }
                }

                if (parte.startsWith(NfcLogic.VOLUME_MEDIA_CUSTOM))
                {
                    String[] customMediaVolume = parte.split("_");
                    if(customMediaVolume.length==4)
                    {
                        String mediaValueString = customMediaVolume[3];
                        Log.i("Volumul media este", mediaValueString);
                        int MediaValue = Integer.parseInt(mediaValueString);
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MediaValue ,0);
                    }
                }

                if (parte.startsWith(NfcLogic.VOLUME_NOTIFICATIONS_CUSTOM))
                {
                    String[] customNotificationsVolume = parte.split("_");
                    if(customNotificationsVolume.length==4)
                    {
                        String notificationsValueString = customNotificationsVolume[3];
                        Log.i("Volumul notif este", notificationsValueString);
                        int NotificationsValue = Integer.parseInt(notificationsValueString);
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, NotificationsValue ,0);
                    }
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
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, 6,0);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 5,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_NOTIFICATIONS_PROFILES))
                {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 3,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_MEDIA_PROFILES))
                {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 8,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_VIBRATE))
                {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                }

                if(parte.startsWith(NfcLogic.VOLUME_SILENT))
                {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }

                if(parte.startsWith(NfcLogic.VOLUME_NORMAL))
                {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }

                if(parte.startsWith(NfcLogic.VOLUME_MEDIA_NIGHTMODE))
                {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3,0);
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 2,0);
                }

                if(parte.startsWith(NfcLogic.VOLUME_NOTIFICATIONS_WORK))
                {
                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
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
