//************************************************************
//        * Name:  Manasbi Parajuli                                    *
//        * Project:  Project 2- Konane            *
//        * Class:  AI 331                       *
//        * Date:  3/7/18                           *
//        ************************************************************

package edu.ramapo.mparajul.konane.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.ramapo.mparajul.konane.FileIO.FileDialog;
import edu.ramapo.mparajul.konane.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Check if the user saved the game as this activity will be called as Intent from
        // MainActivity when the save is successful. Then, all the activities in this task will be
        // removed from the recent tasks list.
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT",
                false)) {
            finishAndRemoveTask();
        }

        // setup CardView listener
        CardView startGame = findViewById(R.id.start_cardview);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(StartActivity.this);
                dialog.setContentView(R.layout.board_options);

                RadioGroup radioGroup = dialog.findViewById(R.id.boardSizeRadio);
                dialog.show();

                // set listeners in the radio Group
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int childCount = group.getChildCount();
                        // Loop through the radio buttons list
                        for (int x = 0; x < childCount; x++) {
                            RadioButton btn = (RadioButton) group.getChildAt(x);
                            // set the value of the selected button as the depth selected
                            // by the user to look in branch and bound
                            // once selected, make the dialog box disappear
                            if (btn.getId() == checkedId) {
                                switch (btn.getId()) {
                                    case (R.id.six):
                                        startGame(6);
                                        break;
                                    case (R.id.eight):
                                        startGame(8);
                                        break;
                                    case (R.id.ten):
                                        startGame(10);
                                        break;
                                    default:
                                        startGame(6);
                                        break;
                                }
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });

        // setup Resume Game listener
        CardView resumeGame = findViewById(R.id.resume_cardview);
        resumeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File mPath = new File(Environment.getExternalStorageDirectory() + "//DIR//");
                FileDialog fileDialog = new FileDialog(StartActivity.this, mPath, ".txt");
                fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
                    public void fileSelected(File file) {

                        // warn the user if the file format is invalid or could not be read
                        if (!resumeGame(file.toString())){
                            Toast.makeText(StartActivity.this, "Error while loading game.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                fileDialog.showDialog();
            }
        });
    }

    // Create intent to start Main Activity
    // Receives: boardSize -> the board Size in String value
    // Returns: null
    public void startGame(int boardSize) {
        System.out.println("boardsize: " + boardSize);

        Intent launchGame = new Intent (this, MainActivity.class);

        // put extra information to tell that we are initiating a new game
        launchGame.putExtra("gameIntent", "new_game");
        launchGame.putExtra("boardSize", boardSize);
        startActivity(launchGame);
    }

    // Load game state and save the information as extra message when calling intent
    // Receives: null
    // Returns: null
    public boolean resumeGame(String fileName) {
        String line;      // read line from the file
        String nextPlayer = "";
        String human = "";
        int totalLines = 0, line_count = 0, player1score = 0, player2score = 0,rowCount = 0;

        BufferedReader bufferedReader;
        File file = new File (fileName);

        // Try opening the file to read
        // Handle exceptions accordingly
        try {
            FileInputStream fis = new FileInputStream(file);
            // Read from the file
            try{
                bufferedReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                line = bufferedReader.readLine();
                while (line != null) {
                    // read next line from the file and increment number of lines read
                    line = bufferedReader.readLine();
                    totalLines++;
                }
            }catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        // initialize board size
        String[][] board = new String[totalLines - 5][totalLines - 5];

        // Try opening the file to read
        // Handle exceptions accordingly
        try {
            FileInputStream fis = new FileInputStream(file);
            // Read from the file
            try{
                bufferedReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                line = bufferedReader.readLine();
                while (line != null && (line_count <= totalLines)) {
                    // Read player 1's (Black) score from the file
                    if (line_count == 0) {
                        try {
                            player1score = Integer.parseInt(line.split(": ")[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    // Read player 2's (White) score from the file
                    else if (line_count == 1) {
                        try {
                            player2score = Integer.parseInt(line.split(": ")[1]);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    // Read the board state from the file
                    else if (line_count >= 3 && line_count < (totalLines - 2)) {

                        try {
                            String[] row = line.split(" ");
                            for (int j = 0; j < row.length; j++) {
                                board[rowCount][j] = row[j];
                            }
                            rowCount++;
                        }catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }

                    // Get the current player's turn
                    if (line_count == (totalLines - 2)) {
                        try {
                            if (line.split(": ")[1].trim().equals("White")) {
                                nextPlayer = "W";
                            }else {
                                nextPlayer = "B";
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }

                    // Get which piece the human player is
                    if (line_count == (totalLines - 1)) {
                        try {
                            // Human: White
                            if (line.split(": ")[1].trim().equals("White")) {
                                human = "White";
                            }
                            // Human: Black
                            else {
                                human = "Black";
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                    // read next line from the file and increment number of lines read
                    line = bufferedReader.readLine();
                    line_count++;
                }

                // Declare intent and put values necessary to load a game as Extras
                Intent resumeGameState = new Intent(this, MainActivity.class);
                int playerScores[] = {player1score, player2score};
                resumeGameState.putExtra("playerScores", playerScores);
                resumeGameState.putExtra("nextPlayer", nextPlayer);
                resumeGameState.putExtra("humanPiece", human);
                resumeGameState.putExtra("gameIntent", "resume_game");
                resumeGameState.putExtra("boardSize", totalLines - 5);

                // Put 2Dimensional board state as extra message in our intent
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("boardState", board);
                resumeGameState.putExtras(mBundle);

                // start MainActivity
                startActivity(resumeGameState);
                return true;
            }catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}