package project.src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * This is an offline 'online' store application. You may create multiple
 * accounts, browse and search for products, add them to the account's cart and
 * purchase them as well as view your past orders.
 *
 * @author Josh Howson
 * @version 1.0
 */
public class Project extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //start app
        launch(args);
    }

    /**
     * Entry point for the application. Does not use the application's primaryStage,
     * but instead uses the SceneLoader Class' static currentStage stage for easy 
     * swapping of scenes from different classes.
     *
     * @param primaryStage main stage for the application
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        //read data from file
        Store.readAccounts();
        Store.readProducts();
        
        //Load the main stage of the store
        Scene scene = SceneLoader.getLoginPage();
        SceneLoader.currentStage.setScene(scene);
        SceneLoader.currentStage.setTitle("minus-one: Online Store");
        Image icon = new Image("images/icon.png");
        SceneLoader.currentStage.getIcons().add(icon);
        
        //show the stage
        SceneLoader.currentStage.show();
    }
}