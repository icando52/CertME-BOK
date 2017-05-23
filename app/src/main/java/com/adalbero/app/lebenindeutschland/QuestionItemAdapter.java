package com.adalbero.app.lebenindeutschland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adalbero.app.lebenindeutschland.data.Exam;
import com.adalbero.app.lebenindeutschland.data.Question;

import java.util.List;

/**
 * Created by Adalbero on 16/05/2017.
 */

public class QuestionItemAdapter extends ArrayAdapter<Question> {
    private final LayoutInflater mInflater;
    private Exam mExam;
    private ResultCallback mCallback;

    public QuestionItemAdapter(Context context, List<Question> objects, Exam exam, ResultCallback callback) {
        super(context, R.layout.question_item, objects);

        mInflater = LayoutInflater.from(context);
        mExam = exam;
        mCallback = callback;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.question_item, parent, false);
        } else {
            view = convertView;
        }

        Question question = getItem(position);

        QuestionViewHolder holder = new QuestionViewHolder(view, mExam, false, mCallback);
        holder.show(question);

        return view;
    }

}
