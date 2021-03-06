package in.principal.examfragment;

import in.principal.activity.R;
import in.principal.adapter.AmrAdapter;
import in.principal.adapter.SeClassSecAdapter;
import in.principal.dao.SectionDao;
import in.principal.dao.TempDao;
import in.principal.fragment.CoScholastic;
import in.principal.sqlite.Circle;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Section;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Created by vinkrish.
 * Removed consolidation
 */
public class SeClassSec extends Fragment {
    private int sectionId;
    private List<Section> secList = new ArrayList<>();
    private List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private SQLiteDatabase sqliteDatabase;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private SeClassSecAdapter amrAdapter;
    private List<Integer> subIdList = new ArrayList<>();
    private List<Integer> teacherIdList = new ArrayList<>();
    private List<String> subNameList = new ArrayList<>();
    private List<String> teacherNameList = new ArrayList<>();
    private List<Integer> progressList = new ArrayList<>();
    private ArrayList<Circle> circleArrayGrid = new ArrayList<>();
    private CircleAdapter cA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.se_class, container, false);

        Context context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        clearList();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        int classId = t.getClassId();
        String className = t.getClassName();
        sectionId = t.getSectionId();

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        cA = new CircleAdapter(context, R.layout.section_grid, circleArrayGrid);
        gridView.setAdapter(cA);

        ListView lv = (ListView) view.findViewById(R.id.list);
        amrAdapter = new SeClassSecAdapter(context, R.layout.se_class_sec_list, amrList);
        lv.setAdapter(amrAdapter);

        Button perfClas = (Button) view.findViewById(R.id.seClass);
        perfClas.setText("Class " + className);
        TextView clasText = (TextView) view.findViewById(R.id.classtext);
        clasText.setText("Class  " + className);

        Button coScholastic = (Button) view.findViewById(R.id.coscholastic);
        coScholastic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new CoScholastic(), getFragmentManager());
            }
        });

        secList = SectionDao.selectSection(classId, sqliteDatabase);
        for (Section s : secList) {
            secIdList.add(s.getSectionId());
            secNameList.add(s.getSectionName());
        }

        updateView();
        updateListView();

        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TempDao.updateSubjectId(subIdList.get(pos), sqliteDatabase);
                TempDao.updateTeacherId(teacherIdList.get(pos), sqliteDatabase);
                ReplaceFragment.replace(new SeSecSub(), getFragmentManager());
            }
        });

        return view;
    }

    private void updateView() {
        for (int loop = 0; loop < secList.size(); loop++) {
            Section s = secList.get(loop);
            if (sectionId == s.getSectionId()) {
                Circle c = new Circle(0, secNameList.get(loop), true);
                circleArrayGrid.add(c);
            } else {
                Circle c = new Circle(0, secNameList.get(loop), false);
                circleArrayGrid.add(c);
            }
            cA.notifyDataSetChanged();
        }
    }

    private void clearList() {
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        amrList.clear();
        subIdList.clear();
        subNameList.clear();
        teacherIdList.clear();
        teacherNameList.clear();
        progressList.clear();
        circleArrayGrid.clear();
    }

    private void updateListView() {
        Cursor c = sqliteDatabase.rawQuery("select A.SubjectId, A.TeacherId, B.SubjectName,C.Name from subjectteacher A, subjects B, teacher C where A.SectionId=" + sectionId + " and" +
                " A.SubjectId=B.SubjectId and A.TeacherId=C.TeacherId and B.has_partition!=1", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            subIdList.add(c.getInt(c.getColumnIndex("SubjectId")));
            teacherIdList.add(c.getInt(c.getColumnIndex("TeacherId")));
            subNameList.add(c.getString(c.getColumnIndex("SubjectName")));
            teacherNameList.add(c.getString(c.getColumnIndex("Name")));
            c.moveToNext();
        }
        c.close();

        List<Integer> tempId = new ArrayList<>();
        List<String> tempName = new ArrayList<>();
        List<Integer> tempSubjectId = new ArrayList<>();
        Cursor c1 = sqliteDatabase.rawQuery("select A.TeacherId, B.TheorySubjectId, B.PracticalSubjectId, C.Name from " +
                "subjectteacher A, subjects B, teacher C where A.SectionId=" + sectionId + " and A.SubjectId=B.SubjectId and " +
                "A.TeacherId=C.TeacherId and B.has_partition=1", null);
        c1.moveToFirst();
        while (!c1.isAfterLast()) {
            tempId.add(c1.getInt(c1.getColumnIndex("TeacherId")));
            tempName.add(c1.getString(c1.getColumnIndex("Name")));
            tempId.add(c1.getInt(c1.getColumnIndex("TeacherId")));
            tempName.add(c1.getString(c1.getColumnIndex("Name")));
            tempSubjectId.add(c1.getInt(c1.getColumnIndex("TheorySubjectId")));
            tempSubjectId.add(c1.getInt(c1.getColumnIndex("PracticalSubjectId")));
            c1.moveToNext();
        }
        c1.close();

        for (int i = 0; i < tempSubjectId.size(); i++) {
            Cursor c2 = sqliteDatabase.rawQuery("select SubjectName from subjects where SubjectId=" + tempSubjectId.get(i), null);
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                subIdList.add(tempSubjectId.get(i));
                subNameList.add(c2.getString(c2.getColumnIndex("SubjectName")));
                teacherIdList.add(tempId.get(i));
                teacherNameList.add(tempName.get(i));
                c2.moveToNext();
            }
            c2.close();
        }

        for (Integer subId : subIdList) {
            //progressList.add(ExmAvgDao.selectSeAvg(sectionId, subId, sqliteDatabase));
            progressList.add(0);
        }

        for (int i = 0; i < subIdList.size(); i++) {
            amrList.add(new AdapterOverloaded(subNameList.get(i), teacherNameList.get(i)));
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
                    ReplaceFragment.replace(new SeClassSec(), getFragmentManager());
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

                if (selected) {
                    p.setColor(getResources().getColor(R.color.universal));
                    canvas.drawArc(rectF, 0, 360, false, p);
                } else {
                    canvas.drawArc(rectF, 0, 360, false, defaultPaint);
                }
            }
        }
    }
}
