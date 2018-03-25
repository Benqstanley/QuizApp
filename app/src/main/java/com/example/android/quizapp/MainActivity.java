package com.example.android.quizapp;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.example.android.quizapp.Question;

public class MainActivity extends AppCompatActivity {
    /*onCreate reads in the String-Array from arrays that contains the questions, answer options, and answers.
     * Synthesizes that list into a list of Questions
      * displays the first question and the question number*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            rawList = getResources().getStringArray(R.array.questionArray);
            synthesizeList(rawList);
            currentQuestion = questionList.get(0);
        }else{
            questionListState = savedInstanceState.getStringArrayList(STATE_QUESTIONLISTSTATE);
            score = savedInstanceState.getInt(STATE_SCORE);
            nextQuestion = savedInstanceState.getInt(STATE_NEXTQUESTION);
            createQuestionListFromState();
            currentQuestion = questionList.get(nextQuestion-1);
            done = savedInstanceState.getBoolean(STATE_DONE);
        }
        if(done){
            finishUp();
        }else {
            displayQuestion(currentQuestion);
            updateQuestionNumber();
            updateScoreTV();
        }
    }
    static final String STATE_SCORE = "score";
    static final String STATE_NEXTQUESTION = "nextQuestion";
    static final String STATE_QUESTIONLISTSTATE = "questionListState";
    static final String STATE_DONE = "done";


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        createState();
        savedInstanceState.putStringArrayList(STATE_QUESTIONLISTSTATE, questionListState);
        savedInstanceState.putInt(STATE_SCORE, score);
        savedInstanceState.putInt(STATE_NEXTQUESTION, nextQuestion);
        savedInstanceState.putBoolean(STATE_DONE, done);
    }


    String[] rawList; //An array of Strings for the raw question info.
    ArrayList<Question> questionList = new ArrayList<Question>(); //The synthesized ilst. It's randomized from original order.
    ArrayList<String> usersAnswer = new ArrayList<>(); //Stores the user's answer to the current question.
    ArrayList<CheckBox> checkAnswerViews = new ArrayList<>(); //Stores the views where the user inputs their answer
    ArrayList<RadioButton> radioAnswerViews = new ArrayList<RadioButton>();
    EditText editAnswer;
    Question currentQuestion = new Question();

    Boolean done = false;
    int score = 0; //Player's score, starts at 0 and is incremented for correct answers
    int nextQuestion = 1; //The list position of the next question to be displayed. This lags one behind the displayed question number.
    ArrayList<String> questionListState = new ArrayList<>();


    /*Goes through rawList and converts the entries into Question objects. Adds the results to questionList

     */
    public void synthesizeList (String[] rList){
        int j = rList.length;
        String currentType = "none";
        for(int k = 0; k < j; k++){
            if(rList[k].equals("RadioButton") || rList[k].equals("EditText") || rList[k].equals("CheckBox")){
                currentType = rList[k];
            }
            if(rList[k].contains("Question")){
                Question question = new Question();
                question.setQuestionType(currentType);
                question.setQuestionText(rList[k].substring(10));
                ArrayList<String> optionList = new ArrayList<String>();
                ArrayList<String> answerList = new ArrayList<String>();
                int l = k+1;
                while (l < j && rList[l].substring(0, 7).contains("Option")) {
                    optionList.add(rList[l].substring(8));
                    l++;
                }
                while (l < j && rList[l].substring(0, 4).contains("Ans")) {
                    answerList.add(rList[l].substring(5));
                    l++;
                }
                k = l - 1;
                Collections.shuffle(optionList);
                question.setAnswerOptions(optionList);
                question.setAnswerList(answerList);
                questionList.add(question);
            }
        }
       Collections.shuffle(questionList);
    }

    public void createState(){
        questionListState.clear();
        for(Question q: questionList) {
            questionListState.add(q.getQuestionType());
            questionListState.add(q.getQuestionText());
            ArrayList<String> optionsList = q.getAnswerOptions();
            ArrayList<String> answersList = q.getAnswerList();
            int answerOptionsSize = optionsList.size();
            int answerListSize = answersList.size();
            for (int j = 0; j < answerOptionsSize; j++) {
                String temp = "Option: " + optionsList.get(j);
                questionListState.add(temp);
            }
            for (int j = 0; j < answerListSize; j++) {
                String temp = "Ans: " + answersList.get(j);
                questionListState.add(temp);
            }
        }
    }
    public void reinitialize(View view){
        createState();
        createQuestionListFromState();
        currentQuestion = questionList.get(nextQuestion-1);
        displayQuestion(currentQuestion);
    }
    public void createQuestionListFromState(){
        questionList.clear();
        int qSize = questionListState.size();
        for(int j = 0; j < qSize; j++){
            Question q = new Question();
            q.setQuestionType(questionListState.get(j));
            q.setQuestionText(questionListState.get(j+1));
            int l = j+2;
            ArrayList<String> optionList = new ArrayList<>();
            ArrayList<String> answerList = new ArrayList<>();
            while (l < qSize && questionListState.get(l).substring(0, 7).contains("Option")) {
                optionList.add(questionListState.get(l).substring(8));
                l++;
            }
            while (l < qSize && questionListState.get(l).substring(0, 4).contains("Ans")) {
                answerList.add(questionListState.get(l).substring(5));
                l++;
            }
            q.setAnswerOptions(optionList);
            q.setAnswerList(answerList);
            questionList.add(q);
            j = l-1;

        }

    }
    /*
    Finds the view where the question number is displayed. Creates a string to display.
    Sets text to the created string.
     */
    public void updateQuestionNumber(){
        TextView questionNumber = findViewById(R.id.question_number);
        String displayString = "#" + nextQuestion;
        questionNumber.setText(displayString);
    }

