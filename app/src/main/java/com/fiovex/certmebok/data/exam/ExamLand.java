package com.fiovex.certmebok.data.exam;

import com.fiovex.certmebok.R;
import com.fiovex.certmebok.controller.AppController;
import com.fiovex.certmebok.controller.Store;
import com.fiovex.certmebok.data.question.Question;

public class ExamLand extends Exam {
    private String mLand;

    public ExamLand(String name, String subtitle) {
        super(name, subtitle);
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return q.getTheme().equals(mLand);
    }

    @Override
    protected void createQuestionList() {
        String name = Store.getSelectedLandName();
        mLand = name;
        super.createQuestionList();
    }

    @Override
    public void resetQuestionList() {
        createQuestionList();
    }

    @Override
    public String getTitle(boolean showSize) {
        if (mLand == null) {
            return "Select domain...";
        }

        int n = getSize();
        return mLand + (showSize ? " (" + n + ")" : "");
    }

    @Override
    public int getIconResource() {
        String code = Store.getSelectedLandCode();
        if (code == null) return super.getIconResource();

        String name = "wappen_" + code.toLowerCase();
        int resId = AppController.getImageResourceByName(name);
        return resId;
    }

    @Override
    protected int onGetColorResource() {
        return R.color.colorLand;
    }

    public boolean isIconColor() {
        return true;
    }
}
