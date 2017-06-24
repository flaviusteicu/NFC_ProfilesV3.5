package com.example.flavius6.nfcprofiles;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter myNfcAdapter;
    private boolean writeMode = false;
    public static final String MIME_TEXT_PLAIN = "text/plain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
        if (!Settings.System.canWrite(this)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + this.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, 111);
        }

        NfcLogic.context = getApplicationContext();

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("MMM", "On new intent form tag");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Log.i("MMM", "On new intent ready to read");
            ///read from a tag
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                Log.i("MMM", "On new intent ready not null" + rawMsgs);
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                    Log.i("MMMM", "mesaj from nfc"  + msgs[i]);
                    Toast.makeText(getApplicationContext(), "mesaj from nfc: " + msgs[i], Toast.LENGTH_LONG);
                }
                String textFromNfc = NfcLogic.buildTagViews(msgs);
                NfcLogic.ToggleSettings(textFromNfc);
            }
        }
    }

    public void GoToProfileActivity(View view) {
        Intent myIntent = new Intent(getApplicationContext(), ProfilePredefinedActivity.class);
        startActivity(myIntent);
        //when you want to close previous activity
//        finish();
    }

    public void GoToNfcActivity(View view) {
        Intent myIntent = new Intent(getApplicationContext(), CreateProfile.class);
        startActivity(myIntent);
    }

    public void AboutPage(View view) {
        Intent myIntent = new Intent(getApplicationContext(), About.class);
        startActivity(myIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, myNfcAdapter);
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


}
