package kr.ac.hanyang.infosec.androiddiagnosis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> packageCollection;
    ExpandableListView expListView;

    PackageManager pm;
    DiagnosisModule dm;
    List<ApplicationInfo> applications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pm = getPackageManager();
        applications = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        createGroupList();
        createCollection();

        expListView = (ExpandableListView) findViewById(R.id.laptop_list);

        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                this, groupList, packageCollection);
        expListView.setAdapter(expListAdapter);

        //setGroupIndicatorToRight();
        expListView.setOnChildClickListener(new OnChildClickListener() {

            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });
    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        for (ApplicationInfo appInfo : applications) {
            groupList.add(appInfo.packageName);
        }
    }

    private void createCollection() {
        ArrayList<String> permissions = null;
        ArrayList<String> services = null;
        ArrayList<String> receivers = null;
        ArrayList<String> certificates = null;

        String[] appPermission;
        String[] appService;
        String[] appReceiver;
        String[] appCertificate;

        packageCollection = new LinkedHashMap<String, List<String>>();

        dm = new DiagnosisModule();
        for (ApplicationInfo appInfo : applications) {
            childList = new ArrayList<String>();

            // permission 추가
            permissions = dm.getPermission(pm, appInfo);
            for (String permission : permissions) {
                if (permission != null) {
                    appPermission = permission.split(",");
                    if (appInfo.packageName.equals(appPermission[0])) {
//                        Log.e("appPermission[1]", appPermission[1]);
                        childList.add(appPermission[1]);
                    }
                }
            }

            // Service 추가
            services = dm.getService(pm, appInfo);
            for (String service : services) {
                if (service != null) {
                    appService = service.split(",");
                    if (appInfo.packageName.equals(appService[0])) {
//                    Log.e("appService[1]", appService[1]);
                        childList.add(appService[1]);
                    }
                }
            }

            // Receiver 추가
            receivers = dm.getReceiver(pm, appInfo);
            for (String receiver : receivers) {
                if (receiver != null) {
                    appReceiver = receiver.split(",");
                    if (appInfo.packageName.equals(appReceiver[0])) {
//                    Log.e("appService[1]", appService[1]);
                        childList.add(appReceiver[1]);
                    }
                }
            }

            packageCollection.put(appInfo.packageName, childList);
        }
    }

    private void loadChild(String[] laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);
    }

}
