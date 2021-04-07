package client.gui;

/**
 * Observer Interface for the WAM GUI
 * @param <E> generic parameter
 * @authors Mark Craft and Gnandeep Gottipati
 */
public interface Observer<E> {
    void update (E subject);
}
