tiny-spring-android
===================

This is very simple Android module built on Tinyspring - goals of this project are:

- follow Spring principles
- unobtrusive (there is no need to extend framework specific classes to get access to Spring/Application context or to views and layouts injected) 
- small footprint and follow Spring principles
- XML configuration for POJO beans
- annotated bean injection in Android classes through annotations (Activities etc.)
- annotated view/layout injections
- getting access to spring context is possible whenever you have the Android context available

public void refresh(Context context) {
    Settings settings = ((Application) context.getApplicationContext()).getSpringContext().getBean("settings", Settings.class);

The following example shows how does it work.

Define android:name in your application AndroidManifest.xml as:

<application android:icon="@drawable/icon" android:label="@string/app_name" android:name="com.h2.tinyspring.android.Application">

And create an Activity:

@AndroidLayout(R.layout.main) <---- this layout is automatically injected from /res/layout folder
public class MainActivity extends Activity {

	@AndroidView(R.id.textView) <---- this view is automatically injected from /res/layout folder
	private TextView textView;
	
	@AndroidView(R.id.progressBar) <---- this view is automatically injected from /res/layout folder
	private TextProgressBar progressBar;

	@AndroidView(R.id.linearLayout) <---- this layout is automatically injected from /res/layout folder
	private LinearLayout layout;

	@Inject <---- this POJO is automatically injected and configured in /assets/tinyspring-android.xml (standard Spring XML configuration)
	private Settings settings;

	@Inject <---- the Spring context is automatically injected and available if needed
	private Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((Application) getApplicationContext()).inject(this); <---- this is the only line you need to add in your Android classes (Activities)
	}
}