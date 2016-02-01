package in.principal.attendancefragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.principal.activity.R;
import in.principal.dao.ClasDao;
import in.principal.dao.SectionDao;
import in.principal.dao.StudentAttendanceDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Clas;
import in.principal.sqlite.Section;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.ReplaceFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
@SuppressLint("UseSparseArrays")
public class AttendanceClass extends Fragment {
    private Context context;
    private Activity act;
    private AlertDialog alertDialog;
    private int classId;
    private String dateSelected;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private CustomAdapter amrAdapter;
    private List<Section> secList = new ArrayList<>();
    private List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private List<Integer> progressList = new ArrayList<>();
    private int classProgress, index;
    private String className;
    private String[] items;
    private List<Clas> clasList = new ArrayList<>();
    private List<Integer> classIdList = new ArrayList<>();
    private List<String> classNameList = new ArrayList<>();
    private List<Integer> absCountList = new ArrayList<>();
    private SQLiteDatabase sqliteDatabase;
    private List<String> absenteeNameList = new ArrayList<>();
    private ProgressDialog pDialog;
    private Button classBC;
    private TextView classNam, classPercent;
    private ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attendance_class, container, false);
        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(act);

        clearList();

        classBC = (Button) view.findViewById(R.id.classButton);
        classNam = (TextView) view.findViewById(R.id.className);
        pb = (ProgressBar) view.findViewById(R.id.classAvgProgress);
        classPercent = (TextView) view.findViewById(R.id.classpercent);

        RelativeLayout ll = (RelativeLayout) view.findViewById(R.id.datePicker);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Temp t = TempDao.selectTemp(sqliteDatabase);
        classId = t.getClassId();
        className = t.getClassName();
        dateSelected = t.getSelectedDate();

        TextView date = (TextView) view.findViewById(R.id.dat);
        date.setText(dateSelected);

        LinearLayout selecClass = (LinearLayout) view.findViewById(R.id.selectClass);
        selecClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle("Select class");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                        alertDialog.dismiss();
                        Temp t = new Temp();
                        t.setClassId(classIdList.get(index));
                        t.setClassName(classNameList.get(index));
                        TempDao.updateClass(t, sqliteDatabase);
                        ReplaceFragment.replace(new AttendanceClass(), getFragmentManager());
                    }

                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //	progressList = PercentageAttendance.findSectionsAttendance(context, secIdList, dateSelected);
        //	absenteeCntList = sqlHandler.secAbsenteeCnt(secIdList, dateSelected, yesterday, otherday);

        ListView lv = (ListView) view.findViewById(R.id.list);
        amrAdapter = new CustomAdapter(context, R.layout.att_list, amrList);
        lv.setAdapter(amrAdapter);

        new CalledBackLoad().execute();

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Temp t = new Temp();
                t.setSectionId(secIdList.get(pos));
                t.setSectionName(secNameList.get(pos));
                TempDao.updateSection(t, sqliteDatabase);
                ReplaceFragment.replace(new AttendanceSection(), getFragmentManager());
            }
        });

        return view;
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
            items = classNameList.toArray(new String[classNameList.size()]);
            classProgress = findClassAttendance(context, classId, dateSelected);
            absCountList = findSecAbsCount(secIdList, dateSelected);
            populateList();
            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            pDialog.dismiss();
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
        }
    }

    private List<Integer> findSecAbsCount(List<Integer> secIds, String date) {
        List<Integer> absDaysList = new ArrayList<>();
        for (Integer id : secIds) {
            absDaysList.add(StudentAttendanceDao.secAbsentCount(id, date, sqliteDatabase));
        }
        return absDaysList;
    }

    private int findClassAttendance(Context context, int classId, String date) {
        int classProgress = 0;
        double absentCount = 0;
        double totalStrength = StudentsDao.clasTotalStrength(classId, sqliteDatabase);
        absentCount = StudentAttendanceDao.clasAbsentCount(classId, date, sqliteDatabase);
        if (absentCount == 0) {
            absentCount = StudentAttendanceDao.clasDailyMarked(date, classId, sqliteDatabase);
            if (absentCount == -1) {
                absentCount = totalStrength;
            }
        }
        double progress = ((totalStrength - absentCount) / totalStrength) * 100;
        classProgress = (int) progress;
        return classProgress;
    }

    private void clearList() {
        amrList.clear();
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        clasList.clear();
        classIdList.clear();
        classNameList.clear();
        absCountList.clear();
        progressList.clear();
        absenteeNameList.clear();
    }

    private void populateList() {
        for (Integer id : secIdList) {
            StringBuilder sb = new StringBuilder();
            Cursor cur = sqliteDatabase.rawQuery("select S.Name from students S, studentattendance SA where SA.DateAttendance='" + dateSelected + "' and sa.SectionId=" + id + " and S.StudentId=SA.StudentId", null);
            cur.moveToFirst();
            if (cur.getCount() > 0) {
                while (!cur.isAfterLast()) {
                    sb.append("...").append(cur.getString(cur.getColumnIndex("Name")));
                    cur.moveToNext();
                }
                if (sb.length() > 30) {
                    absenteeNameList.add(sb.substring(3, 30) + "...");
                } else {
                    absenteeNameList.add(sb.substring(3));
                }
            } else {
                absenteeNameList.add("-");
            }
            cur.close();
        }

        Map<Integer, Integer> map = new HashMap<>();
        Cursor cp1 = sqliteDatabase.rawQuery("SELECT SectionId,TypeOfLeave, CASE TypeOfLeave " +
                "WHEN 'NA' THEN 100 ELSE (1-cast(count(*) as double)/cast ((Select count(*) from Students where SectionId=A.SectionId ) as double))*100 " +
                "END AS aveg FROM studentattendance A Where DateAttendance='" + dateSelected + "' AND A.ClassId=" + classId + " GROUP BY SectionId", null);
        cp1.moveToFirst();
        while (!cp1.isAfterLast()) {
            map.put(cp1.getInt(cp1.getColumnIndex("SectionId")), cp1.getInt(cp1.getColumnIndex("aveg")));
            cp1.moveToNext();
        }
        cp1.close();
        for (Integer i : secIdList) {
            try {
                int avg = map.get(i);
                progressList.add(avg);
            } catch (NullPointerException e) {
                progressList.add(0);
            }

        }
        amrList.clear();
        for (int i = 0; i < secIdList.size(); i++) {
            amrList.add(new AdapterOverloaded(secNameList.get(i), absenteeNameList.get(i), absCountList.get(i) + "", progressList.get(i)));
        }
    }

    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String csvSplitBy = "-";
            String[] data = dateSelected.split(csvSplitBy);
            int year = Integer.parseInt(data[0]);
            int month = Integer.parseInt(data[1]) - 1;
            int day = Integer.parseInt(data[2]);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            if (view.isShown()) {
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(year, month, day);
                if (GregorianCalendar.getInstance().get(Calendar.YEAR) < cal.get(Calendar.YEAR)) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "Selected future date !");
                } else if (GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH) &&
                        GregorianCalendar.getInstance().get(Calendar.MONTH) <= cal.get(Calendar.MONTH)
                        && GregorianCalendar.getInstance().get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "Selected future date !");
                } else if (Calendar.SUNDAY == cal.get(Calendar.DAY_OF_WEEK)) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "Sundays are not working days");
                } else {
                    TempDao.setThreeAbsDays(year, month, day, sqliteDatabase);
                    ReplaceFragment.replace(new AttendanceClass(), getFragmentManager());
                }
            }
        }
    }

    public class CustomAdapter extends ArrayAdapter<AdapterOverloaded> {
        private int resource;
        private Context context;
        private ArrayList<AdapterOverloaded> data = new ArrayList<>();
        private LayoutInflater inflater = null;

        public CustomAdapter(Context context, int resource, ArrayList<AdapterOverloaded> listArray) {
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
                holder.txt1 = (TextView) row.findViewById(R.id.idx);
                holder.txt2 = (TextView) row.findViewById(R.id.txt);
                holder.txt3 = (TextView) row.findViewById(R.id.score);
                holder.pb = (ProgressBar) row.findViewById(R.id.avgProgress);
                holder.percentage = (TextView) row.findViewById(R.id.percent);
                row.setTag(holder);
            } else holder = (RecordHolder) row.getTag();

            if (position % 2 == 0) {
                row.setBackgroundResource(R.drawable.list_selector1);
            } else {
                row.setBackgroundResource(R.drawable.list_selector2);
            }

            AdapterOverloaded listItem = data.get(position);
            holder.txt1.setText(listItem.getText1());
            holder.txt2.setText(listItem.getText2());
            holder.txt3.setText(listItem.getText3());

            if (listItem.getInt1() >= 75) {
                holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
            } else if (listItem.getInt1() >= 50) {
                holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
            } else {
                holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
            }
            holder.pb.setProgress(listItem.getInt1());
            holder.percentage.setText(String.valueOf(listItem.getInt1() + "%"));

            row.setTag(holder);
            holder.txt2.setOnClickListener(nameClickListener);

            return row;
        }

        private OnClickListener nameClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView mListView = (ListView) v.getParent().getParent();
                final int position = mListView.getPositionForView((View) v.getParent());
                List<String> nameList = new ArrayList<>();
                Cursor cur = sqliteDatabase.rawQuery("select S.Name from students S, studentattendance SA where SA.DateAttendance='" + dateSelected + "' and sa.SectionId=" + secIdList.get(position) + " and " +
                        "S.StudentId=SA.StudentId", null);
                cur.moveToFirst();
                if (cur.getCount() > 0) {
                    while (!cur.isAfterLast()) {
                        nameList.add(cur.getString(cur.getColumnIndex("Name")));
                        cur.moveToNext();
                    }
                } else {
                    nameList.add("-");
                }
                cur.close();

                String[] items2 = nameList.toArray(new String[nameList.size()]);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(act);
                builder2.setTitle("List of Absentees");
                builder2.setItems(items2, null);
                builder2.show();
            }
        };

        class RecordHolder {
            TextView txt1;
            TextView txt2;
            TextView txt3;
            ProgressBar pb;
            TextView percentage;
        }
    }
}
