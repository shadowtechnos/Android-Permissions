package com.shadow.technos.permissionstutorial;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private AlertDialog.Builder builder;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private boolean pressedLater;
    private boolean pressedDontAskAgain;
    private long laterPressedTime;

    private final String[] permissions = {"Camera", "Media & Storage"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creating builder object for alert dialoges
        builder = new AlertDialog.Builder(MainActivity.this);

        // calling sharedpreferences and getting sharedpreferences values
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        pressedLater = sharedPref.getBoolean(getResources().getString(R.string.later), false);
        pressedDontAskAgain = sharedPref.getBoolean(getResources().getString(R.string.dont_ask_again), false);
        laterPressedTime = sharedPref.getLong(getResources().getString(R.string.later_pressed_time), 0);

        // Check if all the permissions were been granted
        // If not granted show a dialog requesting permissions from the user.
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            alertDialogeForAskingPermissions();

            // check if pressedLater variable is been true
        } else if (pressedLater) {
            if (laterPressedTime != 0) {

                // check if its been 1 hour since later is been pressed.
                Date dateObj = new Date();
                long timeNow = dateObj.getTime();
                long oneHourLater = laterPressedTime + (3600 * 1000);
                if (oneHourLater <= timeNow) {

                    requestPermission();
                    editor.putBoolean(getResources().getString(R.string.later), false);
                    editor.commit();
                }
            }
            // If pressed don't ask again the app should bot request permissions again.
        } else if (!pressedDontAskAgain)
            requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECEIVE_SMS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                //few important permissions were not been granted
                // ask the user again.
                alertDialoge();
            }
        }
    }

    private void alertDialoge() {

        //code to Set the message and title from the strings.xml file
        builder.setMessage(R.string.dialoge_desc).setTitle(R.string.we_request_again);

        builder.setCancelable(false)
                .setPositiveButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                })
                .setNegativeButton(R.string.dont_ask_again, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'Don't Ask Again' Button
                        // the sharedpreferences value is true
                        editor.putBoolean(getResources().getString(R.string.dont_ask_again), true);
                        editor.commit();
                        dialog.cancel();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void alertDialogeForAskingPermissions() {

        //code to Set the message and title from the strings.xml file
        builder.setMessage(getResources().getString(R.string.app_name) + " needs access to " + permissions[0] + ", " + permissions[1]).setTitle(R.string.permissions_required);

        //Setting message manually and performing action on button click
        //builder.setMessage("Do you want to close this application ?")
        builder.setCancelable(false)
                .setPositiveButton(R.string.later, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Action for 'Later'
                        //Saving later boolean value as true, also saving time of pressed later
                        Date dateObj = new Date();
                        long timeNow = dateObj.getTime();
                        editor.putLong(getResources().getString(R.string.later_pressed_time), timeNow);
                        editor.putBoolean(getResources().getString(R.string.later), true);
                        editor.commit();
                        dialog.cancel();
                    }
                })
                .setNegativeButton(R.string.give_permissions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_SMS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.SEND_SMS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECEIVE_SMS)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            }
        }
    }
}
