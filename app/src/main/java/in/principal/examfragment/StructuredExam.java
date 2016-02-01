package in.principal.examfragment;

import in.principal.activity.R;
import in.principal.dao.ClasDao;
import in.principal.dao.ExmAvgDao;
import in.principal.dao.SectionDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.CircleObject;
import in.principal.sqlite.Clas;
import in.principal.sqlite.Section;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.PKGenerator;
import in.principal.util.ReplaceFragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
public class StructuredExam extends Fragment {
    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private List<Clas> clasList = new ArrayList<>();
    private ArrayList<CircleObject> circleArrayGrid = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.se, container, false);
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        GridView gridView = (GridView) view.findViewById(R.id.gridView);

        clearList();

        clasList = ClasDao.selectClas(sqliteDatabase);
        for (Clas c : clasList) {
            int avg = ExmAvgDao.seClassAvg(c.getClassId(), sqliteDatabase);
            circleArrayGrid.add(new CircleObject(avg, PKGenerator.trim(0, 6, c.getClassName())));
        }
        CircleAdapter cA = new CircleAdapter(context, R.layout.circle_grid, circleArrayGrid);
        gridView.setAdapter(cA);

        return view;
    }

    public void clearList() {
        circleArrayGrid.clear();
        clasList.clear();
    }

    public class CircleAdapter extends ArrayAdapter<CircleObject> {
        Context context;
        int layoutResourceId;
        ArrayList<CircleObject> data = new ArrayList<>();
        protected ListView mListView;
        private LayoutInflater inflater = null;

        public CircleAdapter(Context context, int layoutResourceId, ArrayList<CircleObject> gridArray) {
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
                holder.clasTxt = (TextView) row.findViewById(R.id.clas);
                row.setTag(holder);
            } else holder = (RecordHolder) row.getTag();

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            FrameLayout fl = (FrameLayout) row.findViewById(R.id.fl);

            CircleObject gridItem = data.get(position);
            holder.clasTxt.setText(gridItem.getClas());
            SampleView sV = new SampleView(context, gridItem.getProgressInt());
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
            TextView clasTxt;
        }

        private class SampleView extends View {
            Paint p, defaultPaint;
            RectF rectF1;
            int localInt;

            public SampleView(Context context, int i) {
                super(context);
                setFocusable(true);
                localInt = i;
                init();
            }

            public void init() {
                p = new Paint();
                defaultPaint = new Paint();
                defaultPaint.setAntiAlias(true);
                defaultPaint.setStyle(Paint.Style.STROKE);
                defaultPaint.setStrokeWidth(9);
                Resources res = getResources();
                int defalt = res.getColor(R.color.defalt);
                defaultPaint.setColor(defalt);
                rectF1 = new RectF(10, 10, 110, 110);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                p.setAntiAlias(true);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(9);
                if (localInt >= 270) {
                    p.setColor(getResources().getColor(R.color.green));
                } else if (localInt >= 180) {
                    p.setColor(getResources().getColor(R.color.orange));
                } else if (localInt > 0) {
                    p.setColor(getResources().getColor(R.color.red));
                }
                canvas.drawArc(rectF1, 0, 360, false, defaultPaint);
                canvas.drawArc(rectF1, 270, Float.parseFloat(localInt + ""), false, p);
            }
        }
    }

    public void viewClickListener(int position) {
        Clas c = clasList.get(position);
        Temp t = new Temp();
        t.setClassId(c.getClassId());
        t.setClassName(c.getClassName());
        TempDao.updateClass(t, sqliteDatabase);
        List<Section> secList = SectionDao.selectSection(c.getClassId(), sqliteDatabase);
        for (Section s : secList) {
            Temp t2 = new Temp();
            t2.setSectionId(s.getSectionId());
            t2.setSectionName(s.getSectionName());
            TempDao.updateSection(t2, sqliteDatabase);
            break;
        }
        ReplaceFragment.replace(new SeClassSec(), getFragmentManager());
    }

}
