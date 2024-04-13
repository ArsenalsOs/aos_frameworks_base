package com.android.internal.util.aos;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.SystemProperties;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AosHooks {
    private static final String TAG = "AosHooks";

    private static final String PACKAGE_FINSKY = "com.android.vending";
    private static final String PACKAGE_GMS = "com.google.android.gms";

    private static volatile boolean sIsGms = false;
    private static volatile boolean sIsFinsky = false;
    private static volatile boolean sIsTrustedApp = false;

    private static Application sApplication;

    private AosHooks() { }

    public static void onNewApplication(Application app) {
        sApplication = app;
        final String packageName = sApplication.getPackageName();
        final String processName = Application.getProcessName();
        sIsGms = packageName.equals(PACKAGE_GMS);
        sIsFinsky = packageName.equals(PACKAGE_FINSKY);
        ApplicationInfo info = sApplication.getApplicationInfo();
        if (info != null) {
            sIsTrustedApp = info.getHiddenApiEnforcementPolicy() == ApplicationInfo.HIDDEN_API_ENFORCEMENT_DISABLED;
        }

        if (sIsGms) {
            spoofBuildGms();
            return;
        }
        if (sIsTrustedApp) {
            Log.d(TAG, "onNewApplication is trusted app " + packageName);
        } else {
            Log.i(TAG, "onNewApplication not trusted app " + packageName);
        }
    }

    private static void setBuildFieldValue(String key, Object value) {
        try {
            Field field = Build.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set build " + key, e);
        }
    }

    private static void setVersionFieldString(String key, String value) {
        try {
            Field field = Build.VERSION.class.getDeclaredField(key);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, "Failed to set version " + key, e);
        }
    }

    private static void spoofBuildGms() {
        // Alter build parameters to avoid hardware attestation enforcement
        setBuildFieldValue("BRAND", "motorola");
        setBuildFieldValue("MANUFACTURER", "motorola");
        setBuildFieldValue("DEVICE", "griffin");
        setBuildFieldValue("ID", "MCC24.246-37");
        setBuildFieldValue("FINGERPRINT", "motorola/griffin_retcn/griffin:6.0.1/MCC24.246-37/42:user/release-keys");
        setBuildFieldValue("MODEL", "XT1650-05");
        setBuildFieldValue("PRODUCT", "griffin_retcn");
        setVersionFieldString("SECURITY_PATCH", "2016-07-01");
    }

    private static boolean isCallerSafetyNet() {
        return sIsGms && Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(elem -> elem.getClassName().contains("DroidGuard"));
    }

    public static void onEngineGetCertificateChain() {
        // Check stack for SafetyNet or Play Integrity
        if (isCallerSafetyNet() || sIsFinsky) {
            Log.w(TAG, "onEngineGetCertificateChain package " + sApplication.getPackageName() + " sIsGms " + sIsGms + " sIsFinsky " + sIsFinsky);
            throw new UnsupportedOperationException();
        }
    }
}
