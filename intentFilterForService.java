public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager manager = getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // NOTE: Provide some data to help the Intent resolver
        // Query for all activities that match my filter and request that the filter used
        //  to match is returned in the ResolveInfo
        List<ResolveInfo> infos = manager.queryIntentActivities (intent,
                PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo info : infos) {
            ActivityInfo activityInfo = info.activityInfo;
            IntentFilter filter = info.filter;
            if (filter != null && (filter.hasAction(Intent.ACTION_MAIN) ||
                    filter.hasCategory(Intent.CATEGORY_LAUNCHER))) {
                // This activity resolves my Intent with the filter I'm looking for
                String activityPackageName = activityInfo.packageName;
                String activityName = activityInfo.name;

                Log.e("TEST","Activity "+activityPackageName + "/" + activityName);
            }
        }