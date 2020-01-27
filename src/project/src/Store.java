package project.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.scene.control.Alert;

/**
 * Class containing methods relating to running the store. Only one user may be
 * logged into the store at any time (currentAccount). Because this class will
 * not be instantiated, all of its fields and methods are static.
 *
 * @author Josh Howson
 */
public class Store {

    /**
     * ArrayList containing all products in the store. The index of each product
     * in the array is equal to the product's id.
     */
    public static ArrayList<Product> productDirectory = new ArrayList<>();

    /**
     * ArrayList containing all accounts in the store.
     */
    public static ArrayList<Account> accountDirectory = new ArrayList<>();

    /**
     * Variable that contains the current user's account. null when logged out.
     * setCurrentAccount() assigns this value when there is a successful login.
     */
    public static Account currentAccount = null;

    /**
     * Method to create new product. Adds product to the end of the
     * accountDirectory ArrayList.
     *
     * @param name the product name
     * @param price price of the product
     * @param imageURL the filename of the image stored in /project/images/ eg:
     * "image1.png"
     * @param description the description of the item
     */
    public static void createProduct(String name, double price, String imageURL, String description) {

        productDirectory.size();
        productDirectory.add(new Product(name, price, imageURL, description));

    }

    /**
     * Method to create a new account. Adds account to the end of the
     * accountDirectory ArrayList.
     *
     * @param username The username to give the account
     * @param password The password to log in to the account with
     */
    public static void createAccount(String username, String password) throws IllegalArgumentException {

        //throws exception if there is another account in the array with the same username
        for (Account i : accountDirectory) {
            if (i.getUsername().equals(username)) {
                throw new IllegalArgumentException(
                        "That username is already in use!");
            }
        }
        //auto-assigns id based on number of accounts in array
        accountDirectory.add(new Account(username, password));

        //save to file after account is created
        saveAccounts();
    }

    /**
     * This method is similar to createAccount, except it is intended to load an
     * existing account into memory. Skips checking whether or not the username
     * is taken as it is only run at startup.
     *
     * readAccounts() sends lines from the accounts.txt file to this method to
     * add accounts to the accountDirectory array list.
     *
     * @param username The account's username
     * @param password The account's password
     * @param balance
     */
    public static void loadExistingAccount(String username, String password, double balance) {

        accountDirectory.add(new Account(username, password, balance));
    }

    /**
     * Uses a for-each loop to search through the array for the given username,
     * if found, verifies that it is the correct password. If valid, it changes
     * the current account.
     *
     * @param username
     * @param password
     * @return true if login is successful, false otherwise
     */
    public static boolean login(String username, String password) {

        //find account
        for (Account account : accountDirectory) {
            //check if username is the one being searched for
            if (username.equals(account.getUsername())) {
                //verify password
                if (password.equals(account.getPassword())) {
                    //if login credentials match, set working account to this account
                    setCurrentAccount(account);
                    //login successful, breakout of loop
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the current working account
     *
     * @param account
     */
    private static void setCurrentAccount(Account account) {

        currentAccount = account;
    }

    /**
     * This method will read the Accounts from the file and store the info into
     * the accountDirectory array by creating multiple account objects.
     *
     * ACCOUNT FILE LINES CONTAIN: (USERNAME,PASSWORD,BALANCE)
     */
    public static void readAccounts() {

        //csv file containing all records
        File accountsFile = new File("accounts.txt");
        String line;
        String[] data;
        try (Scanner input = new Scanner(accountsFile)) {
            while (input.hasNextLine()) {
                //split up the csv into array 'data'
                line = input.nextLine();
                data = line.split(",");

                //assigning these to variables for readability
                String username = data[0];
                String password = data[1];
                double balance = Double.parseDouble(data[2]);

                //adds account to directory
                loadExistingAccount(username, password, balance);

            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
            fileNotFoundAlert("Account info was not read.");
        }
    }

    /**
     * Reads all lines from the products.txt file and creates the products,
     * adding them to the productDirectory array list.
     *
     * PRODUCT FILE LINES CONTAIN: (NAME,PRICE,IMAGEURL,DESCRIPTION)
     */
    public static void readProducts() {

        //csv file containing all records
        File productsFile = new File("products.txt");
        String line;
        String[] data;
        try (Scanner input = new Scanner(productsFile)) {
            while (input.hasNextLine()) {
                //split up the csv into array 'data'
                line = input.nextLine();
                data = line.split(",");

                //assigning these to variables for readability
                String name = data[0];
                double price = Double.parseDouble(data[1]);
                String url = data[2];
                String description = data[3];

                //adds product to the directory
                createProduct(name, price, url, description);
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
            fileNotFoundAlert("Product data was not read.");
        }
    }

    /**
     * Writes the entire contents of the accountDirectory array list to the
     * file. Invokes the account object's toCSV() method to get the formatted
     * line.
     */
    public static void saveAccounts() {

        File accountsFile = new File("accounts.txt");
        try (PrintWriter writer = new PrintWriter(accountsFile)) {
            for (Account i : accountDirectory) {
                //Gets the account details arranged in a formatted CSV string
                //System.out.println(i.toCSV());
                writer.println(i.toCSV());
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex.toString());
            fileNotFoundAlert("Account data was not written.");
        }
    }

    /**
     * This alert occurs multiple times so I introduced a common method to save
     * space
     *
     * @param message is the unique error message displayed to the user
     */
    private static void fileNotFoundAlert(String message) {

        Alert fileNotFound = new Alert(Alert.AlertType.ERROR);
        fileNotFound.setTitle("Error");
        fileNotFound.setHeaderText("File not found");
        fileNotFound.setContentText(message);
        fileNotFound.show();
    }

    /**
     * This is a "smart" search result function. It searches through the
     * productDirectory for a product with a title that matches or includes the
     * query. It is case insensitive.
     *
     * Firstly, it looks to find products that begin with the query. Next, it
     * looks to find a match in the entire title and finally
     *
     * @param query the string to search for in the products
     * @return an array list containing all matching products
     */
    public static ArrayList<Product> searchQuery(String query) {

        //all comparisons done in lowercase to make the query case-insensitive
        query = query.toLowerCase();
        ArrayList<Product> results = new ArrayList();

        //first check if any items begin with the query
        for (Product p : productDirectory) {
            if (p.getName().toLowerCase().startsWith(query)) {
                results.add(p);
            }
        }

        //then look in the entire name
        for (Product p : productDirectory) {
            if (p.getName().toLowerCase().contains(query) && !results.contains(p)) {
                results.add(p);
            }
        }
        /*Next, add items that include the query in the description. Only add
        a matched product if it IS NOT already in the array!
         */
        for (Product p : productDirectory) {
            if (p.getDescription().toLowerCase().contains(query) && !results.contains(p)) {
                results.add(p);
            }
        }
        return results;
    }
}
