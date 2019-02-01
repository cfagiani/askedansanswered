package com.cataractsoftware.askandanswered.repository;

import android.app.Application;
import android.os.AsyncTask;
import com.cataractsoftware.askandanswered.dao.QuestionDao;
import com.cataractsoftware.askandanswered.db.QuestionDatabase;
import com.cataractsoftware.askandanswered.entity.Question;
import com.cataractsoftware.askandanswered.rest.JServiceClient;

import java.util.List;

public class QuestionRepository {
    private final QuestionDao questionDao;
    private final JServiceClient jServiceClient;
    private boolean useLocal = false;

    public QuestionRepository(Application app) {
        QuestionDatabase db = QuestionDatabase.getDatabase(app);
        jServiceClient = new JServiceClient(app);
        questionDao = db.questionDao();
    }

    public void update(Question question) {
        if (useLocal) {
            questionDao.update(question);
        }
    }

    public List<Question> getQuestionBatch(int batchSize) {
        if (useLocal) {
            return questionDao.getQuestionBatch(batchSize);
        } else {
            return jServiceClient.getQuestionBatch(batchSize);
        }
    }

    public void setSource(boolean local) {
        useLocal = local;
    }

    private static class insertAsyncTask extends AsyncTask<Question, Void, Void> {
        private QuestionDao taskDao;

        insertAsyncTask(QuestionDao dao) {
            taskDao = dao;
        }

        @Override
        protected Void doInBackground(Question... questions) {
            taskDao.insertQuestions(questions);
            return null;
        }
    }

}
