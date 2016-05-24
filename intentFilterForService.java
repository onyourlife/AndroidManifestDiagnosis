public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : packages) {
            try {
                PackageInfo packageInfo = getPackageManager().getPackageInfo(appInfo.packageName, PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
                for (ServiceInfo serviceInfo : packageInfo.services) {
                    Bundle metaData = serviceInfo.metaData;
                    if (metaData != null) {                    
                        Log.e("Service: ", serviceInfo.packageName + " / " + serviceInfo.exported + " / " + serviceInfo.name + " / " + serviceInfo.permission);
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
               // e.printStackTrace();
            }
        }
    }
}
