package com.adalbero.app.lebenindeutschland.data.question;

import android.util.Log;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.controller.AppController;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Adalbero on 17/05/2017.
 */

public class QuestionDB {
    private List<Question> mQuestions;

    public void load() {
        mQuestions = new ArrayList<>();

        int idx = 1;
        try {
            InputStream inputStream = AppController.openRawResource(R.raw.question_list);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            String header = reader.readLine();

            while ((line = reader.readLine()) != null) {
                Question q = Question.parse(line);
                mQuestions.add(q);
                idx++;
            }
        } catch (IOException e) {
            String msg = "QuestionDB.load: line:" + idx;

            Log.e("lid", msg, e);
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

            crashlytics.log("lid: " + msg);
            crashlytics.recordException(e);
        }

    }

    public Question getQuestion(String num) {
        int count = mQuestions.size();
        for (int idx=0; idx<count; idx++) {
            Question q = mQuestions.get(idx);
            if (q.getNum().equals(num)) {
                return q;
            }
        }

        return null;
    }

    public List<Question> listAll() {
        return mQuestions;
    }

    public List<String> listDistinctLand() {
        List<String> result = new ArrayList<>();

        for (Question q : mQuestions) {
            if ("land".equals(q.getAreaCode())) {
                String code = q.getNum();
                String name = q.getTheme();
                String value = code.substring(0, 2) + ";" + name;
                if (!result.contains(value)) result.add(value);
            }
        }

        Collections.sort(result, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.substring(3).compareTo(o2.substring(3));
            }
        });

        return result;
    }

    public List<String> listAllTheme() {
        List<String> result = new ArrayList<>();
        String list[] = {"13", "3", "24", "23", "1", "5", "59", "46", "95", "21", "56", "102",
                "152", "50", "191", "173", "33", "244", "297", "264"};

        for (String num : list) {
            Question q = getQuestion(num);
            result.add(q.getTheme());
        }

        return result;
    }


    public Set<String> getAllTags() {
        Set<String> tags = new TreeSet<>();

        for (Question q : mQuestions) {
            tags.addAll(q.getTags());
        }

        return tags;
    }
}
