import java.util.*;
import java.io.*;
import java.net.*;

import java.util.Arrays;

/**
 * Server class
 *
 * This class handles input from all Clients, reads in all file data at the start and writes all file data at
 * shutdown, and processes all necessary information for the program
 *
 * @author Charlie Schmidt, Zachary DeFazio, lab sec 002
 * @version 11/29/2022
 */
public class Server implements Runnable {

    // After readFiles() is run, the sellers, customers, and products ArrayLists will hold all the necessary
    // information until the shutDown() method is run and all the new information is written to the files.
    static ArrayList<Seller> sellers;
    static ArrayList<Customer> customers;
    static ArrayList<Product> products;

    // non-static Socket to connect to each client in each thread
    Socket socket;

    // non-static User object to reference the current user
    User currentUser;

    // static object used for synchronization
    static final Object OBJ = new Object();

    public Server(Socket socket) {
        this.socket = socket;
    }

    // run method for thread
    public void run() {

        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            // open output stream to client, flush send header, then input stream
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            String input;

            // while loop repeats, waiting for input from the user to signify which action will be taken
            do {
                input = (String) ois.readObject();
                switch (input) {
                    case "LOGIN" -> login("existing", ois, oos);
                    case "NEWACCOUNT" -> login("create_new_account", ois, oos);
                    case "ADDPRODUCT" -> addProduct(ois, oos);
                    case "DELETEPRODUCT" -> deleteProduct(ois, oos);
                    case "EDITPRODUCT" -> editProduct(ois, oos);
                    case "ADDSTORE" -> addStore(ois, oos);
                    case "ADDTOCART" -> addToCart(ois, oos);
                    case "REMOVEFROMCART" -> removeFromCart(ois, oos);
                    case "PURCHASECART" -> purchaseCart(oos);
                    case "GETCARTPRODUCTS" -> getCartProducts(oos);
                    case "PURCHASEPRODUCT" -> purchaseProduct(ois, oos);
                    case "SEARCHPRODUCTS" -> searchProducts(ois, oos);
                    case "SEARCHSELLERPRODUCTS" -> searchSellerProducts(ois, oos);
                    case "GETPRODUCTS" -> getProducts(oos);
                    case "GETSELLERPRODUCTS" -> getSellerProducts(oos);
                    case "GETCUSTOMERPRODUCTS" -> getCustomerProducts(oos);
                    case "GETSELLERSTORES" -> getSellerStores(oos);
                    case "GETSALES" -> getSales(ois, oos);
                    case "GETCARTNUM" -> getCartNum(oos);
                    case "GETCUSTOMERNAMES" -> getCustomerNames(oos);
                    case "GETCUSTOMERCART" -> getCustomerCart(ois, oos);
                    case "REFRESH" -> refresh(oos);
                    case "SELLERSTATISTICS1" -> sellerStatistics1(oos,ois);
                    case "SELLERSTATISTICS2" -> sellerStatistics2(oos, ois);
                    case "SORTSELLERSTATISTICS1" -> sortSellerStatistics1(oos, ois);
                    case "SORTSELLERSTATISTICS2" -> sortSellerStatistics2(oos, ois);
                    case "CUSTOMERSTATISTICS" -> customerStatistics(oos, ois);
                    case "CUSTOMERSTATISTICS2" -> customerStatistics2(oos, ois);
                    case "SORTCUSTOMERSTATISTICS1" -> sortCustomerStatistics1(oos, ois);
                    case "SORTCUSTOMERSTATISTICS2" -> sortCustomerStatistics2(oos, ois);
                    default -> oos.writeObject(null);
                }
            } while (!input.equals("EXIT"));
        } catch (ClassNotFoundException | IOException c) {
            c.printStackTrace();
        } finally {
            try {
                if (oos != null) oos.close();
            } catch (IOException e) {
                System.out.println("Failed to close oos");
            }
            try {
                if (ois != null) ois.close();
            } catch (IOException e) {
                System.out.println("Failed to close ois");
            }
        }
    }

    // login method that takes login info as input and writes a null object to the client if the login fails
    public void login(String loginType, ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {

        String loginUsername = (String) ois.readObject();
        String loginPassword = (String) ois.readObject();

        // if the new created user already exists (or existing user doesn't exist), a null value is returned
        // and written to the client to be handled client-side with an error message
        if (loginType.equals("create_new_account")) {
            String userType = (String) ois.readObject();

            User tempUser = createNewUser(loginUsername, loginPassword, userType);

            if (tempUser != null) {
                currentUser = tempUser;
                oos.writeObject("successful");
                oos.writeObject(tempUser);
            } else {
                oos.writeObject("unsuccessful");
            }

        } else {
            User tempUser = loginUser(loginUsername, loginPassword);

            if (tempUser != null) {
                currentUser = tempUser;
                oos.writeObject("successful");
                oos.writeObject(tempUser);
            } else {
                oos.writeObject("unsuccessful");
            }
        }
    }

    public static User loginUser(String username, String password) {
        for (Seller seller : sellers) {
            if (username.equals(seller.getUsername()) && password.equals(seller.getPassword())) {
                return seller;
            }
        }


        for (Customer customer : customers) {
            if (username.equals(customer.getUsername()) && password.equals(customer.getPassword())) {
                return customer;
            }
        }
        return null;
    }

    public static synchronized User createNewUser(String username, String password, String userType) {
        for (Seller seller : sellers) {
            if (username.equals(seller.getUsername()) ) {
                return null;
            }
        }

        for (Customer customer : customers) {
            if (username.equals(customer.getUsername())) {
                return null;
            }
        }

        if (userType.equals("seller")) {
            Seller tempSeller = new Seller(username, password, new ArrayList<>(), new ArrayList<>());
            sellers.add(tempSeller);
            return tempSeller;
        } else {
            Customer tempCustomer = new Customer(username, password, new ArrayList<>(), new ArrayList<>());
            customers.add(tempCustomer);
            return tempCustomer;
        }
    }

    // method for a Seller to add a product
    public void addProduct(ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {
        String name = (String) ois.readObject();
        String store = (String) ois.readObject();
        String description = (String) ois.readObject();
        int quantityAvailable = ois.readInt();
        double price = ois.readDouble();

        try {
            Product tempProduct = new Product(name, store, description, quantityAvailable, price);

            // will throw IllegalArgumentException if the product's store is not owned by the current user
            ((Seller) currentUser).addProduct(tempProduct);
            synchronized (OBJ) {
                products.add(tempProduct);
            }
            oos.writeObject("Product was added successfully");
        } catch (IllegalArgumentException e) {
            oos.writeObject(e.getMessage());
        }
    }

    // method for a Seller to delete a product
    public void deleteProduct(ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {
        String name = (String) ois.readObject();

        // will throw IllegalArgumentException if the product doesn't exist
        try {
            synchronized (OBJ) {
                ((Seller) currentUser).deleteProduct(name);
                for (int i = 0; i < products.size(); i++) {
                    if (products.get(i).getName().equals(name)) {
                        products.remove(i);
                        break;
                    }
                }
            }
            oos.writeObject("Product was successfully removed");
        } catch (IllegalArgumentException e) {
            oos.writeObject("The product could not be removed!");
        }
    }

    // method for a Seller to edit a product
    public void editProduct(ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        String name = (String) ois.readObject();
        Product ogProduct = ((Seller) currentUser).getProduct(name);
        if (ogProduct == null) {
            ois.readObject();
            oos.writeObject("You don't own that product!");
        } else {
            try {
                synchronized (OBJ) {
                    String nname = (String) ois.readObject();
                    String sname = (String) ois.readObject();
                    String desc = (String) ois.readObject();
                    int amt = ois.readInt();
                    double cost = ois.readDouble();
                    Product mprod = new Product(nname, sname, desc, amt, cost);
                    ((Seller) currentUser).editProduct(name, mprod);
                    for (Product product : products) {
                        if (product.getName().equals(ogProduct.getName()) &&
                                product.getStore().equals(ogProduct.getStore())) {
                            products.remove(product);
                            products.add(mprod);
                            break;
                        }
                    }
                }
                oos.writeObject("The product was successfully edited");
            } catch (IllegalArgumentException iae) {
                oos.writeObject(iae.getMessage());
            }
        }
    }

    public void addStore(ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        String name = (String) ois.readObject();
        try {
            for (Product product : products) {
                if (product.getStore().equals(name)) {
                    throw new IllegalArgumentException("That store already exists!");
                }
            }
            ((Seller) currentUser).addStore(name);
            oos.writeObject("Store was added successfully");
        } catch (IllegalArgumentException iae) {
            oos.writeObject(iae.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // method for a Customer to add a product to cart
    public void addToCart(ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        String name = (String) ois.readObject();
        String store = (String) ois.readObject();

        synchronized (OBJ) {
            for (Product product : products) {
                if (product.getName().equals(name) && product.getStore().equals(store)) {
                    ((Customer) currentUser).addProductToCart(product);
                    oos.writeObject("The product was successfully added to the cart");
                    return;
                }
            }
            oos.writeObject("The product is no longer available!");
        }
    }

    // method for a Customer to remove a product from their cart
    public void removeFromCart(ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {
        String name = (String) ois.readObject();
        String store = (String) ois.readObject();

        try {
            ((Customer) currentUser).removeProductFromCart(name, store);
            oos.writeObject("The product was successfully removed from the cart");
        } catch (IllegalArgumentException e) {
            oos.writeObject(e.getMessage());
        }
    }

    // method for a Customer to purchase all the products in their cart (if they are still available)
    public void purchaseCart(ObjectOutputStream oos) throws IOException {
        ArrayList<Product> tempCart = ((Customer) currentUser).getCartProducts();
        ArrayList<Product> newCart = new ArrayList<>();
        synchronized (OBJ) {
            for (Product cartProduct : tempCart) {
                for (Product product : products) {
                    if (product.getName().equals(cartProduct.getName()) &&
                            product.getStore().equals(cartProduct.getStore()) && product.getQuantityAvailable() > 0) {
                        newCart.add(product);
                        product.setQuantityAvailable(product.getQuantityAvailable() - 1);
                    }
                }
            }
            ((Customer) currentUser).setCartProducts(newCart);
            ((Customer) currentUser).purchaseAll();
        }
        oos.writeObject("successful");
    }

    // method for a customer to get the products that are in their cart
    public void getCartProducts(ObjectOutputStream oos) throws IOException {
        ArrayList<Product> tempProducts = ((Customer) currentUser).getCartProducts();
        oos.writeObject(tempProducts.size());
        for (Product tempProduct : tempProducts) {
            oos.writeObject(tempProduct);
        }
    }

    // method for a Customer to purchase a product
    // NOTE: the product quantity is decremented in products ArrayList but not in the Seller.products ArrayList
    //      (if the seller is also logged in). The Seller will need to press refresh (and the refresh must be created)
    public void purchaseProduct(ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {
        String name = (String) ois.readObject();
        String store = (String) ois.readObject();

        synchronized (OBJ) {
            for (Product product : products) {
                if (product.getName().equals(name) && product.getStore().equals(store)) {
                    if (product.getQuantityAvailable() > 0) {
                        product.setQuantityAvailable(product.getQuantityAvailable() - 1);
                        ((Customer) currentUser).purchaseProduct(product);
                        oos.writeObject("The product was successfully bought");
                    } else {
                        oos.writeObject("The selected product is out of stock!");
                    }
                    return;
                }
            }
            oos.writeObject("The selected product does not exist!");
        }
    }

    // method for a customer to search products with a keyword (for name, store, or description)
    public void searchProducts(ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {
        String searchTerm = (String) ois.readObject();
        ArrayList<Product> searchResults = new ArrayList<>();
        synchronized (OBJ) {
            for (Product product : products) {
                if (product.getName().contains(searchTerm) || product.getStore().contains(searchTerm) ||
                        product.getDescription().contains(searchTerm)) {
                    searchResults.add(product);
                }
            }
        }
        oos.writeObject(searchResults.size());
        for (Product searchResult : searchResults) {
            oos.writeObject(searchResult);
        }
    }

    public void searchSellerProducts(ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {
        String searchTerm = (String) ois.readObject();
        ArrayList<Product> searchResults = new ArrayList<>();
        synchronized (OBJ) {
            for (Product product : ((Seller) currentUser).getProducts()) {
                if (product.getName().contains(searchTerm) || product.getStore().contains(searchTerm) ||
                        product.getDescription().contains(searchTerm)) {
                    searchResults.add(product);
                }
            }
        }
        oos.writeObject(searchResults.size());
        for (Product searchResult : searchResults) {
            oos.writeObject(searchResult);
        }
    }

    // method for a customer to get the products they have bought
    public void getProducts(ObjectOutputStream oos) throws IOException {
        synchronized (OBJ) {
            oos.flush();
            oos.reset();
            oos.writeObject(products.size());
            for (Product product : products) {
                oos.writeObject(product);
            }
        }
    }

    public void getSellerProducts(ObjectOutputStream oos) throws IOException {
        ArrayList<Product> tempProducts = ((Seller) currentUser).getProducts();
        oos.reset();
        oos.flush();
        oos.writeObject(tempProducts.size());
        for (Product tempProduct : tempProducts) {
            oos.writeObject(tempProduct);
        }
    }

    public void getCustomerProducts(ObjectOutputStream oos) throws IOException {
        oos.writeObject(((Customer) currentUser).getProducts());
    }

    public void getSellerStores(ObjectOutputStream oos) throws IOException {
        ArrayList<String> stores = ((Seller) currentUser).getStoreNames();
        oos.reset();
        oos.flush();
        oos.writeObject(stores.size());
        for (String store : stores) {
            oos.writeObject(store);
        }
    }

    public void getSales(ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        String store = (String) ois.readObject();
        ArrayList<String> sales = new ArrayList<>();
        for (Customer customer : customers) {
            for (Product product : customer.getProducts()) {
                if (product.getStore().equals(store)) {
                    sales.add(String.format("Product: %s, Customer: %s, Revenue: %.2f",
                            product.getName(), customer.getUsername(), product.getPrice()));
                }
            }
        }
        oos.writeObject(sales.size());
        for (String sale : sales) {
            oos.writeObject(sale);
        }
    }

    public void getCartNum(ObjectOutputStream oos) throws IOException {
        int cartNum = 0;
        for (Customer customer : customers) {
            cartNum += customer.getCartProducts().size();
        }
        oos.writeObject(cartNum);
    }

    public void getCustomerNames(ObjectOutputStream oos) throws IOException {
        oos.writeObject(customers.size());
        for (Customer customer : customers) {
            oos.writeObject(customer.getUsername());
        }
    }

    public void getCustomerCart(ObjectInputStream ois, ObjectOutputStream oos) throws IOException,
            ClassNotFoundException {
        String customerName = (String) ois.readObject();
        for (Customer customer : customers) {
            if (customer.getUsername().equals(customerName)) {
                oos.writeObject(customer.getCartProducts().size());
                for (Product cartProduct : customer.getCartProducts()) {
                    oos.writeObject(cartProduct);
                }
                break;
            }
        }
    }

    public void refresh(ObjectOutputStream oos) throws IOException {
        oos.writeObject(sellers);
        oos.writeObject(customers);
        oos.writeObject(products);
    }

    // read in all the data from files into ArrayLists
    public static void readFiles() {

        // read in all the Product objects
        products = new ArrayList<>();
        try (FileReader fr = new FileReader("products.txt");
             BufferedReader br = new BufferedReader(fr)) {
            ArrayList<String[]> lines = new ArrayList<>();
            while (br.ready()) {
                lines.add(br.readLine().split(","));
            }
            for (String[] line : lines) {
                products.add(new Product(line[0], line[1], line[2], Integer.parseInt(line[3]),
                        Double.parseDouble(line[4])));
            }
        } catch (FileNotFoundException ignored) {
            // empty block
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // read in all the Seller objects
        sellers = new ArrayList<>();
        try (FileReader fr = new FileReader("sellers.txt");
             BufferedReader br = new BufferedReader(fr)) {
            ArrayList<String[]> lines = new ArrayList<>();
            while (br.ready()) {
                lines.add(br.readLine().split(";"));
            }
            ArrayList<String> sellerStoreNames = new ArrayList<>();
            ArrayList<Product> sellerProducts = new ArrayList<>();
            for (String[] line : lines) {
                if (line.length == 3) {
                    sellerStoreNames = new ArrayList<>(Arrays.asList(line[2].split(",")));
                    sellerProducts = new ArrayList<>();
                    for (String storeName : sellerStoreNames) {
                        for (Product product : products) {
                            if (product.getStore().equals(storeName)) {
                                sellerProducts.add(product);
                            }
                        }
                    }
                }
                sellers.add(new Seller(line[0], line[1], sellerStoreNames, sellerProducts));
            }
        } catch (FileNotFoundException ignored) {
            // empty block
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // read in all the Customer objects
        customers = new ArrayList<>();
        try (FileReader fr = new FileReader("customers.txt");
             BufferedReader br = new BufferedReader(fr)) {
            ArrayList<String[]> lines = new ArrayList<>();
            while (br.ready()) {
                lines.add(br.readLine().split(";"));
            }

            // while (br.ready()) {
            //     lines.add(br.readLine().split("((?<=;)|(?=;))"));
            // }


            String[] productNames;
            String[] cartProductNames;
            ArrayList<Product> customerProducts;
            ArrayList<Product> customerCartProducts;
            for (String[] line : lines) {
                customerProducts = new ArrayList<>();
                if (line.length > 2) {
                    productNames = line[2].split(",");
                    for (String productName : productNames) {
                        for (Product product : products) {
                            if (product.getName().equals(productName)) {
                                customerProducts.add(product);
                            }
                        }
                    }
                }
                customerCartProducts = new ArrayList<>();
                if (line.length > 3) {
                    cartProductNames = line[3].split(",");
                    for (String cartProductName : cartProductNames) {
                        for (Product product : products) {
                            if (product.getName().equals(cartProductName)) {
                                customerCartProducts.add(product);
                            }
                        }
                    }
                }
                customers.add(new Customer(line[0], line[1], customerProducts, customerCartProducts));
            }
        } catch (FileNotFoundException ignored) {
            // empty block
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    ArrayList<String> sellerStores2 = new ArrayList<>();
    ArrayList<Integer> counts = new ArrayList<>();

    public void customerStatistics(ObjectOutputStream oos, ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        ArrayList<String> sellerStores1;
        int count = 0;
        Product prodName1;
        StringBuilder s = new StringBuilder();
        s.append("Seller" + "     ");
        s.append("Store Name" + "      ");
        s.append("Count" + "\n");
        sellerStores2.clear();
        for (Seller seller1 : sellers) {
            String sellerName = seller1.getUsername();
            sellerStores1 = seller1.getStoreNames(); // Getting the store name for each seller
            for (String sellerStore : sellerStores1) { // Parsing through each store name
                count = 0;
                for (Customer cust : customers) { // Parsing each customer
                    if (cust.getUsername().equals(currentUser.getUsername())) {
                        ArrayList<Product> prodList = cust.getProducts();
                        for (int i = 0; i < prodList.size(); i++) {
                            String productName = prodList.get(i).getName(); // Getting the product names for each customer
                            String store = prodList.get(i).getStore();
                            if (store.equals(sellerStore)) {
                                // Comparing the store name of the products bought by the customer to the current
                                // store name
                                count++;
                            }
                        }
                    }
                }
                s.append(sellerName + "     ");
                s.append(sellerStore + "     ");
                s.append(count + "\n");
                sellerStores2.add(sellerStore);
                counts.add(count);
            }
            oos.writeObject(s);
        }
        //oos.writeObject(s);
    }

    public void sortCustomerStatistics1(ObjectOutputStream oos, ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        StringBuilder str = new StringBuilder();
        StringBuilder str1 = new StringBuilder();
        int option = (int) ois.readObject();
        if (option == 0) {
            str.append("Store Name" + "     ");
            str.append("Quantity" + "\n");
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < sellerStores2.size(); i++) {
                String temp = sellerStores2.get(i) + "-" + counts.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str.append(sortData[0] + "     ");
                str.append(sortData[1] + "\n");
            }
            oos.writeObject(str);
            str = new StringBuilder();
            sortList.clear();
        } else if (option == 1) {
            str1.append("Store Name" + "     ");
            str1.append("Quantity" + "\n");
            ArrayList<String> sortList1 = new ArrayList<>();
            for (int i = 0; i < sellerStores2.size(); i++) {
                String temp = counts.get(i) + "-" + sellerStores2.get(i);
                sortList1.add(temp);
            }
            Collections.sort(sortList1);
            for (int i = 0; i < sortList1.size(); i++) {
                String[] sortData = sortList1.get(i).split("-");
                str1.append(sortData[1] + "     ");
                str1.append(sortData[0] + "\n");
            }
            oos.writeObject(str1);
            str1 = new StringBuilder();
            sortList1.clear();
        }
    }

    ArrayList<String> sellStoreList = new ArrayList<>();
    ArrayList<String> productNameList = new ArrayList<>();
    ArrayList<String> custNameList = new ArrayList<>();
    public void customerStatistics2(ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
        String customerName;
        String prodSeller = "";
        StringBuilder st = new StringBuilder();
        st.append("Customer Name" + "      " + "Store" + "      " + "Product Name" + "\n");
        for (Customer cust : customers) {
            customerName = String.valueOf(cust.getUsername());
            if (customerName.equals(currentUser.getUsername())) {
                for (Product custProd : cust.getProducts()) {
                    String productName = custProd.getName();
                    String store = custProd.getStore();
                    ArrayList<String> sellerStores = new ArrayList<>();
                    for (Seller seller : sellers) {
                        sellerStores = seller.getStoreNames();
                        if (sellerStores.contains(store)) {
                            st.append(customerName + "      " + store + "      " + productName + "\n");
                            custNameList.add(customerName);
                            sellStoreList.add(store);
                            productNameList.add(productName);
                        }
                    }
                }
                oos.writeObject(st);
            }
        }
    }

    public void sortCustomerStatistics2(ObjectOutputStream oos, ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        StringBuilder str = new StringBuilder();
        StringBuilder str1 = new StringBuilder();
        int option = (int) ois.readObject();
        //System.out.println("In SORTCUSOTMERSTATISTICS2 with: " + option);
        str.append("Customer Name" + "      " + "Store Name" + "     " + "Product Name" + "\n");
        if (option == 0) {
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < custNameList.size(); i++) {
                String temp = custNameList.get(i) + "-" + sellStoreList.get(i) + "-" + productNameList.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str.append(sortData[0] + "      " + sortData[1] + "     " + sortData[2] + "\n");
            }
            oos.writeObject(str);
            str = new StringBuilder();
        } else if (option == 1) {
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < custNameList.size(); i++) {
                String temp = sellStoreList.get(i) + "-" + custNameList.get(i) + "-" + productNameList.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str.append(sortData[1] + "     " + sortData[0] + "     " + sortData[2] + "\n");
            }
            oos.writeObject(str);
            str = new StringBuilder();
        } else if (option == 2) {
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < custNameList.size(); i++) {
                String temp = productNameList.get(i) + "-" + sellStoreList.get(i) + "-" + custNameList.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str.append(sortData[2] + "     " + sortData[1] + "     " + sortData[0] + "\n");
            }
            oos.writeObject(str);
            str = new StringBuilder();
        }
    }


    ArrayList<Integer> counts1 = new ArrayList<>();
    ArrayList<String> sellStore1 = new ArrayList<>();
    public void sellerStatistics1(ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
        ArrayList<String> sellerStores = new ArrayList<>();
        int count = 0;
        StringBuilder str1 = new StringBuilder();
        str1.append("Seller Name" + "     " + "Store Name" + "     " + "Quantity" + "\n");
        for (Seller seller : sellers) {
            String sellerName = seller.getUsername();
            sellerStores = seller.getStoreNames();
            for (String sellerStore : sellerStores) {
                count = 0;
                for (Customer cust : customers) {
                    ArrayList<Product> prodList = cust.getProducts();
                    for (int i = 0; i < prodList.size(); i++) {
                        if (prodList.get(i).getStore().equals(sellerStore)) {
                            // Comparing the store name of the products bought by the customer to the current store name
                            count++;
                        }

                    }
                }
                str1.append(sellerName + "     " + sellerStore + "     " + count + "\n");
                sellStore1.add(sellerStore);
                counts1.add(count);
            }
            oos.writeObject(str1);
            str1 = new StringBuilder();
        }
    }

    public void sortSellerStatistics1(ObjectOutputStream oos, ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        StringBuilder str2 = new StringBuilder();
        int option = (int) ois.readObject();
        str2.append("Store Name" + "     " + "Quantity" + "\n");
        if (option == 0) {
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < sellStore1.size(); i++) {
                String temp = sellStore1.get(i) + "-" + counts.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str2.append(sortData[0] + "     " + sortData[1]);
            }
            oos.writeObject(str2);
            str2 = new StringBuilder();
        } else if (option == 1) {
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < sellStore1.size(); i++) {
                String temp = counts.get(i) + "-" + sellStore1.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str2.append(sortData[1] + "     " + sortData[0]);
            }
            oos.writeObject(str2);
            str2 = new StringBuilder();
        }
    }

    ArrayList<String> prodName = new ArrayList<>();
    ArrayList<Integer> productCount = new ArrayList<>();
    public void sellerStatistics2(ObjectOutputStream oos, ObjectInputStream ois) throws IOException {
        int prodCount = 0;
        StringBuilder str = new StringBuilder();
        str.append("Product Name" + "     " + "Quantity" + "\n");
        for (Product product : products) {
            prodCount = 0;
            for (Customer cust1 : customers) {
                ArrayList<Product> custProd = cust1.getProducts();
                for (int i = 0; i < custProd.size(); i++) {
                    if (product.getName().equals(custProd.get(i).getName())) {
                        prodCount++;
                    }
                }
            }
            str.append(product.getName() + "     " + prodCount + "\n");
            prodName.add(product.getName());
            productCount.add(prodCount);
        }
        oos.writeObject(str);
    }
    public void sortSellerStatistics2(ObjectOutputStream oos, ObjectInputStream ois) throws IOException,
            ClassNotFoundException {
        StringBuilder str = new StringBuilder();
        int option = (int) ois.readObject();
        str.append("Product Name" + "     " + "Quantity" + "\n");
        if (option == 0) {
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < prodName.size(); i++) {
                String temp = prodName.get(i) + "-" + productCount.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str.append(sortData[0] + "     " + sortData[1] + "\n");
            }
            oos.writeObject(str);
        } else if (option == 1) {
            ArrayList<String> sortList = new ArrayList<>();
            for (int i = 0; i < prodName.size(); i++) {
                String temp = prodName.get(i) + "-" + productCount.get(i);
                sortList.add(temp);
            }
            Collections.sort(sortList);
            for (int i = 0; i < sortList.size(); i++) {
                String[] sortData = sortList.get(i).split("-");
                str.append(sortData[1] + "     " + sortData[0] + "\n");
            }
            oos.writeObject(str);
        }
    }

    // write all the current data to the files
    public static void shutDown() {
        // write all the Seller objects to the seller file
        try (FileWriter fw = new FileWriter("sellers.txt");
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (Seller seller : sellers) {
                bw.write(seller.toString());
                bw.newLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // write all Customer objects to the customer file
        try (FileWriter fw = new FileWriter("customers.txt");
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (Customer customer : customers) {
                bw.write(customer.toString());
                bw.newLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // write all Product objects to the product file
        try (FileWriter fw = new FileWriter("products.txt");
             BufferedWriter bw = new BufferedWriter(fw)) {
            for (Product product : products) {
                bw.write(product.getString());
                bw.newLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {

        // add shutDownHook to call shutDown() method when application is shut down
        ShutDownTask shutDownTask = new ShutDownTask();
        Runtime.getRuntime().addShutdownHook(shutDownTask);

        // read in the data from all the files
        readFiles();

        // handle connections
        try (ServerSocket ss = new ServerSocket(4242)) {
            // infinite server loop, spawn new thread for each connection
            while (true) {
                Socket s = ss.accept();
                Server server = new Server(s);
                new Thread(server).start();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * ShutDownTask class
     *
     * This class creates a thread that runs when the server is shutdown, which calls the shutDown() method to write
     * all the necessary data to files
     *
     * @author Charlie Schmidt, lab sec 002
     * @version 12/12/2022
     */
    private static class ShutDownTask extends Thread {
        @Override
        public void run() {
            shutDown();
        }
    }
}