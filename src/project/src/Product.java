package project.src;

/**
 * Class to hold info on an individual product.
 *
 * @author Josh Howson
 */
public class Product {

    /**
     * The name of the product
     */
    private String name;
    /**
     * The price of the product
     */
    private double price;
    /**
     * The URL where the product's image can be found
     */
    private final String imageURL;
    /**
     * The description of the item
     */
    private String description;

    /**
     * Basic constructor for the product
     *
     * @param name the name of the product
     * @param price the price of the product
     * @param imageURL the filename of the product's photo stored in must be in
     * format: "filename.filetype", eg: image1.png.
     * @param description the item's description
     */
    public Product(String name, double price, String imageURL, String description) {
        
        this.name = name;
        this.price = price;
        //append directory URL to the filename
        this.imageURL = "images/" + imageURL;
        this.description = description;
    }

    /**
     *
     * @return the name of the product
     */
    public String getName() {
        
        return name;
    }

    /**
     *
     * @param name value to set the product's name
     */
    public void setName(String name) {
        
        this.name = name;
    }

    /**
     *
     * @return the price of the product
     */
    public double getPrice() {
        
        return price;
    }

    /**
     * Returns the formatted price as a string
     *
     * @return the formatted price string
     */
    public String getPriceAsString() {
        
        return String.format("$%.2f", price);
    }

    /**
     *
     * @return the relative URL of the product's image
     */
    public String getImageURL() {
        
        return imageURL;
    }

    /**
     *
     * @param price value to set the product's price
     */
    public void setPrice(double price) {
        
        this.price = price;
    }

    /**
     *
     * @return the product's description
     */
    public String getDescription() {
        
        return description;
    }

    /**
     *
     * @param description value to set the product's description
     */
    public void setDescription(String description) {
        
        this.description = description;
    }

    /**
     * Creates a comma separated line to be written to a CSV file
     *
     * @return a string containing all necessary data, separated by commas
     */
    public String toCSV() {

        String format = "%s,%.2f,%s,%s";
        return String.format(format, name, price, imageURL, description);
    }
}
