package com.zdsoft.mytinker.Tinker;


import android.content.Context;
import android.util.Log;

import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.listener.PatchListener;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.zdsoft.mytinker.Tinker.reporter.SampleLoadReporter;
import com.zdsoft.mytinker.Tinker.reporter.SamplePatchReporter;
import com.zdsoft.mytinker.Tinker.reporter.SampleTinkerReport;
import com.zdsoft.mytinker.Tinker.service.SampleResultService;

public class TinkerManager {
    private static final String TAG = "Tinker.TinkerManager";
    private static boolean isInstalled = false;
    private static ApplicationLike mAppLike;
    /**
     * you can specify all class you want.
     * sometimes, you can only install tinker in some process you want!
     *
     * @param appLike
     */
    static void installTinker(ApplicationLike appLike) {
        mAppLike = appLike;
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        //or you can just use DefaultLoadReporter
        LoadReporter loadReporter = new SampleLoadReporter(appLike.getApplication());
        //or you can just use DefaultPatchReporter
        PatchReporter patchReporter = new SamplePatchReporter(appLike.getApplication());
        //or you can just use DefaultPatchListener
        PatchListener patchListener = new DefaultPatchListener(appLike.getApplication());
        //you can set your own upgrade patch if you need
        AbstractPatch upgradePatchProcessor = new UpgradePatch();
        TinkerInstaller.install(appLike,
                loadReporter, patchReporter, patchListener,
                SampleResultService.class, upgradePatchProcessor);
        isInstalled = true;

        SampleTinkerReport report=  new SampleTinkerReport();
        report.setReporter(new SampleTinkerReport.Reporter(){
            @Override
            public void onReport(int key) {
                Log.e("Tinker.Report", key+"");
            }

            @Override
            public void onReport(String message) {
                Log.e("Tinker.Report", message);
            }
        });
    }

    /**
     * 完成patch文件的加载
     * @param path
     */
    public static void loadPatch(String path){
        if (Tinker.isTinkerInstalled()){
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),path);
        }
    }

    private static Context getApplicationContext(){
        if (mAppLike != null){
            return mAppLike.getApplication().getApplicationContext();
        }
        return null;
    }
}