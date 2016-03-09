package in.principal.fragment;

import in.principal.activity.R;
import in.principal.activity.R.animator;
import in.principal.dao.TempDao;
import in.principal.sqlite.ExpChild;
import in.principal.sqlite.ExpGroup;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * Created by vinkrish.
 * I would write this class a better way if i've to start over again, optimize it if you can.
 */
@SuppressLint("InflateParams")
public class CoScholastic extends Fragment {

    private Context context;
    private ExpandListAdapter ExpAdapter;
    private ArrayList<ExpGroup> ExpListItems;
    private ExpandableListView ExpandList;
    private String className, secName;
    @SuppressWarnings("unused")
    private int childPos, termWise = 0, Term, SecHeadingId, TopicId, AspectId, CoScholasticId, classId;
    private ArrayList<Integer> termList = new ArrayList<>();
    private ArrayList<Integer> secHeadingList = new ArrayList<>();
    private ArrayList<String> secNameList = new ArrayList<>();
    private ArrayList<Integer> topicList = new ArrayList<>();
    private ArrayList<String> topicNameList = new ArrayList<>();
    private ArrayList<Integer> aspectList = new ArrayList<>();
    private ArrayList<String> aspectNameList = new ArrayList<>();
    private SqlDbHelper sqlHandler;
    private SQLiteDatabase sqliteDatabase;
    private int[] child = new int[5];
    private int[] childPosArr = new int[]{-1, -1, -1, -1, -1};
    private ProgressDialog pDialog;
    private SparseArray<ArrayList<Integer>> termMap = new SparseArray<>();
    private SparseArray<ArrayList<Integer>> secMap = new SparseArray<>();
    private SparseArray<ArrayList<Integer>> topicMap = new SparseArray<>();
    private SparseArray<ArrayList<String>> termMaps = new SparseArray<>();
    private SparseArray<ArrayList<String>> secMaps = new SparseArray<>();
    private SparseArray<ArrayList<String>> topicMaps = new SparseArray<>();
    private ArrayList<Integer> termComplete = new ArrayList<>();
    private ArrayList<Integer> secComplete = new ArrayList<>();
    private ArrayList<Integer> topicComplete = new ArrayList<>();
    private SparseIntArray termCompleteMap = new SparseIntArray();
    private SparseIntArray secCompleteMap = new SparseIntArray();
    private SparseIntArray topicCompleteMap = new SparseIntArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coscholastic, container, false);
        context = AppGlobal.getContext();
        sqlHandler = AppGlobal.getSqlDbHelper();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(this.getActivity());

        Temp t = TempDao.selectTemp(sqliteDatabase);
        classId = t.getClassId();
        className = t.getClassName();
        secName = t.getSectionName();

        Button perfClas = (Button) view.findViewById(R.id.seClass);
        perfClas.setText("Class " + className);
        Button perfSe = (Button) view.findViewById(R.id.seSec);
        perfSe.setText("Section " + secName);

        setCoScholasticId();
        new CalledBackLoad().execute();

        ExpandList = (ExpandableListView) view.findViewById(R.id.expandibles);
        ExpandList.setGroupIndicator(null);
        ExpListItems = SetStandardGroups();
        ExpAdapter = new ExpandListAdapter(context, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);

        ExpandList.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                //	childPos = child[groupPosition-1];
                for (int i = groupPosition; i < 5; i++) {
                    childPosArr[i] = -1;
                }
                if (groupPosition == 1) {
                    if (childPosArr[0] != -1) {
                        groupFirst();
                    }
                } else if (groupPosition == 2) {
                    if (childPosArr[1] != -1) {
                        groupSecond();
                    }
                } else if (groupPosition == 3) {
                    if (childPosArr[2] != -1) {
                        groupThird();
                    }
                } else if (groupPosition == 4) {
                    if (childPosArr[3] != -1) {
                        groupFourth();
                    }
                }

                return false;
            }
        });

        ExpandList.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                //	childPos = childPosition;
                //	v.setBackgroundColor(Color.BLUE);
                //	Log.d("parent-child", ExpGroup.get(groupPosition) +
                //			ExpChild.get(ExpGroup.get(groupPosition)).get(childPosition));
                for (int i = groupPosition; i < 5; i++) {
                    childPosArr[i] = -1;
                }
                if (groupPosition == 0) {
                    childPosArr[0] = childPosition;
                    groupFirst();
                } else if (groupPosition == 1) {
                    childPosArr[1] = childPosition;
                    groupSecond();
                } else if (groupPosition == 2) {
                    childPosArr[2] = childPosition;
                    groupThird();
                } else if (groupPosition == 3) {
                    childPosArr[3] = childPosition;
                    groupFourth();
                } else {
                    childPosArr[4] = childPosition;
                    groupFifth();
                }
                return false;
            }
        });

        return view;
    }

    private void groupFirst() {
        //	child[0] = childPos;
        child[0] = childPosArr[0];
        ExpListItems.clear();
        ExpListItems = new ArrayList<>();
        int[] img1 = {R.drawable.cross, R.drawable.tick};
        String[] header = {"Select Evaluation", "Select Term", "Select Section Heading", "Select Topic", "Select Aspect"};
        String[] h = {"Select Evaluation", "Term wise", "Exam wise"};
        ArrayList<ExpChild> ch_list;
        for (int i = 0; i < 5; i++) {
            ch_list = new ArrayList<>();
            if (i == 0) {
                ExpChild ec0 = new ExpChild();
                ec0.setText(h[0]);
                ch_list.add(ec0);
                ExpChild ec1 = new ExpChild();
                ec1.setText(h[1]);
                ch_list.add(ec1);
                ExpChild ec2 = new ExpChild();
                ec2.setText(h[2]);
                ch_list.add(ec2);
            } else if (i == 1) {
                //	if(childPos==1){
                if (childPosArr[0] == 1) {
                    int temp = 0;
                    for (Integer term : termList) {
                        ExpChild e = new ExpChild();
                        if (termCompleteMap.get(termList.get(temp)) == 1) {
                            e.setSelectedChild(1);
                        } else {
                            e.setSelectedChild(0);
                        }
                        e.setText("Term " + term);
                        ch_list.add(e);
                    }
                }
            }
            ExpGroup eg = new ExpGroup();
            if (i == 0) {
                eg.setImage1(img1[1]);
                eg.setText1(h[childPosArr[0]]);
            } else {
                eg.setImage1(img1[0]);
                eg.setText1(header[i]);
            }
            eg.setItems(ch_list);
            ExpListItems.add(eg);
        }
        ExpAdapter = new ExpandListAdapter(context, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        //	ExpAdapter.notifyDataSetChanged();
    }

    private void groupSecond() {
        Term = termList.get(childPosArr[1]);
        child[1] = childPosArr[1];
        ExpListItems.clear();
        ExpListItems = new ArrayList<>();
        int[] img1 = {R.drawable.cross, R.drawable.tick};
        String[] header = {"Select Evaluation", "Select Term", "Select Section Heading", "Select Topic", "Select Aspect"};
        String[] h = {"Select Evaluation", "Term wise", "Exam wise"};
        ArrayList<ExpChild> ch_list;
        for (int i = 0; i < 5; i++) {
            /*for(int a = 0, nsize = termMap.size(); a < nsize; a++) {
			    Log.d("iterate", termMap.valueAt(a)+"");
			}*/
            ch_list = new ArrayList<>();
            if (i == 0) {
                ExpChild ec0 = new ExpChild();
                ec0.setText(h[0]);
                ch_list.add(ec0);
                ExpChild ec1 = new ExpChild();
                ec1.setText(h[1]);
                ch_list.add(ec1);
                ExpChild ec2 = new ExpChild();
                ec2.setText(h[2]);
                ch_list.add(ec2);
            } else if (i == 1) {
                int temp = 0;
                for (Integer term : termList) {
                    ExpChild e = new ExpChild();
                    if (termCompleteMap.get(termList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText("Term " + term);
                    ch_list.add(e);
                }
            } else if (i == 2) {
                secHeadingList = termMap.get(Term);
                secNameList = termMaps.get(Term);
				/*for(int intt = 0, nsize = termMap.size(); intt < nsize; intt++) {
					if(Term==termMap.keyAt(intt)){
						secHeadingList = termMap.valueAt(intt);
						secNameList = termMaps.valueAt(intt);
					}
				}*/
                int temp = 0;
                for (String secName : secNameList) {
                    ExpChild e = new ExpChild();
                    if (secCompleteMap.get(secHeadingList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText(secName);
                    ch_list.add(e);
                    temp += 1;
                }
            }
            ExpGroup eg = new ExpGroup();
            if (i == 0) {
                eg.setImage1(img1[1]);
                eg.setText1(h[child[0]]);
            } else if (i == 1) {
                eg.setImage1(img1[1]);
                eg.setText1("Term " + termList.get(childPosArr[1]));
            } else {
                eg.setImage1(img1[0]);
                eg.setText1(header[i]);
            }
            eg.setItems(ch_list);
            ExpListItems.add(eg);
        }
        ExpAdapter = new ExpandListAdapter(context, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        //	ExpAdapter.notifyDataSetChanged();
    }

    private void groupThird() {
        SecHeadingId = secHeadingList.get(childPosArr[2]);
        child[2] = childPosArr[2];
        ExpListItems.clear();
        ExpListItems = new ArrayList<>();
        int[] img1 = {R.drawable.cross, R.drawable.tick};
        String[] header = {"Select Evaluation", "Select Term", "Select Section Heading", "Select Topic", "Select Aspect"};
        String[] h = {"Select Evaluation", "Term wise", "Exam wise"};
        ArrayList<ExpChild> ch_list;
        for (int i = 0; i < 5; i++) {
            ch_list = new ArrayList<>();
            if (i == 0) {
                ExpChild ec0 = new ExpChild();
                ec0.setText(h[0]);
                ch_list.add(ec0);
                ExpChild ec1 = new ExpChild();
                ec1.setText(h[1]);
                ch_list.add(ec1);
                ExpChild ec2 = new ExpChild();
                ec2.setText(h[2]);
                ch_list.add(ec2);
            } else if (i == 1) {
                int temp = 0;
                for (Integer term : termList) {
                    ExpChild e = new ExpChild();
                    if (termCompleteMap.get(termList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText("Term " + term);
                    ch_list.add(e);
                }
            } else if (i == 2) {
                secHeadingList = termMap.get(Term);
                secNameList = termMaps.get(Term);
                int temp = 0;
                for (String secName : secNameList) {
                    ExpChild e = new ExpChild();
                    if (secCompleteMap.get(secHeadingList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText(secName);
                    ch_list.add(e);
                    temp += 1;
                }
            } else if (i == 3) {
                topicList = secMap.get(SecHeadingId);
                topicNameList = secMaps.get(SecHeadingId);
                int temp = 0;
                for (String topicName : topicNameList) {
                    ExpChild e = new ExpChild();
                    if (topicCompleteMap.get(topicList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText(topicName);
                    ch_list.add(e);
                    temp += 1;
                }
            }
            ExpGroup eg = new ExpGroup();
            if (i == 0) {
                eg.setImage1(img1[1]);
                eg.setText1(h[child[0]]);
            } else if (i == 1) {
                eg.setImage1(img1[1]);
                eg.setText1("Term " + termList.get(child[1]));
            } else if (i == 2) {
                eg.setImage1(img1[1]);
                eg.setText1(secNameList.get(childPosArr[2]));
            } else {
                eg.setImage1(img1[0]);
                eg.setText1(header[i]);
            }
            eg.setItems(ch_list);
            ExpListItems.add(eg);
        }
        ExpAdapter = new ExpandListAdapter(context, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        //	ExpAdapter.notifyDataSetChanged();
    }

    private void groupFourth() {
        TopicId = topicList.get(childPosArr[3]);
        child[3] = childPosArr[3];
        ExpListItems.clear();
        ExpListItems = new ArrayList<>();
        int[] img1 = {R.drawable.cross, R.drawable.tick};
        String[] header = {"Select Evaluation", "Select Term", "Select Section Heading", "Select Topic", "Select Aspect"};
        String[] h = {"Select Evaluation", "Term wise", "Exam wise"};
        ArrayList<ExpChild> ch_list;
        for (int i = 0; i < 5; i++) {
            ch_list = new ArrayList<>();
            if (i == 0) {
                ExpChild ec0 = new ExpChild();
                ec0.setText(h[0]);
                ch_list.add(ec0);
                ExpChild ec1 = new ExpChild();
                ec1.setText(h[1]);
                ch_list.add(ec1);
                ExpChild ec2 = new ExpChild();
                ec2.setText(h[2]);
                ch_list.add(ec2);
            } else if (i == 1) {
                int temp = 0;
                for (Integer term : termList) {
                    ExpChild e = new ExpChild();
                    if (termCompleteMap.get(termList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText("Term " + term);
                    ch_list.add(e);
                }
            } else if (i == 2) {
                secHeadingList = termMap.get(Term);
                secNameList = termMaps.get(Term);
                int temp = 0;
                for (String secName : secNameList) {
                    ExpChild e = new ExpChild();
                    if (secCompleteMap.get(secHeadingList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText(secName);
                    ch_list.add(e);
                    temp += 1;
                }
            } else if (i == 3) {
				/*for(int a = 0, nsize = topicCompleteMap.size(); a < nsize; a++) {
					Log.d("iterate", topicCompleteMap.valueAt(a)+"");
				}*/
                topicList = secMap.get(SecHeadingId);
                topicNameList = secMaps.get(SecHeadingId);
                int temp = 0;
                for (String topicName : topicNameList) {
                    ExpChild e = new ExpChild();
                    if (topicCompleteMap.get(topicList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText(topicName);
                    ch_list.add(e);
                    temp += 1;
                }
            } else if (i == 4) {
                Cursor c = sqliteDatabase.rawQuery("select * from cceaspectprimary where TopicId=" + TopicId, null);
                c.moveToFirst();
                aspectList.clear();
                aspectNameList.clear();
                while (!c.isAfterLast()) {
                    aspectList.add(c.getInt(c.getColumnIndex("AspectId")));
                    aspectNameList.add(c.getString(c.getColumnIndex("AspectName")));
                    c.moveToNext();
                }
                c.close();
                int temp = 0;
                for (String aspectName : aspectNameList) {
                    ExpChild e = new ExpChild();
                    if (sqlHandler.isThereCoSchGrade(aspectList.get(temp)) == 1) {
                        e.setSelectedChild(1);
                    } else {
                        e.setSelectedChild(0);
                    }
                    e.setText(aspectName);
                    ch_list.add(e);
                    temp += 1;
                }
            }
            ExpGroup eg = new ExpGroup();
            if (i == 0) {
                eg.setImage1(img1[1]);
                eg.setText1(h[child[0]]);
            } else if (i == 1) {
                eg.setImage1(img1[1]);
                eg.setText1("Term " + termList.get(child[1]));
            } else if (i == 2) {
                eg.setImage1(img1[1]);
                eg.setText1(secNameList.get(child[2]));
            } else if (i == 3) {
                eg.setImage1(img1[1]);
                eg.setText1(topicNameList.get(childPosArr[3]));
            } else {
                eg.setImage1(img1[0]);
                eg.setText1(header[i]);
            }
            eg.setItems(ch_list);
            ExpListItems.add(eg);
        }
        ExpAdapter = new ExpandListAdapter(context, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        //	ExpAdapter.notifyDataSetChanged();
    }

    private void groupFifth() {
        AspectId = aspectList.get(childPosArr[4]);
        int isPresent = sqlHandler.isThereCoSchGrade(AspectId);
        if (isPresent == 1) {
            Bundle b = new Bundle();
            b.putInt("Term", Term);
            b.putInt("TopicId", TopicId);
            b.putInt("AspectId", AspectId);

            Fragment fragment = new ViewCCSGrade();
            fragment.setArguments(b);
            getFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(animator.fade_in, animator.fade_out)
                    .replace(R.id.content_frame, fragment).addToBackStack(null).commit();

        }
    }

    private void setCoScholasticId() {
        boolean found = false;
        String s = "";
        Cursor c = sqliteDatabase.rawQuery("select CoScholasticId, ClassIDs from ccecoscholastic", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            s = c.getString(c.getColumnIndex("ClassIDs"));
            String[] sArray = s.split(",");
            for (String str : sArray) {
                if (str.equals(classId + "")) {
                    CoScholasticId = c.getInt(c.getColumnIndex("CoScholasticId"));
                    found = true;
                    break;
                }
            }
            if (found) {
                c.moveToLast();
            }
            c.moveToNext();
        }
        c.close();
    }

    private ArrayList<ExpGroup> SetStandardGroups() {
        int[] img1 = {R.drawable.cross, R.drawable.tick};
        String[] header = {"Select Evaluation", "Select Term", "Select Section Heading", "Select Topic", "Select Aspect"};
        ArrayList<ExpGroup> grp_list = new ArrayList<>();
        ArrayList<ExpChild> ch_list;
        for (int i = 0; i < 5; i++) {
            ch_list = new ArrayList<>();
            if (i == 0) {
                ExpChild ec0 = new ExpChild();
                ec0.setText("Select Evaluation");
                ch_list.add(ec0);
                ExpChild ec1 = new ExpChild();
                ec1.setText("Term wise");
                ch_list.add(ec1);
                ExpChild ec2 = new ExpChild();
                ec2.setText("Exam wise");
                ch_list.add(ec2);
            }
            ExpGroup eg = new ExpGroup();
            eg.setImage1(img1[0]);
            eg.setText1(header[i]);
            eg.setItems(ch_list);
            grp_list.add(eg);
        }
        return grp_list;
    }

    public class ExpandListAdapter extends BaseExpandableListAdapter {
        private Context context;
        private ArrayList<ExpGroup> groups;

        public ExpandListAdapter(Context context, ArrayList<ExpGroup> groups) {
            this.context = context;
            this.groups = groups;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            ArrayList<ExpChild> chList = groups.get(groupPosition).getItems();
            return chList.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            ExpChild child = (ExpChild) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.child_item, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.childText1);
            tv.setText(child.getText());

            if (child.getSelectedChild() == 1) {
                //	tv.setSelected(true);
                tv.setTextColor(getResources().getColor(R.color.green));
                //	tv.setBackgroundColor(getResources().getColor(R.color.green));
            }

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            ArrayList<ExpChild> chList = groups.get(groupPosition).getItems();
            return chList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            ExpGroup group = (ExpGroup) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.group_item, null);
            }

            ImageView iv1 = (ImageView) convertView.findViewById(R.id.img1);
            iv1.setImageResource(group.getImage1());
            TextView tv = (TextView) convertView.findViewById(R.id.text1);
            tv.setText(group.getText1());
            //	ImageView iv2 = (ImageView) convertView.findViewById(R.id.img2);

            if (isExpanded && getChildrenCount(groupPosition) > 0) {
                convertView.setPadding(0, 0, 0, 0);
                //	iv2.setImageResource(R.drawable.tick);
            } else {
                convertView.setPadding(0, 0, 0, 20);
                //	iv2.setImageResource(R.drawable.cross);
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
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

            Cursor c1 = sqliteDatabase.rawQuery("select distinct Term from exams where ClassId=" + classId, null);
            c1.moveToFirst();
            termList.clear();
            while (!c1.isAfterLast()) {
                termList.add(c1.getInt(c1.getColumnIndex("Term")));
                c1.moveToNext();
            }
            c1.close();

            for (Integer t : termList) {
                Cursor c2 = sqliteDatabase.rawQuery("select * from ccesectionheading where CoScholasticId=" + CoScholasticId, null);
                c2.moveToFirst();
                secHeadingList.clear();
                secNameList.clear();
                while (!c2.isAfterLast()) {
                    secHeadingList.add(c2.getInt(c2.getColumnIndex("SectionHeadingId")));
                    secNameList.add(c2.getString(c2.getColumnIndex("SectionName")));
                    c2.moveToNext();
                }
                c2.close();

                for (Integer s : secHeadingList) {
                    Cursor c3 = sqliteDatabase.rawQuery("select * from ccetopicprimary where SectionHeadingId=" + s, null);
                    c3.moveToFirst();
                    topicList.clear();
                    topicNameList.clear();
                    while (!c3.isAfterLast()) {
                        topicList.add(c3.getInt(c3.getColumnIndex("TopicId")));
                        topicNameList.add(c3.getString(c3.getColumnIndex("TopicName")));
                        c3.moveToNext();
                    }
                    c3.close();

                    for (Integer top : topicList) {
                        Cursor c4 = sqliteDatabase.rawQuery("select * from cceaspectprimary where TopicId=" + top, null);
                        c4.moveToFirst();
                        aspectList.clear();
                        aspectNameList.clear();
                        while (!c4.isAfterLast()) {
                            aspectList.add(c4.getInt(c4.getColumnIndex("AspectId")));
                            aspectNameList.add(c4.getString(c4.getColumnIndex("AspectName")));
                            c4.moveToNext();
                        }
                        c4.close();

                        //	aspectComplete.clear();
                        int topComp = 0;
                        for (Integer a : aspectList) {
                            if (sqlHandler.isThereCoSchGrade(a) == 1) {
                                topComp = 1;
                            } else {
                                topComp = 0;
                            }
                        }
                        topicMap.put(top, aspectList);
                        topicMaps.put(top, aspectNameList);
                        topicCompleteMap.put(top, topComp);
                        topicComplete.add(topComp);
                    }
                    int secComp = 0;
                    secMap.put(s, topicList);
                    secMaps.put(s, topicNameList);
                    if (topicComplete.contains(0)) {
                        secComplete.add(0);
                    } else {
                        secComp = 1;
                        secComplete.add(1);
                    }
                    secCompleteMap.put(s, secComp);
                    secComplete.add(secComp);
                }
                int termComp = 0;
                termMap.put(t, secHeadingList);
                termMaps.put(t, secNameList);
                if (secComplete.contains(0)) {
                    termComplete.add(0);
                } else {
                    termComp = 1;
                    termComplete.add(1);
                }
                termCompleteMap.put(t, termComp);
                termComplete.add(termComp);
            }

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
        }
    }

}
