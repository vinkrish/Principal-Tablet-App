package in.principal.attendancefragment;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.json.JSONException;
import org.json.JSONObject;

import in.principal.activity.R;
import in.principal.adapter.AttGraph;
import in.principal.dao.StudentAttendanceDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.DateTracker;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.DateTrackerModel;
import in.principal.util.ReplaceFragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * I would write this class a better way if i've to start over again, optimize it if you can.
 */
public class MonthlyReportStud extends Fragment {
    private Context context;
    private Activity act;
    private SQLiteDatabase sqliteDatabase;
    private JSONObject monObject;
    private JSONObject monthObject;
    private String[] mon;
    private String[] month;
    private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer multiRenderer;
    private static final int SERIES_NR = 2;
    private int classId, classStrength;
    private long studentId;
    private double absentCnt, noOfDays;
    private List<Integer> intMon = new ArrayList<>();
    private List<String> stringMon = new ArrayList<>();
    private List<String> stringMonth = new ArrayList<>();
    private List<Integer> studAbsCnt, studAvgList, totalDays;
    private List<Integer> clasAbsCnt, clasAvgList;
    private List<String> startDateList = new ArrayList<>();
    private List<String> endDateList = new ArrayList<>();
    private ArrayList<AdapterOverloaded> amrList;
    private AttGraph attGraph;
    private ListView lv;
    private ProgressDialog pDialog;
    private TextView daysPresent;
    private ProgressBar pb;
    private LinearLayout layout;

