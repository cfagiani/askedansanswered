package com.cataractsoftware.askandanswered;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.TextView;
import com.cataractsoftware.askandanswered.entity.Question;
import com.cataractsoftware.askandanswered.model.QuestionViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String CUR_ID_KEY = "questionId";
    private static final String LOG_TAG = "MainActivity";
    private QuestionViewModel questionViewModel;
    private String answerPrompt;
    private TextView categoryText;
    private TextView questionText;
    private TextView answerText;
    private Question currentQuestion;
    private GestureDetector gestureDetector;
    private String source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        answerPrompt = getResources().getString(R.string.show_ans_prompt);
        questionViewModel = ViewModelProviders.of(this).get(QuestionViewModel.class);
        categoryText = findViewById(R.id.categoryText);
        questionText = findViewById(R.id.questionText);
        answerText = findViewById(R.id.answerText);
        gestureDetector = new GestureDetector(this, new SwipeListnener());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setQuestionSourceFromPreferences();
        questionViewModel.getCurrentQuestion().observe(this, (question)-> {
            if (question != null) {
                currentQuestion = question;
                categoryText.setText(currentQuestion.getCategory());
                questionText.setText(currentQuestion.getText());
                answerText.setText(answerPrompt);
            }
        });

        bindNextQuestion(false);
    }

    @Override
    protected void onPostResume() {
        setQuestionSourceFromPreferences();
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    // this is needed to allow us to propagate the gestures even if the user swipes on the answer view
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }


    public void showAnswer(View source) {
        if (currentQuestion != null) {
            answerText.setText(currentQuestion.getAnswer());
        }
    }


    private void bindNextQuestion(boolean reverse) {
        //if we displayed the answer, then we're seen
        boolean markAsSeen = !answerText.getText().equals(answerPrompt);
        if (reverse) {
            questionViewModel.getPreviousQuestion();
        } else {
            questionViewModel.getNextQuestion(markAsSeen);
        }
    }

    private void setQuestionSourceFromPreferences() {
        String remoteSource = getResources().getString(R.string.jservice_q_source);
        source = PreferenceManager.getDefaultSharedPreferences(this).getString(SettingsActivity.SOURCE_KEY, remoteSource);
        if (source.equals(remoteSource)) {
            questionViewModel.useLocalSource(false);
        } else {
            questionViewModel.useLocalSource(true);
        }
    }

    class SwipeListnener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() > e2.getX()) {
                bindNextQuestion(false);
            } else {
                bindNextQuestion(true);
            }
            return true;
        }
    }
}
