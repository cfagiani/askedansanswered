package com.cataractsoftware.askandanswered.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import com.cataractsoftware.askandanswered.entity.Question;

import java.util.List;

@Dao
public interface QuestionDao {

    @Insert
    void insertQuestions(Question... questions);

    @Update
    void update(Question question);

    @Query("SELECT * from QUESTION WHERE FLAGGED IS NULL ORDER BY LAST_SEEN ASC LIMIT :batchSize")
    List<Question> getQuestionBatch(int batchSize);

}
