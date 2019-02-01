package com.cataractsoftware.askandanswered;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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
        bindNextQuestion(false);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
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
            currentQuestion = questionViewModel.getPreviousQuestion();
        } else {
            currentQuestion = questionViewModel.getNextQuestion(markAsSeen);
        }
        if (currentQuestion != null) {
            categoryText.setText(currentQuestion.getCategory());
            questionText.setText(currentQuestion.getText());
            answerText.setText(answerPrompt);
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
