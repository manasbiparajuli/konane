//************************************************************
//        * Name:  Manasbi Parajuli                                    *
//        * Project:  Project 2- Konane            *
//        * Class:  AI 331                       *
//        * Date:  3/7/18                           *
//        ************************************************************

package edu.ramapo.mparajul.konane.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

import edu.ramapo.mparajul.konane.R;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        mainMenu();
        playAgain();
        editResult();
    }

    // Setup card view listener for the user to go into the start page
    // Receives: null
    // Returns: null
    public void mainMenu() {
        CardView menu = findViewById(R.id.main_menu_cardview);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuClick();
            }
        });
    }

    // Setup card view listener for the user to restart the game
    // Receives: null
    // Returns: null
    public void playAgain() {
        CardView playAgain = findViewById(R.id.play_again_cardview);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClick();
            }
        });
    }

    // Edit the text view based on the result of the game
    // and whose values are passed from the Main Activity as extra message in the intent
    // Receives: Values from the intent
    // Returns: null
    public void editResult() {
        // Get the string value from the passed intent
        String resultValue = getIntent().getExtras().getString("result");
        TextView textView = findViewById(R.id.game_result);

        // set text of the text view based on the result of the game
        if (resultValue.equals("player1won")) {
            textView.setText(R.string.player1_won);
        }else if (resultValue.equals("player2won")){
            textView.setText(R.string.player2_won);
        }else {
            textView.setText(R.string.match_drawn);
        }
    }

    // Restart game by creating an intent
    // Receives: null
    // Returns: null
    public void playClick() {
        Intent playGame = new Intent(this, MainActivity.class);
        playGame.putExtra("gameIntent", "new_game");
        startActivity(playGame);
    }

    // Open start page by creating an intent
    // Receives: null
    // Returns: null
    public void menuClick() {
        Intent main_menu = new Intent (this, StartActivity.class);
        startActivity(main_menu);
    }
}