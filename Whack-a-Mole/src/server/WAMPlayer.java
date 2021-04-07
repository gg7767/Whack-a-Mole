package server;

import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author: Gnandeep Gottipati and Mark Craft
 * @description Player class for the wam server
 */

public class WAMPlayer implements WAMProtocol, Runnable {

    private Socket sock;

    private PrintWriter printer;

    private Scanner scanner;

    private int playerNumber;

    private int score;

    private Server server;

    /**
     * Constructor for the wam player
     * @param socket socket
     * @param playerNumber id of the player
     * @param server: server class
     * @throws IOException
     */

    public WAMPlayer(Socket socket, int playerNumber, Server server) throws IOException {
        this.playerNumber = playerNumber;
        this.sock = socket;
        this.printer = new PrintWriter(socket.getOutputStream());
        this.scanner = new Scanner(socket.getInputStream());
        score = 0;
        this.server = server;
    }


    /**
     * Sends a message to the client
     * @param message message that has to be sent
     */
    public  void sendMove(String message){
        System.out.println(message);
        this.printer.println(message);
        this.printer.flush();
    }

    /**
     * reads the move from the client
     * @return
     */
    public String readMove(){
        if (scanner.hasNextLine()){
            return this.scanner.nextLine();
        }
        return "";
    }


    public int getScore(){
        return score;
    }

    /**
     * closes the socket
     * @throws IOException
     */
    public void close() throws IOException {
        this.sock.close();
    }

    /**
     * runs the thread
     */
    @Override
    public void run(){
        String response = "";
        while(true){
            try {
                response = this.readMove();
            }
            catch (Exception e){

            }
            String[] tokens = response.split(" ");
            if (tokens[0].equals(WHACK)){
                this.score += server.downMole(Integer.parseInt(tokens[1]));
                this.sendMove(SCORE + " " + server.scores());
            }
            else{
                this.sendMove(ERROR);
            }
        }



        }

    /**
     * returns the id of the player
     */
    public int getPlayerNumber(){
        return this.playerNumber;
    }


}
