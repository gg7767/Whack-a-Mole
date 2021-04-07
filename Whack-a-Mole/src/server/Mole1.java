package server;

import common.WAMProtocol;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a mole to be placed in the board
 * @authors Mark Craft and Gnandeep Gottipati
 */
public class Mole1 extends Thread {
    // If the mole is up(clickable) or not
    private boolean isUp;

    private ArrayList<WAMPlayer> players;


    private Server server;

    /**
     * When a mole is created, it starts in a down state
     */
    public Mole1 (ArrayList<WAMPlayer> players, Server server){
        this.players = players;
        isUp = false;
        this.server = server;
    }

    /**
     * Gets the state of the Mole
     * @return if the mole is up or not
     */
    public boolean getIsUp(){
        return isUp;
    }

    /**
     * Sets the mole to an up(clickable) state
     */
    public void setUp(){

        isUp = true;


    }

    /**
     * Sets the mole to a down state(clicking a down state mole loses points)
     */
    public void setDown(){
        isUp = false;



    }

    public int generateNumber(int min, int max){
        Random r = new Random();
        int number = r.nextInt(max - min) + min;
        return number;
    }

    /**
     * The string representation of the mole
     * @return "Mole!" for a mole
     */
    public String toString(){
        return "Mole!";
    }

    /**
     * allows the mole to go up and down by itself
     */

    @Override
    public void run(){
        while(this.server.done()){
            Random random = new Random();
            int mole = random.nextInt(server.getRows()*server.getCols());
            for (WAMPlayer player: players){
                player.sendMove(WAMProtocol.MOLE_UP+ " " + mole);
            }
            server.getWhackedMole().add(mole);
            try {
                sleep(random.nextInt(2)*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (WAMPlayer player: players){
                player.sendMove(WAMProtocol.MOLE_DOWN + " " + mole);
            }
            server.getWhackedMole().remove((Object) mole);
        }
    }
}

