package com.cataractsoftware.askandanswered.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
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
    private MutableLiveData<Question> currentQuestion;
    private int currentQuestionIndex = -1;
    private volatile boolean loading = false;

    public QuestionViewModel(Application app) {
        super(app);
        questionRepository = new QuestionRepository(app);
        loadQuestionBatch();
    }

    public MutableLiveData<Question> getCurrentQuestion() {
        if (currentQuestion == null) {
            currentQuestion = new MutableLiveData<>();
        }
        return currentQuestion;
    }

    public void getPreviousQuestion() {
        if (currentQuestionIndex > 0 && currentQuestionIndex <= questionBatch.size() && questionBatch.size() > 0) {
            currentQuestionIndex--;
        }
        getCurrentQuestion().setValue(questionBatch.get(currentQuestionIndex));
    }

    public void getNextQuestion(boolean markAsSeen) {
        if (questionBatch != null && currentQuestionIndex >= 0 && markAsSeen) {
            Question q = questionBatch.get(currentQuestionIndex);
            q.setLastSeen(System.currentTimeMillis());
            questionRepository.update(q);
        }
        currentQuestionIndex++;
        if (questionBatch == null || currentQuestionIndex >= questionBatch.size()) {
            loadQuestionBatch();
        } else if (currentQuestionIndex < questionBatch.size()) {
            getCurrentQuestion().setValue(questionBatch.get(currentQuestionIndex));
        } else {
            Log.w(LOG_TAG, "Could not get any questions");
        }
    }

    private void loadQuestionBatch() {
        if (!loading) {
            loading = true;
            new AsyncTask<Void, Void, List<Question>>() {
                @Override
                protected List<Question> doInBackground(Void... voids) {
                    return questionRepository.getQuestionBatch(BATCH_SIZE);
                }

                @Override
                protected void onPostExecute(List<Question> data) {
                    if (questionBatch != null && questionBatch.size() > 0) {
                        data.addAll(0, questionBatch.subList(Math.max(questionBatch.size() - HISTORY_SIZE, 0), questionBatch.size()));
                    }
                    questionBatch = data;
                    currentQuestionIndex = questionBatch != null ? Math.min(questionBatch.size(), HISTORY_SIZE) : 0;
                    getCurrentQuestion().postValue(questionBatch.get(currentQuestionIndex));
                    loading = false;
                }
            }.execute();
        }
    }


    public void flagQuestion(Question q) {
        if (q != null) {
            q.setFlagged(true);
            questionRepository.update(q);
        }

    }

    public void useLocalSource(boolean useLocal) {
        if (questionRepository.useLocalSource(useLocal)) {
            questionBatch = questionRepository.getQuestionBatch(BATCH_SIZE);
        }
    }

}
