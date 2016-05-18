package kr.ac.hanyang.infosec.section315;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/DeviceMetrics");
            dir.mkdir();

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat curDateFormat = new SimpleDateFormat("yyMMddHHmmss");

            String strCurDate = curDateFormat.format(date);

            File file = new File(Environment.getExternalStorageDirectory() + "/DeviceMetrics/" + strCurDate + "_Permission.txt");
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);

            for (ApplicationInfo applicationInfo : packages) {
                //            Log.d("test", "App: " + applicationInfo.name + " Package: " + applicationInfo.packageName);
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS | PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);

                    //Get Permissions
                    String[] requestedPermissions = packageInfo.requestedPermissions;

                    if (requestedPermissions != null) {
                        for (String requestedPermission : requestedPermissions) {
                            String protectionLevel = null;
                            if (packageInfo.permissions != null) {
                                for (PermissionInfo permission : packageInfo.permissions) {
                                    switch (permission.protectionLevel) {
                                        case PermissionInfo.PROTECTION_NORMAL:
                                            protectionLevel = ",normal";
                                            break;
                                        case PermissionInfo.PROTECTION_DANGEROUS:
                                            protectionLevel = ",dangerous";
                                            break;
                                        case PermissionInfo.PROTECTION_SIGNATURE:
                                            protectionLevel = ",signature";
                                            break;
                                        case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
                                            protectionLevel = ",signatureOrSystem";
                                            break;
                                        default:
                                            protectionLevel = "";
                                            break;
                                    }
                                }
                                // Log.d("RequestedPermission", applicationInfo.packageName + "," + requestedPermission + "," + protectionLevel);
                                fos.write((applicationInfo.packageName + "," + requestedPermission + protectionLevel + "\n").getBytes());
                            } else {
                                // Log.d("RequestedPermission", applicationInfo.packageName + "," + requestedPermission);
                                fos.write((applicationInfo.packageName + "," + requestedPermission + "\n").getBytes());
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
            fos.close();
//            Log.d("FOS: ", "FileWriter is completed.");
            Toast.makeText(MainActivity.this, "Completed.", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
