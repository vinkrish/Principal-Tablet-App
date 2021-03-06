package in.principal.activity;

import in.principal.fragment.StudentList;
import in.principal.fragment.StudentProfile;
import in.principal.sqlite.SqlDbHelper;
import in.principal.util.AnimationUtils;
import in.principal.util.CommonDialogUtils;
import in.principal.util.DateTrackerModel;
import in.principal.util.ExceptionHandler;
import in.principal.util.NetworkUtils;
import in.principal.util.ReplaceFragment;
import in.principal.util.AppGlobal;
import in.principal.dao.TempDao;
import in.principal.dao.StudentAttendanceDao;
import in.principal.attendancefragment.Attendance;
import in.principal.attendancefragment.AttendanceClass;
import in.principal.attendancefragment.AttendanceSection;
import in.principal.fragment.Dashbord;
import in.principal.fragment.HomeworkView;
import in.principal.attendancefragment.MonthlyReport;
import in.principal.attendancefragment.MonthlyReportClass;
import in.principal.sliptestfragment.PerfClass;
import in.principal.sliptestfragment.PerfST;
import in.principal.sliptestfragment.PerfSecSub;
import in.principal.sliptestfragment.Performance;
import in.principal.examfragment.SeClassSec;
import in.principal.examfragment.SeSecSub;
import in.principal.sliptestfragment.StDashbord;
import in.principal.examfragment.StructuredExam;
import in.principal.fragment.TextSms;
import in.principal.sqlite.DateTracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * Created by vinkrish.
 */
