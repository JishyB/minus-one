package project.src;

import java.util.ArrayList;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;

/**
 * Account is a class that holds all data about a user. Can get username,
 * balance and add credit and controls adding to and removing from cart as well
 * as purchasing an order.
 *
 * TODO: check that username is not in use before creating the object
 *
 * @author Josh Howson
 */
public class Account {

    /**
     * Account's username
     */
    private final String username;

    /**
     * Account's password
     */
    private String password;

    /**
     * Account's current balance
     */
    private double balance;
    /**
     * Cart belonging to the Account. Contains all added items
     */
    private Cart cart;
    /**
     * Contains a list of all orders placed by the Account
     */
    private final ArrayList<Order> orders;

    /**
     * Constructor for Account. Default value for balance is zero.
     *
     * @param username The value to set the username
     * @param password The value to set the password
     */
    public Account(String username, String password) {

        this.cart = new Cart();
        this.username = username;
        this.password = password;
        balance = 0;
        orders = new ArrayList<>();
    }

    /**
     * Constructor that loads existing accounts from the file. Balance is
     * included in case the user has added credit to their account.
     *
     * @param username The account's username
     * @param password The account's password
     * @param balance The account's balance
     */
    public Account(String username, String password, double balance) {

        this.cart = new Cart();
        this.username = username;
        this.password = password;
        this.balance = balance;
        orders = new ArrayList<>();
    }

    /**
     *
     * @return the account's username
     */
    public String getUsername() {

        return username;
    }

    /**
     *
     * @return the account's cart
     */
    public Cart getCart() {

        return cart;
    }

    /**
     *
     * @return the account's orders
     */
    public ArrayList<Order> getOrders() {

        return orders;
    }

    /**
     *
     * @return the account's password
     */
    public String getPassword() {

        return password;
    }

    /**
     *
     * @return the account's current balance
     */
    public double getBalance() {

        return balance;
    }

    /**
     * Formats the account's balance as a string like: "$99.99"
     *
     * @return the formatted balance string
     */
    public String getBalanceAsString() {

        return String.format("$%.2f", balance);
    }

    /**
     * Method to add credit to the account's balance. Only adds credit if the
     * given amount is a positive value.
     *
     * @param amount The amount of credits to add to the balance
     * @throws IllegalArgumentException if the given amount is a negative value
     */
    public void addCredit(double amount) throws IllegalArgumentException {

        if (amount >= 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException("Amount must be a positive value");
        }
    }

    /**
     * Method to set the account's password. Checks that their old password is
     * correct and that it is not the same as the previous password before
     * setting.
     *
     * If I get to the setting screen, then I will use this, otherwise it may
     * not be used
     *
     * @param oldPass Should be the correct old (current) password
     * @param newPass Value to set the new password
     * @throws IllegalArgumentException if the given newPass is the same as the
     * old password
     * @throws IllegalArgumentException if the given oldPass does not match the
     * current password
     */
    public void setPassword(String oldPass, String newPass) {

        if (oldPass.equals(this.password)) {
            if (!oldPass.equals(newPass)) {
                this.password = newPass;
            } else {
                throw new IllegalArgumentException("New password cannot be the same as old password!");
            }
        } else {
            throw new IllegalArgumentException("Given password does not match current password!");
        }
    }

    /**
     * Adds the given item to the account's cart.
     *
     * @param p The item to be added to the cart
     */
    public void addToCart(Product p) {
        
        cart.addToCart(p);
    }

    /**
     * Removes the given item from the cart
     *
     * @param p The item to be added to the cart
     */
    public void removeFromCart(Product p) {
        
        cart.removeFromCart(p);
    }

    /**
     * Formats a string to be written to a CSV file
     *
     * @return the formatted line to be added to the CSV file
     */
    public String toCSV() {
        
        String format = "%s,%s,%.2f";
        return String.format(format, username, password, balance);
    }

