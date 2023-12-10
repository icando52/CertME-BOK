package com.fiovex.certmebok.data.exam;

import com.fiovex.certmebok.controller.AppController;
import com.fiovex.certmebok.data.question.Question;

import java.util.List;

public class ExamArea extends Exam {
    private int mColor;

    public ExamArea(String name) {
        super(name, null);
    }

    @Override
    public String getSubtitle() {
        return "by Domain";
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return q.getArea().equals(getName());
    }

    @Override
    public int getColor() {
        if (mColor == 0) {
            if (getSize() == 0) {
                mColor = super.getColor();
            } else {
                String num = getQuestionList().get(0);
                mColor = AppController.getInstance().getQuestionDB().getQuestion(num).getAreaColor();
            }
        }

        return mColor;
    }

    @Override
    public int getIconResource() {
        return super.getIconResource();
    }

    protected String getArea() {
        return getTitle(false);
    }

}
