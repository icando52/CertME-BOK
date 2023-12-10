package com.fiovex.certmebok.ui.exam;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fiovex.certmebok.R;
import com.fiovex.certmebok.controller.AppController;
import com.fiovex.certmebok.controller.Clock;
import com.fiovex.certmebok.controller.Statistics;
import com.fiovex.certmebok.controller.Store;
import com.fiovex.certmebok.data.exam.Exam;
import com.fiovex.certmebok.data.question.Question;
import com.fiovex.certmebok.data.result.ExamResult;
import com.fiovex.certmebok.data.result.ResultInfo;
import com.fiovex.certmebok.ui.common.ProgressView;
import com.fiovex.certmebok.ui.common.ResultCallback;
import com.fiovex.certmebok.ui.common.StatView;
import com.fiovex.certmebok.ui.question.QuestionActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExamActivity extends AppCompatActivity implements ResultCallback {

    private Exam mExam;
    private ExamResult mResult;
    private Clock mClock;

    private ListView mListView;
    private TextView mClockView;
    private TextView mResultView;
    private StatView mStatView;

    private List<Question> mData;
    private QuestionItemAdapter mAdapter;
    private int mSortMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exam);

        mExam = AppController.getCurrentExam();

        String title = mExam.getTitle(false);
        getSupportActionBar().setTitle(title);

        mResult = new ExamResult();

        updateData();
        mAdapter = new QuestionItemAdapter(this, mData, mResult, this);

        mClockView = findViewById(R.id.view_clock);
        mClock = new Clock(mClockView);

        mResultView = findViewById(R.id.view_result);

        mStatView = findViewById(R.id.view_stat);
        mStatView.setExam(mExam);
        mStatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goStatDialog();
            }
        });

        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                boolean inline = Store.getExamInline();
                if (!inline)
                    goQuestion(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exam_menu, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean inline = Store.getExamInline();

        menu.findItem(R.id.menu_expand).setVisible(!inline);
        menu.findItem(R.id.menu_collapse).setVisible(inline);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setInline(boolean inline) {
        Store.setExamInline(inline);

        String msg = (inline ? "Question inline mode" : "Question view mode");
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        invalidateOptionsMenu();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sort:
                goSortDialog();
                return true;
            case R.id.menu_expand:
                setInline(true);
                return true;
            case R.id.menu_collapse:
                setInline(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mResult.reset();

        int position = Store.getQuestionIdx();
        mListView.setSelection(position);

        mAdapter.notifyDataSetChanged();

        updateResult();

        boolean isNew = mClock.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mFirebaseAnalytics.setCurrentScreen(this, "Exam: " + mExam.getTitle(true), null);
    }


    @Override
    protected void onStop() {
        super.onStop();

        mClock.pause();
    }

    private void updateData() {
        List<String> questions = mExam.getQuestionList();
        mData = new ArrayList();
        if (questions != null) {
            for (String questionNum : questions) {
                Question q = AppController.getQuestionDB().getQuestion(questionNum);
                mData.add(q);
            }
        }

        if (mAdapter != null) {
            mAdapter.clear();
            mAdapter.addAll(mData);
            mAdapter.notifyDataSetChanged();

            mListView.setSelection(0);
        }
    }

    private void updateStat() {
        mStatView.setExam(mExam);
        TextView viewRating = (TextView) findViewById(R.id.view_rating);
        float rating = Statistics.getInstance().getRating(mExam.getQuestionList());

        viewRating.setText(String.format("%.0f", 100 * rating));
    }

    private void updateResult() {
        ResultInfo resultInfo = mResult.getResult();

        getSupportActionBar().setSubtitle(String.format("%d of %d", mResult.getResult().getAnswered(), mResult.getCount()));

        updateStat();

        TextView text_total_value = (TextView) findViewById(R.id.text_value1);
        text_total_value.setText(String.format("%d of %d (%.0f%%)", resultInfo.answered, resultInfo.total, resultInfo.getAnsweredPerc()));

        TextView text_answerd_value = (TextView) findViewById(R.id.text_value2);
        text_answerd_value.setText(String.format("%d of %d (%.0f%%)", resultInfo.right, resultInfo.answered, resultInfo.getRightPerc()));

        ProgressView progressView = (ProgressView) findViewById(R.id.view_progress);
        progressView.setProgress(resultInfo);

        if (resultInfo.isFinished()) {
            mClock.stop();

            if (resultInfo.isPass()) {
                mResultView.setText("Pass");
                mResultView.setTextColor(ContextCompat.getColor(this, R.color.colorRightDark));
            } else {
                mResultView.setText("Fail");
                mResultView.setTextColor(ContextCompat.getColor(this, R.color.colorWrongDark));
            }

            mResultView.setVisibility(View.VISIBLE);
        }
    }

    private void goQuestion(int idx) {
        Store.setQuestionIdx(idx);
        Intent intent = new Intent(this, QuestionActivity.class);
        this.startActivity(intent);
    }

    private void goSortDialog() {
        SortDialog dialog = new SortDialog();
        dialog.setCallback(this);
        dialog.show(this.getFragmentManager(), "sort");
    }

    private void doSort() {
        Map<String, String> answerMap = mResult.getAnswerMap();
        mExam.sortQuestionList(mSortMethod);
        mResult.setAnswerMap(answerMap);
        updateData();
    }

    public String getSortMethodText(int sortMethod) {
        if (sortMethod == 0) {
            return "sort by question shuffle";
        } else if (Math.abs(sortMethod) == 1) {
            return "sort by question number " + (sortMethod > 0 ? "ascending" : "descending");
        } else if (Math.abs(sortMethod) == 2) {
            return "sort by question rating " + (sortMethod > 0 ? "ascending" : "descending");
        } else {
            return "sort by " + sortMethod;
        }
    }

    @Override
    public void onResult(Object parent, Object param) {
        if ("SORT".equals(parent)) {
            try {
                mSortMethod = (int) param;
            } catch (Exception ex) {
            }

            doSort();
            return;
        }

        updateResult();
    }

    private void goStatDialog() {
        ExamStatDialog dialog = new ExamStatDialog();
        dialog.setExamp(mExam);
        dialog.show(this.getFragmentManager(), "stat");
    }
}