@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class Dashboard extends BaseActivity {
    private Activity activity;
    private DrawerLayout drawerLayout;
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
        activity = AppGlobal.getActivity();
        sqlHandler = AppGlobal.getSqlDbHelper();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {

                    case R.id.dashboard_item:
                        ReplaceFragment.replace(new Dashbord(), getFragmentManager());
                        return true;

                    case R.id.attendance_item:
                        updateDate();
                        ReplaceFragment.replace(new Attendance(), getFragmentManager());
                        return true;

                    case R.id.monthly_report_item:
                        monthlyReport();
                        return true;

                    case R.id.sliptest_item:
                        ReplaceFragment.replace(new Performance(), getFragmentManager());
                        return true;

                    case R.id.homework_item:
                        updateDate();
                        ReplaceFragment.replace(new HomeworkView(), getFragmentManager());
                        return true;

                    case R.id.exam_item:
                        ReplaceFragment.replace(new StructuredExam(), getFragmentManager());
                        return true;

                    case R.id.students_item:
                        ReplaceFragment.replace(new StudentList(), getFragmentManager());
                        return true;

                    case R.id.sms_item:
                        if (NetworkUtils.isNetworkConnected(context)) {
                            ReplaceFragment.replace(new TextSms(), getFragmentManager());
                        } else {
                            CommonDialogUtils.displayAlertWhiteDialog(activity, "Check Internet Connection");
                        }
                        return true;

                    default:
                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        android.support.v7.app.ActionBarDrawerToggle actionBarDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();


        if (savedInstanceState == null) {
            selectDefaultFragment();
        }

        //registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_STATUS"));
    }

    private void updateDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = GregorianCalendar.getInstance();
        Date da = calendar.getTime();
        TempDao.updateSelectedDate(simpleDateFormat.format(da), sqliteDatabase);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
        }
    };

    private void selectDefaultFragment() {
        ReplaceFragment.replace(new Dashbord(), getFragmentManager());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        //registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_STATUS"));
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        //unregisterReceiver(broadcastReceiver);
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
        } else {
            menu.getItem(0).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.searchId:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view = this.getLayoutInflater().inflate(R.layout.dialog_search, null);

                studIdList.clear();
                studNameList.clear();

                Cursor c = sqliteDatabase.rawQuery("select A.StudentId, A.Name, B.ClassName,C.SectionName " +
                        "from students A, Class B, Section C " +
                        "where B.ClassId=A.ClassId and C.SectionId=A.SectionId " +
                        "group by A.StudentId", null);
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
                    String s = c.getString(c.getColumnIndex("Name")) + " (" + c.getString(c.getColumnIndex("ClassName")) + " - " + c.getString(c.getColumnIndex("SectionName")) + ")";
                    studNameList.add(s);
                    c.moveToNext();
                }
                c.close();

                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, studNameList);
                final AutoCompleteTextView textView2 = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView2);
                textView2.setAdapter(adapter2);

                builder.setView(view);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int idx = studNameList.indexOf(textView2.getText().toString());
                        if (idx != -1) {
                            TempDao.updateStudentId(studIdList.get(idx), sqliteDatabase);
                            ReplaceFragment.replace(new StudentProfile(), getFragmentManager());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", null);

                AlertDialog dialog = builder.create();
                dialog.getWindow().setGravity(Gravity.TOP);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.y = 10; // top margin
                dialog.getWindow().setAttributes(layoutParams);
                dialog.show();
                // builder.show();
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

    public void toDashbord(View v) {
        ReplaceFragment.clearBackStack(getFragmentManager());
        ReplaceFragment.replace(new Dashbord(), getFragmentManager());
        //	getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        /*int backStackCount = getFragmentManager().getBackStackEntryCount();
        for (int i = 0; i < backStackCount; i++) {
		    int backStackId = getFragmentManager().getBackStackEntryAt(i).getId();
		    fragmentManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		}*/
    }

    private void monthlyReport(){
        String line = StudentAttendanceDao.selectFirstAtt(sqliteDatabase);
        String csvSplitBy = "-";
        if (!line.equals("")) {

            String[] data = line.split(csvSplitBy);
            int firstMonth = Integer.parseInt(data[1]) - 1;

            String last = StudentAttendanceDao.selectLastAtt(sqliteDatabase);

            Calendar cal = GregorianCalendar.getInstance();
            int currentDay = cal.get(Calendar.DAY_OF_MONTH);
            int currentMonth = cal.get(Calendar.MONTH);
            int currentYear = cal.get(Calendar.YEAR);

            if (currentMonth == firstMonth) {
                DateTracker dt = new DateTracker();
                dt.setFirstDate(line);
                dt.setLastDate(last);
                dt.setSelectedMonth(cal.get(Calendar.MONTH));
                sqlHandler.updateDateTracker(dt);
            } else if (currentMonth > firstMonth) {
                DateTracker dt = DateTrackerModel.getDateTracker3(currentDay, currentMonth, currentYear);
                sqlHandler.updateDateTracker(dt);
            }
            ReplaceFragment.replace(new MonthlyReport(), getFragmentManager());
        }
    }

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();
        return dateFormat.format(today);
    }

    public void toStDashbord(View v) {
        TempDao.updateSelectedDate(getToday(), sqliteDatabase);
        ReplaceFragment.replace(new StDashbord(), getFragmentManager());
    }

    public void todaysAttendance(View v) {
        ReplaceFragment.replace(new Attendance(), getFragmentManager());
    }

    public void classAttendance(View v) {
        ReplaceFragment.replace(new AttendanceClass(), getFragmentManager());
    }

    public void sectionAttendance(View v) {
        ReplaceFragment.replace(new AttendanceSection(), getFragmentManager());
    }

    public void monthlyReport(View v) {
        ReplaceFragment.replace(new MonthlyReport(), getFragmentManager());
    }

    public void monthlyReportClass(View v) {
        ReplaceFragment.replace(new MonthlyReportClass(), getFragmentManager());
    }

    public void se(View v) {
        ReplaceFragment.replace(new StructuredExam(), getFragmentManager());
    }

    public void stPerf(View v) {
        ReplaceFragment.replace(new Performance(), getFragmentManager());
    }

    public void stPerfClass(View v) {
        ReplaceFragment.replace(new PerfClass(), getFragmentManager());
    }

    public void seClass(View v) {
        ReplaceFragment.replace(new SeClassSec(), getFragmentManager());
    }

    public void SeSecSub(View v) {
        ReplaceFragment.replace(new SeSecSub(), getFragmentManager());
    }

    public void stPerfSec(View v) {
        ReplaceFragment.replace(new PerfSecSub(), getFragmentManager());
    }

    public void stPerfSt(View v) {
        ReplaceFragment.replace(new PerfST(), getFragmentManager());
    }

    public void homeworkHome(View v) {
        ReplaceFragment.replace(new HomeworkView(), getFragmentManager());
    }
}
