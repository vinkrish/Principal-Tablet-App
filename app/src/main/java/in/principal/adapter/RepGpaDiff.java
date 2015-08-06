package in.principal.adapter;

import in.principal.activity.R;
import in.principal.sqlite.RepDiffColor;
import in.principal.sqlite.RepDiffSub;
import in.principal.sqlite.ReportColor;
import in.principal.sqlite.ReportSub;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RepGpaDiff extends ArrayAdapter<ReportSub> {
    int resource;
    Context context;
    ArrayList<ReportSub> data = new ArrayList<>();
    ArrayList<ReportColor> color = new ArrayList<>();
    ArrayList<RepDiffSub> data2 = new ArrayList<>();
    ArrayList<RepDiffColor> color2 = new ArrayList<>();

    public RepGpaDiff(Context context, int resource, ArrayList<ReportSub> listArray, ArrayList<ReportColor> colors, ArrayList<RepDiffSub> listArray2, ArrayList<RepDiffColor> colors2) {
        super(context, resource, listArray);
        this.context = context;
        this.resource = resource;
        this.data = listArray;
        this.color = colors;
        this.data2 = listArray2;
        this.color2 = colors2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);

            holder = new RecordHolder();
            holder.txt1 = (TextView) row.findViewById(R.id.txt1);
            holder.txt2 = (TextView) row.findViewById(R.id.txt2);
            holder.txt3 = (TextView) row.findViewById(R.id.txt3);
            holder.pb = (ProgressBar) row.findViewById(R.id.avgProgress);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        if (position % 2 == 0) {
            row.setBackgroundColor(Color.rgb(255, 255, 255));
        } else {
            row.setBackgroundColor(Color.rgb(237, 239, 242));
        }

        ReportSub listItem = data.get(position);
        ReportColor listColor = color.get(position);
        holder.txt1.setText(listItem.getText1());
        holder.txt2.setText(listItem.getText2());

        RepDiffSub listItem2 = data2.get(position);
        RepDiffColor listColor2 = color2.get(position);
        holder.int1.setText(listItem2.getInt1() + "");
        if (listColor2.getColor1().equals("green")) {
            holder.int1.setBackground(context.getResources().getDrawable(R.drawable.green_circle));
        } else if (listColor2.getColor1().equals("red")) {
            holder.int1.setBackground(context.getResources().getDrawable(R.drawable.red_circle));
        } else {
            holder.int1.setBackground(context.getResources().getDrawable(R.drawable.blue_circle));
        }

        holder.txt3.setText(listItem.getText3());
        if (listColor.getColor1().equals("green")) {
            holder.txt3.setTextColor(context.getResources().getColor(R.color.green));
        } else if (listColor.getColor1().equals("orange")) {
            holder.txt3.setTextColor(context.getResources().getColor(R.color.orange));
        } else {
            holder.txt3.setTextColor(context.getResources().getColor(R.color.red));
        }

        if (listItem.getInt1() >= 75) {
            holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_green));
        } else if (listItem.getInt1() >= 50) {
            holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_orange));
        } else {
            holder.pb.setProgressDrawable(context.getResources().getDrawable(R.drawable.progress_red));
        }
        holder.pb.setProgress(listItem.getInt1());

        return row;
    }

    static class RecordHolder {
        TextView txt1;
        TextView txt2;
        TextView txt3;
        TextView int1;
        ProgressBar pb;
    }

    public void updateColor(GradientDrawable d, String color) {
        if (color.equals("green")) {
            d.setColor(context.getResources().getColor(R.color.green));
        } else if (color.equals("red")) {
            d.setColor(context.getResources().getColor(R.color.red));
        } else if (color.equals("blue")) {
            d.setColor(context.getResources().getColor(R.color.universal));
        }
    }

}
