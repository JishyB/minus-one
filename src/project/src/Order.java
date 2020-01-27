package project.src;

import java.util.ArrayList;
import java.util.Date;

/**
 * Immutable class containing items purchased, total, the date when the order
 * was made.
 *
 * @author Josh Howson
 */
public final class Order {

    private final ArrayList<Product> items;

    /**
     * The total cost of the order
     */
    private final double total;

    /**
     * The date the order was placed on
     */
    private final Date date;

    /**
     * The count of the items purchased
     */
    private final int quantity;

    /**
     * Constructor for order.
     *
     * Sets the date value to the time at which it was instantiated.
     *
     * @param items the items purchased in the order.
     * @param total the total cost of the order
     * @param quantity the number of items purchased
     */
    public Order(ArrayList items, double total, int quantity) {

        this.items = items;
        this.total = total;
        //no-arg constructor represents the time at which it was instantiated
        this.date = new Date();
        this.quantity = quantity;
    }

    /**
     * Returns a clone of the the list of items to keep the class immutable
     *
     * @return a clone of the order's items
     */
    public ArrayList<Product> getItems() {
        
        //clone getItems
        return (ArrayList<Product>) items.clone();
    }

    /**
     *
     * @return the total price of the order
     */
    public double getTotal() {
        
        return total;
    }

    /**
     * Formatted string representation of the total price
     *
     * @return the formatted price
     */
    public String getTotalAsString() {
        
        return String.format("$%.2f", total);
    }

    /**
     *
     * @return the number of items in the order
     */
    public int getQuantity() {
        
        return quantity;
    }

    /**
     * Formatted date string
     *
     * @return the formatted date string
     */
    public String getDate() {
        
        String line = date.toGMTString();
        String data[] = line.split(" ");
        return data[0] + "/" + data[1] + "/" + data[2];
    }
}