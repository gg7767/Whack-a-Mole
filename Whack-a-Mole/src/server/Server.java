package server;

import client.gui.GameBoard;

import client.gui.NetworkClient;
import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * @author: Gnandeep Gottipati and Mark Craft
 * @description Server of the WAM game
 */

public class Server implements WAMProtocol, Runnable  {
    private ServerSocket server;


    private ArrayList<WAMPlayer> players;

    private Mole1[][] board;

    private int rows;

    private int cols;

    private int playerNumber;

    private int time;

    private ArrayList<Integer> whackedmoles;

    private boolean done;


    /**
     * Constructor for the server of the class
     * @param port: port of the socket
     * @param rows: no. of rows
     * @param cols: no. of cols
     * @param players: array list of all the players
     * @param time: time period of the game
     * @throws IOException
     */

    public Server(int port, int rows, int cols, int players, int time) throws IOException {
        this.server = new ServerSocket(port);
        this.rows = rows;
        this.cols = cols;
        this.playerNumber= players;
        this.time = time * 1000;
        this.players = new ArrayList<>();
        this.whackedmoles = new ArrayList<>();
        this.done = true;


    }


    /**
     * runs the entire server for the client
     */
    @Override
    public void run ()  {

        System.out.println("Waiting for players");
        for (int i =0; i<playerNumber; i++){
            Socket socket = null;
            try {
                socket = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            WAMPlayer player = null;
            try {
                player = new WAMPlayer(socket, i, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.players.add(player);
        }

        for (WAMPlayer player: players){
            player.sendMove(WELCOME + " " + this.rows +" " + this.cols+ " " + this.players.size() + " " + player.getPlayerNumber());
        }
        for (WAMPlayer player: players){
            Thread thread = new Thread(player);
            thread.start();

        }

        Mole1 mole =new Mole1(players, this);
        mole.start();

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis()-start <= time)
        {

        }

        done = false;
        for (int i=0; i<rows*cols; i++)
        {
            for (WAMPlayer player : players)
            {
                player.sendMove(MOLE_DOWN + " " + i);
            }
        }

        this.checkStatus();









    }

    /**
     * returns the arraylist of the wacked moles
     * @return
     */

    public ArrayList<Integer> getWhackedMole(){
        return this.whackedmoles;
    }

    /**
     * checks who wins the game and looses the game and ties the game
     */

    public void checkStatus(){
        String allScores = scores();
        String [] tokens = allScores.split(" ");
        if (tokens.length == 1)
        {
            players.get(0).sendMove(GAME_WON);
        }
        else if (tokens.length == 2)
        {
            if (Integer.parseInt(tokens[0]) > Integer.parseInt(tokens[1]))
            {
                players.get(0).sendMove(GAME_WON);
                players.get(1).sendMove(GAME_LOST);
            }
            else if (Integer.parseInt(tokens[0]) < Integer.parseInt(tokens[1]))
            {
                players.get(1).sendMove(GAME_WON);
                players.get(0).sendMove(GAME_LOST);
            }
            else if (Integer.parseInt(tokens[0]) == Integer.parseInt(tokens[1]))
            {
                players.get(0).sendMove(GAME_TIED);
                players.get(1).sendMove(GAME_TIED);
            }
        }
        else if(tokens.length == 3)
        {
            if ((Integer.parseInt(tokens[0]) == Integer.parseInt(tokens[1])) && (Integer.parseInt(tokens[0]) == Integer.parseInt(tokens[2])))
            {
                players.get(0).sendMove(GAME_TIED);
                players.get(1).sendMove(GAME_TIED);
                players.get(2).sendMove(GAME_TIED);
            }
            else if ((Integer.parseInt(tokens[0]) >= Integer.parseInt(tokens[1])) && (Integer.parseInt(tokens[0]) >= Integer.parseInt(tokens[2])))
            {
                players.get(0).sendMove(GAME_WON);
                players.get(1).sendMove(GAME_LOST);
                players.get(2).sendMove(GAME_LOST);
            }

            else if ((Integer.parseInt(tokens[1]) >= Integer.parseInt(tokens[0])) && (Integer.parseInt(tokens[1]) >= Integer.parseInt(tokens[2])))
            {
                players.get(1).sendMove(GAME_WON);
                players.get(0).sendMove(GAME_LOST);
                players.get(2).sendMove(GAME_LOST);
            }

            else if ((Integer.parseInt(tokens[2]) >= Integer.parseInt(tokens[1])) && (Integer.parseInt(tokens[2]) >= Integer.parseInt(tokens[0])))
            {
                players.get(2).sendMove(GAME_WON);
                players.get(1).sendMove(GAME_LOST);
                players.get(0).sendMove(GAME_LOST);
            }
        }
    }

    /**
     * returns the points of given each players
     * @param moleNumber: mole number
     * @return
     */
    public int downMole(int moleNumber){
        if (this.whackedmoles.contains(moleNumber)){
            for (WAMPlayer player: players){
                player.sendMove(WAMProtocol.MOLE_DOWN + " " + moleNumber);
            }
            return 2;
        }
        else{
            return -1;
        }
    }

    /**
     * stringified version scores of which would sent
     * @return
     */

    public String scores(){
        String result = "";
        for (int i = 0; i<players.size(); i++){
            int points = players.get(i).getScore();
            if (i==0){
                result += points;
            }
            else {
                result = " " + points;
            }
        }
        return result;
    }

    /**
     * check if the game is  done
     * @return
     */
    public boolean done(){
        return this.done;
    }

    /**
     * gets the cols
     * @return
     */
    public int getCols(){
        return this.cols;
    }

    /**
     * gets the rows
     * @return
     */
    public int getRows() {
        return rows;
    }

    /**
     * main function server which runs the server
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        int rows = Integer.parseInt(args[1]);
        int cols = Integer.parseInt(args[2]);
        int players = Integer.parseInt(args[3]);
        int gameDuration = Integer.parseInt(args[4]);
        Server server = new Server(port, rows, cols, players, gameDuration);
        server.run();


    }
}
