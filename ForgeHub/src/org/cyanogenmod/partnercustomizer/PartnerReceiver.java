/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cyanogenmod.partnercustomizer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

/**
 * This class posts notifications that are used to populate the Partner Row of the Leanback Launcher
 * It also allows the system/launcher to find the correct partner customization
 * package.
 *
 * Packages using this broadcast receiver must also be a system app to be used for
 * partner customization.
 */
public class PartnerReceiver extends BroadcastReceiver {
    private static final String ACTION_PARTNER_CUSTOMIZATION =
            "com.google.android.leanbacklauncher.action.PARTNER_CUSTOMIZATION";

    private static final String EXTRA_ROW_WRAPPING_CUTOFF =
            "com.google.android.leanbacklauncher.extra.ROW_WRAPPING_CUTOFF";

    private static final String PARTNER_GROUP = "partner_row_entry";
    private static final String BLACKLIST_PACKAGE = "com.google.android.leanbacklauncher.replacespackage";

    private static final String KODI_PKG_NAME = "org.xbmc.kodi";
    private static final String CORTEX_PKG_NAME = "tv.ouya.oe.installer";
    private static final String SLING_PKG_NAME = "com.sling";
    private static final String CRACKLE_PKG_NAME = "com.crackle.androidtv";
    private static final String HULU_PKG_NAME = "com.hulu.plus";
    private static final String CRUNCHY_PKG_NAME = "com.crunchyroll.crunchyroid";
    private static final String NETFLIX_PKG_NAME = "com.netflix.ninja";

    private Context mContext;
    private NotificationManager mNotifMan;
    private PackageManager mPkgMan;

    // Cutoff value for when the Launcher displays the Partner row as a single
    // row, or a two row grid. Can be used for correctly positioning the partner
    // app entries.
    private int mRowCutoff = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mContext == null) {
            mContext = context;
            mNotifMan = (NotificationManager)
                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mPkgMan = mContext.getPackageManager();
        }

        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)||
                Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            postNotification(getPackageName(intent));
        } else if (ACTION_PARTNER_CUSTOMIZATION.equals(action)) {
            mRowCutoff = intent.getIntExtra(EXTRA_ROW_WRAPPING_CUTOFF, 0);
            postNotification(KODI_PKG_NAME);
            postNotification(NETFLIX_PKG_NAME);
            postNotification(CORTEX_PKG_NAME);
            postNotification(SLING_PKG_NAME);
            postNotification(HULU_PKG_NAME);
            postNotification(CRACKLE_PKG_NAME);
            postNotification(CRUNCHY_PKG_NAME);
        }
    }

    private void postNotification(String pkgName) {
        int sort;
        int resId;
        int backupResId;
        int titleId;
        int backupTitleId;

        switch (pkgName) {
			case CORTEX_PKG_NAME:
                sort = 1;
                resId = R.drawable.ic_cortex_banner;
                backupResId = R.drawable.ic_cortex_banner;
                titleId = R.string.cortex;
                backupTitleId = R.string.cortex;
                break;
           case KODI_PKG_NAME:
                sort = 2;
                resId = R.drawable.ic_kodi_banner;
                backupResId = R.drawable.ic_kodi_banner;
                titleId = R.string.kodi;
                backupTitleId = R.string.kodi;
                break;
            case NETFLIX_PKG_NAME:
                sort = 3;
                resId = R.drawable.ic_netflix_banner;
                backupResId = R.drawable.ic_netflix_banner;
                titleId = R.string.netflix;
                backupTitleId = R.string.netflix;
                break;
            case HULU_PKG_NAME:
                sort = 4;
                resId = R.drawable.ic_hulu_banner;
                backupResId = R.drawable.ic_hulu_banner;
                titleId = R.string.hulu;
                backupTitleId = R.string.hulu;
                break;
            case CRACKLE_PKG_NAME:
                sort = 5;
                resId = R.drawable.ic_crackle_banner;
                backupResId = R.drawable.ic_crackle_banner;
                titleId = R.string.crackle;
                backupTitleId = R.string.crackle;
                break;
             case CRUNCHY_PKG_NAME:
                sort = 6;
                resId = R.drawable.ic_crunchy_banner;
                backupResId = R.drawable.ic_crunchy_banner;
                titleId = R.string.crunchy;
                backupTitleId = R.string.crunchy;
                break;   
             case SLING_PKG_NAME:
                sort = 7;
                resId = R.drawable.ic_sling_banner;
                backupResId = R.drawable.ic_sling_banner;
                titleId = R.string.sling;
                backupTitleId = R.string.sling;
                break;     
            default:
                return;
        }

        postNotification(sort, resId, backupResId, titleId, backupTitleId, pkgName);
    }

    private void postNotification(int sort, int resId, int backupResId,
            int titleId, int backupTitleId, String pkgName) {
        int id = resId;
        Intent intent = mPkgMan.getLeanbackLaunchIntentForPackage(pkgName);

        if (intent == null) {
            titleId = backupTitleId;
            resId = backupResId;
            intent = getBackupIntent(pkgName);
        }

        Notification.Builder bob = new Notification.Builder(mContext);
        Bundle extras = new Bundle();
        extras.putString(BLACKLIST_PACKAGE, pkgName);

        bob.setContentTitle(mContext.getString(titleId))
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), resId))
                .setContentIntent(PendingIntent.getActivity(mContext, 0, intent, 0))
                .setCategory(Notification.CATEGORY_RECOMMENDATION)
                .setGroup(PARTNER_GROUP)
                .setSortKey(sort+"")
                .setColor(mContext.getResources().getColor(R.color.partner_color))
                .setExtras(extras);

        mNotifMan.notify(id, bob.build());
    }

    private Intent getBackupIntent(String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pkgName));

        return intent;
    }

    private String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        String pkg = uri != null ? uri.getSchemeSpecificPart() : null;
        return pkg;
    }

}
