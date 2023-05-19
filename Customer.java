import java.util.ArrayList;
import java.io.*;

/**
 * Customer class
 *
 * Contains basic information about a customer in the marketplace, with methods to access and edit that information
 *
 * @author Charlie Schmidt, lab sec 002
 * @version 11/29/2022
 */
public class Customer extends User {
    private ArrayList<Product> products;
    private ArrayList<Product> cartProducts;

    // constructor to create new Customer object (no longer handles file i/o)
    public Customer(String username, String password, ArrayList<Product> products,
                    ArrayList<Product> cartProducts) {

        super(username, password);

        this.products = products;
        this.cartProducts = cartProducts;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setCartProducts(ArrayList<Product> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public ArrayList<Product> getCartProducts() {
        return cartProducts;
    }

    public void purchaseProduct(Product product) {
        products.add(product);
        cartProducts.removeIf(product::equals);
    } 

    public void purchaseAll() {
        products.addAll(cartProducts);

        cartProducts = new ArrayList<>();
    }

    public void addProductToCart(Product product) {
        cartProducts.add(product);
    }

    public void removeProductFromCart(String name, String store) throws IllegalArgumentException {
        for (int i = 0; i < cartProducts.size(); i++) {
            if (cartProducts.get(i).getName().equals(name) && cartProducts.get(i).getStore().equals(store)) {
                cartProducts.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("That product is not in your cart!");
    }

    public File exportPurchases() {
        File exportedPurchases = new File(this.getUsername() + "_purchases.txt");
        try (FileWriter fw = new FileWriter(exportedPurchases);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (Product product : products) {
                bw.write(product.getString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return exportedPurchases;
    }

    @Override
    public String toString() {
        String[] prodNameArray = new String[products.size()];
        for (int i = 0; i < prodNameArray.length; i++) {
            prodNameArray[i] = products.get(i).getName();
        }
        String[] cartNameArray = new String[cartProducts.size()];
        for (int i = 0; i < cartNameArray.length; i++) {
            cartNameArray[i] = cartProducts.get(i).getName();
        }

        String prodString = String.join(",", prodNameArray);

        String cartString = String.join(",", cartNameArray);

        if (prodString.equals("")) {
            prodString = " ";
        }

        if (cartString.equals("")) {
            cartString = " ";
        }
        

        //For the file reading to work, empty arrays need to be written as spaces instead of blanks
        // (i.e "username;password; ; " instead of "username;password;;")
        return String.format("%s;%s;%s", super.toString(), prodString,
                cartString);
    }
}
