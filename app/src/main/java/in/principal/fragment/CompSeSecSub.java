package in.principal.fragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.CompList1;
import in.principal.adapter.CompList2;
import in.principal.dao.ExmAvgDao;
import in.principal.dao.SectionDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TempDao;
import in.principal.model.Circle;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Section;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.CommonDialogUtils;
import in.principal.util.ReplaceFragment;

import android.app.Activity;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by vinkrish.
 */

public class CompSeSecSub extends Fragment {
    private Context context;
    private Activity act;
    private SqlDbHelper sqlHandler;
    private String className;
    private int subjectId, classId, isCompare = 0;
    private List<Section> secList = new ArrayList<>();
    private static List<Integer> secIdList = new ArrayList<>();
    private static List<Integer> secIdList2 = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private List<Integer> examIdList = new ArrayList<>();
    private List<String> examNameList = new ArrayList<>();
    private TextView comp1Sec1, comp1Sec2, comp2Sec1, comp2Sec2, comp2Sec3;
    private SQLiteDatabase sqliteDatabase;
    private ListView lv;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private CompList1 compAdapter1;
    private CompList2 compAdapter2;
    private List<String> secNameList2 = new ArrayList<>();
    private List<Integer> progressList1 = new ArrayList<>();
    private List<Integer> progressList2 = new ArrayList<>();
    private List<Integer> progressList3 = new ArrayList<>();
    private int noOfSections = 0;
    private ArrayList<Circle> circleArrayGrid = new ArrayList<>();
    private CircleAdapter cA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comp_se_secsub, container, false);

        FrameLayout fl2 = (FrameLayout) view.findViewById(R.id.fl_comp1);
        FrameLayout fl3 = (FrameLayout) view.findViewById(R.id.fl_comp2);
        comp1Sec1 = (TextView) view.findViewById(R.id.comp1sec1);
        comp1Sec2 = (TextView) view.findViewById(R.id.comp1sec2);
        comp2Sec1 = (TextView) view.findViewById(R.id.comp2sec1);
        comp2Sec2 = (TextView) view.findViewById(R.id.comp2sec2);
        comp2Sec3 = (TextView) view.findViewById(R.id.comp2sec3);

        lv = (ListView) view.findViewById(R.id.list);
        context = this.getActivity().getApplicationContext();
        sqlHandler = SqlDbHelper.getInstance(context);
        sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
        act = this.getActivity();

        clearList();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        subjectId = t.getSubjectId();
        classId = t.getClassId();
        className = t.getClassName();

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        cA = new CircleAdapter(context, R.layout.section_grid, circleArrayGrid);
        gridView.setAdapter(cA);

        Cursor c = sqliteDatabase.rawQuery("select * from comp", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            secIdList2.add(c.getInt(c.getColumnIndex("SecId")));
            isCompare = c.getInt(c.getColumnIndex("IsCompare"));
            noOfSections += 1;
            c.moveToNext();
        }
        c.close();

        for (Integer id : secIdList2) {
            secNameList2.add(SectionDao.getSecName(id, sqliteDatabase));
        }

        if (isCompare == 1) {
            sqliteDatabase.execSQL("delete from comp");
            Cursor c2 = sqliteDatabase.rawQuery("select ExamId,ExamName from exams where ClassId=" + classId, null);
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                examIdList.add(c2.getInt(c2.getColumnIndex("ExamId")));
                examNameList.add(c2.getString(c2.getColumnIndex("ExamName")));
                c2.moveToNext();
            }
            c2.close();
            if (noOfSections == 2) {
                fl2.setVisibility(View.VISIBLE);
                updateCompare2();
            } else {
                fl3.setVisibility(View.VISIBLE);
                updateCompare3();
            }
        }

        Button perfClas = (Button) view.findViewById(R.id.seClass);
        perfClas.setText("Class " + className);

        Button reeturn = (Button) view.findViewById(R.id.reeturn);
        reeturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new SeSecSub(), getFragmentManager());
            }
        });
        Button go = (Button) view.findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOfSections < 2 || noOfSections > 3) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "select min of 2 and max of 3 sections");
                } else {
                    sqliteDatabase.execSQL("update comp set IsCompare=1");
                    ReplaceFragment.replace(new CompSeSecSub(), getFragmentManager());
                }
            }
        });

        TextView subj = (TextView) view.findViewById(R.id.subinfo);
        subj.setText(SubjectsDao.getSubjectName(subjectId, sqliteDatabase));

        secList = SectionDao.selectSection(classId, sqliteDatabase);
        for (Section s : secList) {
            secIdList.add(s.getSectionId());
            secNameList.add(s.getSectionName());
        }

        updateView();

        return view;

    }

    private static boolean isSecIdPresent(int secId) {
        if (secIdList2.contains(secId)) {
            return true;
        } else {
            return false;
        }
    }

    private void clearList() {
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        examIdList.clear();
        examNameList.clear();
        secNameList2.clear();
        secIdList2.clear();
        progressList1.clear();
        progressList2.clear();
        progressList3.clear();
    }

    private void updateCompare2() {
        comp1Sec1.setText("Section " + secNameList2.get(0));
        comp1Sec2.setText("Section " + secNameList2.get(1));
        for (Integer examId : examIdList) {
            progressList1.add(ExmAvgDao.getSeSecSubAvg(examId, secIdList2.get(0), subjectId, sqliteDatabase));
            progressList2.add(ExmAvgDao.getSeSecSubAvg(examId, secIdList2.get(1), subjectId, sqliteDatabase));
        }
        for (int i = 0; i < examIdList.size(); i++) {
            amrList.add(new AdapterOverloaded(examNameList.get(i), progressList1.get(i), progressList2.get(i)));
        }
        compAdapter1 = new CompList1(context, R.layout.comp_list1, amrList);
        lv.setAdapter(compAdapter1);
    }

    private void updateCompare3() {
        comp2Sec1.setText("Section " + secNameList2.get(0));
        comp2Sec2.setText("Section " + secNameList2.get(1));
        comp2Sec3.setText("Section " + secNameList2.get(2));
        for (Integer examId : examIdList) {
            progressList1.add(ExmAvgDao.getSeSecSubAvg(examId, secIdList2.get(0), subjectId, sqliteDatabase));
            progressList2.add(ExmAvgDao.getSeSecSubAvg(examId, secIdList2.get(1), subjectId, sqliteDatabase));
            progressList3.add(ExmAvgDao.getSeSecSubAvg(examId, secIdList2.get(2), subjectId, sqliteDatabase));
        }

        for (int i = 0; i < examIdList.size(); i++) {
            amrList.add(new AdapterOverloaded(examNameList.get(i), progressList1.get(i), progressList2.get(i), progressList3.get(i)));
        }
        compAdapter2 = new CompList2(context, R.layout.comp_list2, amrList);
        lv.setAdapter(compAdapter2);
    }

    private void updateView() {
        for (Section s : secList) {
            int per = ExmAvgDao.seSecSubAvg(s.getSectionId(), subjectId, sqliteDatabase);
            if (secIdList2.contains(s.getSectionId())) {
                Circle c = new Circle(per, s.getSectionName(), true);
                circleArrayGrid.add(c);
            } else {
                Circle c = new Circle(per, s.getSectionName(), false);
                circleArrayGrid.add(c);
            }
            cA.notifyDataSetChanged();
        }
    }

    public class CircleAdapter extends ArrayAdapter<Circle> {
        Context context;
        int layoutResourceId;
        ArrayList<Circle> data = new ArrayList<>();
        protected ListView mListView;
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
                holder.secTxtWhite = (TextView) row.findViewById(R.id.sectionWhite);
                row.setTag(holder);
            } else holder = (RecordHolder) row.getTag();

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            FrameLayout fl = (FrameLayout) row.findViewById(R.id.fl);

            Circle gridItem = data.get(position);
            SampleView sV = new SampleView(context, gridItem.getProgressInt(), gridItem.isSelected());
            if (gridItem.isSelected()) {
                holder.secTxtBlack.setVisibility(View.GONE);
                holder.secTxtWhite.setText(gridItem.getSec());
            } else {
                holder.secTxtWhite.setVisibility(View.GONE);
                holder.secTxtBlack.setText(gridItem.getSec());
            }
            fl.addView(sV, layoutParams);

            sV.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int sectionId = secIdList.get(position);
                    boolean added = isSecIdPresent(sectionId);
                    if (added) {
                        sqlHandler.removeComp(sectionId);
                        ReplaceFragment.replace(new CompSeSecSub(), getFragmentManager());
                    } else {
                        sqlHandler.insertComp(sectionId);
                        ReplaceFragment.replace(new CompSeSecSub(), getFragmentManager());
                    }
                }
            });
            return row;
        }

        public class RecordHolder {
            TextView secTxtBlack;
            TextView secTxtWhite;
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
                rectF = new RectF(10, 10, 90, 90);
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
                if (selected) {
                    //	canvas.drawCircle(90, 65, 35, circlePaint);
                }
            }
        }
    }
}
