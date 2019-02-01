package com.cataractsoftware.askandanswered.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.util.Log;
import com.cataractsoftware.askandanswered.entity.Question;
import com.cataractsoftware.askandanswered.repository.QuestionRepository;

import java.util.List;


public class QuestionViewModel extends AndroidViewModel {
    private static final int BATCH_SIZE = 100;
    private static final int HISTORY_SIZE = 20;
    private static final String LOG_TAG = "QuestionViewModel";
    private final QuestionRepository questionRepository;
    private List<Question> questionBatch;
    private int currentQuestion = -1;

    public QuestionViewModel(Application app) {
        super(app);
        questionRepository = new QuestionRepository(app);
        questionBatch = questionRepository.getQuestionBatch(BATCH_SIZE);
    }

    public Question getPreviousQuestion() {
        if (currentQuestion > 0 && currentQuestion <= questionBatch.size() && questionBatch.size() > 0) {
            currentQuestion--;
        }
        return questionBatch.get(currentQuestion);
    }

    public Question getNextQuestion(boolean markAsSeen) {
        if (questionBatch != null && currentQuestion >= 0 && markAsSeen) {
            Question q = questionBatch.get(currentQuestion);
            q.setLastSeen(System.currentTimeMillis());
            questionRepository.update(q);
        }
        currentQuestion++;
        if (questionBatch == null || currentQuestion >= questionBatch.size()) {
            currentQuestion = questionBatch != null ? Math.min(questionBatch.size(), HISTORY_SIZE) : 0;
            List<Question> nextBatch = questionRepository.getQuestionBatch(BATCH_SIZE);
            if (questionBatch != null && questionBatch.size() > 0) {
                nextBatch.addAll(0, questionBatch.subList(Math.max(questionBatch.size() - HISTORY_SIZE, 0), questionBatch.size()));
            }
            questionBatch = nextBatch;
        }
        if (currentQuestion < questionBatch.size()) {
            return questionBatch.get(currentQuestion);
        } else {
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
