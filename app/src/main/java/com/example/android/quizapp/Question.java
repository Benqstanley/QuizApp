package com.example.android.quizapp;

import java.util.ArrayList;

/**
 * Created by Ben on 3/7/2018.
 */

public class Question{
    private String questionText;
    private String questionType;
    ArrayList<String> answerOptions = new ArrayList<String>();
    ArrayList<String> answerList = new ArrayList<>();

    public void setQuestionText(String string){
        this.questionText = string;
    }
    public String getQuestionText(){
        return this.questionText;
    }
    public void setQuestionType(String string){
        this.questionType = string;
    }
    public String getQuestionType(){
        return this.questionType;
    }

    public void setAnswerOptions(ArrayList<String> options){
        this.answerOptions = options;
    }
    public ArrayList<String> getAnswerOptions(){
        return this.answerOptions;
    }
    public void setAnswerList(ArrayList<String> list){
        this.answerList = list;
    }
    public ArrayList<String> getAnswerList(){
        return this.answerList;
    }

}
