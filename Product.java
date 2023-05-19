import java.io.Serializable;

/**
 * Product class
 *
 * Contains basic information about a Product in the marketplace
 *
 * @author Charlie Schmidt, lab sec 002
 * @version 11/29/2022
 */
public class Product implements Serializable {
    private String name;
    private String store;
    private String description;
    private int quantityAvailable;
    private double price;

    //constructor to initialize product object (existing product read in from file)
    public Product(String name, String store, String description, int quantityAvailable, double price)
            throws IllegalArgumentException {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("The product needs a name!");
        }
        if (store == null || store.equals("")) {
            throw new IllegalArgumentException("The product needs a store!");
        }
        if (description == null || description.equals("")) {
            throw new IllegalArgumentException("The product needs a description!");
        }
        if (price < 0) {
            throw new IllegalArgumentException("The price cannot be negative!");
        }
        if (quantityAvailable < 0) {
            throw new IllegalArgumentException("The quantity cannot be less than 0!");
        }
        this.name = name;
        this.store = store;
        this.description = description;
        this.quantityAvailable = quantityAvailable;
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getStore() {
        return store;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public String summary() {
        return String.format("%s\n\t%s\n\t$%.2f\n", getName(), getStore(), getPrice());
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass() == Product.class && ((Product) o).getString().equals(this.getString());
    }
    //Returns a string representation of the product
    public String getString() {
        return String.format("%s,%s,%s,%d,%.2f", name, store, description, quantityAvailable, price);
    };

    @Override
    public String toString() {
        return String.format("Name: %s, Store: %s, Price: %.2f", name, store, price);
    }
}