    /**
     * If account has enough credits, create new order and subtract credits.
     * User must re-enter password
     *
     * wow, this method got bigger than I expected. If I have time, I'll
     * consolidate the alerts into a more compact method
     */
    public void purchase() {

        //get quantity and total
        int quantity = cart.getItemCount();

        //add shipping to total ($7.80)
        double amount = cart.getTotal() + 7.8;

        //enter password alert
        Alert confirmPassword = new Alert(Alert.AlertType.INFORMATION);
        confirmPassword.setHeaderText("Enter password to continue");
        PasswordField passwordField = new PasswordField();
        confirmPassword.getDialogPane().setContent(passwordField);
        confirmPassword.getDialogPane().setPadding(new Insets(10));

        if (balance >= amount) {
            //confirm correct password before purchasing
            Optional<ButtonType> result = confirmPassword.showAndWait();
            result.ifPresent(e -> {
                if (result.get() == ButtonType.OK) {
                    if (passwordField.getText().equals(Store.currentAccount.getPassword())) {
                        //attempt topurchase

                        orders.add(new Order(cart.getItems(), amount, quantity));
                        balance -= amount;

                        //save to file
                        Store.saveAccounts();

                        Alert purchaseSuccess = new Alert(Alert.AlertType.INFORMATION);
                        purchaseSuccess.setTitle("Success");
                        purchaseSuccess.setHeaderText("Your purchase was successful");
                        purchaseSuccess.setContentText("Your order is on the way!");
                        purchaseSuccess.show();

                        //TODO: if I get view orders screen working, redirect to that screen instead
                        SceneLoader.currentStage.setScene(SceneLoader.getOrderView(orders));

                        //remove items from cart when purchased
                        cart = new Cart();

                    } else {
                        //not enough credits
                        Alert wrongPass = new Alert(Alert.AlertType.ERROR);
                        wrongPass.setTitle("Error");
                        wrongPass.setHeaderText("Incorrect Password");
                        wrongPass.setContentText("Please try again");
                        wrongPass.show();

                    }
                }
            });

        } else {
            //not enough credits
            Alert notEnough = new Alert(Alert.AlertType.ERROR);
            notEnough.setTitle("Error");
            notEnough.setHeaderText("Not enough credits");
            notEnough.setContentText("Use the 'Add credit' button to increase your balance");
            notEnough.show();
        }
    }

    /**
     * Overloads the purchase method, passing only one product instead of an
     * entire cart of products.
     *
     * If account has enough credits, create new order and subtract credits.
     * User must re-enter password.
     *
     * @param p
     */
    public void purchase(Product p) {

        double amount = p.getPrice();
        int quantity = 1;
        Cart tempCart = new Cart();
        tempCart.addToCart(p);

        //enter password alert
        Alert confirmPassword = new Alert(Alert.AlertType.INFORMATION);
        confirmPassword.setHeaderText("Enter password to continue");
        PasswordField passwordField = new PasswordField();
        confirmPassword.getDialogPane().setContent(passwordField);
        confirmPassword.getDialogPane().setPadding(new Insets(10));

        if (balance >= amount) {
            //confirm correct password before purchasing
            Optional<ButtonType> result = confirmPassword.showAndWait();
            result.ifPresent(e -> {
                if (result.get() == ButtonType.OK) {
                    if (passwordField.getText().equals(Store.currentAccount.getPassword())) {
                        //attempt topurchase

                        orders.add(new Order(tempCart.getItems(), amount, quantity));
                        balance -= amount;

                        //save to file
                        Store.saveAccounts();

                        Alert purchaseSuccess = new Alert(Alert.AlertType.INFORMATION);
                        purchaseSuccess.setTitle("Success");
                        purchaseSuccess.setHeaderText("Your purchase was successful");
                        purchaseSuccess.setContentText("Your order is on the way!");
                        purchaseSuccess.show();

                        //TODO: if I get view orders screen working, redirect to that screen instead
                        SceneLoader.currentStage.setScene(SceneLoader.getOrderView(orders));

                    } else {
                        //not enough credits
                        Alert wrongPass = new Alert(Alert.AlertType.ERROR);
                        wrongPass.setTitle("Error");
                        wrongPass.setHeaderText("Incorrect Password");
                        wrongPass.setContentText("Please try again");
                        wrongPass.show();

                    }
                }
            });

        } else {
            //not enough credits
            Alert notEnough = new Alert(Alert.AlertType.ERROR);
            notEnough.setTitle("Error");
            notEnough.setHeaderText("Not enough credits");
            notEnough.setContentText("Use the 'Add credit' button to increase your balance");
            notEnough.show();
        }
    }
}