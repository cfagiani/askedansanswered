package com.cataractsoftware.askandanswered.rest;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cataractsoftware.askandanswered.entity.Question;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CluebaseClient {
    private static final int TIMEOUT_SECS = 2;
    private static final String LOG_TAG = "CluebaseClient";
    private static final String ANSWER_FIELD = "response";
    private static final String QUESTION_FIELD = "clue";
    private static final String CATEGORY_FIELD = "category";
    private static final String STATUS_FIELD = "status";
    private static final String DATA_FIELD = "data";
    private static final String API_ROOT = "http://cluebase.lukelav.in/clues/random";

    private RequestQueue queue;

    public CluebaseClient(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public List<Question> getQuestionBatch(int size) {
        CountDownLatch latch = new CountDownLatch(1);
        List<Question> questionList = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_ROOT + "?limit=" + size, null, (response) -> {
            try {
                if (!"success".equalsIgnoreCase(response.getString(STATUS_FIELD))) {
                    Log.e(LOG_TAG, "Could not fetch clues");
                }
                JSONArray clueArr = response.getJSONArray(DATA_FIELD);
                for (int i = 0; i < clueArr.length(); i++) {

                    Question q = new Question();
                    JSONObject obj = clueArr.getJSONObject(i);
                    q.setAnswer(obj.getString(ANSWER_FIELD));
                    q.setText(obj.getString(QUESTION_FIELD));
                    q.setCategory(obj.getString(CATEGORY_FIELD));
                    questionList.add(q);

                }
            } catch (JSONException ex) {
                Log.e(LOG_TAG, "Could not parse json object in response", ex);
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
