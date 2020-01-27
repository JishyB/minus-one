package project.src;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This Class contains multiple static methods used to load scenes into the main
 * program's stage.
 *
 * @author Josh Howson
 */
public class SceneLoader {

    /**
     * The main stage of the online store
     */
    public static Stage currentStage = new Stage();

    /**
     * Creates the home screen that consists of sections for header, promotional
     * banner and deals
     *
     * @return the completed home screen scene
     */
    public static Scene getHomeScreen() {

        //header of the page. This is reusable (I think).
        VBox headerBox = createHeader();

        //banner image to show a deal
        ImageView banner = new ImageView("images/banner.png");
        banner.setFitWidth(750);
        banner.preserveRatioProperty().set(true);

        StackPane bannerBox = new StackPane(banner);
        bannerBox.cursorProperty().set(Cursor.HAND);
        bannerBox.setOnMouseClicked(e -> {
            //search for laptop and display results
            currentStage.setScene(getSearchQuery("Laptop", Store.searchQuery("laptop")));
        });

        //solve duplicate products problem:
        Random rand = new Random();
        int limit = Store.productDirectory.size();
        int r1 = rand.nextInt(limit);
        int r2;
        do {
            r2 = rand.nextInt(limit);
        } while (r2 == r1);
        int r3;
        do {
            r3 = rand.nextInt(limit);
        } while (r3 == r2 || r3 == r1);

        //deal 1        
        Product deal1 = Store.productDirectory.get(r1);
        VBox deal1Box = createDealBox(deal1);
        //deal 2
        Product deal2 = Store.productDirectory.get(r2);
        VBox deal2Box = createDealBox(deal2);
        //deal 3
        Product deal3 = Store.productDirectory.get(r3);
        VBox deal3Box = createDealBox(deal3);

        //holds 3 stackpanes that each display a deal. reuses the same pane
        HBox dealsBox = new HBox(6, deal1Box, deal2Box, deal3Box);

        //contains 3 panes: header, banner and deals
        VBox container = new VBox(8, headerBox, bannerBox, dealsBox);
        //background color
        container.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#fcfcfc"), CornerRadii.EMPTY, Insets.EMPTY)));

        //allows for page scrolling
        ScrollPane scroll = new ScrollPane(container);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-font-size: 15;");
        Scene homeScene = new Scene(scroll, 817, 600);

        return homeScene;
    }

