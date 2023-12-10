package com.fiovex.certmebok.ui.main;

import static android.graphics.Typeface.*;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fiovex.certmebok.R;
import com.fiovex.certmebok.controller.AppController;
import com.fiovex.certmebok.ui.common.StatView;
import com.fiovex.certmebok.controller.Statistics;
import com.fiovex.certmebok.data.exam.Exam;
import com.fiovex.certmebok.data.exam.ExamHeader;

import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class ExamItemAdapter extends ArrayAdapter<Exam> {
    private final LayoutInflater mInflater;

    public Statistics mStat;

    public ExamItemAdapter(Context context, List<Exam> data) {
        super(context, R.layout.exam_item, data);

        mInflater = LayoutInflater.from(context);
        mStat = Statistics.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;

        view = mInflater.inflate(R.layout.exam_item, parent, false);

        final Exam exam = getItem(position);

        String name = exam.getTitle(true);
        int style = NORMAL;
        int color = Color.TRANSPARENT;
        int visible = View.VISIBLE;
        int gravity = Gravity.LEFT;

        if (exam instanceof ExamHeader) {
            style = Typeface.BOLD + Typeface.ITALIC;
            visible = View.GONE;
            name = "\n" + name;
            gravity = Gravity.CENTER_HORIZONTAL;
        } else {
            color = exam.getColor();
        }


        StatView viewStat = view.findViewById(R.id.view_stat);
        viewStat.setExam(exam);
        viewStat.setVisibility(visible);

        View item_view = view.findViewById(R.id.item_view);
        GradientDrawable background = (GradientDrawable) AppController.getCompatDrawable(R.drawable.shape_roundrect);
        background.setColor(color);
        item_view.setBackground(background);

        TextView text_name = view.findViewById(R.id.text_name);
        text_name.setText(name);
        text_name.setTypeface(null, style);
        text_name.setGravity(gravity);

        TextView text_subtitle = view.findViewById(R.id.text_subtitle);
        String subtitle = exam.getSubtitle();
        if (subtitle == null) {
            text_subtitle.setVisibility(View.GONE);
        } else {
            text_subtitle.setVisibility(View.VISIBLE);
            text_subtitle.setText(subtitle);
            text_subtitle.setGravity(gravity);
        }

        View btn_details = view.findViewById(R.id.img_details);
        btn_details.setVisibility(visible);

        return view;
    }

}
