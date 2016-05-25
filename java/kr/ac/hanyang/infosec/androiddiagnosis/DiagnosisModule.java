package kr.ac.hanyang.infosec.androiddiagnosis;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

public class DiagnosisModule {

    private String result;
    private Process p;
    private boolean rooting = false;

    public String getService(PackageManager pm, ApplicationInfo appInfo) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
            for (ServiceInfo serviceInfo : packageInfo.services) {
                result = null;
                if (serviceInfo.isEnabled()) {
                    result = serviceInfo.packageName + " / " + serviceInfo.exported + " / " + serviceInfo.name + " / " + serviceInfo.permission;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // e.printStackTrace();
        }
        return result;
    }

    public void getCertificate(PackageManager pm, ApplicationInfo packageApp) {
        PackageInfo packageInfo = null;
        CertificateFactory cf = null;
        X509Certificate c = null;

        try {
            packageInfo = pm.getPackageInfo(packageApp.packageName, PackageManager.GET_SIGNATURES);
            Log.e("PackageName : ", packageApp.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);

        try {
            cf = CertificateFactory.getInstance("X509");
            c = (X509Certificate) cf.generateCertificate(input);

//            Log.i("IssuerDN", c.getIssuerDN().getName());
            Log.i("IssuerX500Principal", c.getIssuerX500Principal().getName());
//            Log.i("SubjectDN", c.getSubjectDN().getName());
            Log.i("Signature", c.getSignature().toString());
            Log.e("SerialNumber", c.getSerialNumber().toString());

            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getPublicKey().getEncoded());

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]);
                if (appendString.length() == 1) hexString.append("0");
                hexString.append(appendString);
            }
            Log.i("Cer: ", hexString.toString());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void getReceiver(PackageManager pm, ApplicationInfo appInfo) {

        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        List<ResolveInfo> activities = pm.queryBroadcastReceivers(intent, 0);

        for (ResolveInfo resolveInfo : activities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if ((activityInfo != null) && (resolveInfo.activityInfo.packageName.equals(appInfo.packageName))) {
                Log.e("Receiver", resolveInfo.activityInfo.packageName + " / " + activityInfo.name);
            }
        }
    }

    public void getPermission(PackageManager pm, ApplicationInfo appInfo) {
        try {
            PackageInfo packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS | PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);

            String[] requestedPermissions = packageInfo.requestedPermissions;

            if (requestedPermissions != null) {
                for (String requestedPermission : requestedPermissions) {
                    String protectionLevel = null;
                    if (packageInfo.permissions != null) {
                        for (PermissionInfo permission : packageInfo.permissions) {
                            switch (permission.protectionLevel) {
                                case PermissionInfo.PROTECTION_NORMAL:
                                    protectionLevel = "normal";
                                    break;
                                case PermissionInfo.PROTECTION_DANGEROUS:
                                    protectionLevel = "dangerous";
                                    break;
                                case PermissionInfo.PROTECTION_SIGNATURE:
                                    protectionLevel = "signature";
                                    break;
                                case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM:
                                    protectionLevel = "signatureOrSystem";
                                    break;
                                default:
                                    protectionLevel = "";
                                    break;
                            }
                        }
                        Log.d("Permission", appInfo.packageName + "," + requestedPermission + "," + protectionLevel);
                    } else {
                        Log.d("Permission", appInfo.packageName + "," + requestedPermission);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean checkRooting() {
        try {
            p = Runtime.getRuntime().exec("su");

            p.waitFor();

            if (p.exitValue() != 255) {
                rooting = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rooting;
    }
}
