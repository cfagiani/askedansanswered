package com.cataractsoftware.askandanswered.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;
import com.cataractsoftware.askandanswered.entity.Question;
import com.cataractsoftware.askandanswered.repository.QuestionRepository;

import java.util.List;


public class QuestionViewModel extends AndroidViewModel {
    private static final int BATCH_SIZE = 200;
    private static final String LOG_TAG = "QuestionViewModel";
    private final QuestionRepository questionRepository;
    private List<Question> questionBatch;
    private int currentQuestion = -1;

    public QuestionViewModel(Application app) {
        super(app);
        questionRepository = new QuestionRepository(app);
       questionBatch = questionRepository.getQuestionBatch(BATCH_SIZE);
    }

    public Question getNextQuestion(boolean markAsSeen) {
        if (questionBatch != null && currentQuestion >= 0 && markAsSeen) {
            Question q = questionBatch.get(currentQuestion);
            q.setLastSeen(System.currentTimeMillis());
            questionRepository.update(q);
        }
        currentQuestion++;
        if (questionBatch == null || currentQuestion >= questionBatch.size()) {
            currentQuestion = 0;
            questionBatch = questionRepository.getQuestionBatch(BATCH_SIZE);
        }
        if (currentQuestion < questionBatch.size()) {
            return questionBatch.get(currentQuestion);
        }else{
            Log.w(LOG_TAG, "Could not get any questions");
            return null;
        }
    }

    public void flagQuestion(Question q) {
        if (q != null) {
            q.setFlagged(true);
            questionRepository.update(q);
        }

    }

}
