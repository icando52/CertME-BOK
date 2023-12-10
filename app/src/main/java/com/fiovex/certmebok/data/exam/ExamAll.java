package com.fiovex.certmebok.data.exam;

import com.fiovex.certmebok.R;
import com.fiovex.certmebok.data.question.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamAll extends Exam {
    public ExamAll(String name, String subtitle) {
        super(name, subtitle);
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return !q.getAreaCode().equals("land");
    }

    @Override
    protected int onGetColorResource() {
        return R.color.colorAll;
    }

    @Override
    public int getIconResource() {
        return R.drawable.wappen_de;
    }

    public boolean isIconColor() {
        return true;
    }



}
