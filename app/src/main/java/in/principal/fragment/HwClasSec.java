package in.principal.fragment;

import in.principal.activity.R;
import in.principal.adapter.AsecAdapter;
import in.principal.dao.HomeworkDao;
import in.principal.dao.SectionDao;
import in.principal.dao.SubjectsDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.AdapterOverloaded;
import in.principal.sqlite.Section;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Subjects;
import in.principal.sqlite.Temp;
import in.principal.sqlite.Homework;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.ReplaceFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
 */
public class HwClasSec extends Fragment {
    private Context context;
    private Activity act;
    private String dateSelected;
    private int classId, index;
    private int sectionId;
    private String className, sectionName;
    private List<Homework> listHW;
    private ListView lv;
    private List<Integer> subIdList = new ArrayList<>();
    private List<String> subNameList = new ArrayList<>();
    private List<String> hwMesList = new ArrayList<>();
    private List<Integer> classIdList = new ArrayList<>();
    private List<String> classNameList = new ArrayList<>();
    private List<Section> secList = new ArrayList<>();
    private List<Integer> secIdList = new ArrayList<>();
    private List<String> secNameList = new ArrayList<>();
    private String[] items;
    private String[] items2;
    private SQLiteDatabase sqliteDatabase;
    private AlertDialog alertDialog;
    private TextView secTV, clasTV, clasDash, secDash, date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hw_clas_sec, container, false);
        act = AppGlobal.getActivity();
        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        lv = (ListView) view.findViewById(R.id.list);

        clearList();

        Temp t = TempDao.selectTemp(sqliteDatabase);
        dateSelected = t.getSelectedDate();
        classId = t.getClassId();
        className = t.getClassName();
        sectionId = t.getSectionId();
        sectionName = t.getSectionName();

        clasDash = (TextView) view.findViewById(R.id.hwclasButton);
        clasDash.setText("Class " + className);
        secDash = (TextView) view.findViewById(R.id.hwsecButton);
        secDash.setText("Section " + sectionName);
        clasTV = (TextView) view.findViewById(R.id.clastxt);
        clasTV.setText(className);
        secTV = (TextView) view.findViewById(R.id.sectxt);
        secTV.setText(sectionName);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.datePicker);
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        date = (TextView) view.findViewById(R.id.datetxt);
        date.setText(dateSelected);

        updateYesHw();
        updateView();

        items = classNameList.toArray(new String[classNameList.size()]);
        LinearLayout selecClass = (LinearLayout) view.findViewById(R.id.classPicker);
        selecClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle("Select class");
                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        index = which;
                        if (classIdList.size() > 0) {
                            alertDialog.dismiss();
                            secTV.setText("Section");
                            lv.setAdapter(null);
                            clasDash.setText(classNameList.get(index));
                            secDash.setText("");
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
            }
        });
        secList.clear();
        secIdList.clear();
        secNameList.clear();
        secList = SectionDao.selectSection(classId, sqliteDatabase);
        for (Section s : secList) {
            secIdList.add(s.getSectionId());
            secNameList.add(s.getSectionName());
        }

        items2 = secNameList.toArray(new String[secNameList.size()]);
        LinearLayout selecSec = (LinearLayout) view.findViewById(R.id.sectionPicker);
        selecSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

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

    public void clearList() {
        subIdList.clear();
        hwMesList.clear();
        subNameList.clear();
    }

    public void updateView() {
        listHW = HomeworkDao.selectHomework(sectionId, dateSelected, sqliteDatabase);
        if (listHW.size() != 0) {
            clearList();
            for (Homework hw : listHW) {
                String subjectIds = hw.getSubjectIDs();
                String splitBy = ",";
                String[] id = subjectIds.split(splitBy);
                for (String subjectId : id) {
                    subIdList.add(Integer.parseInt(subjectId));
                }
                String messageBody = hw.getHomework() + " ";

                while (messageBody.contains("##")) {
                    String s = messageBody.replaceAll("##","# # ");
                    messageBody = s;
                }

                String splitBy2 = "#";
                String[] message = messageBody.split(splitBy2);
                hwMesList.addAll(Arrays.asList(message));
            }
            List<Subjects> subList = SubjectsDao.selectSubjects(sqliteDatabase);
            Subjects s = new Subjects();
            s.setSubjectId(0);
            s.setSubjectName("extra");
            subList.add(s);
            for (Integer i : subIdList) {
                for (Subjects sub : subList) {
                    if (i.equals(sub.getSubjectId())) {
                        subNameList.add(sub.getSubjectName());
                        break;
                    }
                }
            }
            ArrayList<AdapterOverloaded> amrList = new ArrayList<AdapterOverloaded>();
            for (int i = 0, j = subIdList.size(); i < j; i++) {
                amrList.add(new AdapterOverloaded(subNameList.get(i), hwMesList.get(i).trim()));
            }
            AsecAdapter asecAdapter = new AsecAdapter(context, R.layout.hw_message_list, amrList);
            lv.setAdapter(asecAdapter);

        } else {
            CommonDialogUtils.displayAlertWhiteDialog(act, "This section has no homework for this day");
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
                    //	date.setText(dateSelected);
                    //	updateView();
                    TempDao.updateSelectedDate(dateSelected, sqliteDatabase);
                    ReplaceFragment.replace(new HomeworkView(), getFragmentManager());
                }
            }
        }
    }

}
