package in.principal.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.AsecAdapter;
import in.principal.dao.ExmAvgDao;
import in.principal.dao.SectionDao;
import in.principal.dao.StudentsDao;
import in.principal.dao.TempDao;
import in.principal.model.Circle;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Section;
import in.principal.sqlite.Students;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

public class StudentClassSec extends Fragment {
    private Context context;
    private int sectionId;
    private List<Section> secList = new ArrayList<>();
    private List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private SQLiteDatabase sqliteDatabase;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private ArrayList<Circle> circleArrayGrid = new ArrayList<>();
    private List<Integer> studentIdList = new ArrayList<>();
    private List<Integer> studIDList = new ArrayList<>();
    private List<String> studentNameList = new ArrayList<>();
    private CircleAdapter cA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stud_clas_sec, container, false);

        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        clearList();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        int classId = t.getClassId();
        String className = t.getClassName();
        sectionId = t.getSectionId();

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        cA = new CircleAdapter(context, R.layout.section_grid, circleArrayGrid);
        gridView.setAdapter(cA);

        Button perfClas = (Button) view.findViewById(R.id.seClass);
        perfClas.setText("Class " + className);
        TextView clasText = (TextView) view.findViewById(R.id.classtext);
        clasText.setText("Class  " + className);

        secList = SectionDao.selectSection(classId, sqliteDatabase);
        for (Section s : secList) {
            secIdList.add(s.getSectionId());
            secNameList.add(s.getSectionName());
        }

        updateView();
        updateListView();

        ListView lv = (ListView) view.findViewById(R.id.list);
        AsecAdapter asecAdapter = new AsecAdapter(context, R.layout.asec_list, amrList);
        lv.setAdapter(asecAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TempDao.updateStudentId(studIDList.get(position), sqliteDatabase);
                ReplaceFragment.replace(new SearchStudST(), getFragmentManager());
            }
        });

        return view;
    }

    private void updateView() {
        for (int loop = 0; loop < secList.size(); loop++) {
            Section s = secList.get(loop);
            int per = ExmAvgDao.seSecAvg(s.getSectionId(), sqliteDatabase);
            if (sectionId == s.getSectionId()) {
                Circle c = new Circle(per, secNameList.get(loop), true);
                circleArrayGrid.add(c);
            } else {
                Circle c = new Circle(per, secNameList.get(loop), false);
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
        studIDList.clear();
        studentIdList.clear();
        studentNameList.clear();
        circleArrayGrid.clear();
    }

    private void updateListView() {
        List<Students> studentList = StudentsDao.selectStudents(sectionId, sqliteDatabase);
        for(Students s: studentList){
            studIDList.add(s.getStudentId());
            studentIdList.add(s.getRollNoInClass());
            studentNameList.add(s.getName());
        }
        amrList.clear();
        for(int i=0; i<studentList.size(); i++){
            amrList.add(new AdapterOverloaded(studentIdList.get(i)+"",studentNameList.get(i)));
        }
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
                holder.secTxtWhite = (TextView) row.findViewById(R.id.sectionWhite);
                row.setTag(holder);
            } else {
                holder = (RecordHolder) row.getTag();
            }

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

            sV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sectionId = secIdList.get(position);
                    Temp t = new Temp();
                    t.setSectionId(sectionId);
                    t.setSectionName(secNameList.get(position));
                    TempDao.updateSection(t, sqliteDatabase);
                    ReplaceFragment.replace(new StudentClassSec(), getFragmentManager());
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
                defaultPaint = new Paint();
                defaultPaint.setAntiAlias(true);
                defaultPaint.setStyle(Paint.Style.STROKE);
                defaultPaint.setStrokeWidth(6);
                Resources res = getResources();
                int defalt = res.getColor(R.color.universal);
                defaultPaint.setColor(defalt);
                rectF = new RectF(10, 10, 90, 90);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawArc(rectF, 0, 360, false, defaultPaint);
            }
        }
    }

}
