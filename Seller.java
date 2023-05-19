import java.util.ArrayList;
import java.io.*;
import java.util.Arrays;

/**
 * Seller class
 *
 * This class contains the stores and products that a Seller owns, along with methods to add, edit, and delete
 * products. The Seller can also import or export files containing product information.
 *
 * @author Charlie Schmidt, lab sec 002
 * @version 11/29/2022
 */
public class Seller extends User {
    private ArrayList<String> storeNames;
    private ArrayList<Product> products;

    // constructor to create new instance of a Seller (no longer handles file i/o)
    public Seller(String username, String password, ArrayList<String> storeNames,
                  ArrayList<Product> products) {
        super(username, password);
        this.storeNames = storeNames;
        this.products = products;
    }

    public void setStoreNames(ArrayList<String> storeNames) {
        this.storeNames = storeNames;
    }

    public ArrayList<String> getStoreNames() {
        return storeNames;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void addStore(String storeName) throws IllegalArgumentException {
        if (storeNames.contains(storeName)) {
            throw new IllegalArgumentException("That store already exists!");
        }
        storeNames.add(storeName);
    }

    public Product getProduct(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) return product;
        }
        return null;
    }

    public void addProduct(Product product)
            throws IllegalArgumentException {
        if (storeNames.contains(product.getStore())) {
            products.add(product);
        } else {
            throw new IllegalArgumentException("You don't own that store!");
        }
    }

    public void editProduct(String name, Product newProduct) throws IllegalArgumentException {
        boolean storeIsOwned = false;
        for (String storeName : storeNames) {
            if (newProduct.getStore().equals(storeName)) {
                storeIsOwned = true;
                break;
            }
        }
        if (!storeIsOwned) {
            throw new IllegalArgumentException("You don't own that store!");
        }
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equals(name)) {
                products.remove(i);
                products.add(newProduct);
                return;
            }
        }
        throw new IllegalArgumentException("You can't edit that product!");
    }

    public void deleteProduct(String name) throws IllegalArgumentException {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equals(name)) {
                products.remove(i);
                return;
            }
        }
        throw new IllegalArgumentException("You don't own that product!");
    }

    // Seller can import products; file i/o will be handled client-side, this method receives an ArrayList of
    // all the lines in the file
    public void importProducts(ArrayList<String> lines) throws IllegalArgumentException {
        try {
            String[][] splitLines = new String[lines.size()][5];
            for (int i = 0; i < splitLines.length; i++) {
                splitLines[i] = lines.get(i).split(",");
            }
            Product[] newProducts = new Product[splitLines.length];

            for (int i = 0; i < newProducts.length; i++) {
                String[] splitLine = splitLines[i];
                newProducts[i] = new Product(splitLine[0], splitLine[1], splitLine[2],
                        Integer.parseInt(splitLine[3]), Double.parseDouble(splitLine[4]));
            }
            products.addAll(Arrays.asList(newProducts));
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IllegalArgumentException("The given file is not in the right format!");
        }
    }

    public File exportProducts() {
        File exportedProducts = new File(this.getUsername() + "_products.txt");
        try (FileWriter fw = new FileWriter(exportedProducts);
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (Product product : products) {
                bw.write(product.getString());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return exportedProducts;
    }

    @Override
    public String toString() {
        String storeNameString = String.join(",", storeNames);
        return String.format("%s;%s", super.toString(), storeNameString);
    }
}
