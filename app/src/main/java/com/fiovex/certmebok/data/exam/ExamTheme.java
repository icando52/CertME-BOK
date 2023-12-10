package com.fiovex.certmebok.data.exam;

import com.fiovex.certmebok.controller.AppController;
import com.fiovex.certmebok.data.question.Question;

/**
 * Created by Adalbero on 27/05/2017.
 */

public class ExamTheme extends ExamArea {
    public ExamTheme(String name) {
        super(name);
    }

    @Override
    public String getSubtitle() {
        return getArea();
    }

    @Override
    protected boolean onFilterQuestion(Question q) {
        return q.getTheme().equals(getName());
    }

    @Override
    protected String getArea() {
        String num = getQuestionList().get(0);
        Question question = AppController.getQuestionDB().getQuestion(num);

        return question.getArea();
    }
}
