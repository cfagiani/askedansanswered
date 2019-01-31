package com.cataractsoftware.askandanswered.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
//@Builder
@Entity(tableName = "question")
public class Question {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private Long id;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "text")
    private String text;
    @ColumnInfo(name = "answer")
    private String answer;
    @ColumnInfo(name = "last_seen")
    private Long lastSeen;
    @ColumnInfo(name = "flagged")
    private Boolean flagged;

    public Question(){
        //needed for Room since the Room & Lombok annotation processors don't play nicely together
    }
}
