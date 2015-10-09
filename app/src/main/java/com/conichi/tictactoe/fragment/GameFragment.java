package com.conichi.tictactoe.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.conichi.tictactoe.R;
import com.conichi.tictactoe.entity.Board;
import com.conichi.tictactoe.entity.Point;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GameFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.layoutGame)
    LinearLayout layoutGame;

    public static final String AI_SIGN = "ai_sign";

    //Board coordinates that will be attached to the buttons
    private static final int[][] POINTS = {
            {0,0}, {0,1}, {0,2},
            {1,0}, {1,1}, {1,2},
            {2,0}, {2,1}, {2,2}};

    //the value of the AI player, which is null if the game is Human vs Human
    private Board.Value AIValue;
    //the value of the last player in the Human vs Human game, by default it is O so that the X player will play first
    private Board.Value lastPlayerValue = Board.Value.O;
    //initialize board
    private Board board = new Board();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        ButterKnife.bind(this, view);

        setButtons();
        setAIPlayer();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //set buttons by looping through all parent view's children, clearing texts
    //and setting points as button tags so we can access them in the OnClickListener
    private void setButtons() {
        int pointIndex = 0;

        for (int i = 0; i < layoutGame.getChildCount(); i++) {
            LinearLayout view = (LinearLayout) layoutGame.getChildAt(i);
            for (int j = 0; j < view.getChildCount(); j++) {
                Button button = (Button) view.getChildAt(j);
                button.setOnClickListener(this);
                button.setText("");

                int[] pointCoordinates = POINTS[pointIndex];
                Point point = new Point(pointCoordinates[0], pointCoordinates[1]);
                button.setTag(point);

                pointIndex++;
            }
        }
    }

    //getting AI player value from the Fragment's arguments, if selected value is X, AI draws first
    private void setAIPlayer() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.getString(AI_SIGN) != null) {
            AIValue = Board.Value.valueOf(bundle.getString(AI_SIGN));

            if (AIValue == Board.Value.X) {
                playAI();
            }
        }
    }

    //firstly the Alpha-Beta pruning Minimax algorithm is run to find and score all the possible plays
    //afterwards, get the best move according to the X or O player, play the move, and set its button text
    //check if the game is over after every move
    private void playAI() {
        board.runAlphaBetaMinimax(Integer.MIN_VALUE, Integer.MAX_VALUE, 0, AIValue);
        Point point = board.getBestMove(AIValue);
        board.playMove(point, AIValue);

        //loop through all parent view's children to find the right button and set its text value
        for (int i = 0; i < layoutGame.getChildCount(); i++) {
            LinearLayout view = (LinearLayout) layoutGame.getChildAt(i);
            for (int j = 0; j < view.getChildCount(); j++) {
                Button button = (Button) view.getChildAt(j);
                Point buttonPoint = (Point) button.getTag();

                if (point.equals(buttonPoint)) {
                    button.setText(AIValue.name());
                    setButtonTextColor(button);
                    break;
                }
            }
        }

        checkIfGameOver();
    }

    //display the Game Over dialog with the choice of going back to home to choose the game type, or start a new game immediately
    private void showGameOverDialog(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().onBackPressed();
                    }
                })
                .setPositiveButton(R.string.new_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newGame();
                    }
                });
        builder.create().show();
    }

    //for new game, reset the board, set lastPlayerValue to be O so that X will be the first to play
    //clear all button texts and check if AI player should play first
    private void newGame() {
        board.resetBoard();
        lastPlayerValue = Board.Value.O;
        setButtons();
        setAIPlayer();
    }

    //check if game over, and display the appropriate dialog
    private void checkIfGameOver() {
        if (board.isXWinner()) {
            showGameOverDialog(R.string.x_won);
        } else if (board.isOWinner()) {
            showGameOverDialog(R.string.o_won);
        } else if (board.isGameDrawn()) {
            showGameOverDialog(R.string.draw);
        }
    }

    //this is the button click listener
    @Override
    public void onClick(View view) {
        Button button = (Button) view;

        //a click is valid only if the button text is empty, meaning the button has not been clicked so far or played by the AI
        if (button.getText().length() == 0) {
            //get the Point from the button tag
            Point point = (Point) button.getTag();

            //check if AI mode is on
            if (AIValue != null) {
                //play the user's move according to the AI's sign
                if (AIValue == Board.Value.X) {
                    board.playMove(point, Board.Value.O);
                    button.setText(Board.Value.O.name());
                }
                else if (AIValue == Board.Value.O) {
                    board.playMove(point, Board.Value.X);
                    button.setText(Board.Value.X.name());
                }

                //after the move is played, play the AI move
                playAI();
            }
            //if AI mode is off, it means it is a Human vs Human game
            else {
                //check the last player's sign, and play the opposite one
                if (lastPlayerValue == Board.Value.X) {
                    board.playMove(point, Board.Value.O);
                    button.setText(Board.Value.O.name());
                    lastPlayerValue = Board.Value.O;
                }
                else if (lastPlayerValue == Board.Value.O) {
                    board.playMove(point, Board.Value.X);
                    button.setText(Board.Value.X.name());
                    lastPlayerValue = Board.Value.X;
                }

                //after the play, check if the game is over
                checkIfGameOver();
            }

            setButtonTextColor(button);
        }
    }

    //set button text color to differentiate the X's and O's
    private void setButtonTextColor(Button button) {
        if (button.getText().equals("X")) {
            button.setTextColor(Color.BLACK);
        }
        else if (button.getText().equals("O")) {
            button.setTextColor(Color.RED);
        }
    }
}