    /**
     * Creates a VBox that contains the image and title of a product.
     *
     * @param fileName the filename of the product's image
     * @param title the title of a product
     * @return VBox containing image and title of a product
     */
    private static VBox createDealBox(Product p) {

        ImageView dealImage = new ImageView(p.getImageURL());
        dealImage.setFitWidth(237);
        dealImage.preserveRatioProperty().set(true);

        Text dealTitle = new Text(p.getName());
        dealTitle.setWrappingWidth(210);

        Text dealPrice = new Text(p.getPriceAsString());
        StackPane priceBox = new StackPane(dealPrice);
        priceBox.setAlignment(Pos.BOTTOM_RIGHT);

        VBox textBox = new VBox(10, dealTitle, priceBox);
        textBox.setPadding(new Insets(10));
        textBox.setStyle("-fx-font-size: 15");

        VBox dealBox = new VBox(10, dealImage, textBox);
        VBox.setVgrow(dealTitle, Priority.ALWAYS);
        VBox.setVgrow(priceBox, Priority.ALWAYS);

        dealBox.setCursor(Cursor.HAND);
        dealBox.setOnMouseClicked(e -> {
            currentStage.setScene(getListing(p));
        });
        dealBox.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-insets: 5;"
                + "-fx-border-radius: 0;"
                + "-fx-border-color: #898989;");
        return dealBox;
    }

    /**
     * Creates a product listing page to show product information including
     * price, shipping, image as well as allows the user to add to cart or buy
     * now.
     *
     * @param p the product to be displayed
     * @return the completed product listing scene
     */
    public static Scene getListing(Product p) {

        /*This is a wrapper container for the content VBox. Allows for
        scrolling both horizontally and vertically when the content does not 
        fit on the screen*/
        ScrollPane container = new ScrollPane();

        //header at top of screen
        VBox header = createHeader();

        //Listing HBox containing the image and listing details
        //Stackpane containing an imageview
        StackPane imagePane = createListingImage(p.getImageURL());
        imagePane.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-insets: 5;"
                + "-fx-border-color: #898989;");
        VBox details = createDetailsPane(p.getName(), p.getPriceAsString(), p);

        HBox listing = new HBox(10, imagePane, details);

        //product description, flowpane wraps text better
        FlowPane descriptionPane = new FlowPane();
        Text desc = new Text(p.getDescription());
        desc.setWrappingWidth(760);

        descriptionPane.getChildren().add(desc);

        /*child of container, this pane holds 3 sections: header, listing and
        description*/
        VBox content = new VBox(10, header, listing, descriptionPane);
        container.setContent(content);
        container.setStyle("-fx-font-size: 15px;");
        content.setPadding(new Insets(20));
        Scene listingScene = new Scene(container, 817, 600);

        return listingScene;
    }

    /**
     * Creates a pane containing a listing's image.
     *
     * @return the StackPane for the product listing
     */
    private static StackPane createListingImage(String url) {

        StackPane imagePane = new StackPane();
        ImageView photo = new ImageView(url);
        imagePane.getChildren().add(photo);

        //image sizing settings
        photo.setFitHeight(350);
        photo.setPreserveRatio(true);

        return imagePane;
    }

    /**
     * Creates the details pane for the product listing.
     *
     * @return returns the created VBox
     */
    private static VBox createDetailsPane(String name, String priceValue, Product p) {

        //the title for the listing
        Text title = new Text(name);
        title.setWrappingWidth(340);

        //the price of the item
        HBox priceBox = new HBox(10);
        Label priceLabel = new Label("Our Price:");
        Label price = new Label(priceValue);
        priceBox.getChildren().addAll(priceLabel, price);

        //shipping costs of the item
        Label shipLabel = new Label("Shipping: ");
        Label shipPrice = new Label("$7.80");
        HBox shipBox = new HBox(10, shipLabel, shipPrice);

        //purchase buttons
        Button buyItNow = new Button("Buy It Now");
        buyItNow.setOnAction(e -> {
            Store.currentAccount.purchase(p);
        });

        //add item to cart
        Button addToCart = new Button("Add to cart");
        addToCart.setOnAction(e -> {
            Store.currentAccount.addToCart(p);
        });
        HBox buyButtonBox = new HBox(10, buyItNow, addToCart);
        //a little more vertical space for the buttons
        buyButtonBox.setPadding(new Insets(10, 0, 0, 0));

        //add buyItNow and addToCart to buyButtonBox so that they are nicely
        //laid out beside each other
        buyButtonBox.setStyle("-fx-font-size: 15px; font-weight: bold;");

        //contains title, price and buy buttons, laid out vertically
        VBox details = new VBox(20, title, priceBox, shipBox, buyButtonBox);
        details.setPadding(new Insets(10));
        details.setStyle("-fx-font-size: 20");

        return details;
    }

    /**
     * Creates the login page using a parent VBox to separate header and
     * content, an HBox to separate the login and signup panes and various
     * fields in both.
     *
     * @return completed login page scene
     */
    public static Scene getLoginPage() {

        //page header without the search bar
        //header of the page
        //logo
        ImageView logo = new ImageView("images/logo small.png");
        logo.setFitWidth(150);
        logo.preserveRatioProperty().set(true);

        StackPane logoWrapper = new StackPane(logo);
        logoWrapper.setAlignment(Pos.CENTER_LEFT);

        //box to hold header items
        HBox.setHgrow(logoWrapper, Priority.ALWAYS);

        VBox loginBox = createSignInPane();

        /*VBox with all of the fields to create the sign up pane on the right 
        side of the screen*/
        VBox signupBox = createSignUpPane();

        /*root pane, contains two side-by-side columns for logging in (loginBox) and 
        signing up (signupBox)*/
        HBox content = new HBox(10, loginBox, signupBox);

        content.setPadding(new Insets(0, 0, 0, 20));
        content.setStyle("-fx-font-size: 15px;");

        //helps lay out header + content
        VBox container = new VBox(10, logoWrapper, content);
        //background color
        container.setBackground(new Background(new BackgroundFill(
                Color.valueOf("#fcfcfc"), CornerRadii.EMPTY, Insets.EMPTY)));

        logoWrapper.setPadding(new Insets(0, 25, 0, 10));
        Scene loginScene = new Scene(container, 655, 450);

        return loginScene;
    }

    /**
     * Creates an HBox containing a header for the page that has nodes for the
     * logo, settings etc
     *
     * @return a completed HBox containing the header of the page
     */
    private static VBox createHeader() {

        //logo
        ImageView logo = new ImageView("images/logo small.png");
        logo.setFitWidth(200);
        logo.preserveRatioProperty().set(true);

        StackPane logoWrapper = new StackPane(logo);
        logoWrapper.setAlignment(Pos.TOP_LEFT);
        logoWrapper.setCursor(Cursor.HAND);
        logoWrapper.setOnMouseClicked(e -> {
            currentStage.setScene(getHomeScreen());
        });

        //user details pane:
        Account user = Store.currentAccount;
        Text username = new Text("Welcome, " + user.getUsername());
        Text balance = new Text("Balance: " + user.getBalanceAsString());

        //add credit and signout buttons as well as their handlers
        Button addCredit = new Button("Add Credit");
        addCredit.setOnAction(e -> {

            //only adds credit if input was detected
            TextInputDialog addCreditDialog = new TextInputDialog();
            addCreditDialog.setTitle("Add (totally real) credit");
            addCreditDialog.setHeaderText("How much credit? (enter decimal value)");

            Optional<String> result = addCreditDialog.showAndWait();

            //checks whether or not input was given
            result.ifPresent(input -> {
                try {
                    double amount = Double.parseDouble(input);
                    if (amount > 10000) {
                        //throw a generic exception to not interfere with the negativeValue alert
                        throw new Exception("Amount too great");
                    }
                    Store.currentAccount.addCredit(amount);
                    currentStage.setScene(getHomeScreen());
                    Store.saveAccounts();
                } catch (NumberFormatException ex) {
                    //not a double error
                    Alert notADouble = new Alert(Alert.AlertType.ERROR);
                    notADouble.setTitle("Error");
                    notADouble.setHeaderText("Credit not added");
                    notADouble.setContentText("Amount must be a positive double value (eg: 10.00)");
                    notADouble.show();
                } catch (IllegalArgumentException ex) {
                    //negative value error
                    Alert negativeValue = new Alert(Alert.AlertType.ERROR);
                    negativeValue.setTitle("Error");
                    negativeValue.setHeaderText("Credit not added");
                    negativeValue.setContentText("Amount must be a positive value!");
                    negativeValue.show();
                } catch (Exception ex) {
                    //amount too great error
                    Alert tooMuch = new Alert(Alert.AlertType.ERROR);
                    tooMuch.setTitle("Error");
                    tooMuch.setHeaderText("Credit not added: Amount too great!");
                    tooMuch.setContentText("For security reasons, you may "
                            + "not add more than $9999.99 to your balance at once. "
                            + "Contact support for further assistance.");
                    tooMuch.show();
                }
            });
        });

        Button signOut = new Button("Sign out");
        signOut.setOnAction(e -> {
            //clear current working account, save accounts and go to login screen
            Store.currentAccount = null;
            Store.saveAccounts();
            currentStage.setScene(getLoginPage());
        });

        Button orders = new Button("My orders");
        orders.setOnAction(e -> {
            currentStage.setScene(getOrderView(Store.currentAccount.getOrders()));
        });

        //button container for horizontal layout
        HBox accountButtons = new HBox(10, addCredit, orders, signOut);
        accountButtons.setPadding(new Insets(5, 0, 0, 0));

        VBox userDetails = new VBox(5, username, balance, accountButtons);
        userDetails.setAlignment(Pos.CENTER_RIGHT);

        //search
        TextField searchField = new TextField();
        searchField.setPromptText("Search for something");
        searchField.setPrefWidth(580);
        searchField.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                //search query
                String query = searchField.getText();
                ArrayList<Product> results = Store.searchQuery(query);

                currentStage.setScene(getSearchQuery(query, results));
            }
        });

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            //search query
            String query = searchField.getText();
            ArrayList<Product> results = Store.searchQuery(query);

            currentStage.setScene(getSearchQuery(query, results));
        });

        Button viewCartButton = new Button("View Cart");
        viewCartButton.setOnAction(e -> {
            currentStage.setScene(getCartView(Store.currentAccount.getCart()));
        });

        HBox searchBox = new HBox(10, searchField, searchButton, viewCartButton);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10, 0, 10, 0));

        //box to hold header items
        HBox.setHgrow(logoWrapper, Priority.ALWAYS);
        HBox logoBox = new HBox(logoWrapper, userDetails);
        VBox headerBox = new VBox(logoBox, searchBox);
        headerBox.setPadding(new Insets(0, 5, 5, 5));
        return headerBox;
    }

    /**
     * Creates a Sign in pane consisting of a VBox with login fields
     *
     * @return loginBox, a completed VBox to be added to a scene.
     */
    private static VBox createSignInPane() {

        //sign in fields
        Label hasAccount = new Label("Have an account?");
        hasAccount.setStyle("-fx-font-size: 20;");

        //username entry
        Label usernameLabel = new Label("Username");
        TextField usernameField = new TextField();

        //contains usernameLabel and usernameField
        HBox usernameBox = new HBox(10, usernameLabel, usernameField);
        usernameBox.setAlignment(Pos.CENTER_RIGHT);

        //password entry
        Label passwordLabel = new Label("Password");
        PasswordField passwordField = new PasswordField();
        //contains passwordLabel and passwordField
        HBox passwordBox = new HBox(10, passwordLabel, passwordField);
        passwordBox.setAlignment(Pos.CENTER_RIGHT);

        usernameField.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                //this is the command to log in
                if (Store.login(username, password)) {

                    //Successful login, go to home screen
                    currentStage.setScene(getHomeScreen());
                    currentStage.show();

                } else {
                    //Failed login
                    Alert wrongLogin = new Alert(Alert.AlertType.ERROR);
                    wrongLogin.setTitle("Sign in error");
                    wrongLogin.setHeaderText("Incorrect Username/Password");
                    wrongLogin.setContentText("Please try again");
                    wrongLogin.show();
                }
            }
        });
        
        passwordField.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                String username = usernameField.getText();
                String password = passwordField.getText();

                //this is the command to log in
                if (Store.login(username, password)) {

                    //Successful login, go to home screen
                    currentStage.setScene(getHomeScreen());
                    currentStage.show();

                } else {
                    //Failed login
                    Alert wrongLogin = new Alert(Alert.AlertType.ERROR);
                    wrongLogin.setTitle("Sign in error");
                    wrongLogin.setHeaderText("Incorrect Username/Password");
                    wrongLogin.setContentText("Please try again");
                    wrongLogin.show();
                }
            }
        });
        
        //login button
        Button signInButton = new Button("Sign in");
        signInButton.setOnAction(e -> {

            String username = usernameField.getText();
            String password = passwordField.getText();

            //this is the command to log in
            if (Store.login(username, password)) {

                //Successful login, go to home screen
                currentStage.setScene(getHomeScreen());
                currentStage.show();

            } else {
                //Failed login
                Alert wrongLogin = new Alert(Alert.AlertType.ERROR);
                wrongLogin.setTitle("Sign in error");
                wrongLogin.setHeaderText("Incorrect Username/Password");
                wrongLogin.setContentText("Please try again");
                wrongLogin.show();
            }
        });

        //contains the fields needed to sign into account
        VBox loginBox = new VBox(10, hasAccount, usernameBox, passwordBox, signInButton);

        return loginBox;
    }

    /**
     * Creates a sign up pane consisting of a VBox with account creation fields.
     * Upon successful account creation, all accounts are saved and rewritten to
     * the file.
     *
     * @return signupBox a completed VBox to be placed into a scene.
     */
    private static VBox createSignUpPane() {

        //create new account fields
        Label createLabel = new Label("Create an account:");
        createLabel.setStyle("-fx-font-size: 20");

        //username
        Label newUsernameLabel = new Label("Username");
        TextField newUsernameField = new TextField();
        //contains newUsernameLabel and newUsernameField
        HBox newUsernameBox = new HBox(10, newUsernameLabel, newUsernameField);
        newUsernameBox.setAlignment(Pos.CENTER_RIGHT);

        //password
        Label newPasswordLabel = new Label("Password");
        PasswordField newPasswordField = new PasswordField();
        //contains newPasswordLabel and newPasswordField
        HBox newPasswordBox = new HBox(10, newPasswordLabel, newPasswordField);
        newPasswordBox.setAlignment(Pos.CENTER_RIGHT);
        //field to verify the password
        Label verifyPasswordLabel = new Label("Verify Password");
        PasswordField verifyPasswordField = new PasswordField();
        //contains verifyPasswordLabel and verifyPasswordField
        HBox verifyPasswordBox = new HBox(10, verifyPasswordLabel, verifyPasswordField);
        verifyPasswordBox.setAlignment(Pos.CENTER_RIGHT);

        //Checkbox for agreeing to the nonexistent T&C
        CheckBox agreeToTerms = new CheckBox("I agree to the Terms & Conditions");
        agreeToTerms.setStyle("-fx-font-size: 12;");

        //create account button
        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(e -> {
            String username = newUsernameField.getText();
            String password = newPasswordField.getText();

            //check to make sure the passwords match
            if (agreeToTerms.isSelected()) {
                if (verifyPasswordField.getText().equals(password)) {

                    //attempt to create the account
                    try {
                        Store.createAccount(username, password);

                        //alert user that account was successfully created
                        Alert createSuccess = new Alert(Alert.AlertType.INFORMATION);
                        createSuccess.setTitle("Success");
                        createSuccess.setHeaderText("Account created");
                        createSuccess.setContentText("You may now sign in to this account");
                        createSuccess.show();
                    } catch (IllegalArgumentException ex) {
                        //alerts the user that the username is taken
                        Alert inUseAlert = new Alert(Alert.AlertType.ERROR);
                        inUseAlert.setTitle("Signup error");
                        inUseAlert.setHeaderText("Username taken");
                        inUseAlert.setContentText("Please choose another and try again.");
                        inUseAlert.show();
                    }

                } else {

                    //alerts user that their entered passwords do not match
                    Alert passwordMatchAlert = new Alert(Alert.AlertType.ERROR);
                    passwordMatchAlert.setTitle("Signup error");
                    passwordMatchAlert.setHeaderText("Passwords do not match");
                    passwordMatchAlert.setContentText("Make sure that both passwords "
                            + "match. Check if your pesky old caps lock is on!");
                    passwordMatchAlert.show();
                }
            } else {

                //alerts user that their entered passwords do not match
                Alert acceptTerms = new Alert(Alert.AlertType.ERROR);
                acceptTerms.setTitle("Signup error");
                acceptTerms.setHeaderText("You must accept the Terms and Conditions");
                acceptTerms.setContentText("Please check the box to agree "
                        + "to our Terms and Conditions");
                acceptTerms.show();
            }
        });

        /*contains all of the fields needed to sign up for a new account, layed 
        out vertically*/
        VBox signupBox = new VBox(10, createLabel, newUsernameBox, newPasswordBox, verifyPasswordBox, agreeToTerms, createAccountButton);
        return signupBox;
    }

    /**
     * Displays the search results from a passed array list of products.
     *
     * @param results the results to be displayed on the search query page
     * @return
     */
    public static Scene getSearchQuery(String query, ArrayList<Product> results) {

        VBox header = createHeader();
        
        //shows what the user searched for
        Text queryValue = new Text("Results for \"" + query + "\"");
        StackPane searchQuery = new StackPane(queryValue);
        searchQuery.setAlignment(Pos.CENTER_LEFT);
        searchQuery.setPadding(new Insets(2));
        
        //Arraylist filled with completed thumbnails, which are VBoxes
        ArrayList<VBox> thumbs = new ArrayList<>();
        results.forEach((p) -> {
            thumbs.add(createThumbnail(p));
        });

        //displays the thumbnails in a wrapping horizontal flowpane
        FlowPane resultsPane = new FlowPane(10, 10);
        resultsPane.getChildren().addAll(thumbs);

        VBox content = new VBox(header, searchQuery, resultsPane);
        content.setPadding(new Insets(20));
        ScrollPane container = new ScrollPane(content);
        container.setStyle("-fx-font-size: 15;");
        Scene searchQueryScene = new Scene(container, 817, 600);
        return searchQueryScene;
    }

    /**
     * Creates a small thumbnail to display a product's basic details
     *
     * @param p the product to be turned into a thumbnail
     * @return the completed thumbnail box
     */
    private static VBox createThumbnail(Product p) {

        //product thumbnail image
        ImageView img = new ImageView(p.getImageURL());
        img.setFitWidth(176);
        img.preserveRatioProperty().set(true);
        StackPane image = new StackPane(img);
        image.setAlignment(Pos.CENTER);

        //trim the na e to better fit the thumbnail
        String trimName = p.getName();
        if (trimName.length() > 60) {
            trimName = trimName.substring(0, 60) + "...";
        }

        //thumbnail title
        Text title = new Text(trimName);
        title.setWrappingWidth(162);
        //thumbnail price
        Text price = new Text(p.getPriceAsString());
        //vbox to hold the title + price. For layout only.
        VBox textBox = new VBox(title, price);
        textBox.setPadding(new Insets(5));
        //VBox to hold image and text
        VBox thumbnail = new VBox(5, image, textBox);
        thumbnail.setStyle("-fx-border-style: solid inside;"
                + "-fx-border-width: 1;"
                + "-fx-border-insets: 2;"
                + "-fx-border-radius: 1;"
                + "-fx-border-color: #898989;"
                + "-fx-font-size: 13");
        thumbnail.setCursor(Cursor.HAND);
        thumbnail.setOnMouseClicked(e -> {
            //change the stage to this product's listing
            currentStage.setScene(getListing(p));
        });

        return thumbnail;
    }

    /**
     * Creates the Cart View scene using the header, an HBox to separate the
     * cart items on the left from the cart info on the right.
     *
     * @param c the cart to be displayed
     * @return the completed View Cart scene
     */
    public static Scene getCartView(Cart c) {

        VBox header = createHeader();
        //sends all cart items to be turned into boxes to be displayed
        ArrayList<HBox> cartItems = new ArrayList<>();
        c.getItems().forEach((p) -> {
            cartItems.add(createCartItem(p));
        });

        VBox cartItemsBox = new VBox(10);
        cartItemsBox.getChildren().addAll(cartItems);

        //allow for scrolling through cart items while leaving the sidebar in the same place
        ScrollPane cartItemsContainer = new ScrollPane(cartItemsBox);
        cartItemsContainer.setPadding(new Insets(10));

        /*info about the current state of the cart:*/
        //tally up the cost
        double total = 0;
        total = c.getItems().stream().map((p) -> p.getPrice()).reduce(total, (accumulator, _item) -> accumulator + _item);
        String subtotalFormat = String.format("Subtotal: $%.2f", total);
        Text subTotal = new Text(subtotalFormat);

        //shipping is just flat rate
        Text shipping = new Text("Shipping: $7.80");

        double grandTotal = total + 7.8;

        String formatTotal = String.format("Grand total: $%.2f", grandTotal);
        Text totalPrice = new Text(formatTotal);
        totalPrice.setStyle("-fx-font-size: 20;");

        //buy it now button
        Button buyItNow = new Button("Buy it Now");
        buyItNow.setStyle("-fx-font-size: 20;");
        buyItNow.setOnAction(e -> {
            //purchase everything in the current cart
            Store.currentAccount.purchase();
        });

        Text quantity = new Text("Total items: " + c.getItems().size());

        VBox cartInfo = new VBox(10, quantity, subTotal, shipping, totalPrice, buyItNow);
        cartInfo.setAlignment(Pos.BOTTOM_RIGHT);
        cartInfo.setPrefWidth(200);
        cartInfo.autosize();

        //separates left/right; cart items/cart info
        BorderPane content = new BorderPane(cartItemsContainer);
        content.setRight(cartInfo);
        content.getRight().autosize();

        VBox container = new VBox(10, header, content);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-font-size: 15;");

        Scene cartViewScene = new Scene(container, 817, 600);
        return cartViewScene;
    }

    /**
     *
     * @param p the item to be turned into a list item for the cart
     * @return the cart item HBox
     */
    private static HBox createCartItem(Product p) {

        //the item's image
        ImageView image = new ImageView(p.getImageURL());
        image.setFitWidth(100);
        image.setPreserveRatio(true);

        //trims the name to fit the screen
        String trimName = p.getName();
        if (trimName.length() > 25) {
            trimName = trimName.substring(0, 25) + "...";
        }
        Text name = new Text(trimName);
        Text price = new Text(p.getPriceAsString());
        //holds the price
        BorderPane priceBox = new BorderPane(price);
        HBox.setHgrow(priceBox, Priority.ALWAYS);
        //remove cart item button
        Button remove = new Button("Remove");
        remove.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(remove, Priority.ALWAYS);

        remove.setOnAction(e -> {
            //remove from cart and refresh the scene
            Store.currentAccount.removeFromCart(p);
            currentStage.setScene(getCartView(Store.currentAccount.getCart()));
        });

        HBox cartItemBox = new HBox(10, image, name, priceBox, remove);

        cartItemBox.setAlignment(Pos.CENTER_LEFT);

        return cartItemBox;
    }

    /**
     * Creates the scene to view an account's previous orders as a list.
     *
     * @param order The list of orders to be displayed in the view orders screen
     * @return The completed view orders scene
     */
    public static Scene getOrderView(ArrayList<Order> order) {

        //create header for the top of the page
        VBox header = createHeader();

        //lays out each order so that it is spaced nicely
        GridPane content = new GridPane();

        //count to put each orderBox into the right row in the gridpane
        int count = 0;
        for (Order o : order) {
            //add the orderBox as a new row
            content.addRow(count++, createOrderBox(o));
        }

        //allow for scrolling when there are too many orders to fit the screen
        ScrollPane contentContainer = new ScrollPane();
        contentContainer.setContent(content);
        contentContainer.setPadding(new Insets(20));

        VBox root = new VBox(10, header, contentContainer);
        root.setStyle("-fx-font-size: 15");
        root.setPadding(new Insets(20));
        Scene orderScene = new Scene(root, 817, 600);
        return orderScene;
    }

    /**
     * Creates an HBox containing the details of an order including the item
     * thumbnails, the quantity, the price and the date the purchase was made.
     *
     * @param o the order to be processed
     * @return the completed order view list item
     */
    private static GridPane createOrderBox(Order o) {

        /*use a flowpane to display the product thumbnails so that they wrap 
        around when there are too many to fit on one line*/
        FlowPane thumbnailBox = new FlowPane(5, 5);
        o.getItems().stream().map((p) -> new ImageView(p.getImageURL())).map((thumb) -> {
            //add thumbail to flowpane
            thumb.setFitWidth(60);
            return thumb;
        }).map((thumb) -> {
            thumb.setPreserveRatio(true);
            return thumb;
        }).forEachOrdered((thumb) -> {
            thumbnailBox.getChildren().add(thumb);
        });
        thumbnailBox.setMaxWidth(350);

        //order info
        Text quantity = new Text("" + o.getQuantity());
        Text price = new Text(o.getTotalAsString());
        Text date = new Text(o.getDate());

        //add nodes to the correct columns
        GridPane orderBox = new GridPane();
        orderBox.addColumn(0, thumbnailBox);
        orderBox.addColumn(1, quantity);
        orderBox.addColumn(2, price);
        orderBox.addColumn(3, date);

        GridPane.setHalignment(date, HPos.RIGHT);
        orderBox.setHgap(50);
        orderBox.setVgap(10);
        orderBox.setPadding(new Insets(10));
        
        GridPane.setHgrow(orderBox, Priority.ALWAYS);

        return orderBox;
    }
}
