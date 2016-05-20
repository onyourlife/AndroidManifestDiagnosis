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
        String packageName = this.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;

        PackageInfo packageInfo = null;
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageApp : packages) {
            try {
                //            packageInfo = pm.getPackageInfo(packageName, flags);
                packageInfo = pm.getPackageInfo(packageApp.packageName, flags);
                Log.e("PackageName : ", packageApp.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            Signature[] signatures = packageInfo.signatures;

            byte[] cert = signatures[0].toByteArray();

            InputStream input = new ByteArrayInputStream(cert);

            CertificateFactory cf = null;
            try {
                cf = CertificateFactory.getInstance("X509");
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            
            X509Certificate c = null;
            try {
                c = (X509Certificate) cf.generateCertificate(input);
                Log.e("IssuerDN", c.getIssuerDN().getName());
                Log.e("IssuerX500Principal", c.getIssuerX500Principal().getName());
                Log.e("SubjectDN", c.getSubjectDN().getName());
                Log.e("Signature", c.getSignature().toString());
                Log.e("SerialNumber", c.getSerialNumber().toString());
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            try {
                MessageDigest md = MessageDigest.getInstance("SHA1");
                byte[] publicKey = md.digest(c.getPublicKey().getEncoded());

                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < publicKey.length; i++) {
                    String appendString = Integer.toHexString(0xFF & publicKey[i]);
                    if (appendString.length() == 1) hexString.append("0");
                    hexString.append(appendString);
                }
                Log.d("Example", "Cer: " + hexString.toString());
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
        }
    }
}
