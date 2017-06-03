package com.moviemagic.dpaul.android.app

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.support.v7.app.AlertDialog
import android.support.v7.preference.PreferenceManager;
import groovy.transform.CompileStatic

@CompileStatic
class MyMoviesEULA {
    private String EULA_PREFIX = "eula_"
    private Activity mActivity

    public MyMoviesEULA(Activity context) {
        mActivity = context
    }

    // Get the package info
    private PackageInfo getPackageInfo() {
        PackageInfo pi = null
        try {
            pi = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES)
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace()
        }
        return pi
    }

    public void show() {
        PackageInfo versionInfo = getPackageInfo()

        // the eulaKey changes every time you increment the version number in the AndroidManifest.xml
        final String eulaKey = EULA_PREFIX + versionInfo.versionCode
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity)
        boolean hasBeenShown = prefs.getBoolean(eulaKey, false)
        if(hasBeenShown == false){

            // Show the Eula
            String title = mActivity.getString(R.string.app_name) + " v" + versionInfo.versionName

            //Includes the updates as well so users know what changed.
            String message = mActivity.getString(R.string.app_update_detail) + "\n\n" + mActivity.getString(R.string.eula_detail)

            // Disable orientation changes, to prevent parent activity
            // reinitialization
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(mActivity.getString(R.string.eula_accept), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Mark this version as read.
                    SharedPreferences.Editor editor = prefs.edit()
                    editor.putBoolean(eulaKey, true)
                    editor.commit()
                    dialogInterface.dismiss()
                    // Enable orientation changes based on device's sensor
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
                }
            })
                    .setNegativeButton(mActivity.getString(R.string.eula_decline), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Close the activity as they have declined the EULA
                    mActivity.finish()
                    // Enable orientation changes based on device's sensor
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
                }

            })
            builder.create().show()
        }
    }
}