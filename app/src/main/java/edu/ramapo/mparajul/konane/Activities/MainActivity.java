//************************************************************
//        * Name:  Manasbi Parajuli                *
//        * Project:  Project 2- Konane            *
//        * Class:  AI 331                         *
//        * Date:  3/7/18                          *
//        ************************************************************

package edu.ramapo.mparajul.konane.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import edu.ramapo.mparajul.konane.Model.Game;
import edu.ramapo.mparajul.konane.Adapters.ImageAdapter;
import edu.ramapo.mparajul.konane.R;

public class MainActivity extends AppCompatActivity {
    // create a game object
    Game game = new Game();

    // Variables to define each player's first click
    private boolean sourceMoveClickedPlayer1 = true;
    private boolean sourceMoveClickedPlayer2 = true;
    private boolean playerOneTurn = true;
    private boolean isHumanBlackPlayer = true;

    // the default values for the source and destination positions in the board is set to negative
    private int sourceValue = -1;       // the position of the first click on the board
    private int destinationValue = -1;  // the destination of the piece during a move
    private int[] deviceDimensions = new int[2];

    private String[] adapterValues;

    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = LayoutInflater.from(this).inflate(R.layout.activity_main, null);

        // Get the device dimensions in pixels
        // Dimensions are stored in the order of height and width
        deviceDimensions[0] = Resources.getSystem().getDisplayMetrics().heightPixels;
        deviceDimensions[1] = Resources.getSystem().getDisplayMetrics().widthPixels;

