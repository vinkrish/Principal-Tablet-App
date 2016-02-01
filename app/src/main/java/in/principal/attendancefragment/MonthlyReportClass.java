package in.principal.attendancefragment;

import in.principal.activity.R;
import in.principal.dao.ClasDao;
import in.principal.dao.SectionDao;
import in.principal.dao.StudentAttendanceDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Clas;
import in.principal.sqlite.DateTracker;
import in.principal.sqlite.Section;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.DateTrackerModel;
import in.principal.util.ReplaceFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
public class MonthlyReportClass extends Fragment {
    private AlertDialog alertDialog;
    private Context context;
    private Activity act;
    private SqlDbHelper sqlHandler;
    private MonthlyAdapter amrAdapter;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private List<Clas> clasList = new ArrayList<>();
    private List<Integer> classIdList = new ArrayList<>();
    private List<String> classNameList = new ArrayList<>();
    private List<Integer> progressList;
    private int classProgress, index, idx;
    private List<Section> secList = new ArrayList<>();
    private List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private List<String> absenteeNameList = new ArrayList<>();
    private String[] item;
    private String[] items = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private String firstDate, lastDate, className;
    private int noOfDays, classId, savedMonth;
    private static SQLiteDatabase sqliteDatabase;
    private ProgressDialog pDialog;
    private TextView selectmonthly, classPercent, classNam;
    private Button classBC;
    private ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.monthly_report_class, container, false);
        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqlHandler = AppGlobal.getSqlDbHelper();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(act);

        RelativeLayout ll = (RelativeLayout) view.findViewById(R.id.datePicker);
        LinearLayout selecClass = (LinearLayout) view.findViewById(R.id.selectClass);
        selectmonthly = (TextView) view.findViewById(R.id.monthly);
        classBC = (Button) view.findViewById(R.id.classButton);
        classNam = (TextView) view.findViewById(R.id.className);
        pb = (ProgressBar) view.findViewById(R.id.classAvgProgress);
        ListView lv = (ListView) view.findViewById(R.id.list);
        classPercent = (TextView) view.findViewById(R.id.classpercent);

        amrAdapter = new MonthlyAdapter(context, R.layout.amr_list, amrList);
        lv.setAdapter(amrAdapter);

        init();

        selecClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle("Select class");
                builder.setSingleChoiceItems(item, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                        alertDialog.dismiss();
                        Temp t = new Temp();
                        t.setClassId(classIdList.get(index));
                        t.setClassName(classNameList.get(index));
                        TempDao.updateClass(t, sqliteDatabase);
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
                Temp t = new Temp();
                t.setSectionId(secIdList.get(pos));
                t.setSectionName(secNameList.get(pos));
                TempDao.updateSection(t, sqliteDatabase);
                ReplaceFragment.replace(new MonthlyReportSection(), getFragmentManager());
            }
        });


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
        intializeList();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        classId = t.getClassId();
        className = t.getClassName();

        DateTracker dt = sqlHandler.selectDateTracker();
        firstDate = dt.getFirstDate();
        lastDate = dt.getLastDate();
        //	noOfDays = dt.getNoOfDays();
        savedMonth = dt.getSelectedMonth();

        noOfDays = StudentAttendanceDao.noOfWorkingClassDays(firstDate, lastDate, classId, sqliteDatabase);

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

            clasList = ClasDao.selectClas(sqliteDatabase);
            for (Clas c : clasList) {
                classIdList.add(c.getClassId());
                classNameList.add(c.getClassName());
            }
            item = classNameList.toArray(new String[classNameList.size()]);
            classProgress = inClassMonthly(firstDate, lastDate, classId, noOfDays);
            progressList = secMonthly(firstDate, lastDate, secIdList);
            absenteeNameList = StudentAttendanceDao.secMontAbsentee(firstDate, lastDate, secIdList, noOfDays, sqliteDatabase);

            populateList();

            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            selectmonthly.setText(items[savedMonth]);
            classBC.setText("Class " + className);
            classNam.setText(className);
            if (classProgress >= 75) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
            } else if (classProgress >= 50) {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
            } else {
                pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
            }
            pb.setProgress(classProgress);
            classPercent.setText(classProgress + "%");
            amrAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }

    private int inClassMonthly(String startDate, String endDate, int classId, int noOfDays) {
        int progress = 0;
        double classStrength = StudentsDao.clasTotalStrength(classId, sqliteDatabase);
        double absCnt = StudentAttendanceDao.clasMontAbsCnt(startDate, endDate, classId, sqliteDatabase);
        double avg = (((classStrength * noOfDays) - absCnt) / (classStrength * noOfDays)) * 100;
        progress = (int) avg;
        return progress;
    }

    private List<Integer> secMonthly(String startDate, String endDate, List<Integer> secIdList) {
        List<Integer> progress = new ArrayList<>();
        for (Integer secId : secIdList) {
            noOfDays = StudentAttendanceDao.noOfWorkingSecDays(startDate, endDate, secId, sqliteDatabase);
            double classStrength = StudentsDao.secTotalStrength(secId, sqliteDatabase);
            double absCnt = StudentAttendanceDao.secMontAbsCnt(startDate, endDate, secId, sqliteDatabase);
            double avg = (((classStrength * noOfDays) - absCnt) / (classStrength * noOfDays)) * 100;
            progress.add((int) avg);
        }
        return progress;
    }

    private void intializeList() {
        amrList.clear();
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        clasList.clear();
        classIdList.clear();
        classNameList.clear();
        absenteeNameList.clear();
    }

    private void populateList() {
        amrList.clear();
        for (int i = 0; i < secIdList.size(); i++) {
            amrList.add(new AdapterOverloaded(secNameList.get(i), absenteeNameList.get(i), progressList.get(i)));
        }
    }

    public class MonthlyAdapter extends ArrayAdapter<AdapterOverloaded> {
        private int resource;
        private Context context;
        private ArrayList<AdapterOverloaded> data = new ArrayList<>();
        private LayoutInflater inflater = null;

        public MonthlyAdapter(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
            super(context, resource, listArray);
            this.context = context;
            this.resource = resource;
            this.data = listArray;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RecordHolder holder;

            if (row == null) {
                row = inflater.inflate(resource, parent, false);

                holder = new RecordHolder();
                holder.idx = (TextView) row.findViewById(R.id.idx);
                holder.txtAbsentee = (TextView) row.findViewById(R.id.txt);
                holder.pb = (ProgressBar) row.findViewById(R.id.avgProgress);
                holder.percentage = (TextView) row.findViewById(R.id.percent);
                row.setTag(holder);
            } else holder = (RecordHolder) row.getTag();

            if (position % 2 == 0)
                row.setBackgroundResource(R.drawable.list_selector1);
            else
                row.setBackgroundResource(R.drawable.list_selector2);

            AdapterOverloaded listItem = data.get(position);
            holder.idx.setText(listItem.getText1());
            holder.txtAbsentee.setText(listItem.getText3());

            if (listItem.getInt2() >= 75) {
                holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
            } else if (listItem.getInt2() >= 50) {
                holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
            } else {
                holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
            }
            holder.pb.setProgress(listItem.getInt2());
            holder.percentage.setText(String.valueOf(listItem.getInt2() + "%"));

            holder.txtAbsentee.setOnClickListener(nameClickListener);

            return row;
        }

        private OnClickListener nameClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView mListView = (ListView) v.getParent().getParent();
                final int position = mListView.getPositionForView((View) v.getParent());
                int sectionId = secIdList.get(position);
                noOfDays = StudentAttendanceDao.noOfWorkingSecDays(firstDate, lastDate, sectionId, sqliteDatabase);
                List<Integer> studIdList = new ArrayList<>();
                List<Integer> countList = new ArrayList<>();
                List<String> nameCountList = new ArrayList<>();
                List<Integer> perCountList = new ArrayList<>();
                String s = "";
                boolean flag = false;
                boolean flg = false;
                flag = false;
                flg = false;
                studIdList.clear();
                perCountList.clear();
                countList.clear();
                String sql = "SELECT StudentId,(count(*)/" + noOfDays + ".0)*100 as perCount,count(*) as count FROM studentattendance where DateAttendance<='" + lastDate + "' and " +
                        "DateAttendance>='" + firstDate + "' and SectionId=" + sectionId + " and TypeOfLeave!='NA' group by StudentId order by 2 desc";
                Cursor c = sqliteDatabase.rawQuery(sql, null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
                        perCountList.add(c.getInt(c.getColumnIndex("perCount")));
                        countList.add(c.getInt(c.getColumnIndex("count")));
                        c.moveToNext();
                    }
                } else {
                    //	studIdList.add(0);
                    flag = true;
                }
                c.close();

                if (flag) {
                    nameCountList.add("-");
                } else {
                    for (int i = 0; i < countList.size(); i++) {
                        if (perCountList.get(i) > 20) {
                            flg = true;
                            String sql1 = "select Name from Students where StudentId=" + studIdList.get(i);
                            Cursor c1 = sqliteDatabase.rawQuery(sql1, null);
                            c1.moveToFirst();
                            while (!c1.isAfterLast()) {
                                s = c1.getString(c1.getColumnIndex("Name"));
                                c1.moveToNext();
                            }
                            c1.close();
                            StringBuilder sb = new StringBuilder();
                            sb.append("...").append(s + "(" + countList.get(i) + ")");
                            if (sb.length() > 33) {
                                nameCountList.add(sb.substring(3, 33) + "...");
                            } else {
                                nameCountList.add(sb.substring(3));
                            }
                        }
                    }
                    if (!flg) {
                        nameCountList.add("-");
                    }
                }
                String[] items = nameCountList.toArray(new String[nameCountList.size()]);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(act);
                builder2.setTitle("List of Longest Absentees");
                builder2.setItems(items, null);
                builder2.show();
            }
        };

        class RecordHolder {
            TextView idx;
            TextView txtAbsentee;
            ProgressBar pb;
            TextView percentage;
        }

    }

}