    public void createUsersAnswer(String type) {
        usersAnswer.clear();
        switch (type) {
            case "RadioButton":
                for (RadioButton v : radioAnswerViews) {
                    if (v.isChecked()) {
                        usersAnswer.add(v.getText().toString());
                        System.out.println(v.getText().toString());
                    }else{
                        System.out.println("Wasn't added");
                    }
                }
                break;
            case "CheckBox":
                for (CheckBox v : checkAnswerViews) {
                    if (v.isChecked()) {
                        usersAnswer.add(v.getText().toString());
                        Log.w("building userAnswers", v.getText().toString());
                    }
                }
                break;
            case "EditText":
                usersAnswer.add(editAnswer.getText().toString());
                Log.w("building userAnswers", editAnswer.getText().toString());
                break;
        }
        Collections.sort(usersAnswer);
        String uA = "";
        for(int i = 0; i < usersAnswer.size(); i++){
            uA += usersAnswer.get(i);
        }
        Log.v("This Shit", uA);
    }

    public void updateScoreTV(){
        TextView scoreTV = (TextView) findViewById(R.id.score_text_view);
        String display = "Score: " + score;
        scoreTV.setText(display);
    }

    public Boolean checkAnswer(Question question){
        Boolean correct = false;
        int uA = usersAnswer.size();
        ArrayList<String> tempUA = new ArrayList<>();
        for(int j = 0; j < uA; j++){
            tempUA.add(usersAnswer.get(j));
        }
        ArrayList<String> actualAnswer = currentQuestion.getAnswerList();
        ArrayList<String> tempAA = new ArrayList<>();
        int aA = actualAnswer.size();
        for(int j = 0; j < aA; j++){
            tempAA.add(actualAnswer.get(j));
        }
        for(int j = 0; j < uA; j++){
            tempAA.remove(usersAnswer.get(j));
        }
        for(int l = 0; l < aA; l++){
            String vL = "" + l;
            tempUA.remove(actualAnswer.get(l));
        }
        if(tempAA.isEmpty() && tempUA.isEmpty()){
            correct = true;
        }
        return correct;
    }
    /*
    Copeies the value of nextQuestion into int currentQuestion. Displays the next question.
    Increments nextQuestion. Calls updateQuestionNumber(). Calls createUsersAnswer() and sends the current
    question's type as the argument. This creates the list to check against the answer list.
    Then calls checkAnswer and updates score accordingly. Updates the Score TextView as well.

     */
    public void submit(View view){
        Log.w("Submit", "Entered Submit");
        String type = currentQuestion.getQuestionType();
        createUsersAnswer(type);
        if (!done) {
            if (checkAnswer(currentQuestion)) {
                if (nextQuestion < questionList.size()) {
                    score += 1;
                    updateScoreTV();
                    currentQuestion = questionList.get(nextQuestion);
                    displayQuestion(currentQuestion);
                    nextQuestion++;
                    updateQuestionNumber();
                } else if (nextQuestion == questionList.size()) {
                    score += 1;
                    updateScoreTV();
                    done = true;
                }
            } else {
                if(nextQuestion < questionList.size()) {
                    currentQuestion = questionList.get(nextQuestion);
                    displayQuestion(currentQuestion);
                    nextQuestion++;
                    updateQuestionNumber();
                } else if (nextQuestion == questionList.size()) {
                    updateScoreTV();
                    done = true;
                }
                Context context = getApplicationContext();
                CharSequence text = "Incorrect";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }else{
            Context context = getApplicationContext();
            CharSequence text = "Quiz Complete";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        if(done){
            finishUp();
        }
    }



    public void displayQuestion(Question question){
        //Populate the Question Text
        TextView qText = findViewById(R.id.question_text);
        qText.setText(question.getQuestionText());
        String type = question.getQuestionType();
        ArrayList<String> optionList = question.getAnswerOptions();
        int options = optionList.size();
        LinearLayout optionLayout = findViewById(R.id.option_linear_layout);
        optionLayout.removeAllViews();
        radioAnswerViews.clear();
        checkAnswerViews.clear();

        //Populate the appropriate ViewGroup with answer options.

        switch (type){
            case "RadioButton":
                RadioGroup radios = (RadioGroup) LayoutInflater.from(this).inflate(R.layout.answer_radio_group, null);
                for(int j = 0; j < options; j++){
                    Log.v("We made it!", "Making Radios");
                    RadioButton answerOption = (RadioButton) LayoutInflater.from(this).inflate(R.layout.answer_radio_button, null);
                    answerOption.setText(optionList.get(j));
                    radios.addView(answerOption);
                    radioAnswerViews.add(answerOption);
                }
                optionLayout.addView(radios);
                break;
            case "CheckBox":
                for(int j = 0; j < options; j++){
                    CheckBox answerOption = (CheckBox) LayoutInflater.from(this).inflate(R.layout.answer_check_box, null);
                    answerOption.setText(optionList.get(j));
                    optionLayout.addView(answerOption);
                    checkAnswerViews.add(answerOption);
                }
                break;
            case "EditText":
                EditText answerEdit = (EditText) LayoutInflater.from(this).inflate(R.layout.answer_edit_text, null);
                optionLayout.addView(answerEdit);
                editAnswer = answerEdit;
                break;
        }
    }
    /*
    This method will remove unnecessary views from the display and show the user their score.
     */
    public void finishUp(){
        ViewGroup mainLayout = (ViewGroup) findViewById(android.R.id.content);
        mainLayout.removeAllViews();
        RelativeLayout displayFinal = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.score_display, null);
        TextView dispScore = displayFinal.findViewById(R.id.display_score_tv);
        String temp = score + "/10!";
        dispScore.setText(temp);
        mainLayout.addView(displayFinal);

    }


}



