package com.omottec.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by qinbingbing on 15/11/2018.
 */

public class ReplaceLauncherActivity extends Activity {
    public static final String TAG = "LogUtils";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate ReplaceLauncherActivity");
        setContentView(R.layout.a_replace_launcher);
        replaceLauncher();
    }

    private void replaceLauncher() {
        Log.i(TAG, "replaceLauncher");
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        Log.d(TAG, "resolveInfos: " + (resolveInfos == null ? "null" : Arrays.toString(resolveInfos.toArray())));
        if (resolveInfos == null || resolveInfos.size() <= 1) return;

        List<ComponentName> componentNames = new ArrayList<>();
        ComponentName retailComName = null;
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (resolveInfo == null || resolveInfo.activityInfo == null) continue;
            componentNames.add(new ComponentName(resolveInfo.activityInfo.packageName,
                    resolveInfo.activityInfo.name));


        }
        Log.i(TAG, "componentNames: " + componentNames);
        if (componentNames.size() <= 1) return;

        for (ComponentName componentName : componentNames) {
            if ("com.meituan.retail.launcher".equals(componentName.getPackageName())) {
                retailComName = new ComponentName(componentName.getPackageName(), componentName.getClassName());
            } else {
                pm.clearPackagePreferredActivities(componentName.getPackageName());
            }
        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        ComponentName[] components = new ComponentName[componentNames.size()];
        componentNames.toArray(components);
        pm.addPreferredActivity(filter, IntentFilter.MATCH_CATEGORY_EMPTY, components, retailComName);

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.addCategory(Intent.CATEGORY_HOME);
        launcherIntent.addCategory(Intent.CATEGORY_DEFAULT);
        launcherIntent.setComponent(retailComName);
        launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launcherIntent);
    }
}
