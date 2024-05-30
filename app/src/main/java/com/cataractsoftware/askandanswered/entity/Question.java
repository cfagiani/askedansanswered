package com.cataractsoftware.askandanswered.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "question")
public class Question {
    @PrimaryKey(autoGenerate = true)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id) && Objects.equals(category, question.category) && Objects.equals(text, question.text) && Objects.equals(answer, question.answer) && Objects.equals(lastSeen, question.lastSeen) && Objects.equals(flagged, question.flagged);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, text, answer, lastSeen, flagged);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }
}
