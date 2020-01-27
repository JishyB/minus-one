package project.src;

import java.util.ArrayList;

/**
 * Cart extends ProductGroup and adds the ability to add and remove items from
 * the cart as well as the ability to purchase.
 *
 * @author Josh Howson
 */
public class Cart {

    /**
     * List of items in the cary
     */
    private final ArrayList<Product> items;

    /**
     * The number of items in the cart
     */
    private int itemCount;

    /**
     * The sum of the price of the items in the cart
     */
    private double total;

    /**
     * The no-arg constructor for the cart
     */
    public Cart() {
        
        this.items = new ArrayList<>();
        this.total = 0;
    }

    /**
     * Adds given item to cart (Arraylist items inherited from ProductGroup) and
     * recalculates total
     *
     * @param p The product to be added to the cart
     */
    public void addToCart(Product p) {

        items.add(p);
        itemCount = items.size();
        total = calcTotal();
    }

    /**
     * Removes given item from cart (Arraylist items inherited from
     * ProductGroup) and recalculates total
     *
     * @param p The product to be removed from the cart
     */
    public void removeFromCart(Product p) {

        items.remove(p);
        itemCount = items.size();
        total = calcTotal();
    }

    /**
     * 
     * @return The items in the cart
     */
    public ArrayList<Product> getItems() {
        
        return items;
    }

    /**
     * 
     * @return The number of items in the cart
     */
    public int getItemCount() {
        
        return itemCount;
    }

    /**
     * 
     * @return The cart's current total price
     */
    public double getTotal() {
        
        return total;
    }

    /**
     * Recalculates the total price of the cart
     * 
     * @return the recalculated price
     */
    private double calcTotal() {
        
        double result = 0;
        for (Product p : items) {
            result += p.getPrice();
        }
        
        return result;
    }
}
