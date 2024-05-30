package com.cataractsoftware.askandanswered.repository;

import android.app.Application;
import android.os.AsyncTask;
import com.cataractsoftware.askandanswered.dao.QuestionDao;
import com.cataractsoftware.askandanswered.db.QuestionDatabase;
import com.cataractsoftware.askandanswered.entity.Question;
import com.cataractsoftware.askandanswered.rest.CluebaseClient;

import java.util.List;

public class QuestionRepository {
    private final QuestionDao questionDao;
    private final CluebaseClient cluebaseClient;
    private boolean useLocal = false;

    public QuestionRepository(Application app) {
        QuestionDatabase db = QuestionDatabase.getDatabase(app);
        cluebaseClient = new CluebaseClient(app);
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
            return cluebaseClient.getQuestionBatch(batchSize);
        }
    }

    /**
     * Updates the source to local or not. If the value changed, this method returns true. If no change, it returns
     * false.
     *
     * @param local
     * @return - true if the value changed
     */
    public boolean useLocalSource(boolean local) {
        if (useLocal != local) {
            useLocal = local;
            return true;
        }
        return false;
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
