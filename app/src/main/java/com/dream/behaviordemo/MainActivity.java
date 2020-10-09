package com.dream.behaviordemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv = findViewById(R.id.my_list);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("item " + i);
        }
        adapter = new Adapter(list,this);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    public static boolean checkWriteCalendarOpIgnored(Context context) {
        return  checkOpPermission(context,Manifest.permission.WRITE_CALENDAR) == AppOpsManager.MODE_IGNORED;
    }

    public static int checkOpPermission(Context context, final String permission) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = AppOpsManager.MODE_ALLOWED;
        try {
            if (Build.VERSION.SDK_INT < 24) {
                Method method = manager.getClass().getMethod("checkOpNoThrow", new Class[] {
                        int.class, int.class, String.class
                });
                int op = getPermissionOp(permission);
                if (op != -1) {
                    mode = (Integer) method.invoke(manager, op, Process.myUid(), context.getPackageName());
                }
            } else {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
                String op = getPermissionOpStr(permission);
                if(!TextUtils.isEmpty(op)){
                    mode = manager.unsafeCheckOp(op, applicationInfo.uid,applicationInfo.packageName);
                }
            }

        } catch (Exception e){

        }
        Log.e("xxx", "checkOpPermission: mode = " + mode);
        return mode;
    }

    private static int getPermissionOp(final String permission){
        int op = -1;
        switch (permission){
            case Manifest.permission.READ_CONTACTS:
                op = 4;
                break;
            case Manifest.permission.WRITE_CONTACTS:
                op = 5;
                break;
            case Manifest.permission.READ_CALENDAR:
                op = 8;
                break;
            case Manifest.permission.WRITE_CALENDAR:
                op = 9;
                break;
        }
        return op;
    }

    private static String getPermissionOpStr(final String permission){
        String checkStr="";
        switch (permission){
            case Manifest.permission.READ_CONTACTS:
                checkStr = AppOpsManager.OPSTR_READ_CONTACTS;
                break;
            case Manifest.permission.WRITE_CONTACTS:
                checkStr = AppOpsManager.OPSTR_WRITE_CONTACTS;
                break;
            case Manifest.permission.READ_CALENDAR:
                checkStr = AppOpsManager.OPSTR_READ_CALENDAR;
                break;
            case Manifest.permission.WRITE_CALENDAR:
                checkStr = AppOpsManager.OPSTR_WRITE_CALENDAR;
                break;
        }
        return checkStr;
    }

    public void requesePer(){
        String[] arr= {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CALENDAR};
        ActivityCompat.requestPermissions(this, arr, 100);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (String per :
                permissions) {
            boolean isFirst = ActivityCompat.shouldShowRequestPermissionRationale(this, per);
            Log.e("xxx", "onRequestPermissionsResult: per = " + per + "isFirst = " + isFirst);
        }
    }
}