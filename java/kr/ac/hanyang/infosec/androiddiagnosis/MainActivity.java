package kr.ac.hanyang.infosec.androiddiagnosis;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String service = null;

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        DiagnosisModule dm = new DiagnosisModule();
        for (ApplicationInfo appInfo : packages) {
            dm.getCertificate(pm, appInfo);
            dm.getPermission(pm, appInfo);
            dm.getReceiver(pm, appInfo);
            service = dm.getService(pm, appInfo);
            if (service != null) {
                Log.e("Service: ", service);
            }
        }
        Log.e("Rooting", String.valueOf(dm.checkRooting()));
    }
}
