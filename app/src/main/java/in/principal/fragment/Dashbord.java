package in.principal.fragment;

import in.principal.activity.R;
import in.principal.adapter.CustomAlertAdapter;
import in.principal.adapter.DashAdapt1;
import in.principal.adapter.DashAdapt2;
import in.principal.attendancefragment.AttendanceClass;
import in.principal.dao.ClasDao;
import in.principal.dao.StudentAttendanceDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Clas;
import in.principal.sqlite.DashObject;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.DateTrackerModel;
import in.principal.util.PKGenerator;
import in.principal.util.ReplaceFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
@SuppressLint("UseSparseArrays")
public class Dashbord extends Fragment {
    private Context context;
    private Activity act;
    private String lastDate;
    private DashAdapt1 dashAdapt1;
    private DashAdapt2 dashAdapt2;
    private ArrayList<AdapterOverloaded> amrList1 = new ArrayList<>(), amrList2 = new ArrayList<>();
    private List<Clas> clasList = new ArrayList<>();
    private List<Integer> classIdList = new ArrayList<>();
    private List<String> classNameList = new ArrayList<>();
    private List<String> absenteeNameList = new ArrayList<>();
    private static SQLiteDatabase sqliteDatabase;
    private ArrayList<DashObject> dashArrayGrid = new ArrayList<>();
    GridView gridView;
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashbord, container, false);
        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        gridView = (GridView) view.findViewById(R.id.gridView);
        CircleAdapter cA = new CircleAdapter(context, R.layout.dash_grid, dashArrayGrid);
        pDialog = new ProgressDialog(act);

        clearList();
        ListView lv1 = (ListView) view.findViewById(R.id.list1);
        ListView lv2 = (ListView) view.findViewById(R.id.list2);

        lastDate = getToday();

        clasList = ClasDao.selectClas(sqliteDatabase);
        for (Clas c : clasList) {
            classIdList.add(c.getClassId());
            classNameList.add(c.getClassName());
        }

        dashAdapt1 = new DashAdapt1(context, R.layout.dash_list1, amrList1);
        lv1.setAdapter(dashAdapt1);
        dashAdapt2 = new DashAdapt2(context, R.layout.dash_list2, amrList2);
        lv2.setAdapter(dashAdapt2);

        updateView();
        gridView.setAdapter(cA);

        new CalledBackLoad().execute();

        lv1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Temp t = new Temp();
                t.setClassId(classIdList.get(pos));
                t.setClassName(classNameList.get(pos));
                TempDao.updateClass(t, sqliteDatabase);
                ReplaceFragment.replace(new AttendanceClass(), getFragmentManager());
            }
        });

		/*for (Map.Entry<Integer, List<String>> entry : nameMap.entrySet())
        {
			int a = entry.getKey();
			List<String> slist = entry.getValue();
			for(String sli: slist){
				lis1.add(a);
				lis2.add(sli);
			}
		}*/

        lv2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                List<String> nameList = new ArrayList<>();
                Cursor cur = sqliteDatabase.rawQuery("select S.Name, S2.SectionName from students S,section S2, studentattendance SA where SA.DateAttendance='" + lastDate + "' and sa.ClassId=" + classIdList.get(pos) + " and " +
                        "S.StudentId=SA.StudentId and S.SectionId=S2.SectionId", null);
                cur.moveToFirst();
                if (cur.getCount() > 0) {
                    while (!cur.isAfterLast()) {
                        nameList.add(cur.getString(cur.getColumnIndex("Name")) + "   (" + cur.getString(cur.getColumnIndex("SectionName")) + " - sec)");
                        cur.moveToNext();
                    }
                } else {
                    nameList.add("-");
                }
                cur.close();
                String[] items = nameList.toArray(new String[nameList.size()]);
                AlertDialog.Builder builder2 = new AlertDialog.Builder(act);
                builder2.setTitle("List of Absentees" + "  (" + classNameList.get(pos) + " - class)");
                builder2.setItems(items, null);
                builder2.show();
            }
        });

        return view;
    }

    private void updateView() {
        List<Integer> secIdList = new ArrayList<>();

        Cursor cur = sqliteDatabase.rawQuery("select count(*) as count from section", null);
        cur.moveToFirst();
        int sectionCount = cur.getInt(cur.getColumnIndex("count"));
        cur.close();

        Cursor c = sqliteDatabase.rawQuery("SELECT distinct SectionId from studentattendance where DateAttendance='" + getToday() + "'", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            secIdList.add(c.getInt(c.getColumnIndex("SectionId")));
            c.moveToNext();
        }
        c.close();

        int presentStrength = DateTrackerModel.getTotalStudents(context, secIdList);
        int totalStrength = DateTrackerModel.getTotalStudents(context);
        if (secIdList.size() > 0) {
            int totalAbsent = DateTrackerModel.getAbsentCount(context, getToday());
            int totalPresent = presentStrength - totalAbsent;
            int presentPercent = (int) (((double) totalPresent / (double) totalStrength) * 3.6 * 100);
            dashArrayGrid.add(new DashObject(presentPercent, totalPresent + "/" + totalStrength, "Present"));
        } else {
            dashArrayGrid.add(new DashObject(0, 0 + "/" + totalStrength, "Present"));
        }
        int pendingCount = sectionCount - secIdList.size();
        int pendingPer = (int) (((double) pendingCount / (double) sectionCount) * 3.6 * 100);
        dashArrayGrid.add(new DashObject(pendingPer, (sectionCount - secIdList.size()) + "/" + sectionCount, "Pending Classes"));
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
            populateList1();
            populateList2();
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            dashAdapt1.notifyDataSetChanged();
            dashAdapt2.notifyDataSetChanged();
        }
    }

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();
        return dateFormat.format(today);
    }

    private void clearList() {
        dashArrayGrid.clear();
        amrList1.clear();
        amrList2.clear();
        clasList.clear();
        classIdList.clear();
        classNameList.clear();
        absenteeNameList.clear();
    }

    private void populateList1() {
        List<Integer> progressList = findClasssAttendance(classIdList, lastDate);
        amrList1.clear();
        for (int i = 0; i < clasList.size(); i++) {
            amrList1.add(new AdapterOverloaded(PKGenerator.trim(0, 9, classNameList.get(i)), progressList.get(i)));
        }
    }

    private List<Integer> findClasssAttendance(List<Integer> classIds, String date) {
        List<Integer> classWiseProgress = new ArrayList<>();
        for (Integer id : classIds) {
            double absentCount = 0;
            double totalStrength = StudentsDao.clasTotalStrength(id, sqliteDatabase);
            absentCount = StudentAttendanceDao.clasAbsentCount(id, date, sqliteDatabase);
            if (absentCount == 0) {
                absentCount = StudentAttendanceDao.clasDailyMarked(date, id, sqliteDatabase);
                if (absentCount == -1) {
                    absentCount = totalStrength;
                }
            }
            double progress = ((totalStrength - absentCount) / totalStrength) * 100;
            classWiseProgress.add((int) progress);
        }
        return classWiseProgress;
    }

    private void populateList2() {
        for (Integer id : classIdList) {
            StringBuilder sb = new StringBuilder();
            Cursor cur = sqliteDatabase.rawQuery("select S.Name from students S, studentattendance SA where SA.DateAttendance='" + lastDate + "' and sa.ClassId=" + id + " and S.StudentId=SA.StudentId", null);
            cur.moveToFirst();
            if (cur.getCount() > 0) {
                while (!cur.isAfterLast()) {
                    sb.append("...").append(cur.getString(cur.getColumnIndex("Name")));
                    cur.moveToNext();
                }
                if (sb.length() > 25) {
                    absenteeNameList.add(sb.substring(3, 25) + "...");
                } else {
                    absenteeNameList.add(sb.substring(3));
                }
            } else {
                absenteeNameList.add("-");
            }
            cur.close();
        }
        amrList2.clear();
        for (int i = 0; i < clasList.size(); i++) {
            amrList2.add(new AdapterOverloaded(PKGenerator.trim(0, 9, classNameList.get(i)), absenteeNameList.get(i)));
        }
    }

    public class CircleAdapter extends ArrayAdapter<DashObject> {
        Context context;
        int layoutResourceId;
        ArrayList<DashObject> data = new ArrayList<>();
        protected ListView mListView;

        public CircleAdapter(Context context, int layoutResourceId, ArrayList<DashObject> gridArray) {
            super(context, layoutResourceId, gridArray);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.data = gridArray;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RecordHolder holder;

            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new RecordHolder();
                holder.outOf = (TextView) row.findViewById(R.id.text1);
                holder.str = (TextView) row.findViewById(R.id.text2);
                row.setTag(holder);

            } else {
                holder = (RecordHolder) row.getTag();
            }

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            FrameLayout fl = (FrameLayout) row.findViewById(R.id.fl);

            DashObject gridItem = data.get(position);
            holder.outOf.setText(gridItem.getOutOf());
            holder.str.setText(gridItem.getStr());
            SampleView sV = new SampleView(context, gridItem.getProgressInt(), position);
            fl.addView(sV, layoutParams);

            sV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewClickListener(position);
                }
            });
            return row;
        }

        public class RecordHolder {
            TextView outOf;
            TextView str;
        }

        public void viewClickListener(int position) {
            if (position == 1) {
                Cursor cp = sqliteDatabase.rawQuery("select C.ClassName , S.SectionName from section S, class C where S.ClassId=C.ClassId and S.SectionId not in " +
                        "(select distinct SectionId from studentattendance where DateAttendance='" + getToday() + "') order by ClassName", null);
                List<String> pendClasSec = new ArrayList<>();
                cp.moveToFirst();
                while (!cp.isAfterLast()) {
                    pendClasSec.add(cp.getString(cp.getColumnIndex("ClassName")) + " - " + cp.getString(cp.getColumnIndex("SectionName")));
                    cp.moveToNext();
                }
                cp.close();
                String[] pendItems = pendClasSec.toArray(new String[pendClasSec.size()]);
//				AlertDialog.Builder builder = new AlertDialog.Builder(act);
//				builder.setTitle("Pending Classes");
//				builder.setPositiveButton("Ok", null);
//				builder.setItems(pendItems, null);
//				builder.show();
                AlertDialog.Builder myDialog = new AlertDialog.Builder(act);
                final ListView listview = new ListView(act);
                LinearLayout layout = new LinearLayout(act);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(listview);
                myDialog.setView(layout);
                myDialog.setTitle("Pending Classes");
                CustomAlertAdapter arrayAdapter = new CustomAlertAdapter(act, Arrays.asList(pendItems));
                listview.setAdapter(arrayAdapter);
                AlertDialog myalertDialog = null;
                myalertDialog = myDialog.show();
                myalertDialog.getWindow().setLayout(400, 700);
            }
        }

        private class SampleView extends View {
            Paint p, defaultPaint;
            RectF rectF1;
            int localInt;
            int posi;

            public SampleView(Context context, int i, int pos) {
                super(context);
                setFocusable(true);
                localInt = i;
                posi = pos;
                init();
            }

            public void init() {
                p = new Paint();
                defaultPaint = new Paint();
                defaultPaint.setAntiAlias(true);
                defaultPaint.setStyle(Paint.Style.STROKE);
                defaultPaint.setStrokeWidth(7);
                Resources res = getResources();
                int defalt = res.getColor(R.color.defalt);
                defaultPaint.setColor(defalt);
                rectF1 = new RectF(10, 10, 120, 120);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                p.setAntiAlias(true);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(7);

                if (posi == 0) {
                    if (localInt >= 270)
                        p.setColor(getResources().getColor(R.color.green));
                    else if (localInt >= 180)
                        p.setColor(getResources().getColor(R.color.orange));
                    else if (localInt > 0)
                        p.setColor(getResources().getColor(R.color.red));
                } else {
                    if (localInt >= 270)
                        p.setColor(getResources().getColor(R.color.red));
                    else if (localInt >= 180)
                        p.setColor(getResources().getColor(R.color.orange));
                    else if (localInt > 0)
                        p.setColor(getResources().getColor(R.color.green));
                }
                canvas.drawArc(rectF1, 0, 360, false, defaultPaint);
                canvas.drawArc(rectF1, 270, Float.parseFloat(localInt + ""), false, p);
            }

        }

    }
}
