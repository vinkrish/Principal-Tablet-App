package in.principal.examfragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.principal.activity.R;
import in.principal.adapter.Capitalize;
import in.principal.adapter.SssAdapter;
import in.principal.comparefragment.CompSeSecSub;
import in.principal.dao.ActivitiDao;
import in.principal.dao.MarksDao;
import in.principal.dao.SectionDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TeacherDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.Activiti;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Circle;
import in.principal.sqlite.Section;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

/**
 * Created by vinkrish.
 * Looks like this need to be optimized, good luck with that.
 */
public class SeSecSub extends Fragment {
    private Context context;
    private int subjectId, classId, sectionId, progres;
    private List<Section> secList = new ArrayList<>();
    private static List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private List<Integer> examIdList = new ArrayList<>();
    private List<String> examNameList = new ArrayList<>();
    private List<Integer> avgList = new ArrayList<>();
    private List<Activiti> activitiList = new ArrayList<>();
    private SQLiteDatabase sqliteDatabase;
    private List<AdapterOverloaded> amrList = new ArrayList<>();
    private SssAdapter amrAdapter;
    private CircleAdapter cA;
    private ArrayList<Circle> circleArrayGrid = new ArrayList<>();
    private final Map<Object, Object> mi1 = new HashMap<>();
    private final Map<Object, Object> mi2 = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.se_sec_sub, container, false);
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        subjectId = t.getSubjectId();
        classId = t.getClassId();
        sectionId = t.getSectionId();
        String className = t.getClassName();
        String secName = t.getSectionName();
        int teacherId = t.getTeacherId();

        clearList();

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        cA = new CircleAdapter(context, R.layout.section_grid, circleArrayGrid);
        gridView.setAdapter(cA);

        ListView lv = (ListView) view.findViewById(R.id.list);
        amrAdapter = new SssAdapter(context, R.layout.se_exam_list, amrList);
        lv.setAdapter(amrAdapter);

        secList = SectionDao.selectSection(classId, sqliteDatabase);
        for (Section s : secList) {
            secIdList.add(s.getSectionId());
            secNameList.add(s.getSectionName());
        }

        Button perfClas = (Button) view.findViewById(R.id.seClass);
        perfClas.setText("Class " + className);
        Button perfSe = (Button) view.findViewById(R.id.seSec);
        perfSe.setText("Section " + secName);

        Button compare = (Button) view.findViewById(R.id.compare);
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.clearBackStack(getFragmentManager());
                ReplaceFragment.replace(new CompSeSecSub(), getFragmentManager());
            }
        });

        TextView subj = (TextView) view.findViewById(R.id.subinfo);
        subj.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase));
        TextView teacher = (TextView) view.findViewById(R.id.teacherinfo);
        teacher.setText(Capitalize.capitalThis(TeacherDao.getTeacherName(teacherId, sqliteDatabase)));

        updateListView();
        updateView();

        ProgressBar pb = (ProgressBar) view.findViewById(R.id.subAvgProgress);
        if (progres >= 75) {
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
        } else if (progres >= 50) {
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
        } else {
            pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
        }
        pb.setProgress(progres);
        TextView pecent = (TextView) view.findViewById(R.id.percent);
        pecent.setText(progres + "%");

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TempDao.updateExamId(examIdList.get(pos), sqliteDatabase);
                activitiList.clear();
                activitiList = ActivitiDao.selectActiviti(examIdList.get(pos), subjectId, sectionId, sqliteDatabase);
                if (activitiList.size() != 0) {
                    ReplaceFragment.replace(new SeSubAct(), getFragmentManager());
                } else {
                    Boolean b1 = (Boolean) mi1.get(examIdList.get(pos));
                    Boolean b2 = (Boolean) mi2.get(examIdList.get(pos));
                    if (b1 != null && b1)
                        ReplaceFragment.replace(new SeSubStud(), getFragmentManager());
                    else if (b2 != null && b2)
                        ReplaceFragment.replace(new SeSubStudGrade(), getFragmentManager());
                    else Toast.makeText(context, "Data not entered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void clearList() {
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        examIdList.clear();
        examNameList.clear();
        avgList.clear();
        amrList.clear();
        circleArrayGrid.clear();
    }

    private void updateView() {
        for (int loop = 0; loop < secList.size(); loop++) {
            Section s = secList.get(loop);
            int per = (int)(progres * 3.6);
            if (sectionId == s.getSectionId()) {
                Circle c = new Circle(per, secNameList.get(loop), true);
                circleArrayGrid.add(c);
            } else {
                Circle c = new Circle(0, secNameList.get(loop), false);
                circleArrayGrid.add(c);
            }
            cA.notifyDataSetChanged();
        }
    }

    private void updateListView() {
        Cursor c = sqliteDatabase.rawQuery("select ExamId,ExamName from exams where ClassId=" + classId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            examIdList.add(c.getInt(c.getColumnIndex("ExamId")));
            examNameList.add(c.getString(c.getColumnIndex("ExamName")));
            c.moveToNext();
        }
        c.close();

        int averag = 0;
        for (Integer id : examIdList) {
            //avgList.add(ExmAvgDao.getSeSecSubAvg(id, sectionId, subjectId, sqliteDatabase));
            int exmAvg = getExamAvg(id, subjectId);
            avgList.add(exmAvg);
            averag += exmAvg;
            int markEntry = MarksDao.isThereExamMark(id, sectionId, subjectId, sqliteDatabase);
            if (markEntry == 1) mi1.put(id, true);
            int gradeEntry = MarksDao.isThereExamGrade(id, sectionId, subjectId, sqliteDatabase);
            if (gradeEntry == 1) mi2.put(id, true);
        }

        progres = averag / examIdList.size();

        for (int i = 0; i < examIdList.size(); i++) {
            try {
                amrList.add(new AdapterOverloaded(examNameList.get(i), avgList.get(i)));
            } catch (IndexOutOfBoundsException e) {
                amrList.add(new AdapterOverloaded(examNameList.get(i), 0, 0));
            }
        }

        amrAdapter.notifyDataSetChanged();
    }


    public class CircleAdapter extends ArrayAdapter<Circle> {
        private Context context;
        private int layoutResourceId;
        private ArrayList<Circle> data = new ArrayList<>();
        private LayoutInflater inflater = null;

        public CircleAdapter(Context context, int layoutResourceId, ArrayList<Circle> gridArray) {
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
                holder.secTxtBlack = (TextView) row.findViewById(R.id.sectionBlack);
                holder.ll = (LinearLayout) row.findViewById(R.id.sec_grid);
                row.setTag(holder);
            } else {
                holder = (RecordHolder) row.getTag();
            }

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            FrameLayout fl = (FrameLayout) row.findViewById(R.id.fl);

            Circle gridItem = data.get(position);
            SampleView sV = new SampleView(context, gridItem.getProgressInt(), gridItem.isSelected());
            holder.secTxtBlack.setText(gridItem.getSec());
            if (gridItem.isSelected()) {
                holder.ll.setActivated(true);
            }
            fl.addView(sV, layoutParams);

            holder.ll.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    sectionId = secIdList.get(position);
                    Temp t = new Temp();
                    t.setSectionId(sectionId);
                    t.setSectionName(secNameList.get(position));
                    TempDao.updateSection(t, sqliteDatabase);
                    ReplaceFragment.replace(new SeSecSub(), getFragmentManager());
                }
            });
            return row;
        }

        public class RecordHolder {
            TextView secTxtBlack;
            LinearLayout ll;
        }

        private class SampleView extends View {
            Paint p, defaultPaint, circlePaint;
            RectF rectF;
            int localInt;
            boolean selected;

            public SampleView(Context context, int i, boolean b) {
                super(context);
                setFocusable(true);
                localInt = i;
                selected = b;
                init();
            }

            public void init() {
                circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                circlePaint.setStyle(Paint.Style.FILL);
                circlePaint.setColor(Color.BLACK);
                p = new Paint();
                defaultPaint = new Paint();
                defaultPaint.setAntiAlias(true);
                defaultPaint.setStyle(Paint.Style.STROKE);
                defaultPaint.setStrokeWidth(6);
                Resources res = getResources();
                int defalt = res.getColor(R.color.defalt);
                defaultPaint.setColor(defalt);
                rectF = new RectF(10, 15, 80, 85);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                p.setAntiAlias(true);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(6);

                if (localInt >= 270) {
                    p.setColor(getResources().getColor(R.color.green));
                } else if (localInt >= 180) {
                    p.setColor(getResources().getColor(R.color.orange));
                } else if (localInt > 0) {
                    p.setColor(getResources().getColor(R.color.red));
                }
                canvas.drawArc(rectF, 0, 360, false, defaultPaint);
                canvas.drawArc(rectF, 270, Float.parseFloat(localInt + ""), false, p);
            }
        }
    }

    private int getExamAvg(long examId, int subjectId) {
        int avg = 0;
        int sectionAvg = MarksDao.getSectionAvg(examId, subjectId, sectionId, sqliteDatabase);
        Cursor cursor = sqliteDatabase.rawQuery("select Mark from marks where ExamId=" + examId + " and SubjectId=" + subjectId +
                " and StudentId in (select StudentId from Students where SectionId = " + sectionId + ")", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0 && sectionAvg != 0) {
            avg = sectionAvg;
        } else {
            Cursor cursor1 = sqliteDatabase.rawQuery("select Grade from marks where ExamId=" + examId + " and SubjectId=" + subjectId +
                    " and StudentId in (select StudentId from Students where SectionId = " + sectionId + ")", null);
            cursor1.moveToFirst();
            if (cursor1.getCount() > 0) {
                avg = MarksDao.getSectionAvg(classId, sectionId, subjectId, examId, sqliteDatabase);
            }
            cursor1.close();
        }
        cursor.close();
        return avg;
    }

}
