package floatingmuseum.userhunter;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import floatingmuseum.userhunter.utils.SystemUtil;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Floatingmuseum on 2017/6/12.
 *
 * 查看栈顶包名
 */
public class Sylvanas {

    private static Sylvanas sylvanas;
    private ActivityManager activityManager;
    private PackageManager packageManager;
    private Disposable disposable;

    public static Sylvanas getInstance() {
        if (sylvanas == null) {
            synchronized (Sylvanas.class) {
                if (sylvanas == null) {
                    sylvanas = new Sylvanas();
                }
            }
        }
        return sylvanas;
    }

    private Sylvanas() {
//        BusManager.register(this);
        activityManager = (ActivityManager) App.context.getSystemService(Context.ACTIVITY_SERVICE);
        packageManager = App.context.getPackageManager();
//
//        blackPackages.add("com.android.settings"); // 设置
//        blackPackages.add("com.android.browser"); // 自带浏览器
//        blackPackages.add("com.android.systemui"); // 最近使用程序列表页
//        blackPackages.add("com.google.android.googlequicksearchbox"); // Google搜索
//        blackPackages.add("com.wsandroid.suite.intelempg"); // mcafee
//        blackPackages.add("com.lenovo.safecenter.hd"); // 乐安全
//        blackPackages.add("com.ebensz.browser"); // E人E本浏览器
//        blackPackages.add("com.ebensz.filemanager"); // E人E本文件管理器
//        blackPackages.add("com.ebensz.guards"); // E人E本安全管理
//        blackPackages.add("com.sec.android.app.sbrowser");//三星浏览器
//        blackPackages.add("com.sina.weibo");//微博
//        blackPackages.add("com.samsung.android.service.aircommand");//三星S Pen添加应用界面
//        blackPackages.add("com.wssyncmldm");//三星的更新界面
//        List<String> launchers = AppAnalyseUtil.getOtherLauncherAppPackageName();
//        for (String launcher : launchers) {
//            Logger.d("DisableManager屏蔽...其他启动器包名:" + launcher);
//        }
//        otherLauncherApp.addAll(AppAnalyseUtil.getOtherLauncherAppPackageName());//获取系统内其他Launcher
//
//        lastWhitePackages.add("cn.wps.moffice_eng");// wps
//        lastWhitePackages.add("cn.zhl.book");// 知好乐电子书包
//        lastWhitePackages.add("com.android.packageinstaller");//应用中心有时安装后也会跳
    }

    public void startCheck(){
        Logger.d("希尔瓦娜斯...startCheck()");
        if (isRunning()) {
            //已经处于运行中
            Logger.d("希尔瓦娜斯...已经处于运行中");
            return;
        }
        disposable = Flowable.interval(0, 100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        checkingTop();
                    }
                });
    }

    private void checkingTop() {
        String[] packageNameAndClassName = SystemUtil.getTopPackageNameClassName(App.context, activityManager);
        if (packageNameAndClassName == null) {
            return;
        }

        String packageName = packageNameAndClassName[0];
        String className = packageNameAndClassName[1];
        Varimathras.getInstance().countingAppUsingTime(packageName, className);
    }

    public void stopCheck(){
        Logger.d("希尔瓦娜斯...stopCheck()");
        if (isRunning()) {
            disposable.dispose();
        }
    }

    private boolean isRunning() {
        if (disposable == null || disposable.isDisposed()) {
            return false;
        } else {
            return true;
        }
    }
}
