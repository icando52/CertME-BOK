package com.fiovex.certmebok.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.fiovex.certmebok.R;
import com.fiovex.certmebok.controller.AppController;
import com.fiovex.certmebok.controller.Dialog;
import com.fiovex.certmebok.controller.Store;
import com.fiovex.certmebok.data.exam.Exam;
import com.fiovex.certmebok.data.exam.ExamHeader;
import com.fiovex.certmebok.data.exam.ExamLand;
import com.fiovex.certmebok.data.exam.ExamSearch;
import com.fiovex.certmebok.data.exam.ExamTag;
import com.fiovex.certmebok.ui.common.ResultCallback;
import com.fiovex.certmebok.ui.exam.ExamActivity;
import com.fiovex.certmebok.ui.settings.SettingsActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ResultCallback {

    private List<Exam> data;
    private ExamItemAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();

        updateData();

        mAdapter = new ExamItemAdapter(this, data);

        mListView = findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                onItemSelected(position);
            }
        });
    }

    private void init() {
        if (Store.getString(ExamSearch.KEY, null) == null) {
            List<String> terms = Arrays.asList(new String[]{""});
            Store.setList(ExamSearch.KEY, terms);
        }

        if (Store.getString(ExamTag.KEY, null) == null) {
            List<String> terms = Arrays.asList(new String[]{"image"});
            Store.setList(ExamTag.KEY, terms);
        }
    }

    private void onItemSelected(int position) {
        Exam exam = data.get(position);

        if (exam instanceof ExamHeader) return;

        AppController.setExamIdx(position);

        if (exam.onPrompt(this, this)) {
            return;
        } else if (exam instanceof ExamLand) {
            String land = Store.getSelectedLandCode();
            if (land == null) {
                doSelectLand();
                return;
            }
        }

        goExam(exam.getName());
    }

    private void updateData() {

        if (data == null) {
            data = new ArrayList();
            for (Exam exam : AppController.getExamList()) {
                data.add(exam);
            }
        } else {
            for (Exam exam : data) {
                exam.resetQuestionList();
            }

            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                goSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mListView.setSelection(AppController.getExamIdx());

        Store.resetExamResult();

        if (Store.getLand() == null) {
            doSelectLand();
        }

        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mFirebaseAnalytics.setCurrentScreen(this, "Main", null);
    }

    private void goSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void goExam(String examName) {
        Exam exam = AppController.getExam(examName);

        if (exam.getSize() == 0) {
            Dialog.promptDialog(this, "No questions in list " + exam.getTitle(false));
            updateData();
            return;
        }

        Store.setExamName(examName);
        Store.setQuestionIdx(0);

        Intent intent = new Intent(this, ExamActivity.class);
        this.startActivity(intent);
    }

    private void doSelectLand() {
        WelcomeDialog dialog = new WelcomeDialog();
        dialog.setCallback(this);
        dialog.show(this.getFragmentManager(), "land");
    }

    @Override
    public void onResult(Object parent, Object param) {

        if (parent instanceof Exam) {
            String name = (String) param;

            Exam exam = (Exam) parent;

            goExam(name);
        } else if (parent instanceof WelcomeDialog) {
            String land = (String) param;
            updateData();
        }
    }

//    private void doTest() {
//        doSelectLand();
//    }
}
