package com.fiovex.certmebok.controller;

import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDexApplication;
import android.util.DisplayMetrics;
import com.fiovex.certmebok.data.exam.Exam;
import com.fiovex.certmebok.data.exam.ExamAll;
import com.fiovex.certmebok.data.exam.ExamArea;
import com.fiovex.certmebok.data.exam.ExamExercise;
import com.fiovex.certmebok.data.exam.ExamHeader;
import com.fiovex.certmebok.data.exam.ExamLand;
import com.fiovex.certmebok.data.exam.ExamRandom;
import com.fiovex.certmebok.data.exam.ExamSearch;
import com.fiovex.certmebok.data.exam.ExamStat;
import com.fiovex.certmebok.data.exam.ExamTag;
import com.fiovex.certmebok.data.exam.ExamTheme;
import com.fiovex.certmebok.data.question.QuestionDB;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AppController extends MultiDexApplication {

    private static AppController mInstance;

    private QuestionDB mQuestionDB;
    private List<Exam> mExamList;
    private int mExamIdx;

    public static AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        loadQuestionDB();
        loadExamList();
    }

    public static QuestionDB getQuestionDB() {
        AppController app = getInstance();

        if (app.mQuestionDB == null) {
            loadQuestionDB();
        }

        return app.mQuestionDB;
    }

    private static void loadQuestionDB() {
        AppController app = getInstance();

        app.mQuestionDB = new QuestionDB();
        app.mQuestionDB.load();
    }

    public static List<Exam> getExamList() {
        AppController app = getInstance();

        if (app.mExamList == null) {
            loadExamList();
        }

        return app.mExamList;
    }

    public static void loadExamList() {

        List<Exam> examList = new ArrayList<>();

        examList.add(new ExamHeader("Question Banks"));

        examList.add(new ExamAll("All", "All general questions"));
        examList.add(new ExamLand("Domain", "Questions specific to specific domains"));
        examList.add(new ExamExercise("Randomized Test", "Random questions not answered yet"));
        examList.add(new ExamRandom("Practice Test", "Exam simulator. 60 questions in 60 minutes."));

        examList.add(new ExamHeader("By Domain"));
        examList.add(new ExamArea("D1.0 General Security Concepts 12%"));
        examList.add(new ExamArea("D2.0 Threats, Vulnerabilities, and Mitigations 22%"));
        examList.add(new ExamArea("D3.0 Security Architecture 18%"));
        examList.add(new ExamArea("D4.0 Security Operations 28%"));
        examList.add(new ExamArea("D5.0 Security Program Management and Oversight 20%"));

        examList.add(new ExamHeader("By Exam Objective"));
        List<String> themes = getQuestionDB().listAllTheme();
        for (String theme : themes) {
            examList.add(new ExamTheme(theme));
        }

        examList.add(new ExamHeader("Filter"));
        examList.add(new ExamSearch("Search", "Search questions by keyword"));
        examList.add(new ExamTag("Tags", "Search questions by tag"));

        examList.add(new ExamHeader("Statistics"));
        examList.add(new ExamStat("At least once wrong", "Questions answered at least one time wrong", ExamStat.FILTER_ONCE_WRONG));
        examList.add(new ExamStat("Mostly wrong", "Questions answered more wrong than right", ExamStat.FILTER_MOSTLY_WRONG));
        examList.add(new ExamStat("Last answer wrong", "Questions answered wrong the last time", ExamStat.FILTER_LAST_WRONG));
        examList.add(new ExamStat("Not answered yet", "Questions never answered", ExamStat.FILTER_NOT_ANSWERED));
        examList.add(new ExamStat("Last answer right", "Questions answered right the last time", ExamStat.FILTER_LAST_RIGHT));
        examList.add(new ExamStat("Mostly right", "Questions answered more right than wrong", ExamStat.FILTER_MOSTLY_RIGHT));

        examList.add(new ExamHeader(""));

        getInstance().mExamList = examList;
    }

    public static Exam getCurrentExam() {
        String name = Store.getExamName();
        return getExam(name);
    }

    public static Exam getExam(String name) {
        if (name != null) {
            for (Exam exam : getExamList()) {
                if (exam.getName().equals(name)) {
                    return exam;
                }
            }
        }

        return null;
    }

    public static int getImageResourceByName(String name) {
        return getInstance().getResources().getIdentifier(name, "drawable", getInstance().getPackageName());
    }

    public static Drawable getCompatDrawable(int resId) {
        return ContextCompat.getDrawable(getInstance(), resId);
    }

    public static int getCompatColor(int resId) {
        return ContextCompat.getColor(getInstance(), resId);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return getInstance().getResources().getDisplayMetrics();
    }

    public static InputStream openRawResource(int resId) {
        return getInstance().getResources().openRawResource(resId);
    }

    public static void setExamIdx(int idx) {
        getInstance().mExamIdx = idx;
    }

    public static int getExamIdx() {
        return getInstance().mExamIdx;
    }
}
