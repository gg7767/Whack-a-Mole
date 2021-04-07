package client.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the model for the Whack-A-Mole GUI
 * @authors Mark Craft and Gnandeep Gottipati
 */
public class GameBoard {

    // The model board that will contain the moles
    private Mole[][] board;

    //private final static int MOLE_TIME = 2;

    //private NetworkClient client;

    //private int numPlayers;

    //private int p1Score;

    //private int p2Score;

    //private int p3Score;

    // The list of observers for the game
    private List<Observer<GameBoard>> observers;

    private Status status;


    /**
     * Initializes the list of observers for the game
     */
    public GameBoard() {
        this.observers = new LinkedList<>();
        this.status = Status.NOT_OVER;
    }

    /**
     * Initializes the game board. Fills the board with moles
     *
     * @param row    the number of rows in the board
     * @param column the number of columns in the board
     */
    public void makeBoard(int row, int column) {
        int num = 0;
        this.board = new Mole[row][column];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                Mole mole = new Mole();
                num ++;
                this.board[i][j] = mole;
            }
        }
    }

    /**
     * Represents the string version of the game board
     *
     * @return the string of the board
     */
    public String toString() {
        String x = "";
        x += board[0][0].toString();
        return x;
    }

    /**
     * Possible Statuses of the game
     */
    public enum Status {
        NOT_OVER, WIN, LOST, TIED, ERROR;

        private String message = null;

        public void setMessage(String msg) {
            this.message = msg;
        }

        /**
         * The string version of the Status enumerations
         *
         * @return strings of the Status enumerations
         */
        @Override
        public String toString() {
            return super.toString() +
                    this.message == null ? "" : ('(' + this.message + ')');
        }
    }

    /**
     * Adds an observer to the observer list
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer<GameBoard> observer) {
        this.observers.add(observer);
    }

    /**
     * Alerts the observers to update their boards
     */
    private void alertObservers() {
        for (Observer<GameBoard> obs : this.observers) {
            obs.update(this);
        }
    }



    /**
     * Gets the state of the mole at a given index
     *
     * @param row    the row to check
     * @param column the column to check
     * @return the state of the mole (up or down)
     */
    public Mole getContents(int row, int column) {
        return this.board[row][column];
    }


    /**
     * sets the mole up in the given row and column
     *
     * @param row the row to set the mole up
     * @param column the column to set the mole up
     */
    public  void setMoleUp(int row, int column){
        this.board[row][column].setUp();
        alertObservers();
    }

    /**
     * sets the mole down in the given row and column
     *
     * @param row the row to set the mole down
     * @param column the column to set the mole down
     */
    public  void setMoleDown(int row, int column) {
        this.board[row][column].setDown();
        alertObservers();
    }

    public Status getStatus() {
        return status;
    }

    public void gameWon() {
        this.status = Status.WIN;
        alertObservers();
    }

    public void gameLost() {
        this.status = Status.LOST;
        alertObservers();
    }

    public void gameTied() {
        this.status = Status.TIED;
        alertObservers();
    }
}