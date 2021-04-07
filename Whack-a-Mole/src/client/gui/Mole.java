package client.gui;

import java.util.Random;

/**
 * Represents a mole to be placed in the board
 * @authors Mark Craft and Gnandeep Gottipati
 */
public class Mole {
    // If the mole is up(clickable) or not
    private boolean isUp;





    /**
     * When a mole is created, it starts in a down state
     */
    public Mole(){
        isUp = false;
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




}