    public MonthlyReportStud() {
        monObject = new JSONObject();
        try {
            monObject.put("0", "Jan");
            monObject.put("1", "Feb");
            monObject.put("2", "Mar");
            monObject.put("3", "Apr");
            monObject.put("4", "May");
            monObject.put("5", "Jun");
            monObject.put("6", "Jul");
            monObject.put("7", "Aug");
            monObject.put("8", "Sep");
            monObject.put("9", "Oct");
            monObject.put("10", "Nov");
            monObject.put("11", "Dec");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        monthObject = new JSONObject();
        try {
            monthObject.put("0", "January");
            monthObject.put("1", "February");
            monthObject.put("2", "March");
            monthObject.put("3", "April");
            monthObject.put("4", "May");
            monthObject.put("5", "June");
            monthObject.put("6", "July");
            monthObject.put("7", "August");
            monthObject.put("8", "September");
            monthObject.put("9", "October");
            monthObject.put("10", "November");
            monthObject.put("11", "December");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.monthly_report_stud, container, false);
        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        amrList = new ArrayList<>();
        pDialog = new ProgressDialog(act);
        lv = (ListView) view.findViewById(R.id.list);
        daysPresent = (TextView) view.findViewById(R.id.studentAttendTotal);
        pb = (ProgressBar) view.findViewById(R.id.studentAttendanceAvg);
        layout = (LinearLayout) view.findViewById(R.id.chart);

        clearList();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        studentId = t.getStudentId();
        classId = t.getClassId();
        String className = t.getClassName();
        String sectionName = t.getSectionName();

        Button classBC = (Button) view.findViewById(R.id.classButton);
        classBC.setText("Class " + className);
        Button secBC = (Button) view.findViewById(R.id.sectionButton);
        secBC.setText("Section " + sectionName);
        secBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new MonthlyReportSection(), getFragmentManager());
            }
        });
        TextView nameText = (TextView) view.findViewById(R.id.studentName);
        String name = StudentsDao.getStudentName(studentId, sqliteDatabase);
        nameText.setText(name);

        new CalledBackLoad().execute();

        attGraph = new AttGraph(context, R.layout.att_graph_list, amrList);
        lv.setAdapter(attGraph);

        return view;

    }

    class CalledBackLoad extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Preparing data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            totalDays = new ArrayList<>();
            String csvSplitBy = "-";

            String line = StudentAttendanceDao.selectFirstAtt(sqliteDatabase);
            String[] data = line.split(csvSplitBy);
            int firstYear = Integer.parseInt(data[0]);
            int firstMonth = Integer.parseInt(data[1]) - 1;
            int firstDay = Integer.parseInt(data[2]);

            String last = StudentAttendanceDao.selectLastAtt(sqliteDatabase);
            String[] data2 = last.split(csvSplitBy);
            int lastYear = Integer.parseInt(data2[0]);
            int lastMonth = Integer.parseInt(data2[1]) - 1;
            int lastDay = Integer.parseInt(data2[2]);

            if (firstYear == lastYear) {
                if (firstMonth == lastMonth) {
                    intMon.add(firstMonth);
                    DateTracker dt = DateTrackerModel.getDateTracker1(firstDay, lastDay, firstMonth, firstYear);
                    startDateList.add(dt.getFirstDate());
                    endDateList.add(dt.getLastDate());
                    totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt.getFirstDate(), dt.getLastDate(), classId, sqliteDatabase));
                } else {
                    intMon.add(firstMonth);
                    DateTracker dt = DateTrackerModel.getDateTracker2(firstDay, firstMonth, firstYear);
                    startDateList.add(dt.getFirstDate());
                    endDateList.add(dt.getLastDate());
                    totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt.getFirstDate(), dt.getLastDate(), classId, sqliteDatabase));
                    firstMonth += 1;
                    while (firstMonth < lastMonth) {
                        intMon.add(firstMonth);
                        DateTracker dt2 = DateTrackerModel.getDateTracker(firstMonth, firstYear);
                        startDateList.add(dt2.getFirstDate());
                        endDateList.add(dt2.getLastDate());
                        totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt2.getFirstDate(), dt2.getLastDate(), classId, sqliteDatabase));
                        firstMonth += 1;
                    }
                    if (firstMonth == lastMonth) {
                        intMon.add(firstMonth);
                        DateTracker dt3 = DateTrackerModel.getDateTracker1(1, lastDay, lastMonth, firstYear);
                        startDateList.add(dt3.getFirstDate());
                        endDateList.add(dt3.getLastDate());
                        totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt3.getFirstDate(), dt3.getLastDate(), classId, sqliteDatabase));
                    }
                }
            } else {
                intMon.add(firstMonth);
                DateTracker dtc = DateTrackerModel.getDateTracker2(firstDay, firstMonth, firstYear);
                startDateList.add(dtc.getFirstDate());
                endDateList.add(dtc.getLastDate());
                totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dtc.getFirstDate(), dtc.getLastDate(), classId, sqliteDatabase));
                firstMonth += 1;
                while (firstMonth < 12) {
                    intMon.add(firstMonth);
                    DateTracker dt2c = DateTrackerModel.getDateTracker(firstMonth, firstYear);
                    startDateList.add(dt2c.getFirstDate());
                    endDateList.add(dt2c.getLastDate());
                    totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt2c.getFirstDate(), dt2c.getLastDate(), classId, sqliteDatabase));
                    firstMonth += 1;
                }

                if (lastMonth == 0) {
                    intMon.add(lastMonth);
                    DateTracker dt = DateTrackerModel.getDateTracker1(1, lastDay, lastMonth, lastYear);
                    startDateList.add(dt.getFirstDate());
                    endDateList.add(dt.getLastDate());
                    totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt.getFirstDate(), dt.getLastDate(), classId, sqliteDatabase));
                } else {
                    int tempMonth = 0;
                    intMon.add(tempMonth);
                    DateTracker dt = DateTrackerModel.getDateTracker2(1, tempMonth, lastYear);
                    startDateList.add(dt.getFirstDate());
                    endDateList.add(dt.getLastDate());
                    totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt.getFirstDate(), dt.getLastDate(), classId, sqliteDatabase));
                    tempMonth += 1;
                    while (tempMonth < lastMonth) {
                        intMon.add(tempMonth);
                        DateTracker dt2 = DateTrackerModel.getDateTracker(tempMonth, lastYear);
                        startDateList.add(dt2.getFirstDate());
                        endDateList.add(dt2.getLastDate());
                        totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt2.getFirstDate(), dt2.getLastDate(), classId, sqliteDatabase));
                        tempMonth += 1;
                    }
                    if (tempMonth == lastMonth) {
                        intMon.add(tempMonth);
                        DateTracker dt3 = DateTrackerModel.getDateTracker1(1, lastDay, lastMonth, lastYear);
                        startDateList.add(dt3.getFirstDate());
                        endDateList.add(dt3.getLastDate());
                        totalDays.add(StudentAttendanceDao.noOfWorkingClassDays(dt3.getFirstDate(), dt3.getLastDate(), classId, sqliteDatabase));
                    }
                }
            }
            stringMon.add("");
            stringMonth.add("");
            for (int i = 0; i < intMon.size(); i++) {
                try {
                    stringMon.add(monObject.getString("" + intMon.get(i)));
                    stringMonth.add(monthObject.getString("" + intMon.get(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mon = stringMon.toArray(new String[stringMon.size()]);
            month = stringMonth.toArray(new String[stringMonth.size()]);

            studAbsCnt = StudentAttendanceDao.studMonthlyAttendance(startDateList, endDateList, studentId, sqliteDatabase);
            clasAbsCnt = StudentAttendanceDao.clasMonthlyAttendance(startDateList, endDateList, classId, sqliteDatabase);
            for (Integer i : studAbsCnt) {
                absentCnt += i;
            }
            for (Integer j : totalDays) {
                noOfDays += j;
            }
            classStrength = StudentsDao.clasTotalStrength(classId, sqliteDatabase);
            studAvg();
            classAvg();


            for (int i = 1; i < month.length; i++) {
                amrList.add(new AdapterOverloaded(month[i], (totalDays.get(i - 1) - studAbsCnt.get(i - 1)) + " / " + totalDays.get(i - 1), studAbsCnt.get(i - 1) + ""));
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            multiRenderer = new XYMultipleSeriesRenderer();
            dataset = getTruitonBarDataset();
            multiRenderer = getTruitonBarRenderer();
            myChartSettings(multiRenderer);
            View mChartView = ChartFactory.getBarChartView(context, dataset, multiRenderer, Type.DEFAULT);
            layout.addView(mChartView);

            double progress = ((noOfDays - absentCnt) / noOfDays) * 100;
            if (progress >= 75) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
            } else if (progress >= 50) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
            } else {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
            }
            pb.setProgress((int) progress);
            daysPresent.setText((int) (noOfDays - absentCnt) + " / " + (int) noOfDays + " Days Present");
            attGraph.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

    private void clearList() {
        amrList.clear();
        startDateList.clear();
        endDateList.clear();
        absentCnt = 0;
        noOfDays = 0;
    }

    private void studAvg() {
        studAvgList = new ArrayList<>();
        for (int i = 0, j = intMon.size(); i < j; i++) {
            double temp = ((double) (totalDays.get(i) - studAbsCnt.get(i)) / (double) totalDays.get(i)) * 100;
            studAvgList.add((int) temp);
        }
    }

    private void classAvg() {
        clasAvgList = new ArrayList<>();
        for (int i = 0, j = intMon.size(); i < j; i++) {
            double temp = (((double) (totalDays.get(i) * classStrength) - clasAbsCnt.get(i)) / (double) (totalDays.get(i) * classStrength)) * 100;
            clasAvgList.add((int) temp);
        }
    }

    private XYMultipleSeriesDataset getTruitonBarDataset() {
        dataset = new XYMultipleSeriesDataset();
        ArrayList<String> legendTitles = new ArrayList<>();
        legendTitles.add("Student");
        legendTitles.add("Class Average");
        for (int i = 0; i < SERIES_NR; i++) {
            CategorySeries series = new CategorySeries(legendTitles.get(i));
            if (i == 0) {
                for (int k = 0, j = intMon.size(); k < j; k++) {
                    series.add(studAvgList.get(k));
                }
            } else {
                for (int k = 0, j = intMon.size(); k < j; k++) {
                    series.add(clasAvgList.get(k));
                }
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    public XYMultipleSeriesRenderer getTruitonBarRenderer() {
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(getResources().getColor(R.color.green));
        multiRenderer.addSeriesRenderer(r);
        r = new SimpleSeriesRenderer();
        r.setColor(getResources().getColor(R.color.class_avg));
        multiRenderer.addSeriesRenderer(r);

        return multiRenderer;
    }

    private void myChartSettings(XYMultipleSeriesRenderer multiRenderer) {

        multiRenderer.setAxisTitleTextSize(16);
        multiRenderer.setChartTitleTextSize(10);
        multiRenderer.setLabelsTextSize(15);
        multiRenderer.setLegendTextSize(15);
        multiRenderer.setMargins(new int[]{20, 40, 15, 10});

        multiRenderer.setXAxisMin(0.5);
        multiRenderer.setXAxisMax(12.5);
        multiRenderer.setYAxisMin(0);
        multiRenderer.setYAxisMax(100);
        multiRenderer.setYLabels(10);

        updateRenderer(multiRenderer);

        multiRenderer.setYLabelsAlign(Align.RIGHT);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setXLabelsColor(Color.GRAY);
        //	renderer.setYLabelsColor(Color.BLACK, 0);
        multiRenderer.setBackgroundColor(Color.WHITE);
        multiRenderer.setMarginsColor(Color.WHITE);
        multiRenderer.setBarSpacing(0.5);
        multiRenderer.setShowGrid(false);
        multiRenderer.setPanEnabled(false);
        //  renderer.setGridColor(Color.GRAY);
        multiRenderer.setXLabels(0); // sets the number of integer labels to appear
    }

    private void updateRenderer(XYMultipleSeriesRenderer renderer) {
        for (int i = 0, j = intMon.size() + 1; i < j; i++) {
            renderer.addXTextLabel(i, mon[i]);
        }
    }

}
