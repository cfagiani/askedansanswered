package com.cataractsoftware.askandanswered.repository;

import android.app.Application;
import android.os.AsyncTask;
import com.cataractsoftware.askandanswered.dao.QuestionDao;
import com.cataractsoftware.askandanswered.db.QuestionDatabase;
import com.cataractsoftware.askandanswered.entity.Question;

import java.util.List;

public class QuestionRepository {
    private final QuestionDao questionDao;

    public QuestionRepository(Application app) {
        QuestionDatabase db = QuestionDatabase.getDatabase(app);
        questionDao = db.questionDao();
    }

    public void update(Question question) {
        questionDao.update(question);
    }

    public List<Question> getQuestionBatch(int batchSize) {
        return questionDao.getQuestionBatch(batchSize);
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
