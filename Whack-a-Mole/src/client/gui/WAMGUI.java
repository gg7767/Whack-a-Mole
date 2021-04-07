package client.gui;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import server.Server;

/**
 * The GUI for the client for the game
 * @authors Mark Craft and Gnandeep Gottipati
 */
public class WAMGUI extends Application implements Observer<GameBoard> {

    // The model board
    private GameBoard board;

    // The player whose GUI this belongs to
    private NetworkClient client;


    // The Hbox of the players scores
    private HBox scoreBox;

    // The spots in which the moles will appear
    private GridPane moleSpots;

    private TextField P1Score;

    private TextField P2Score;

    private TextField P3Score;

    private int numPlayers;

    private Server server;

    private int rows;

    private int columns;

    private Label statusLabel;

    /**
     * Initializes the board
     * @throws IOException
     */
    public void init() throws IOException {
        List<String> args = getParameters().getRaw();
        String host = args.get(0);
        int port = Integer.parseInt(args.get(1));
        //this.rows = client.getRows();
        //this.columns = client.getColumns();
        // get host info and port from command line

        //this.server = new Server();
        // Creates the client(player) for the game


        // Initializes the scoreBox
        this.scoreBox = new HBox();

         P1Score = new TextField("Player 1 score: ");
         P2Score = new TextField("Player 2 score: ");
         P3Score = new TextField("Player 3 score: ");
        this.scoreBox.getChildren().addAll(P1Score, P2Score, P3Score);

        //P1Score.setText("Player 1 score: " + server.playerScores()[0]);
        //P2Score.setText("Player 2 score: " + server.playerScores()[1]);
        //P3Score.setText("Player 3 score: " + server.playerScores()[2]);


        // Initializes the space in which the moles will take place
        // All moles will initially start as down/empty
        this.moleSpots = new GridPane();
        // System.out.println(client.getColumns() + ">" + client.getRows());

        for(int i = 0; i < this.columns; i++){
            for(int j = 0; j < this.rows; j++){
            Button button = makeEmptyButton();
            moleSpots.add(button, i, j);
            }
        }
        this.statusLabel = new Label("Welcome to Whack a Mole");


        // Creates the board
        this.board = new GameBoard();
        this.board.addObserver(this);
        this.client = new NetworkClient(new Socket(host, port), this.board);
        //this.numPlayers = this.client.getNumplayers();
    }

    /**
     * Runs the GUI
     * @param stage the stage for the GUI
     */
    @Override
    public void start(Stage stage){

    BorderPane borderPane = new BorderPane();
    borderPane.setCenter(moleSpots);
    borderPane.setTop(scoreBox);
    borderPane.setBottom(statusLabel);
    stage.setTitle("Whack A Mole");
    stage.setScene(new Scene(borderPane));
    stage.show();
    client.startListener();

    }

    /**
     * Makes an empty button in which a mole is down. The player loses
     * a point for clicking on an empty/down mole
     * @return a button representing an empty mole
     */
    public Button makeEmptyButton(){
        Image empty = new Image(getClass().getResourceAsStream("nomole.png"));
        ImageView view = new ImageView(empty);
        Button button = new Button();
        button.setGraphic(view);
        return button;

    }
    /**
     * Makes a button in which a mole is up in.
     * @return
     */
    public Button makeMoleButton(int moleNumber){
        Image mole = new Image(getClass().getResourceAsStream("mole.png"));
        Button button = new Button();
        button.setGraphic(new ImageView(mole));
        button.setOnAction(e -> {
            client.whack(moleNumber, client.getPlayernumber());
        });
        return button;
    }

    /**
     * refreshes the board
     */
    private void refresh()  {
    if (client.getPlayernumber() == 0){
        P1Score.setText(Integer.toString(client.getScore()));
    }
    if (client.getPlayernumber() == 1){
        P2Score.setText(Integer.toString(client.getScore()));
    }
    if (client.getPlayernumber() == 2){
        P3Score.setText(Integer.toString(client.getScore()));
    }
    GameBoard.Status status = board.getStatus();
        switch (status) {
            case ERROR:
                this.statusLabel.setText(status.toString());
                break;
            case WIN:
                this.statusLabel.setText("You won. Yay!");
                break;
            case LOST:
                this.statusLabel.setText("You lost. Boo!");
                break;
            case TIED:
                this.statusLabel.setText("Tie game. Meh.");
                break;
            case NOT_OVER:
                this.statusLabel.setText("game in progress");
                break;
        }
            for(int i = 0; i < client.getRows(); i++){
                for(int j = 0; j < client.getColumns(); j++){
                    if(board.getContents(i, j).getIsUp()){
                        Button mole = makeMoleButton(i*client.getColumns() +j);
                        moleSpots.add(mole, j, i);
                    }
                    else {
                        moleSpots.add(makeEmptyButton(), j, i);
                    }
                }
            }
        }

    /**
     * Calls the refresh function to update the board
     * @param
     */

    @Override
    public void update(GameBoard connectFourBoard) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh();
        }
        else {
            Platform.runLater( () -> this.refresh() );
        }
    }

    /**
     * main function that Launches the application
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)throws IOException{
        Application.launch(args);

    }

}