        setContentView(root);
        loadGameState();
        updateState(game.getBoardState());
        setupListeners();
    }

    // Release media player resources when the app goes into background
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    // Play sound when the app resumes in the foreground
    @Override
    protected void onResume() {
        super.onResume();
        SoundPlayer(MainActivity.this, R.raw.theme);
    }

    // Call listeners for respective buttons and populate the spinner
    // Receives: null
    // Returns : null
    public void setupListeners() {
        saveStateListener();
        nextMoveListener();
        skipTurnListener();
        checkBoxListener();
        selectPlyListener();
    }

    // Attach a button listener to select ply cutoff button
    // Receives: null
    // Returns: null
    public void selectPlyListener() {
        Button button = findViewById(R.id.select_ply_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerListener();
            }
        });
    }

    // Create a dialog for number picker and attach listeners when the user selects a value
    // Receives: null
    // Returns: null
    public void numberPickerListener() {

        // Initialize a dialog box and attach buttons that allow the user to proceed or cancel
        // their number picker choice
        final Dialog setPlyDialog = new Dialog(MainActivity.this);
        setPlyDialog.setTitle("Number Picker");
        setPlyDialog.setContentView(R.layout.number_picker_dialog);

        Button ignore = setPlyDialog.findViewById(R.id.cancel_button);
        Button setNumberPicker = setPlyDialog.findViewById(R.id.set_ply_button);

        // Set the range of values in the number picker
        final NumberPicker numberPicker = setPlyDialog.findViewById(R.id.select_ply_cutoff);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        // Attach listener to number picker to check if the value has been changed
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            }
        });

        // Call listener to allow the user to confirm their choice in number picker
        setNumberPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("new val:" + String.valueOf(numberPicker.getValue()));
                setPlyDialog.dismiss();
            }
        });
        // Call listener to allow the user to cancel their selection in number picker
        ignore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPlyDialog.dismiss();
            }
        });
        // Show the dialog box for the number picker
        setPlyDialog.show();
    }

    // Checkbox Listener for alpha beta pruning
    // Receives: null
    // Returns: null
    public void checkBoxListener() {
        CheckBox checkBox = findViewById(R.id.checkbox_pruning);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    System.out.println("Checked: alpha it is");
                }
                else {
                    System.out.println("Unchecked: booo. not pruned");
                }
            }
        });
    }


    // Setup click listeners for save button
    // Receives: null
    // Returns: null
    public void saveStateListener() {
        Button saveButton = findViewById(R.id.save_state);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Save game clicked!");

                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setTitle("Read File");
                dialog.setContentView(R.layout.savefile_dialog);
                dialog.show();

                Button saveFileButton = dialog.findViewById(R.id.savefilebutton);
                EditText editText = dialog.findViewById(R.id.save_dialog);

                saveFileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String saveFileName = editText.getText().toString();

                        if(!saveGame(saveFileName)){
                            callToast(MainActivity.this, "Unable to save game");
                        }
                        else {
                            callToast(MainActivity.this, "Game Saved!");

                            // Call StartActivity and pass extra message to completely exit from
                            // the app
                            Intent intent = new Intent(getApplicationContext(), StartActivity
                                    .class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    // Setup listeners for skip turn button
    // Change the player's turn based on the player who prompted to skip turn
    // Receives: null
    // Returns: null
    public void skipTurnListener() {
        Button skipButton = findViewById(R.id.skip_turn);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationValue = -1;
                if (playerOneTurn) {
                    playerTwoTurnValues();
                } else{
                    playerOneTurnValues();
                }
            }
        });
    }

    // Load the current state of the game based on user selected option in StartActivity
    // Receives: null
    // Returns: null
    public void loadGameState() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Load game from file
        // Read saved game state
        if (getIntent().getExtras().getString("gameIntent").equals("resume_game")) {
            String[][] savedBoardState = null;
            Object[] objectArray = (Object[]) getIntent().getExtras().getSerializable("boardState");

            // Get the saved board state and convert into multidimensional array
            if (objectArray!= null) {
                savedBoardState = new String[objectArray.length][];
                for (int i = 0; i < objectArray.length; i++) {
                    savedBoardState[i] = (String[]) objectArray[i];
                }
                game.resumeGame(savedBoardState, bundle.getInt("boardSize"));
            }

            // Get other information from the saved game
            if (bundle != null) {
                int[] scores = bundle.getIntArray("playerScores");
                // set player's scores
                game.player1.setPlayerScore(scores[0]);
                game.player2.setPlayerScore(scores[1]);

                updateScore(game.playerScores());
                // set the UI of the player's turns based on their play
                if (bundle.getString("nextPlayer").equals("B")) {
                    playerOneTurnValues();
                }else {
                    playerTwoTurnValues();
                }


                // Get which piece the human player is
                if (bundle.getString("humanPiece").equals("White")){
                    Button player1Button = findViewById(R.id.player1);
                    String setText = "Black: AI";
                    player1Button.setText(setText);

                    Button player2Button = findViewById(R.id.player2);
                    setText = "White: Human";
                    player2Button.setText(setText);

                    isHumanBlackPlayer = false;
                }else {
                    Button player1Button = findViewById(R.id.player1);
                    String setText = "Black: Human";
                    player1Button.setText(setText);

                    Button player2Button = findViewById(R.id.player2);
                    setText = "White: AI";
                    player2Button.setText(setText);

                    isHumanBlackPlayer = true;
                }
            }
        }
        else {
            playerOneTurnValues();
            game.initializeGame(bundle.getInt("boardSize"),bundle.getInt("boardSize"));

            // get the removed pieces' board positions
            HashMap<String, int[]> removedPieces = game.board.getPiecesRemoved();
            guessRemovedPiece (removedPieces);
        }
        labelRowsAndColumns(game.board.getBoardRow());
        adapterValues = new String[game.board.getBoardSize()]; // adapter to store board values
    }

    // Set Dialog box and setup listener for the guess buttons
    // Receives: removed -> the HashMap for board piece and its corresponding removed board position
    // Returns: null
    public void guessRemovedPiece(HashMap<String, int[]> removed) {
        final Dialog guessDialog = new Dialog(MainActivity.this);
        guessDialog.setContentView(R.layout.guess_removed_dialog);
        String[] alphabet = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String pieceValue;

        // Set the text for the first button
        Button first = guessDialog.findViewById(R.id.first_piece_removed);
        pieceValue = alphabet[removed.get("Black")[0]] + " * " + alphabet[removed.get("Black")[1]];
        first.setText(pieceValue);

        // Set the text for the second button
        Button second = guessDialog.findViewById(R.id.second_piece_removed);
        pieceValue = alphabet[removed.get("White")[0]] + " * " + alphabet[removed.get("White")[1]];
        second.setText(pieceValue);

        // Setup listeners for guess buttons
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Black Pressed");
                Button player1 = findViewById(R.id.player1);
                String settext = "Black: Human";
                player1.setText(settext);

                Button player2 = findViewById(R.id.player2);
                settext = "White: AI";
                player2.setText(settext);

                isHumanBlackPlayer = true;
                guessDialog.dismiss();
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("White Pressed");

                Button player1 = findViewById(R.id.player1);
                String settext = "Black: AI";
                player1.setText(settext);

                Button player2 = findViewById(R.id.player2);
                settext = "White: Human";
                player2.setText(settext);

                isHumanBlackPlayer = false;
                guessDialog.dismiss();
            }
        });
        guessDialog.show();
    }

    // Label the rows and column dynamically
    // Receives: row -> the board size
    // Returns: null
    public void labelRowsAndColumns(int row) {
        // Initialize a string array equal to the maximum board size
        String []labelNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        // Get the linear layout and set its weightsum to the board size
        LinearLayout linearLayout = findViewById(R.id.labelRow);
        linearLayout.setWeightSum(row);

        LinearLayout verticalLayout = findViewById(R.id.labelColumn);
        verticalLayout.setWeightSum(row);

        // Label the rows dynamically
        for (int i = 0; i < row; i++ ) {
            Button rowLabelBtn = new Button(this);
            rowLabelBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .WRAP_CONTENT, 85, 1.0f));
            rowLabelBtn.setId(i);
            rowLabelBtn.setText(labelNames[i]);
            rowLabelBtn.setTextSize(13);
            rowLabelBtn.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color
                    .whiteColor));
            rowLabelBtn.setClickable(false);

            Drawable drawable = getApplicationContext().getDrawable(R.drawable.buttonshape);
            rowLabelBtn.setBackground(drawable);
            linearLayout.addView(rowLabelBtn);
        }

        // Label the columns dynamically
        for (int i = 0; i < row; i++ ) {
            Button columnLabelBtn = new Button(this);
            columnLabelBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams
                    .MATCH_PARENT, 85, 1.0f));

            columnLabelBtn.setId(i + 20);
            columnLabelBtn.setText(labelNames[i]);
            columnLabelBtn.setTextSize(13);
            columnLabelBtn.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color
                    .whiteColor));
            columnLabelBtn.setClickable(false);
            Drawable drawable2 = getApplicationContext().getDrawable(R.drawable.buttonshape);
            columnLabelBtn.setBackground(drawable2);

            verticalLayout.addView(columnLabelBtn);
        }
    }
    // Print out the board to the screen dynamically
    // Parameters: adapterValues-> board positions as 1D array
    // Returns: null
    public void makeGrid(String[] adapterValues) {
        // Dynamically create a grid view
        GridView gridview = findViewById(R.id.gridview);
        gridview.setNumColumns(game.board.getBoardRow());

        // Create an image adapter to pass the board array that we get from board class
        ImageAdapter imageAdapter = new ImageAdapter(this, this.adapterValues, deviceDimensions,
                game.board.getBoardRow());
        gridview.setAdapter(imageAdapter);

        boolean movesRemainPlayer1 = game.board.isMovesRemainingOnBoard(game
                .player1.getPlayerColor());
        boolean movesRemainPlayer2 = game.board.isMovesRemainingOnBoard(game
                .player2.getPlayerColor());

        // Both players have no moves remaining
        if (!movesRemainPlayer1 && !movesRemainPlayer2) {
            declareWinner(game.playerScores());
        }

        // Player One gets turn if it has valid moves remaining or if there are no moves
        // remaining for player 2
        else if ((playerOneTurn && movesRemainPlayer1) || (!movesRemainPlayer2)){
            findViewById(R.id.player1).setBackgroundColor(Color.RED);
            findViewById(R.id.player2).setBackgroundColor(Color.WHITE);

            // modify flags if there are no moves remaining for player 2
            if (!movesRemainPlayer2) {
                callToast(MainActivity.this, "White has no more moves.Black's turn");
                game.player1.setCurrentPlay(true);
                game.player2.setCurrentPlay(false);
                playerOneTurn = true;
                findViewById(R.id.player1).setBackgroundColor(Color.RED);
                findViewById(R.id.player2).setBackgroundColor(Color.WHITE);
            }
        }

        // Player Two gets turn if it has valid moves remaining or If there are no moves
        // remaining for player 1
        else if ((!playerOneTurn && movesRemainPlayer2) || (!movesRemainPlayer1)) {
            findViewById(R.id.player1).setBackgroundColor(Color.WHITE);
            findViewById(R.id.player2).setBackgroundColor(Color.RED);
            // modify flags if there are no moves remaining for player 1
            if (!movesRemainPlayer1) {
                callToast(MainActivity.this, "Black has no more moves. White's turn");
                game.player1.setCurrentPlay(false);
                game.player2.setCurrentPlay(true);
                playerOneTurn = false;
                findViewById(R.id.player1).setBackgroundColor(Color.WHITE);
                findViewById(R.id.player2).setBackgroundColor(Color.RED);
            }
        }

        // set click listener for gridview
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            alternatePlayTurn(v,position,playerOneTurn);
        }
        });
    }

    // Helper function that alternates player turns in the game
    // Parameters: View-> current view of the app
    //             position -> the position in the grid where the user clicked
    //             plOneTurn -> determine whether it is player one's turn
    // Returns: null
    public void alternatePlayTurn(View v, int position, boolean plOneTurn) {
        Button plOneButton = findViewById(R.id.player1);
        Button plTwoButton = findViewById(R.id.player2);

        if (plOneTurn && game.player1.isCurrentPlay()) {
            // First click is from the source position
            if (sourceMoveClickedPlayer1) {
                // Check if Player 1 is moving the black piece
                if (!game.player1.getPlayerColor().equals(getCurrentBoardPiece(position))) {
                    game.player1.setIllegalMove(true);
                    game.player1.setCurrentPlay(true);
                    callToast(MainActivity.this, "" + "Illegal! Move Black Piece.");
                    sourceMoveClickedPlayer1 = true;
                }
                // Proceed with the second click as player 1 moved its piece
                else {
                    // Check if the user is making a multiple move
                    // If true, then the previous move's destination should match the current source
                    if (destinationValue != -1 ){
                        // check if source equals the previous move's destination
                        // if false, warn the user and allow the current player to play again
                        if (position != destinationValue) {
                            game.player1.setIllegalMove(true);
                            game.player1.setCurrentPlay(true);
                            callToast(MainActivity.this, "Invalid multiple move");
                            sourceMoveClickedPlayer1 = true;
                        }
                        // Multiple move allowed. User chose the correct piece
                        else {
                            sourceValue = position;
                            sourceMoveClickedPlayer1 = false;
                        }
                    }
                    // destination piece has not been set. This is a single move then.
                    else {
                        sourceValue = position;
                        sourceMoveClickedPlayer1 = false;
                    }
                    v.setFocusable(true);
                    v.setBackgroundColor(Color.rgb(204, 51, 0));
                }
            }

            // Second click is from the destination position
            else {
                // Player 1 makes the move
                game.play(sourceValue, position);

                // Display error message if invalid move
                if (!game.player1.isSuccessfulMove()) {
                    callToast(MainActivity.this, "" + "Invalid Move!");
                }
                // Move was successful
                else  {
                    // Check if the destination piece can be validly moved elsewhere
                    // If true, then the current player gets its turn again
                    if (game.board.checkMoreMoves(position)) {
                        destinationValue = position;
                        playerOneTurn = true;
                        game.player1.setCurrentPlay(true);
                        game.player2.setCurrentPlay(false);
                        plOneButton.setBackgroundColor(Color.RED);
                        plTwoButton.setBackgroundColor(Color.WHITE);

                        Button skip_turn = (Button) findViewById(R.id.skip_turn);
                        skip_turn.setVisibility(View.VISIBLE);
                    }
                    // Player 1's turn is over. Pass the turn to Player 2
                    else {
                        destinationValue = -1;
                        playerOneTurn = false;
                        game.player1.setCurrentPlay(false);
                        game.player2.setCurrentPlay(true);
                        plOneButton.setBackgroundColor(Color.WHITE);
                        plTwoButton.setBackgroundColor(Color.RED);
                    }
                }
                // Reset the flag after the destination is clicked
                sourceMoveClickedPlayer1 = true;
            }
        }

        // Player Two's turn
        else {
            // First click is from the source position
            if (game.player2.isCurrentPlay()) {
                if (sourceMoveClickedPlayer2) {
                    // Check if Player 2 is moving the white piece
                    if (!game.player2.getPlayerColor().equals(getCurrentBoardPiece(position))) {
                        game.player2.setIllegalMove(true);
                        game.player2.setCurrentPlay(true);
                        callToast(MainActivity.this, "" + "Illegal! Move White Piece.");
                        sourceMoveClickedPlayer2 = true;
                    }
                    // Proceed with the second click as player 2 moved its piece
                    else {
                        // Check if the user is making a multiple move
                        // If true, then the previous move's destination should match the current source
                        if (destinationValue != -1 ){
                            // check if source equals the previous move's destination
                            // if false, warn the user and allow the current player to play again
                            if (position != destinationValue) {
                                game.player2.setIllegalMove(true);
                                game.player2.setCurrentPlay(true);
                                callToast(MainActivity.this, "Invalid multiple move");
                                sourceMoveClickedPlayer2 = true;
                            }
                            // Multiple move allowed. User chose the correct piece
                            else {
                                sourceValue = position;
                                sourceMoveClickedPlayer2 = false;
                            }
                        }
                        // destination piece has not been set. This is a single move then.
                        else {
                            sourceValue = position;
                            sourceMoveClickedPlayer2 = false;
                        }
                        v.setFocusable(true);
                        v.setBackgroundColor(Color.rgb(204, 51, 0));
                    }
                }

                // Second click is from the destination position
                else {
                    // Player 2 makes the move
                    game.play(sourceValue, position);

                    // Display error message if invalid move
                    if (!game.player2.isSuccessfulMove()) {
                        callToast(MainActivity.this, "" + "Invalid Move!");
                    }
                    // Move was successful
                    else {
                        // Check if the destination piece can be validly moved elsewhere
                        // If true, then the current player gets its turn again
                        if (game.board.checkMoreMoves(position)) {
                            destinationValue = position;
                            playerOneTurn = false;
                            game.player1.setCurrentPlay(false);
                            game.player2.setCurrentPlay(true);
                            plOneButton.setBackgroundColor(Color.WHITE);
                            plTwoButton.setBackgroundColor(Color.RED);

                            Button skip_turn = (Button) findViewById(R.id.skip_turn);
                            skip_turn.setVisibility(View.VISIBLE);
                        }
                        // Player 2's move is over. Pass the turn to Player 1
                        else {
                            destinationValue = -1;
                            playerOneTurn = true;
                            game.player1.setCurrentPlay(true);
                            game.player2.setCurrentPlay(false);
                            plOneButton.setBackgroundColor(Color.RED);
                            plTwoButton.setBackgroundColor(Color.WHITE);
                        }
                    }
                    // Reset the flag after the destination is clicked
                    sourceMoveClickedPlayer2 = true;
                }
            }
        }
        // Update the scores and board state
        updateScore(game.playerScores());
        updateState(game.getBoardState());
    }

    // Reset the values for Player 1
    // Receives: null
    // Returns: null
    public void playerOneTurnValues() {
        // Set UI for player 1
        findViewById(R.id.player1).setBackgroundColor(Color.RED);
        findViewById(R.id.player2).setBackgroundColor(Color.WHITE);
        findViewById(R.id.skip_turn).setVisibility(View.INVISIBLE);

        playerOneTurn = true;
        game.player1.setCurrentPlay(true);
        game.player2.setCurrentPlay(false);
        sourceMoveClickedPlayer1 = true;
        sourceMoveClickedPlayer2 = true;
    }

    // Reset the values for player 2
    // Receives: null
    // Returns: null
    public void playerTwoTurnValues() {
        // Set UI for player 2
        findViewById(R.id.player1).setBackgroundColor(Color.WHITE);
        findViewById(R.id.player2).setBackgroundColor(Color.RED);
        findViewById(R.id.skip_turn).setVisibility(View.INVISIBLE);
        playerOneTurn = false;

        game.player1.setCurrentPlay(false);
        game.player2.setCurrentPlay(true);
        sourceMoveClickedPlayer1 = true;
        sourceMoveClickedPlayer2 = true;
    }

    // Get the current player's piece color
    // Receives: null
    // Returns: Color of the current player's piece
    public String getCurrentPlayerColor() {
        String currentPlayerColor;
        if (playerOneTurn) {
            currentPlayerColor = "B";
        }else {
            currentPlayerColor = "W";
        }
        return currentPlayerColor;
    }

    // Setup click listeners for next button
    // Receives: searchAlgorithmSelected -> the algorithm that the user selected in the spinner
    // Returns: null
    public void nextMoveListener() {
        Button nextMove = findViewById(R.id.next_move);
        nextMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPlayerColor= getCurrentPlayerColor();

                game.searchByDFS(currentPlayerColor);

            }
        });
    }

    // Blink the next moves
    // Receives: possibleMoves -> a list of possible moves
    // Returns: null
    public void blink (Pair<Integer, List<Integer>> possibleMoves) {
        // Blink the source piece
        ImageView imageView1 = root.findViewWithTag("id" + possibleMoves.first);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        imageView1.startAnimation(animation);

        // blink the possible destination pieces
        for (Integer destinationMoves: possibleMoves.second) {
            ImageView imageView2 = root.findViewWithTag("id" + destinationMoves);
            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
            imageView2.startAnimation(animation2);
        }
    }

    // Function that attempts to save the current game state to a file in internal storage
    // Receives: null
    // Returns: boolean to indicate whether the file was successfully saved or not
    public boolean saveGame(String filename) {
        String serializedFile = "";                 // contents of the file
        String currentPlay;                         // store the value of the current player

        String[] currentBoardValue = convertToStringArray(game.getBoardState());

        // check whose turn it is right now
        if (playerOneTurn) {currentPlay = "Black";}
        else {currentPlay = "White";}

        // Add contents for the file
        serializedFile += "Black: " + game.player1.getPlayerScore() + "\n";
        serializedFile += "White: " + game.player2.getPlayerScore() + "\n";
        serializedFile += "Board:\n";

        // Save the current state of the board to the file
        for (int i = 0 ; i < currentBoardValue.length; i++ ) {
            if (i == 0) {
                serializedFile += currentBoardValue[i] + " ";
            }
            else if ((i + 1) % game.board.getBoardRow() != 0) {
                serializedFile += currentBoardValue[i] + " ";
            }
            else {
                serializedFile += currentBoardValue[i] + " \n";
            }
        }

        serializedFile += "Next Player: " + currentPlay + "\n";
        if (isHumanBlackPlayer) {
            serializedFile += "Human: Black";
        }
        else {
            serializedFile += "Human: White";
        }
        return generateKonaneOnSD(MainActivity.this, filename, serializedFile);
    }

    // Save the current game state in "Konane" folder in the external SD card.
    // Receives: Context -> the current context
    //  saveFileName -> the name of the file to save
    //  serializedContent -> the contents of the current board state
    // Returns: the flag to identify if the save was successful or not.
    public boolean generateKonaneOnSD (Context context, String saveFileName, String
            serializedContent) {
        try {
            File root = new File (Environment.getExternalStorageDirectory(), "Konane");

            // Check if Konane folder exists to save the file
            // If not, then create the directory
            if (!root.exists()) {
                root.mkdirs();
            }
            File file1 = new File (root, saveFileName);
            FileWriter writer = new FileWriter(file1);
            writer.write(serializedContent);
            writer.flush();
            writer.close();
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Resolves the board piece that the player selected
    // Parameter : source -> the position in the board where the user clicked
    // Returns: the current board piece in the source
    public String getCurrentBoardPiece(int source) {
        int row_value = game.board.get2DCoordinates(source)[0];
        int column_value = game.board.get2DCoordinates(source)[1];
        return game.board.getBoard()[row_value][column_value];
    }

    // Refresh the board after a move
    // Parameter : boardArray -> the 2D representation of the board
    // Return: null
    public void updateState(String[][] boardArray) {
        adapterValues = convertToStringArray(boardArray);
        makeGrid(adapterValues);
    }

    // Display messages to the user as Toast
    // Parameters: mContext -> the current context of the app
    //              message -> the message to be displayed in the toast
    // Return: null
    public void callToast(Context mContext, String message) {
        Toast.makeText(mContext, "" + message, Toast.LENGTH_SHORT).show();
    }

    // Updates the scores of the players in the UI
    // Parameter: score -> the scores of the player as an array
    // Return: null
    public void updateScore (int[] score) {
        // Update Player 1's score
        Button scoreId = findViewById(R.id.player1score);
        scoreId.setText(String.valueOf(score[0]));
        scoreId.setHighlightColor(Color.LTGRAY);

        // Update Player 2's score
        Button scoreId2 = findViewById(R.id.player2score);
        scoreId2.setText(String.valueOf(score[1]));
        scoreId2.setHighlightColor(Color.LTGRAY);
    }

    // Create an intent with the game result added as an extra message
    // and call the EndActivity class
    // Receives: score -> the score for the two players
    // Returns: null
    public void declareWinner(int[] score) {
        Intent endAct = new Intent(this, EndActivity.class);
        if (score[0] == score[1]) {
            endAct.putExtra("result", "draw");
        }
        else if (score[0] > score[1]) {
            endAct.putExtra("result", "player1won");
        }
        else if (score[0] < score[1]) {
            endAct.putExtra("result", "player2won");
        }
        startActivity(endAct);
    }

    // Function that converts from 2D string array into 1D string array
    // Parameter: boardArray -> the 2D representation of the board
    // Return: 1D representation of the board
    public String[] convertToStringArray (String[][] boardArray) {
        String[] temp_array = new String[game.board.getBoardSize()];
        int count = 0;

        for (int i = 0; i < game.board.getBoardRow(); i++) {
            for (int j = 0; j < game.board.getBoardColumn(); j++) {
                temp_array[count] = boardArray[i][j];
                count++;
            }
        }
        return temp_array;
    }

    // Media that handles playing music during the game
    public static MediaPlayer mediaPlayer;
    public static void SoundPlayer(Context mContext, int raw_id) {
        mediaPlayer = MediaPlayer.create(mContext, raw_id);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(100,100);
        mediaPlayer.start();
    }
}