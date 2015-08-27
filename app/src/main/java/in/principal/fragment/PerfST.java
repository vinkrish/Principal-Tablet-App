package in.principal.fragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.Capitalize;
import in.principal.adapter.PerfStAdapter;
import in.principal.dao.ClasDao;
import in.principal.dao.SectionDao;
import in.principal.dao.SlipTesttDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.DashObject;
import in.principal.sqlite.SlipTestt;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PerfST extends Fragment {
    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private List<LayoutParams> lp;
    private ListView lv;
    private long slipTestId;
    private int classId, sectionId, subjectId, good, averag, improve, maximumMark, schoolId, teacherId;
    private String className, secName, sliptestName;
    private PerfStAdapter amrAdapter;
    private TextView rollNoTv, nameTv, scoreTv, avgTv;
    private List<Integer> studIdList = new ArrayList<>();
    private List<Integer> rollNoList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Integer> progressList = new ArrayList<>();
    private List<String> markMaxList = new ArrayList<>();
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private boolean ascDescFlag;
    private ImageView rollFlag, nameFlag, scoreFlag, avgFlag;
    private ArrayList<DashObject> dashArrayGrid1 = new ArrayList<>();
    private CircleAdapter cA1;
    private GridView gridView1;
    private ArrayList<DashObject> dashArrayGrid2 = new ArrayList<>();
    private CircleAdapter2 cA2;
    private GridView gridView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.perf_st, container, false);
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        gridView1 = (GridView) view.findViewById(R.id.gridView1);
        cA1 = new CircleAdapter(context, R.layout.gam_grid, dashArrayGrid1);
        gridView2 = (GridView) view.findViewById(R.id.gridView2);
        cA2 = new CircleAdapter2(context, R.layout.gam_grid, dashArrayGrid2);

        clearList();

        rollNoTv = (TextView) view.findViewById(R.id.rollNoSort);
        nameTv = (TextView) view.findViewById(R.id.nameSort);
        scoreTv = (TextView) view.findViewById(R.id.scoreSort);
        avgTv = (TextView) view.findViewById(R.id.avgSort);
        lv = (ListView) view.findViewById(R.id.list);

        rollFlag = (ImageView) view.findViewById(R.id.rollFlag);
        nameFlag = (ImageView) view.findViewById(R.id.nameFlag);
        scoreFlag = (ImageView) view.findViewById(R.id.scoreFlag);
        avgFlag = (ImageView) view.findViewById(R.id.avgFlag);

        final ObjectAnimator rotateRoll = ObjectAnimator.ofFloat(rollFlag, View.ROTATION, 360);
        rotateRoll.setRepeatCount(1);
        rotateRoll.setRepeatMode(ValueAnimator.REVERSE);
        final ObjectAnimator rotateName = ObjectAnimator.ofFloat(nameFlag, View.ROTATION, 360);
        rotateName.setRepeatCount(1);
        rotateName.setRepeatMode(ValueAnimator.REVERSE);
        final ObjectAnimator rotateScore = ObjectAnimator.ofFloat(scoreFlag, View.ROTATION, 360);
        rotateScore.setRepeatCount(1);
        rotateScore.setRepeatMode(ValueAnimator.REVERSE);
        final ObjectAnimator rotateAvg = ObjectAnimator.ofFloat(avgFlag, View.ROTATION, 360);
        rotateAvg.setRepeatCount(1);
        rotateAvg.setRepeatMode(ValueAnimator.REVERSE);

        Temp t = TempDao.selectTemp(sqliteDatabase);
        schoolId = t.getSchoolId();
        slipTestId = t.getSlipTestId();

        SlipTestt st = SlipTesttDao.selectSlipTest(slipTestId, sqliteDatabase);
        classId = st.getClassId();
        sectionId = st.getSectionId();
        subjectId = st.getSubjectId();
        className = ClasDao.getClassName(classId, sqliteDatabase);
        secName = SectionDao.getSecName(sectionId, sqliteDatabase);

        TempDao.updateSubjectId(subjectId, sqliteDatabase);
        Temp t1 = new Temp();
        t1.setClassId(classId);
        t1.setClassName(className);
        TempDao.updateClass(t1, sqliteDatabase);
        Temp t2 = new Temp();
        t2.setSectionId(sectionId);
        t2.setSectionName(secName);
        TempDao.updateSection(t2, sqliteDatabase);

        Cursor cc = sqliteDatabase.rawQuery("select TeacherId,Name from teacher where TeacherId=(select TeacherId from subjectteacher where SectionId=" + sectionId + " and SubjectId=" + subjectId + ")", null);
        cc.moveToFirst();
        while (!cc.isAfterLast()) {
            teacherId = cc.getInt(cc.getColumnIndex("TeacherId"));
            cc.moveToNext();
        }
        cc.close();

        TempDao.updateTeacherId(teacherId, sqliteDatabase);

        Button perfClas = (Button) view.findViewById(R.id.perfClass);
        perfClas.setText("Class " + className);
        Button perfSe = (Button) view.findViewById(R.id.perfSec);
        perfSe.setText("Section " + secName);

        sliptestName = Capitalize.capitalThis(SlipTesttDao.selectSlipTestName(slipTestId, sqliteDatabase));
        TextView stInfo = (TextView) view.findViewById(R.id.stinfo);
        if (sliptestName.length() > 43) {
            StringBuilder sb = new StringBuilder(sliptestName.substring(0, 40));
            stInfo.setText(sb.append("..."));
        } else stInfo.setText(sliptestName);

        TextView subj = (TextView) view.findViewById(R.id.subject_info);
        subj.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase));

        int progres = (int) (((Double) st.getAverageMark() / Double.parseDouble(st.getMaximumMark() + "")) * 100);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.stAvgProgress);
        if (progres >= 75)
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
        else if (progres >= 50)
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
        else
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
        pb.setProgress(progres);
        TextView pecent = (TextView) view.findViewById(R.id.percent);
        pecent.setText(progres + "%");

        lp = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            lp.add(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        populateListView();

        scoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
                rollFlag.setVisibility(View.GONE);
                nameFlag.setVisibility(View.GONE);
                scoreFlag.setVisibility(View.VISIBLE);
                avgFlag.setVisibility(View.GONE);
                rotateScore.start();
                sortAvg();
            }
        });
        avgTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
                rollFlag.setVisibility(View.GONE);
                nameFlag.setVisibility(View.GONE);
                scoreFlag.setVisibility(View.GONE);
                avgFlag.setVisibility(View.VISIBLE);
                rotateAvg.start();
                sortAvg();
            }
        });
        nameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
                rollFlag.setVisibility(View.GONE);
                nameFlag.setVisibility(View.VISIBLE);
                scoreFlag.setVisibility(View.GONE);
                avgFlag.setVisibility(View.GONE);
                rotateName.start();
                sortName();
            }
        });
        rollNoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
                rollFlag.setVisibility(View.VISIBLE);
                nameFlag.setVisibility(View.GONE);
                scoreFlag.setVisibility(View.GONE);
                avgFlag.setVisibility(View.GONE);
                rotateRoll.start();
                sortRollNo();
            }
        });
        return view;
    }

    private void clearList() {
        dashArrayGrid1.clear();
        dashArrayGrid2.clear();
        studIdList.clear();
        amrList.clear();
        rollNoList.clear();
        nameList.clear();
        progressList.clear();
        markMaxList.clear();
        good = 0;
        averag = 0;
        improve = 0;
    }

    private void populateListView() {
        Cursor c = sqliteDatabase.rawQuery("select C.MaximumMark,B.Mark,A.StudentId, A.RollNoInClass, A.Name, case B.Mark when '-1' then 0 else " +
                "(CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, sliptestmark_" + schoolId + " B, sliptest C where B.SlipTestId=" + slipTestId +
                " and C.SlipTestId=" + slipTestId + " and A.StudentId=B.StudentId order by A.RollNoInClass", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            maximumMark = c.getInt(c.getColumnIndex("MaximumMark"));
            studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
            rollNoList.add(c.getInt(c.getColumnIndex("RollNoInClass")));
            nameList.add(c.getString(c.getColumnIndex("Name")));
            markMaxList.add(c.getInt(c.getColumnIndex("Mark")) + "");
            progressList.add(c.getInt(c.getColumnIndex("avg")));
            c.moveToNext();
        }
        c.close();
        scoreTv.setText("Score (" + maximumMark + ")");

        for (Integer i : progressList) {
            if (i >= 75) good += 1;
            else if (i >= 50) averag += 1;
            else improve += 1;
        }
        dashArrayGrid1.add(new DashObject(0, "", ""));
        dashArrayGrid1.add(new DashObject(0, "", ""));
        dashArrayGrid1.add(new DashObject(0, "", ""));
        gridView1.setAdapter(cA1);
        dashArrayGrid2.add(new DashObject(0, good + "", "Good"));
        dashArrayGrid2.add(new DashObject(0, averag + "", "Average"));
        dashArrayGrid2.add(new DashObject(0, improve + "", "Must Improve"));
        gridView2.setAdapter(cA2);

        for (int i = 0, j = rollNoList.size(); i < j; i++)
            amrList.add(new AdapterOverloaded(rollNoList.get(i) + "", nameList.get(i), markMaxList.get(i), progressList.get(i)));

        amrAdapter = new PerfStAdapter(context, R.layout.ps_list, amrList);
        lv.setAdapter(amrAdapter);
    }

    private void sortRollNo() {
        Cursor c1 = sqliteDatabase.rawQuery("select C.MaximumMark,B.Mark,A.StudentId, A.RollNoInClass, A.Name, case B.Mark when '-1' then 0 else " +
                "(CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, sliptestmark_" + schoolId + " B, sliptest C where B.SlipTestId=" + slipTestId +
                " and C.SlipTestId=" + slipTestId + " and A.StudentId=B.StudentId order by A.RollNoInClass", null);
        c1.moveToFirst();
        while (!c1.isAfterLast()) {
            studIdList.add(c1.getInt(c1.getColumnIndex("StudentId")));
            rollNoList.add(c1.getInt(c1.getColumnIndex("RollNoInClass")));
            nameList.add(c1.getString(c1.getColumnIndex("Name")));
            markMaxList.add(c1.getInt(c1.getColumnIndex("Mark")) + "");
            progressList.add(c1.getInt(c1.getColumnIndex("avg")));
            c1.moveToNext();
        }
        c1.close();
        for (int i = 0, j = rollNoList.size(); i < j; i++)
            amrList.add(new AdapterOverloaded(rollNoList.get(i) + "", nameList.get(i), markMaxList.get(i), progressList.get(i)));

        amrAdapter.notifyDataSetChanged();
    }

    private void sortName() {
        Cursor c1 = sqliteDatabase.rawQuery("select C.MaximumMark,B.Mark,A.StudentId, A.RollNoInClass, A.Name, case B.Mark when '-1' then 0 else " +
                "(CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, sliptestmark_" + schoolId + " B, sliptest C where B.SlipTestId=" + slipTestId +
                " and C.SlipTestId=" + slipTestId + " and A.StudentId=B.StudentId order by A.Name", null);
        c1.moveToFirst();
        while (!c1.isAfterLast()) {
            studIdList.add(c1.getInt(c1.getColumnIndex("StudentId")));
            rollNoList.add(c1.getInt(c1.getColumnIndex("RollNoInClass")));
            nameList.add(c1.getString(c1.getColumnIndex("Name")));
            markMaxList.add(c1.getInt(c1.getColumnIndex("Mark")) + "");
            progressList.add(c1.getInt(c1.getColumnIndex("avg")));
            c1.moveToNext();
        }
        c1.close();
        for (int i = 0, j = rollNoList.size(); i < j; i++)
            amrList.add(new AdapterOverloaded(rollNoList.get(i) + "", nameList.get(i), markMaxList.get(i), progressList.get(i)));

        amrAdapter.notifyDataSetChanged();
    }

    private void sortAvg() {
        Cursor c;
        if (ascDescFlag) {
            ascDescFlag = false;
            c = sqliteDatabase.rawQuery("select C.MaximumMark,B.Mark,A.StudentId, A.RollNoInClass, A.Name, case B.Mark when '-1' then 0 else " +
                    "(CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, sliptestmark_" + schoolId + " B, sliptest C where B.SlipTestId=" + slipTestId +
                    " and C.SlipTestId=" + slipTestId + " and A.StudentId=B.StudentId order by avg DESC", null);
        } else {
            ascDescFlag = true;
            c = sqliteDatabase.rawQuery("select C.MaximumMark,B.Mark,A.StudentId, A.RollNoInClass, A.Name, case B.Mark when '-1' then 0 else " +
                    "(CAST (B.Mark as float)/CAST (C.MaximumMark as float))*100 end as avg from students A, sliptestmark_" + schoolId + " B, sliptest C where B.SlipTestId=" + slipTestId +
                    " and C.SlipTestId=" + slipTestId + " and A.StudentId=B.StudentId order by avg", null);
        }

        c.moveToFirst();
        while (!c.isAfterLast()) {
            studIdList.add(c.getInt(c.getColumnIndex("StudentId")));
            rollNoList.add(c.getInt(c.getColumnIndex("RollNoInClass")));
            nameList.add(c.getString(c.getColumnIndex("Name")));
            markMaxList.add(c.getInt(c.getColumnIndex("Mark")) + "");
            progressList.add(c.getInt(c.getColumnIndex("avg")));
            c.moveToNext();
        }
        c.close();
        for (int i = 0, j = rollNoList.size(); i < j; i++)
            amrList.add(new AdapterOverloaded(rollNoList.get(i) + "", nameList.get(i), markMaxList.get(i), progressList.get(i)));

        amrAdapter.notifyDataSetChanged();
    }

    public class CircleAdapter extends ArrayAdapter<DashObject> {
        private Context context;
        private int layoutResourceId;
        private ArrayList<DashObject> data = new ArrayList<>();
        private LayoutInflater inflater = null;

        public CircleAdapter(Context context, int layoutResourceId, ArrayList<DashObject> gridArray) {
            super(context, layoutResourceId, gridArray);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.data = gridArray;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            RecordHolder holder;
            if (row == null) {
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new RecordHolder();
                holder.outOf = (TextView) row.findViewById(R.id.text1);
                holder.str = (TextView) row.findViewById(R.id.text2);
                row.setTag(holder);
            } else holder = (RecordHolder) row.getTag();

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            FrameLayout fl = (FrameLayout) row.findViewById(R.id.fl);

            SampleView sV = new SampleView(context, position);
            fl.addView(sV, layoutParams);
            DashObject gridItem = data.get(position);
            holder.outOf.setText(gridItem.getOutOf());
            holder.str.setText(gridItem.getStr());
            return row;
        }

        public class RecordHolder {
            TextView outOf;
            TextView str;
        }

        private class SampleView extends View {
            Paint circlePaint;
            int posi;

            public SampleView(Context context, int pos) {
                super(context);
                setFocusable(true);
                posi = pos;
                init();
            }

            public void init() {
                circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                circlePaint.setStyle(Paint.Style.FILL);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                if (posi == 0) {
                    circlePaint.setColor(getResources().getColor(R.color.green));
                    canvas.drawCircle(75, 60, 50, circlePaint);
                } else if (posi == 1) {
                    circlePaint.setColor(getResources().getColor(R.color.orange));
                    canvas.drawCircle(75, 60, 50, circlePaint);
                } else if (posi == 2) {
                    circlePaint.setColor(getResources().getColor(R.color.red));
                    canvas.drawCircle(75, 60, 50, circlePaint);
                }
            }
        }
    }

    public class CircleAdapter2 extends ArrayAdapter<DashObject> {
        Context context;
        int layoutResourceId;
        ArrayList<DashObject> data = new ArrayList<>();
        protected ListView mListView;

        public CircleAdapter2(Context context, int layoutResourceId, ArrayList<DashObject> gridArray) {
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
            } else holder = (RecordHolder) row.getTag();

            DashObject gridItem = data.get(position);
            holder.outOf.setText(gridItem.getOutOf());
            holder.str.setText(gridItem.getStr());
            return row;
        }

        public class RecordHolder {
            TextView outOf;
            TextView str;
        }
    }
}
