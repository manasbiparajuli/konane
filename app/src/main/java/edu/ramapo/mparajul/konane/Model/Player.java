//************************************************************
//        * Name:  Manasbi Parajuli                                    *
//        * Project:  Project 2- Konane            *
//        * Class:  AI 331                       *
//        * Date:  3/7/18                           *
//        ************************************************************
package edu.ramapo.mparajul.konane.Model;

/**
 * Created by Manasbi on 1/28/2018.
 */

public class Player {

    private int score = 0;                  // the score of the player
    private String playerColor;             // the color piece for a player
    private boolean currentPlay = false;    // flag to determine the state of play for the player
    private boolean successfulMove = false; // flag to check if the move was successful
    private boolean illegal_move = false;   // flag to check illegal move

    // Constructor for Player class
    // Receives: playerColor -> the color assigned to the player
    //           currentPlay -> flag to see if it is the player's turn
    //           score -> the current score of the player
    // Returns: the Player object
    Player (String playerColor, boolean currentPlay, int score) {
        this.playerColor = playerColor;
        this.currentPlay = currentPlay;
        this.score = score;
    }

    // Return the playerColor
    // Receives: none
    // Returns: The color assigned to the player
    public String getPlayerColor() {
        return playerColor;
    }

    // Returns the current playing state of the user
    // Receives: none
    // Returns: the current playing state of the player
    public boolean isCurrentPlay() {
        return currentPlay;
    }

    // Sets the current playing state of the player
    // Receives: currentPlay -> the current playing state of the player
    // Returns: none
    public void setCurrentPlay(boolean currentPlay) {
        this.currentPlay = currentPlay;
    }

    // Returns the player score
    // Receives: none
    // Returns: the player's score
    public int getPlayerScore() {
        return score;
    }

    // Sets the score of the player
    // Receives: value -> the point for the successful move
    // Returns: none
    public void setPlayerScore(int value) {
        this.score += value;
    }

    // Returns the flag for a successful/unsuccessful move
    // Receives: none
    // Returns: the flag for the successful move
    public boolean isSuccessfulMove() {
        return successfulMove;
    }

    // Sets the flag for a move
    // Receives: the state of the move
    // Returns: null
    public void setSuccessfulMove(boolean successfulMove) {
        this.successfulMove = successfulMove;
    }

    // Sets the flag for illegal move
    // Receives: the current state of the illegal move
    // Returns: null
    public void setIllegalMove(boolean illegal_move) {
        this.illegal_move = illegal_move;
    }
}