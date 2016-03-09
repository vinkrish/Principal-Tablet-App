package in.principal.attendancefragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.PerfStAdapter;
import in.principal.dao.SectionDao;
import in.principal.dao.StudentAttendanceDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.DateTracker;
import in.principal.sqlite.Section;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Students;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.DateTrackerModel;
import in.principal.util.ReplaceFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by vinkrish.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
public class MonthlyReportSection extends Fragment {
    private AlertDialog alertDialog;
    private Context context;
    private Activity act;
    private SqlDbHelper sqlHandler;
    private static SQLiteDatabase sqliteDatabase;
    private PerfStAdapter amrAdapter;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private List<Integer> progressList;
    private int sectionProgress, index, idx;
    private List<Section> secList = new ArrayList<>();
    private List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private List<Integer> studentIdList = new ArrayList<>();
    private List<Long> studIDList = new ArrayList<>();
    private List<String> studentNameList = new ArrayList<>();
    private List<String> daysList = new ArrayList<>();
    private String[] item;
    private String[] items = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private String firstDate, lastDate, className, sectionName;
    private int noOfDays, classId, sectionId, savedMonth;
    private TextView selectmonthly, sectionPercent, clasSecNam, daysCount;
    private Button classBC, secBC;
    private ProgressBar pb;
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.monthly_report_sec, container, false);
        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqlHandler = AppGlobal.getSqlDbHelper();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(act);

        selectmonthly = (TextView) view.findViewById(R.id.monthly);
        classBC = (Button) view.findViewById(R.id.classButton);
        secBC = (Button) view.findViewById(R.id.sectionButton);
        clasSecNam = (TextView) view.findViewById(R.id.clasecName);
        pb = (ProgressBar) view.findViewById(R.id.secAvgProgress);
        sectionPercent = (TextView) view.findViewById(R.id.sectionpercent);
        ListView lv = (ListView) view.findViewById(R.id.list);
        daysCount = (TextView) view.findViewById(R.id.totalDays);

        amrAdapter = new PerfStAdapter(context, R.layout.mr_list, amrList);
        lv.setAdapter(amrAdapter);

        init();

        LinearLayout selecClass = (LinearLayout) view.findViewById(R.id.selectSection);
        selecClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle("Select Section");
                builder.setSingleChoiceItems(item, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                        alertDialog.dismiss();
                        Temp t = new Temp();
                        t.setSectionId(secIdList.get(index));
                        t.setSectionName(secNameList.get(index));
                        TempDao.updateSection(t, sqliteDatabase);
                        init();
                    }

                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TempDao.updateStudentId(studIDList.get(pos), sqliteDatabase);
                ReplaceFragment.replace(new MonthlyReportStud(), getFragmentManager());
            }
        });

        RelativeLayout ll = (RelativeLayout) view.findViewById(R.id.datePicker);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle("Select month");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        idx = which;
                        alertDialog.dismiss();
                        String line = StudentAttendanceDao.selectFirstAtt(sqliteDatabase);
                        String csvSplitBy = "-";

                        String[] data = line.split(csvSplitBy);
                        int firstYear = Integer.parseInt(data[0]);
                        int firstMonth = Integer.parseInt(data[1]) - 1;
                        int firstDay = Integer.parseInt(data[2]);

                        String last = StudentAttendanceDao.selectLastAtt(sqliteDatabase);
                        String[] data2 = last.split(csvSplitBy);
                        int lastYear = Integer.parseInt(data2[0]);
                        //	int lastMonth = Integer.parseInt(data2[1])-1;
                        //	int lastDay = Integer.parseInt(data2[2]);

                        Calendar cal = GregorianCalendar.getInstance();
                        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                        int currentMonth = cal.get(Calendar.MONTH);
                        int currentYear = cal.get(Calendar.YEAR);

                        int selectedMonth = idx;
                        if (currentYear == firstYear) {
                            if (selectedMonth > currentMonth) {
                                CommonDialogUtils.displayAlertWhiteDialog(act, "Attendance is not yet marked");
                            } else if (selectedMonth < firstMonth) {
                                CommonDialogUtils.displayAlertWhiteDialog(act, "Attendance is not yet marked");
                            } else if (selectedMonth == firstMonth) {
                                DateTracker dt = DateTrackerModel.getDateTracker1(firstDay, currentDay, selectedMonth, currentYear);
                                sqlHandler.updateDateTracker(dt);
                                init();
                            } else if (selectedMonth == currentMonth) {
                                DateTracker dt = DateTrackerModel.getDateTracker3(currentDay, selectedMonth, currentYear);
                                sqlHandler.updateDateTracker(dt);
                                init();
                            } else {
                                DateTracker dt = DateTrackerModel.getDateTracker(selectedMonth, currentYear);
                                sqlHandler.updateDateTracker(dt);
                                init();
                            }
                        } else {
                            if (selectedMonth > currentMonth) {
                                CommonDialogUtils.displayAlertWhiteDialog(act, "Attendance is not yet marked");
                            } else {
                                DateTracker dt = DateTrackerModel.getDateTracker(selectedMonth, lastYear);
                                sqlHandler.updateDateTracker(dt);
                                init();
                            }
                        }
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    private void init() {
        initializeList();
        Temp t = TempDao.selectTemp(sqliteDatabase);
        classId = t.getClassId();
        className = t.getClassName();
        sectionId = t.getSectionId();
        sectionName = t.getSectionName();

        DateTracker dt = sqlHandler.selectDateTracker();
        firstDate = dt.getFirstDate();
        lastDate = dt.getLastDate();
        //	noOfDays = dt.getNoOfDays();
        savedMonth = dt.getSelectedMonth();
        noOfDays = StudentAttendanceDao.noOfWorkingSecDays(firstDate, lastDate, sectionId, sqliteDatabase);
        daysCount.setText("Days (" + noOfDays + ")");

        new CalledBackLoad().execute();
    }

    class CalledBackLoad extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Preparing data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            secList = SectionDao.selectSection(classId, sqliteDatabase);
            for (Section s : secList) {
                secIdList.add(s.getSectionId());
                secNameList.add(s.getSectionName());
            }

            List<Students> studentList = StudentsDao.selectStudents(sectionId, sqliteDatabase);
            for (Students s : studentList) {
                studIDList.add(s.getStudentId());
                studentIdList.add(s.getRollNoInClass());
                studentNameList.add(s.getName());
            }

            item = secNameList.toArray(new String[secNameList.size()]);
            sectionProgress = inSecMonthly(firstDate, lastDate, sectionId, noOfDays);
            daysList = studMonthlyDays(firstDate, lastDate, studIDList, noOfDays);
            progressList = studMonthly(firstDate, lastDate, studIDList, noOfDays);
            populateList();
            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            selectmonthly.setText(items[savedMonth]);
            classBC.setText("Class " + className);
            secBC.setText("Section " + sectionName);
            clasSecNam.setText(className + " - " + sectionName);
            if (sectionProgress >= 75) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
            } else if (sectionProgress >= 50) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
            } else {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
            }
            pb.setProgress(sectionProgress);
            sectionPercent.setText(sectionProgress + "%");
            amrAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

    private List<Integer> studMonthly(String startDate, String endDate, List<Long> studIdList, int noOfDays) {
        List<Integer> progress = new ArrayList<>();
        for (Long studId : studIdList) {
            double absCnt = StudentAttendanceDao.studMontAbsCnt(startDate, endDate, studId, sqliteDatabase);
            double avg = ((noOfDays - absCnt) / noOfDays) * 100;
            progress.add((int) avg);
        }
        return progress;
    }

    private List<String> studMonthlyDays(String startDate, String endDate, List<Long> studIdList, int noOfDays) {
        List<String> daysList = new ArrayList<>();
        for (Long studId : studIdList) {
            daysList.add((noOfDays - StudentAttendanceDao.studMontAbsCnt(startDate, endDate, studId, sqliteDatabase)) + "");
        }
        return daysList;
    }

    private int inSecMonthly(String startDate, String endDate, int secId, int noOfDays) {
        int progress = 0;
        double classStrength = StudentsDao.secTotalStrength(secId, sqliteDatabase);
        double absCnt = StudentAttendanceDao.secMontAbsCnt(startDate, endDate, secId, sqliteDatabase);
        double avg = (((classStrength * noOfDays) - absCnt) / (classStrength * noOfDays)) * 100;
        progress = (int) avg;
        return progress;
    }

    private void initializeList() {
        amrList.clear();
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        studIDList.clear();
        studentIdList.clear();
        studentNameList.clear();
        daysList.clear();
    }

    private void populateList() {
        amrList.clear();
        for (int i = 0; i < studentIdList.size(); i++) {
            amrList.add(new AdapterOverloaded(studentIdList.get(i) + "", studentNameList.get(i), daysList.get(i), progressList.get(i)));
        }
    }

}
