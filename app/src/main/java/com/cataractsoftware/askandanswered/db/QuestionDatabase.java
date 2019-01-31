package com.cataractsoftware.askandanswered.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import com.cataractsoftware.askandanswered.dao.QuestionDao;
import com.cataractsoftware.askandanswered.entity.Question;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@Database(entities = {Question.class}, version = 1)
public abstract class QuestionDatabase extends RoomDatabase {
    private static final String LOG_TAG = "questionDatabase";

    public abstract QuestionDao questionDao();

    private static final int BATCH_SIZE = 1000;
    private static final String DATA_FILE = "trivia.txt";
    private static volatile QuestionDatabase INSTANCE;
    private static volatile AssetManager MGR;

    private static RoomDatabase.Callback populationCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(MGR, INSTANCE).execute();
        }
    };

    public static QuestionDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QuestionDatabase.class) {
                if (INSTANCE == null) {
                    MGR = context.getAssets();
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QuestionDatabase.class, "question_database")
                            .addCallback(populationCallback)
                            .allowMainThreadQueries()
                            //TODO: change this if we actually want to migrate
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final QuestionDao dao;
        private final AssetManager manager;

        PopulateDbAsync(AssetManager manager, QuestionDatabase db) {
            dao = db.questionDao();
            this.manager = manager;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            Log.i(LOG_TAG, "Starting population");
            long start = System.currentTimeMillis();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(manager.open(DATA_FILE)))) {
                String headers = br.readLine(); // discard first line since it's the header
                String line = br.readLine();
                ArrayList<Question> batch = new ArrayList<>(BATCH_SIZE);
                while (line != null) {
                    String[] tokens = line.split("\t");
                    if (tokens.length == 3) {
                        Question q = new Question();
                        q.setCategory(tokens[0].trim());
                        q.setText(tokens[1].trim());
                        q.setAnswer(tokens[2].trim());

                        batch.add(q);
                        if (batch.size() == BATCH_SIZE) {
                            dao.insertQuestions(batch.toArray(new Question[0]));
                            batch.clear();
                        }
                    }
                    line = br.readLine();
                }
                //last batch
                if (batch.size() > 0) {
                    dao.insertQuestions(batch.toArray(new Question[0]));
                }
            } catch (IOException err) {
                Log.e(LOG_TAG, "Could not populate database", err);
            }
            Log.i(LOG_TAG, "Finished data population in " + (System.currentTimeMillis() - start) + " ms");
            return null;
        }
    }

}
