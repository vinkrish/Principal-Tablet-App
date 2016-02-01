package in.principal.examfragment;

import java.util.ArrayList;
import java.util.List;

import in.principal.activity.R;
import in.principal.adapter.SssAdapter;
import in.principal.comparefragment.CompSeExam;
import in.principal.dao.ExmAvgDao;
import in.principal.dao.SectionDao;
import in.principal.dao.TempDao;
import in.principal.fragment.CoScholastic;
import in.principal.sqlite.Circle;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Section;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.ReplaceFragment;

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
 * Looks like this need to be optimized, good luck with that.
 */
public class SeExam extends Fragment {
    private Context context;
    private int classId, sectionId;
    private List<Section> secList = new ArrayList<>();
    private static List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private SQLiteDatabase sqliteDatabase;
    private ListView lv;
    private ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
    private SssAdapter amrAdapter;
    private List<Integer> examIdList = new ArrayList<>();
    private List<String> examNameList = new ArrayList<>();
    private List<Integer> avgList = new ArrayList<>();
    private ArrayList<Circle> circleArrayGrid = new ArrayList<>();
    private CircleAdapter cA;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.se_exam, container, false);

        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        clearList();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        classId = t.getClassId();
        String className = t.getClassName();
        sectionId = t.getSectionId();

        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        cA = new CircleAdapter(context, R.layout.section_grid, circleArrayGrid);
        gridView.setAdapter(cA);

        lv = (ListView) view.findViewById(R.id.list);
        amrAdapter = new SssAdapter(context, R.layout.se_exam_list, amrList);
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

        Button compare = (Button) view.findViewById(R.id.compare);
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqliteDatabase.execSQL("delete from comp");
                ReplaceFragment.replace(new CompSeExam(), getFragmentManager());
            }
        });

        Button viewChange = (Button) view.findViewById(R.id.viewChange);
        viewChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new SeClassSec(), getFragmentManager());
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
                TempDao.updateExamId(examIdList.get(pos), sqliteDatabase);
                ReplaceFragment.replace(new SeExamSub(), getFragmentManager());
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

    private void updateListView() {
        Cursor c = sqliteDatabase.rawQuery("select ExamId,ExamName from exams where ClassId=" + classId, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            examIdList.add(c.getInt(c.getColumnIndex("ExamId")));
            examNameList.add(c.getString(c.getColumnIndex("ExamName")));
            c.moveToNext();
        }
        c.close();

        for (Integer id : examIdList) {
            avgList.add(ExmAvgDao.getSeExamAvg(id, sectionId, sqliteDatabase));
        }

        for (int i = 0; i < examIdList.size(); i++) {
            try {
                amrList.add(new AdapterOverloaded(examNameList.get(i), avgList.get(i)));
            } catch (IndexOutOfBoundsException e) {
                amrList.add(new AdapterOverloaded(examNameList.get(i), 0, 0));
            }
        }
        amrAdapter.notifyDataSetChanged();
    }

    private void clearList() {
        amrList.clear();
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        examIdList.clear();
        examNameList.clear();
        avgList.clear();
        circleArrayGrid.clear();
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
            } else holder = (RecordHolder) row.getTag();

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
                    ReplaceFragment.replace(new SeExam(), getFragmentManager());
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

                for (int k = 0; k < secIdList.size(); k++) {
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
    }
}