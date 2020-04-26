package com.victorcharl.guessthepicture;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView picture_container;

    private Button choice1;
    private Button choice2;
    private Button choice3;
    private Button choice4;

    private String imageLocationURL;

    private TextView yourScore;
    private TextView lives;
    private TextView stage;

    private int randomPick;
    private int correctAnswer;
    private int counterScore = 0;
    private int trials = 10;
    private int currentStage = 1;

    private ArrayList<String> countryNames;
    private ArrayList<String> countryFlagUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        picture_container = findViewById(R.id.picture_container);

        lives = findViewById(R.id.remaining_try);
        lives.setText(this.getResources().getString(R.string.your_lives, trials));

        stage = findViewById(R.id.stage_txt);
        stage.setText(this.getResources().getString(R.string.stage_number, currentStage));

        yourScore = findViewById(R.id.your_score);
        yourScore.setText(this.getResources().getString(R.string.yourScore, counterScore));

        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
        choice3 = findViewById(R.id.choice3);
        choice4 = findViewById(R.id.choice4);

        choice1.setOnClickListener(this);
        choice2.setOnClickListener(this);
        choice3.setOnClickListener(this);
        choice4.setOnClickListener(this);

        new downloadWebsiteString().execute();

    }

    private void displayImage(){
        DownloadImage task=new DownloadImage();
        Bitmap downloadedImage;
        try {
            downloadedImage=task.execute(imageLocationURL).get();
            picture_container.setImageBitmap(downloadedImage);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showCorrectAnswer(){
        final Button[] buttonChoices = new Button[]{choice1, choice2, choice3, choice4};
        stage.setText(this.getResources().getString(R.string.stage_number, currentStage));
        for(int i = 0; i < buttonChoices.length; i++){
            if(buttonChoices[i].getText() == countryNames.get(randomPick)){
                final int rightAnswer = i;
                buttonChoices[rightAnswer].setClickable(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonChoices[rightAnswer].setClickable(true);
                    }
                },2000);
            }
            else{
                final int wrongAnswer = i;
                buttonChoices[wrongAnswer].setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonChoices[wrongAnswer].setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showGuessingQuestions();
            }
        },2000);
    }

    private void tryAgain(){
        if(trials == 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("YOU'VE USED ALL OF YOUR LIVES");
            dialog.setCancelable(false);
            dialog.setMessage("YOU WANNA PLAY AGAIN?");
            dialog.setPositiveButton("PLAY AGAIN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recreate();
                }
            });
            dialog.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.create().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.choice1:
                checkTheAnswer(0);
                break;
            case R.id.choice2:
                checkTheAnswer(1);
                break;
            case R.id.choice3:
                checkTheAnswer(2);
                break;
            case R.id.choice4:
                checkTheAnswer(3);
        }
        showCorrectAnswer();
        currentStage++;
    }

    private void checkTheAnswer(int rightAnswer){
        if(correctAnswer == rightAnswer){
            counterScore++;
            yourScore.setText(this.getResources().getString(R.string.yourScore, counterScore));
        }
        else {
            trials--;
            lives.setText(this.getResources().getString(R.string.your_lives, trials));
            tryAgain();
        }
    }


    //https://aboullaite.me/jsoup-html-parser-tutorial-examples/
    //https://medium.com/@princessdharmy/getting-started-with-jsoup-in-android-594e89dc891f
    @SuppressLint("StaticFieldLeak")
    private class downloadWebsiteString extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                //arrays
                countryFlagUrl = new ArrayList<>();
                countryNames = new ArrayList<>();

                String url = "https://flagpedia.net/index";
                //connect to the website
                Document doc = Jsoup.connect(url).get();

                //https://try.jsoup.org/ //JSOUP TESTER
                Elements links = doc.select("img[src]");

                //for (int i = 0; i < links.size(); i++) {
                for (Element link : links) {
                    countryFlagUrl.add("\n" + url.replace("/index", "") + link.attr("src"));
                    countryNames.add(link.attr("alt").replace("Flag of ", ""));
                }
            }

            catch (IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            showGuessingQuestions();
        }
    }

    private void showGuessingQuestions(){
        Random random = new Random();
        int totalElements = countryFlagUrl.size();
        randomPick = random.nextInt(totalElements);
        correctAnswer = random.nextInt(3);
        imageLocationURL = countryFlagUrl.get(randomPick);
        displayImage();

        Button[] buttonText = new Button[]{choice1, choice2, choice3, choice4};
        for (Button button : buttonText) {
            button.setText(countryNames.get(random.nextInt(totalElements)));
        }
        buttonText[correctAnswer].setText(countryNames.get(randomPick));

        Log.i("CORRECT ANSWER", countryNames.get(randomPick));
    }
}
