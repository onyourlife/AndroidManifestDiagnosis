package kr.ac.hanyang.infosec.testexample;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class DiagnosisModule {

    private Process p;
    private boolean rooting = false;

    public ArrayList<String> getService(PackageManager pm, ApplicationInfo appInfo) {
        ArrayList<String> tmpServices = new ArrayList<String>();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
            for (ServiceInfo serviceInfo : packageInfo.services) {
                if (serviceInfo.isEnabled() && serviceInfo.exported) {
                    if(serviceInfo.permission!=null){
                        tmpServices.add(appInfo.packageName + "/<service>\n" + serviceInfo.name + "\n\n<service-permission>\n" + serviceInfo.permission);
                    }else{
                        tmpServices.add(appInfo.packageName + "/<service>\n" + serviceInfo.name);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // e.printStackTrace();
        }
        return tmpServices;
    }

    public ArrayList<String> getReceiver(PackageManager pm, ApplicationInfo appInfo) {
        ArrayList<String> tmpReceivers = new ArrayList<String>();

        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        List<ResolveInfo> activities = pm.queryBroadcastReceivers(intent, 0);

        for (ResolveInfo resolveInfo : activities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if ((activityInfo != null) && (resolveInfo.activityInfo.packageName.equals(appInfo.packageName))) {
//                Log.e("Receiver", appInfo.packageName + " / " + resolveInfo.activityInfo.packageName + " / " + activityInfo.name);
                tmpReceivers.add(appInfo.packageName + "/<receiver>\n" + activityInfo.name);
            }
        }
        return tmpReceivers;
    }

    public ArrayList<String> getPermission(PackageManager pm, ApplicationInfo appInfo) {
        ArrayList<String> tmpPermissions = new ArrayList<String>();
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

                        if ((protectionLevel == null) || (protectionLevel == "")) {
                            tmpPermissions.add(appInfo.packageName + "/<permission>\n" + requestedPermission);
//                        Log.d("Permission", appInfo.packageName + " / " + requestedPermission);
                        } else {
                            tmpPermissions.add(appInfo.packageName + "/<permission>\n" + requestedPermission + "\n\n<protectionLevel>\n" + protectionLevel);
//                        Log.d("Permission", appInfo.packageName + " / " + requestedPermission + " / " + protectionLevel);
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return tmpPermissions;
    }

    public ArrayList<String> getCertificate(PackageManager pm, ApplicationInfo appInfo) {
        ArrayList<String> tmpCertificate = new ArrayList<String>();
        PackageInfo packageInfo = null;
        CertificateFactory cf = null;
        X509Certificate c = null;

        try {
            packageInfo = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_SIGNATURES);
//            Log.e("PackageName", appInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);

        try {
            cf = CertificateFactory.getInstance("X509");
            c = (X509Certificate) cf.generateCertificate(input);

            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getPublicKey().getEncoded());

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]);
                if (appendString.length() == 1) hexString.append("0");
                hexString.append(appendString);
            }

//            Log.i("IssuerDN", appInfo.packageName + " / " + c.getIssuerDN().getName());
//            Log.i("IssuerX500Principal", appInfo.packageName + " / " + c.getIssuerX500Principal().getName());
//            Log.i("SubjectDN", appInfo.packageName + " / " + c.getSubjectDN().getName());
//            Log.i("Signature", appInfo.packageName + " / " + c.getSignature().toString());
//            Log.e("SerialNumber", appInfo.packageName + " / " + c.getSerialNumber().toString());
//            Log.i("Cer", appInfo.packageName + " / " + hexString.toString());

            tmpCertificate.add(appInfo.packageName + "/<X.509>\n" + c.getIssuerX500Principal().getName());
            tmpCertificate.add(appInfo.packageName + "/<SerialNumber>\n" + c.getSerialNumber().toString());
            tmpCertificate.add(appInfo.packageName + "/<Signature>\n" + c.getSignature().hashCode());
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return tmpCertificate;
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
