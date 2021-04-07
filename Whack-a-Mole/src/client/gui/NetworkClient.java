package client.gui;

import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *  Network Client class for the WAMGUI
 *  @authors Mark Craft and Gnandeep Gottipati
 */


public class NetworkClient implements WAMProtocol {
    //Socket to connect to the server
    private Socket socket;
    //Print writer to send messages to the server
    private PrintWriter out;
    // Scanner that takes in input from the server
    private Scanner in;
    //number of rows in the WAMboard
    private int rows;
    //number of columns in the WAMboard
    private int columns;

    private int numplayers;

    private int playernumber;
    // the actual WAM board
    private GameBoard board;

    private int score;

    /**
     * Constructor for the network client
     * @param socket to connect to the board
     * @param board game board
     * @throws IOException
     */

    public NetworkClient(Socket socket, GameBoard board) throws IOException {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new Scanner(socket.getInputStream());
        this.board = board;
        this.score = 0;
    }

    /**
     * Sends a message to the server
     * @param message message that has to be sent
     */
    public void sendMove(String message){
        this.out.println(message);
        this.out.flush();
    }

    /**
     * reads the move from the server
     * @return
     */
    public String readMove(){
        if (in.hasNextLine()){
            return this.in.nextLine();
        }
        return "";
    }

    /**
     * closes the socket
     * @throws IOException
     */
    public void close() throws IOException {
        this.socket.close();
    }

    public void whack(int moleNum, int playernumber){
        int row = moleNum/this.columns;
        int col = moleNum%this.columns;
        board.setMoleDown(row, col);
        this.sendMove(WHACK + " " + moleNum + " " + playernumber);
    }

    /**
     * runs the entire game by taking messages from the server
     */
    public  void run() {
        while (true){
            //initializing the response from the server to an empty string
            String response = "";
            response = this.readMove(); // reads the message and assigns it to response

            String[] tokens = response.split(" "); //parses it

            if (response.equals("")){
                break;
            }


            if (tokens[0].equals(WELCOME)){
                this.rows = Integer.parseInt(tokens[1]);
                this.columns = Integer.parseInt(tokens[2]);
                this.numplayers = Integer.parseInt(tokens[3]);
                this.playernumber = Integer.parseInt(tokens[4]);
                this.board.makeBoard(this.rows, this.columns);
            }

            else if (tokens[0].equals(MOLE_UP)){
                int molenumber = Integer.parseInt(tokens[1]);
                int row = molenumber/this.columns;
                int col = molenumber%this.columns;
                board.setMoleUp(row, col); // sets the mole up and alerts the observers
                System.out.println("Moleup :"+ molenumber);
            }

            else if (tokens[0].equals(MOLE_DOWN)){
                int molenumber = Integer.parseInt(tokens[1]);
                int row = molenumber/this.columns;
                int col = molenumber%this.columns;
                board.setMoleDown(row, col); //sets the mole down and alerts the observers
                System.out.println("Moledown :"+ molenumber);
            }

            else if (tokens[0].equals(SCORE)){
                int index = this.getPlayernumber() + 1;
                score =  Integer.parseInt(tokens[index]);
            }

            else if (tokens[0].equals(GAME_WON)){
                board.gameWon();
            }
            else if (tokens[0].equals(GAME_LOST)){
                board.gameLost();
            }
            else if (tokens[0].equals(GAME_TIED)){
                board.gameTied();
            }
        }

    }

    /**
     * starts a new thread and runs the process
     */

    public void startListener(){
        new Thread(() -> {

                this.run();
        }).start();
    }

    public int getPlayernumber(){
        return this.playernumber;
    }

    public int getScore(){
        return this.score;
    }

    /**
     * gets the number of rows in the board
     * @return rows
     */
    public int getRows(){
        return this.rows;
    }

    /**
     * gets the number of columns in the board
     * @return columns
     */
    public int getColumns(){
        return this.columns;
    }




}