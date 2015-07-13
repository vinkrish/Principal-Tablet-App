package in.principal.activity;

import in.principal.adapter.NavDrawerListAdapter;
import in.principal.model.NavDrawerItem;
import in.principal.sqlite.SqlDbHelper;
import in.principal.util.AnimationUtils;
import in.principal.util.DateTrackerModel;
import in.principal.util.ExceptionHandler;
import in.principal.util.NetworkUtils;
import in.principal.util.ReplaceFragment;
import in.principal.activity.R;
import in.principal.util.AppGlobal;
import in.principal.dao.TempDao;
import in.principal.fragment.SearchStudST;
import in.principal.adapter.Alert;
import in.principal.dao.StudentAttendanceDao;
import in.principal.fragment.Attendance;
import in.principal.fragment.AttendanceClass;
import in.principal.fragment.AttendanceSection;
import in.principal.fragment.Dashbord;
import in.principal.fragment.HomeworkView;
import in.principal.fragment.MonthlyReport;
import in.principal.fragment.MonthlyReportClass;
import in.principal.fragment.PerfClass;
import in.principal.fragment.PerfST;
import in.principal.fragment.PerfSecSub;
import in.principal.fragment.Performance;
import in.principal.fragment.SeClassSec;
import in.principal.fragment.SeSecSub;
import in.principal.fragment.StDashbord;
import in.principal.fragment.StructuredExam;
import in.principal.fragment.TextSms;
import in.principal.fragment.VoiceSms;
import in.principal.sqlite.DateTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class Dashboard extends BaseActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private ActionBarDrawerToggle mDrawerToggle;
	private NavDrawerListAdapter navDrawerListAdapter;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private SqlDbHelper sqlHandler;
	private Context context;
	private static SQLiteDatabase sqliteDatabase;
	private List<String> studNameList = new ArrayList<>();
	private List<Integer> studIdList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.activity_dashboard);

		/*IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);*/

		context = AppGlobal.getContext();
		sqlHandler = AppGlobal.getSqlDbHelper();
		sqliteDatabase = AppGlobal.getSqliteDatabase();

		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getActionBar().setCustomView(R.layout.action_bar);

		mTitle = getTitle();

		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		navDrawerItems = new ArrayList<>();
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));

		navMenuIcons.recycle();
		navDrawerListAdapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(navDrawerListAdapter);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close){

			public void onDrawerClosed(View view){
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView){
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if(savedInstanceState == null){
			selectItem(0);
		}
		
		registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_STATUS"));
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	invalidateOptionsMenu();
	    }
	};

	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	@Override
	protected void onPause(){
		super.onPause();
	}

	@Override
	protected void onResume(){
		registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_STATUS"));
		super.onResume();
	}

	@Override
	protected void onStart(){
		super.onStart();
		registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_STATUS"));
	}

	@Override
	protected void onRestart(){
		super.onRestart();
		registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_STATUS"));
	}
	
	@Override
	protected void onStop(){
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.dashboard, menu);

		if (NetworkUtils.isNetworkConnected(context)) {
			menu.getItem(0).setVisible(false);
		}else{
			menu.getItem(0).setVisible(true);
		}
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(mDrawerToggle.onOptionsItemSelected(item)){
			return true;
		}
		switch (item.getItemId()) {
		case R.id.searchId:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View view = this.getLayoutInflater().inflate(R.layout.dialog_search, null);

			studIdList.clear();
			studNameList.clear();

			Cursor c = sqliteDatabase.rawQuery("select A.StudentId, A.Name, B.ClassName,C.SectionName from students A, Class B, Section C where B.ClassId=A.ClassId and "
					+ "C.SectionId=A.SectionId group by A.StudentId", null);
			c.moveToFirst();
			while(!c.isAfterLast()){
				studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
				String s = c.getString(c.getColumnIndex("Name"))+" ("+c.getString(c.getColumnIndex("ClassName"))+" - "+c.getString(c.getColumnIndex("SectionName"))+")";
				studNameList.add(s);
				c.moveToNext();
			}
			c.close();

			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, studNameList);
			final AutoCompleteTextView textView2 = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView2);
			textView2.setAdapter(adapter2);

			builder.setView(view);

			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int idx = studNameList.indexOf(textView2.getText().toString());
					if(idx!=-1){
						TempDao.updateStudentId(studIdList.get(idx), sqliteDatabase);
						ReplaceFragment.replace(new SearchStudST(), getFragmentManager());
					}
				}
			});
			builder.setNegativeButton("Cancel", null);
			builder.show();
			return true;
		case R.id.action_logout:
			finish();
			Intent intent = new Intent(this, in.principal.activity.LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			AnimationUtils.activityExitVertical(Dashboard.this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);	
		}
	}

	private void selectItem(int position) {
		if(position == 0){
			ReplaceFragment.replace(new Dashbord(), getFragmentManager());
		}else if(position == 1){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			Calendar calendar = GregorianCalendar.getInstance();
			Date da = calendar.getTime();
			TempDao.updateSelectedDate(simpleDateFormat.format(da), sqliteDatabase);
			ReplaceFragment.replace(new Attendance(), getFragmentManager());
		}else if(position == 2){
			String line = StudentAttendanceDao.selectFirstAtt(sqliteDatabase);
			String csvSplitBy = "-";
			if(!line.equals("")){

				String[] data = line.split(csvSplitBy);
				int firstMonth = Integer.parseInt(data[1])-1;

				String last = StudentAttendanceDao.selectLastAtt(sqliteDatabase);

				Calendar cal = GregorianCalendar.getInstance();
				int currentDay = cal.get(Calendar.DAY_OF_MONTH);
				int currentMonth = cal.get(Calendar.MONTH);
				int currentYear = cal.get(Calendar.YEAR);

				if(currentMonth==firstMonth){
					DateTracker dt = new DateTracker();
					dt.setFirstDate(line);
					dt.setLastDate(last);
					dt.setSelectedMonth(cal.get(Calendar.MONTH));
					sqlHandler.updateDateTracker(dt);
				}else if(currentMonth>firstMonth){
					DateTracker dt = DateTrackerModel.getDateTracker3(currentDay, currentMonth, currentYear);
					sqlHandler.updateDateTracker(dt);
				}
				ReplaceFragment.replace(new MonthlyReport(), getFragmentManager());
			}
		}else if(position == 3){
			ReplaceFragment.replace(new Performance(), getFragmentManager());
		}else if(position == 4){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			Calendar calendar = GregorianCalendar.getInstance();
			Date da = calendar.getTime();
			TempDao.updateSelectedDate(simpleDateFormat.format(da), sqliteDatabase);
			ReplaceFragment.replace(new HomeworkView(), getFragmentManager());
		}else if(position == 5){
			ReplaceFragment.replace(new StructuredExam(), getFragmentManager());
		}else if(position == 6){
			if(NetworkUtils.isNetworkConnected(context)){
				ReplaceFragment.replace(new TextSms(), getFragmentManager());
			}else{
				Alert a = new Alert(this);
				a.showAlert("check internet connection");
			}
		}
		mDrawerList.setItemChecked(position, true);
		setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title){
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void toDashbord(View v){
		ReplaceFragment.clearBackStack(getFragmentManager());
		ReplaceFragment.replace(new Dashbord(), getFragmentManager());
		//	getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		/*int backStackCount = getFragmentManager().getBackStackEntryCount();
		for (int i = 0; i < backStackCount; i++) {
		    int backStackId = getFragmentManager().getBackStackEntryAt(i).getId();
		    fragmentManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		}*/
	}

	public void toStDashbord(View v){
		ReplaceFragment.replace(new StDashbord(), getFragmentManager());
	}

	public void todaysAttendance(View v){
		ReplaceFragment.replace(new Attendance(), getFragmentManager());
	}

	public void classAttendance(View v){
		ReplaceFragment.replace(new AttendanceClass(), getFragmentManager());
	}

	public void sectionAttendance(View v){
		ReplaceFragment.replace(new AttendanceSection(), getFragmentManager());
	}

	public void monthlyReport(View v){
		ReplaceFragment.replace(new MonthlyReport(), getFragmentManager());
	}

	public void monthlyReportClass(View v){
		ReplaceFragment.replace(new MonthlyReportClass(), getFragmentManager());
	}
	
	public void se(View v){
		ReplaceFragment.replace(new StructuredExam(), getFragmentManager());
	}
	
	public void stPerf(View v){
		ReplaceFragment.replace(new Performance(), getFragmentManager());
	}
	
	public void stPerfClass(View v){
		ReplaceFragment.replace(new PerfClass(), getFragmentManager());
	}
	
	public void seClass(View v){
		ReplaceFragment.replace(new SeClassSec(), getFragmentManager());
	}
	
	public void SeSecSub(View v){
		ReplaceFragment.replace(new SeSecSub(), getFragmentManager());
	}
	
	public void stPerfSec(View v){
		ReplaceFragment.replace(new PerfSecSub(), getFragmentManager());
	}
	
	public void stPerfSt(View v){
		ReplaceFragment.replace(new PerfST(), getFragmentManager());
	}
	
	public void homeworkHome(View v){
		ReplaceFragment.replace(new HomeworkView(), getFragmentManager());
	}
}
