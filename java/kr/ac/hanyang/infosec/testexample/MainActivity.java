package kr.ac.hanyang.infosec.testexample;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

    private List<String> groupList = null;
    private List<String> childList = null;
    private Map<String, List<String>> packageCollection = null;

    private ExpandableListView expListView = null;
    private ExpandableListAdapter expListAdapter = null;

    private PackageManager pm = null;
    private DiagnosisModule dm = null;
    private List<ApplicationInfo> applications = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pm = getPackageManager();
        applications = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        expListView = (ExpandableListView) findViewById(R.id.laptop_list);

        createGroupList();
        createCollection();

        expListAdapter = new ExpandableListAdapter(MainActivity.this, groupList, packageCollection, applications);
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
            loadChild(appInfo, permissions);

            // Service 추가
            services = dm.getService(pm, appInfo);
            loadChild(appInfo, services);

            // Receiver 추가
            receivers = dm.getReceiver(pm, appInfo);
            loadChild(appInfo, receivers);

            // Certificate 추가
            certificates = dm.getCertificate(pm, appInfo);
            loadChild(appInfo, certificates);

            packageCollection.put(appInfo.packageName, childList);
        }
    }

    private void loadChild(ApplicationInfo appInfo, ArrayList<String> componentInfo) {
        String[] appComponent = null;
        for (String component : componentInfo) {
            if (component != null) {
                appComponent = component.split("/");
                if (appInfo.packageName.equals(appComponent[0])) {
                    childList.add(appComponent[1]);
                }
            }
        }
    }
}