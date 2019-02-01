package com.cataractsoftware.askandanswered.rest;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.cataractsoftware.askandanswered.entity.Question;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class JServiceClient {
    private static final int TIMEOUT_SECS = 2;
    private static final String LOG_TAG = "JServiceClient";
    private static final String ANSWER_FIELD = "answer";
    private static final String QUESTION_FIELD = "question";
    private static final String CATEGORY_FIELD = "category";
    private static final String CAT_NAME_FIELD = "title";
    private static final String API_ROOT = "http://jservice.io/api/random";

    private RequestQueue queue;

    public JServiceClient(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public List<Question> getQuestionBatch(int size) {
        CountDownLatch latch = new CountDownLatch(1);
        List<Question> questionList = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, API_ROOT + "?count=" + size, null, (response) -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    Question q = new Question();
                    JSONObject obj = response.getJSONObject(i);
                    q.setAnswer(obj.getString(ANSWER_FIELD));
                    q.setText(obj.getString(QUESTION_FIELD));
                    q.setCategory(obj.getJSONObject(CATEGORY_FIELD).getString(CAT_NAME_FIELD));
                    questionList.add(q);
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, "Could not parse json object in response", ex);
                }
            }
            latch.countDown();
        },
                (err) -> Log.e(LOG_TAG, "Could not make rest request", err));
        queue.add(request);
        try {
            latch.await(TIMEOUT_SECS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(LOG_TAG, "Interrupt while waiting for service response", e);
        }
        return questionList;
    }
}
