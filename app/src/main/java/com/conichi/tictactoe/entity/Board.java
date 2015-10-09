package com.conichi.tictactoe.entity;

import java.util.ArrayList;

public class Board {

    //representing the X, O, and Empty states of the board
    public enum Value {
        X, O, EMPTY
    }

    //this is the 3x3 board
    private Value[][] board = new Value[3][3];

    //array contains the scores of points after running the Alpha-Beta pruning Minimax algorithm
    private ArrayList<Point> scoredPoints = new ArrayList<>();

    public Board() {
        resetBoard();
    }

    //get current board score by traversing through all rows, columns, and both diagonals
    //to inspect the relation between the X's and O's and setting the correct positive and negative values
    private int getCurrentBoardScore() {
        int score = 0;
        int X, O;

        //Check all rows
        for (int i = 0; i < 3; i++) {
            X = 0;
            O = 0;
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Value.X) {
                    X++;
                } else if (board[i][j] == Value.O) {
                    O++;
                }
            }
            score += getCurrentScore(X, O);
        }

        //Check all columns
        for (int j = 0; j < 3; j++) {
            X = 0;
            O = 0;
            for (int i = 0; i < 3; i++) {
                if (board[i][j] == Value.X) {
                    X++;
                } else if (board[i][j] == Value.O) {
                    O++;
                }
            }
            score += getCurrentScore(X, O);
        }

        //Check right diagonal
        X = 0;
        O = 0;

        for (int i = 0, j = 0; i < 3; i++, j++) {
            if (board[i][j] == Value.X) {
                X++;
            } else if (board[i][j] == Value.O) {
                O++;
            }
        }

        score += getCurrentScore(X, O);

        //Check left diagonal
        X = 0;
        O = 0;

        for (int i = 2, j = 0; i > -1; i--, j++) {
            if (board[i][j] == Value.X) {
                X++;
            } else if (board[i][j] == Value.O) {
                O++;
            }
        }

        score += getCurrentScore(X, O);

        return score;
    }

    //get the score of the current board by inspecting the relation of X's and O's
    //this is the heuristic function used to enable the Minimax algorithm to find the best move
    private int getCurrentScore(int X, int O){
        int currentScore;

        if (X == 3) {
            currentScore = 100;
        } else if (X == 2 && O == 0) {
            currentScore = 10;
        } else if (X == 1 && O == 0) {
            currentScore = 1;
        } else if (O == 3) {
            currentScore = -100;
        } else if (O == 2 && X == 0) {
            currentScore = -10;
        } else if (O == 1 && X == 0) {
            currentScore = -1;
        } else {
            currentScore = 0;
        }
        
        return currentScore;
    }

    //get the list of all possible points to play
    public ArrayList<Point> getPossiblePoints() {
        ArrayList<Point> possiblePoints = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == Value.EMPTY) {
                    possiblePoints.add(new Point(i, j));
                }
            }
        }

        return possiblePoints;
    }

    //check if the game is over
    private boolean isGameOver() {
        return (isXWinner() || isOWinner() || isGameDrawn());
    }

    //check if player X has a winning play
    public boolean isXWinner() {
        //traverse both diagonals, firstly right, then left
        if ((board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[2][2] == Value.X)
                || (board[2][0] == board[1][1] && board[1][1] == board[0][2] && board[0][2] == Value.X)) {
            return true;
        }

        //traverse all the rows and collumns
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][2] == Value.X)
                    || (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[2][i] == Value.X)) {
                return true;
            }
        }

        return false;
    }

    //check if player O has a winning play
    public boolean isOWinner() {
        //traverse both diagonals, firstly right, then left
        if ((board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[2][2] == Value.O)
                || (board[2][0] == board[1][1] && board[1][1] == board[0][2] && board[0][2] == Value.O)) {
            return true;
        }

        //traverse all the rows and collumns
        for (int i = 0; i < 3; i++) {
            if ((board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][2] == Value.O)
                    || (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[2][i] == Value.O)) {
                return true;
            }
        }

        return false;
    }

    //check if there any available plays, if not, the game is drawn
    public boolean isGameDrawn() {
        return getPossiblePoints().isEmpty();
    }

    //sets the given value to the given board
    public void playMove(Point point, Value value) {
        board[point.x][point.y] = value;
    }

    //this is the "brain" of the program, the Alpha-Beta pruning Minimax recursive deterministic fully observable algorithm
    //used for scoring all the possible moves of the AI player by traversing the search tree of all possible moves
    //it uses the Depth First Search (DFS) algorithm and uses the two agents (Max and Min)
    //the Alpha-Beta pruning is used to reduce the searchable tree by cutting off all non-promising nodes
    //which do not effect the end result by "remembering" the best move so far, which reduses the total search time
    //depending on the search order, the algorithm can go twice as deep compared to a plain Minimax algorithm during the same time
    //and it can have O(b^m/2) time complexity, compared to O(b^m) of a plain Minimax
    public int runAlphaBetaMinimax(int alpha, int beta, int depth, Value player) {
        //if beta is less or equal to alpha, it means that this part of the tree cannot contain promising nodes
        //because they are out of scope, and we return either the highes or lowest value
        if (beta <= alpha) {
            if (player == Value.X) {
                return Integer.MAX_VALUE;
            }
            else if (player == Value.O) {
                return Integer.MIN_VALUE;
            }
        }

        //if during all the possible moves the game gets over, evaluate the board
        if (isGameOver())  {
            return getCurrentBoardScore();
        }

        //this will store the array of all possible points
        ArrayList<Point> possiblePoints = getPossiblePoints();

        //if the starting depth is 0 (beginning of the algorithm), clear the scoredPoints array so it can get
        //loaded with new scores
        if (depth == 0) {
            scoredPoints.clear();
        }

        //set max and min values to lowest/highest values
        int maxValue = Integer.MIN_VALUE, minValue = Integer.MAX_VALUE;

        //traverse all possible points to score each play
        for (Point point : possiblePoints) {
            //set currentScore to 0
            int currentScore = 0;

            playMove(point, player);

            //if the move evaluated is of player X, play move, run the algorithm for the opposite player and increase the depth
            if (player == Value.X) {
                currentScore = runAlphaBetaMinimax(alpha, beta, depth + 1, Value.O);

                //get the largest value of maxValue and currentScore from the minimax for the next depth
                maxValue = Math.max(maxValue, currentScore);

                //set alpha to the largest value of currentScore and current alpha
                alpha = Math.max(currentScore, alpha);
            }
            //if the move evaluated is of player O, play move, run the algorithm for the opposite player and increase the depth
            else if (player == Value.O) {
                currentScore = runAlphaBetaMinimax(alpha, beta, depth + 1, Value.X);

                //get the lowest value of minValue and currentScore from the minimax for the next depth
                minValue = Math.min(minValue, currentScore);

                //set beta to the lowest value of currentScore and current beta
                beta = Math.min(currentScore, beta);
            }

            //the scored point is added to the array only on depth 0
            if (depth == 0) {
                scoredPoints.add(new Point(point, currentScore));
            }

            //reset the value of the board which is played in the minimax for analysis
            board[point.x][point.y] = Value.EMPTY;

            //check if pruning has been made, so that the rest of the nodes do not need to get evaluated
            if (currentScore == Integer.MAX_VALUE || currentScore == Integer.MIN_VALUE) {
                break;
            }
        }

        //in case of player X return maxValue, in case of player O return minValue
        return player == Value.X ? maxValue : minValue;
    }

    //traverse the scoredPoints array to find the best move according to the score
    //depending on the X and O player, the search is done for the MAX and MIN value
    public Point getBestMove(Value player) {
        int MAX = Integer.MIN_VALUE;
        int MIN = Integer.MAX_VALUE;

        int bestMoveIndex = -1;

        for (int i = 0; i < scoredPoints.size(); i++) {
            //get the highest scored point for the X player
            if (player == Value.X && MAX < scoredPoints.get(i).score) {
                MAX = scoredPoints.get(i).score;
                bestMoveIndex = i;
            }
            //get the lowest scored point for the O player
            else if (player == Value.O && MIN > scoredPoints.get(i).score) {
                MIN = scoredPoints.get(i).score;
                bestMoveIndex = i;
            }
        }

        return scoredPoints.get(bestMoveIndex);
    }

    //traverse the board an reset the values to the empty state
    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = Value.EMPTY;
            }
        }
    }

}
