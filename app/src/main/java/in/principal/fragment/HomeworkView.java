package in.principal.fragment;

import in.principal.activity.R;
import in.principal.adapter.AsecAdapter;
import in.principal.dao.HomeworkDao;
import in.principal.dao.SectionDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Section;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.PKGenerator;
import in.principal.util.ReplaceFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * Don't expect comments explaining every piece of code, class and function names are self explanatory.
 */
public class HomeworkView extends Fragment {
    private Context context;
    private Activity act;
    private String dateSelected;
    private TextView dateTV, clasTV;
    private String[] items;
    private String[] items2;
    private int index;
    private AlertDialog alertDialog;
    private List<Integer> classIdList = new ArrayList<>();
    private List<String> classNameList = new ArrayList<>();
    private List<Section> secList = new ArrayList<>();
    private List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private SQLiteDatabase sqliteDatabase;
    private ListView lv;
    private List<String> clasNameT = new ArrayList<>();
    private List<Integer> secIdT = new ArrayList<>();
    private List<String> secNameT = new ArrayList<>();
    private List<String> teacherNameT = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.homework, container, false);
        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        lv = (ListView) view.findViewById(R.id.list);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.datePicker);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Temp t = TempDao.selectTemp(sqliteDatabase);
        dateSelected = t.getSelectedDate();

        dateTV = (TextView) view.findViewById(R.id.datetxt);
        clasTV = (TextView) view.findViewById(R.id.clastxt);
        dateTV.setText(dateSelected);

        updateYesHw();

        items = classNameList.toArray(new String[classNameList.size()]);
        LinearLayout selecClass = (LinearLayout) view.findViewById(R.id.classPicker);
        selecClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classIdList.size() > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Select class");
                    builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            index = which;
                            if (classIdList.size() > 0) {
                                alertDialog.dismiss();
                                Temp t = new Temp();
                                t.setClassId(classIdList.get(index));
                                t.setClassName(classNameList.get(index));
                                TempDao.updateClass(t, sqliteDatabase);
                                clasTV.setText(classNameList.get(index));
                                secList.clear();
                                secIdList.clear();
                                secNameList.clear();
                                secList = SectionDao.selectSection(classIdList.get(index), sqliteDatabase);
                                for (Section s : secList) {
                                    secIdList.add(s.getSectionId());
                                    secNameList.add(s.getSectionName());
                                }
                                items2 = secNameList.toArray(new String[secNameList.size()]);
                            }
                        }

                    });
                    alertDialog = builder.create();
                    alertDialog.show();

                } else {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "No homework given to any class for this day");
                }
            }
        });

        items2 = secNameList.toArray(new String[secNameList.size()]);
        LinearLayout selecSec = (LinearLayout) view.findViewById(R.id.sectionPicker);
        selecSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (secIdList.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(act);
                    builder.setTitle("Select section");
                    builder.setSingleChoiceItems(items2, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            index = which;
                            alertDialog.dismiss();
                            if (secIdList.size() > 0) {
                                Temp t = new Temp();
                                t.setSectionId(secIdList.get(index));
                                t.setSectionName(secNameList.get(index));
                                TempDao.updateSection(t, sqliteDatabase);
                                ReplaceFragment.replace(new HwClasSec(), getFragmentManager());
                            }
                        }

                    });
                    alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "No homework given to any section for this day");
                }
            }
        });

        updateNoHw();

        return view;
    }

    public void updateYesHw() {
        classIdList.clear();
        classNameList.clear();
        sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
        Cursor c = sqliteDatabase.rawQuery("select A.ClassId,A.ClassName from class A, homeworkmessage B where B.HomeworkDate='" + dateSelected + "' and A.ClassId=B.ClassId group by A.ClassId", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            classIdList.add(c.getInt(c.getColumnIndex("ClassId")));
            classNameList.add(c.getString(c.getColumnIndex("ClassName")));
            c.moveToNext();
        }
        c.close();
        items = classNameList.toArray(new String[classNameList.size()]);
    }

    public void updateNoHw() {
        List<Integer> hwGivenList = HomeworkDao.getHWGiven(dateSelected, sqliteDatabase);
        clasNameT.clear();
        secNameT.clear();
        secIdT.clear();
        teacherNameT.clear();
        sqliteDatabase = SqlDbHelper.getInstance(context).getWritableDatabase();
        Cursor c = sqliteDatabase.rawQuery("select B.ClassName, A.SectionId, A.SectionName, C.Name from section A, teacher C, class B where A.ClassTeacherId=C.TeacherId and A.ClassId=B.ClassId", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if (!hwGivenList.contains(c.getInt(c.getColumnIndex("SectionId")))) {
                clasNameT.add(c.getString(c.getColumnIndex("ClassName")));
                secIdT.add(c.getInt(c.getColumnIndex("SectionId")));
                secNameT.add(c.getString(c.getColumnIndex("SectionName")));
                teacherNameT.add(c.getString(c.getColumnIndex("Name")));
            }
            c.moveToNext();
        }
        c.close();

        ArrayList<AdapterOverloaded> amrList = new ArrayList<>();
        for (int i = 0, j = secIdT.size(); i < j; i++) {
            amrList.add(new AdapterOverloaded(PKGenerator.trim(0, 8, clasNameT.get(i)) + " - " + secNameT.get(i), teacherNameT.get(i)));
        }
        AsecAdapter asecAdapter = new AsecAdapter(context, R.layout.asec_list, amrList);
        lv.setAdapter(asecAdapter);
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
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(year, month, day);
                Date d = cal.getTime();

                if (GregorianCalendar.getInstance().get(Calendar.YEAR) < cal.get(Calendar.YEAR)) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "Selected future date !");
                } else if (GregorianCalendar.getInstance().get(Calendar.MONTH) < cal.get(Calendar.MONTH) && GregorianCalendar.getInstance().get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "Selected future date !");
                } else if (GregorianCalendar.getInstance().get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH) &&
                        GregorianCalendar.getInstance().get(Calendar.MONTH) <= cal.get(Calendar.MONTH) && GregorianCalendar.getInstance().get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "Selected future date !");
                } else if (Calendar.SUNDAY == cal.get(Calendar.DAY_OF_WEEK)) {
                    CommonDialogUtils.displayAlertWhiteDialog(act, "Sundays are not working days");
                } else {
                    dateSelected = dateFormat.format(d);
                    TempDao.updateSelectedDate(dateSelected, sqliteDatabase);
                    dateTV.setText(dateSelected);
                }
                updateYesHw();
                updateNoHw();
            }
        }
    }

}
