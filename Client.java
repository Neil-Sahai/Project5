import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;

import java.util.ArrayList;

/**
 * Client class
 *
 * This class contains the code for the user interface, and sends all information to the Server to be processed
 * Multiple instances of this class can be run simultaneously
 *
 * @author Charlie Schmidt, Zachary DeFazio, Neil Sahai, Ivan Phillip lab sec 002
 * @version 11/29/2022
 */
public class Client extends JComponent implements Runnable {

    JFrame frame;
    JButton refreshButton;
    JButton searchButton;
    JButton allButton;
    JButton addButton;
    JButton editButton;
    JButton deleteButton;
    JButton addStoreButton;
    JButton sellerExportButton;
    JButton sellerImportButton;
    JButton salesButton;
    JButton cartButton;
    JButton stats;
    JTextField searchBar;
    JButton shoppingCart;
    JButton customerExportButton;
    JList productListing;
    DefaultListModel productModel;
    JButton selectButton;
    JButton sortPriceButton;
    JButton sortQuantityButton;
    JButton customerStatsButton;
    JButton historyButton;
    User user;
    Client client;
    static ObjectOutputStream oos;
    ObjectInputStream ois;

    // action listener for Seller buttons
    @SuppressWarnings("unchecked")
    ActionListener sellerActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == addButton) {
                JTextField nameField = new JTextField(5);
                JTextField storeField = new JTextField(5);
                JTextField descriptionField = new JTextField(10);
                JTextField quantityField = new JTextField(5);
                JTextField priceField = new JTextField(5);

                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("Name:"));
                myPanel.add(nameField);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Store:"));
                myPanel.add(storeField);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Description:"));
                myPanel.add(descriptionField);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Quantity:"));
                myPanel.add(quantityField);
                myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                myPanel.add(new JLabel("Price:"));
                myPanel.add(priceField);

                int result = JOptionPane.showConfirmDialog(frame, myPanel,
                        "Please Enter Product Information", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String name;
                    String store;
                    String description;
                    int quantityAvailable;
                    double price;
                    String response;
                    try {
                        if (nameField.getText() == null || storeField.getText() == null ||
                                descriptionField.getText() == null || quantityField.getText() == null ||
                                priceField.getText() == null) {
                            throw new NumberFormatException();
                        }
                        name = nameField.getText();
                        store = storeField.getText();
                        description = descriptionField.getText();
                        quantityAvailable = Integer.parseInt(quantityField.getText());
                        price = Double.parseDouble(priceField.getText());

                        oos.writeObject("ADDPRODUCT");
                        oos.writeObject(name);
                        oos.writeObject(store);
                        oos.writeObject(description);
                        oos.flush();
                        oos.writeInt(quantityAvailable);
                        oos.writeDouble(price);
                        oos.flush();
                        response = (String) ois.readObject();
                        JOptionPane.showMessageDialog(frame, response);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(frame, "Please enter values in the correct format",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException | ClassNotFoundException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }

            if (e.getSource() == deleteButton) {
                JTextField nameField = new JTextField(5);

                JPanel myPanel = new JPanel();
                myPanel.add(new JLabel("Name of Product to be removed:"));
                myPanel.add(nameField);

                int result = JOptionPane.showConfirmDialog(frame, myPanel,
                        "Please Enter Product Information", JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    String name;
                    String response;
                    try {
                        if (nameField.getText() == null) {
                            throw new NumberFormatException();
                        }
                        name = nameField.getText();

                        oos.writeObject("DELETEPRODUCT");
                        oos.writeObject(name);
                        response = (String) ois.readObject();

                        if (response.equals("The product could not be removed!")) {
                            JOptionPane.showMessageDialog(null, "That product does not exist",
                                    "Fast Basket", JOptionPane.ERROR_MESSAGE);
                        }

                        JOptionPane.showMessageDialog(frame, response);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(frame, "Please enter values in the correct format",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException | ClassNotFoundException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }

            if (e.getSource() == selectButton) {
                Product tempProduct = (Product) productListing.getSelectedValue();
                if (tempProduct == null) {
                    JOptionPane.showMessageDialog(frame, "Click on a product first!");
                } else {
                    String productOutput = String.format("Name: %s\nStore: %s\nDescription: %s\n" +
                                    "Quantity: %d\nPrice: %.2f",
                            tempProduct.getName(), tempProduct.getStore(), tempProduct.getDescription(),
                            tempProduct.getQuantityAvailable(), tempProduct.getPrice());
                    Object[] options1 = {"Delete", "Edit", "Cancel"};
                    int result = JOptionPane.showOptionDialog(frame, productOutput, "Product Info",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);
                    if (result == 2) {
                        // cancel, nothing happens
                    } else if (result == 1) {
                        JTextField nameField = new JTextField(5);
                        JTextField storeField = new JTextField(5);
                        JTextField descriptionField = new JTextField(10);
                        JTextField quantityField = new JTextField(5);
                        JTextField priceField = new JTextField(5);

                        JPanel myPanel = new JPanel();
                        myPanel.add(new JLabel("Name:"));
                        myPanel.add(nameField);
                        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                        myPanel.add(new JLabel("Store:"));
                        myPanel.add(storeField);
                        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                        myPanel.add(new JLabel("Description:"));
                        myPanel.add(descriptionField);
                        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                        myPanel.add(new JLabel("Quantity:"));
                        myPanel.add(quantityField);
                        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                        myPanel.add(new JLabel("Price:"));
                        myPanel.add(priceField);

                        int editResult = JOptionPane.showConfirmDialog(frame, myPanel,
                                "Please Edit Product Information", JOptionPane.OK_CANCEL_OPTION);

                        if (editResult == JOptionPane.OK_OPTION) {
                            String name;
                            String store;
                            String description;
                            int quantityAvailable;
                            double price;
                            String response;
                            try {
                                if (nameField.getText() == null || storeField.getText() == null ||
                                        descriptionField.getText() == null || quantityField.getText() == null ||
                                        priceField.getText() == null) {
                                    throw new NumberFormatException();
                                }
                                name = nameField.getText();
                                store = storeField.getText();
                                description = descriptionField.getText();
                                quantityAvailable = Integer.parseInt(quantityField.getText());
                                price = Double.parseDouble(priceField.getText());

                                oos.writeObject("EDITPRODUCT");
                                oos.writeObject(tempProduct.getName());
                                oos.writeObject(name);
                                oos.writeObject(store);
                                oos.writeObject(description);
                                oos.flush();
                                oos.writeInt(quantityAvailable);
                                oos.writeDouble(price);
                                oos.flush();
                                response = (String) ois.readObject();

                                if (response.equals("You don't own that product!")) {
                                    JOptionPane.showMessageDialog(null, "You don't own that product",
                                            "Fast Basket", JOptionPane.ERROR_MESSAGE);
                                }

                                JOptionPane.showMessageDialog(frame, response);
                            } catch (NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(frame, "Please enter values in the correct format",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException | ClassNotFoundException ioe) {
                                ioe.printStackTrace();
                            }
                        }
                    } else if (result == 0) {
                        String response;
                        try {
                            oos.writeObject("DELETEPRODUCT");
                            oos.writeObject(tempProduct.getName());
                            response = (String) ois.readObject();

                            if (response.equals("The product could not be removed!")) {
                                JOptionPane.showMessageDialog(null, "That product does not exist", "Fast Basket",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                            JOptionPane.showMessageDialog(frame, response);
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(frame, "Please enter values in the correct format",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException | ClassNotFoundException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
            }

            if (e.getSource() == refreshButton) {
                productModel.removeAllElements();
                ArrayList<Product> tempProducts = new ArrayList<>();
                try {
                    oos.writeObject("GETSELLERPRODUCTS");
                    int size = (int) ois.readObject();
                    for (int i = 0; i < size; i++) {
                        tempProducts.add((Product) ois.readObject());
                    }
                } catch (IOException | ClassNotFoundException ioe) {
                    ioe.printStackTrace();
                }

                for (Product tempProduct : tempProducts) {
                    productModel.addElement(tempProduct);
                }
            }

            if (e.getSource() == editButton) {
                JTextField nameField = new JTextField(5);
                JTextField storeField = new JTextField(5);
                JTextField descriptionField = new JTextField(10);
                JTextField quantityField = new JTextField(5);
                JTextField priceField = new JTextField(5);

                ArrayList<Product> prods;
                String[] dd = null;

                try {
                    oos.writeObject("GETSELLERPRODUCTS");
                    prods = (ArrayList<Product>) ois.readObject();
                    dd = new String[prods.size()];
                    for (int i = 0; i < dd.length; i++) {
                        dd[i] = prods.get(i).getName();
                    }
                } catch (ClassNotFoundException | IOException ioex) {
                    ioex.printStackTrace();
                }
                String eprodname = null;
                eprodname = (String) JOptionPane.showInputDialog(null, "Select product to be modified",
                        "Fast Basket", JOptionPane.PLAIN_MESSAGE, null, dd, null);

                if (eprodname != null) {
                    JPanel myPanel = new JPanel();
                    myPanel.add(new JLabel("Name:"));
                    myPanel.add(nameField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Store:"));
                    myPanel.add(storeField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Description:"));
                    myPanel.add(descriptionField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Quantity:"));
                    myPanel.add(quantityField);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(new JLabel("Price:"));
                    myPanel.add(priceField);

                    int result = JOptionPane.showConfirmDialog(frame, myPanel,
                            "Please Edit Product Information", JOptionPane.OK_CANCEL_OPTION);

                    if (result == JOptionPane.OK_OPTION) {
                        String name;
                        String store;
                        String description;
                        int quantityAvailable;
                        double price;
                        String response;
                        try {
                            if (nameField.getText() == null || storeField.getText() == null ||
                                    descriptionField.getText() == null || quantityField.getText() == null ||
                                    priceField.getText() == null) {
                                throw new NumberFormatException();
                            }
                            name = nameField.getText();
                            store = storeField.getText();
                            description = descriptionField.getText();
                            quantityAvailable = Integer.parseInt(quantityField.getText());
                            price = Double.parseDouble(priceField.getText());

                            oos.writeObject("EDITPRODUCT");
                            oos.writeObject(eprodname);
                            oos.writeObject(name);
                            oos.writeObject(store);
                            oos.writeObject(description);
                            oos.flush();
                            oos.writeInt(quantityAvailable);
                            oos.writeDouble(price);
                            oos.flush();
                            response = (String) ois.readObject();

                            if (response.equals("You don't own that product!")) {
                                JOptionPane.showMessageDialog(null, "You don't own that product", "Fast Basket",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                            JOptionPane.showMessageDialog(frame, response);
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(frame, "Please enter values in the correct format",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        } catch (IOException | ClassNotFoundException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }

            }

            if (e.getSource() == addStoreButton) {
                JTextField nameField = new JTextField(5);

                JPanel myPanel = new JPanel();

                myPanel.add(new JLabel("Enter new store name:"));
                myPanel.add(nameField);

                int result = JOptionPane.showConfirmDialog(frame, myPanel, "Add Store",
                        JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    if (nameField.getText() == null || nameField.getText().equals("")) {
                        JOptionPane.showMessageDialog(frame, "The name cannot be blank!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        String response;
                        try {
                            oos.writeObject("ADDSTORE");
                            oos.writeObject(nameField.getText());
                            response = (String) ois.readObject();
                            JOptionPane.showMessageDialog(frame, response);
                        } catch (IOException | ClassNotFoundException nfe) {
                            nfe.printStackTrace();
                        }
                    }
                }
            }

            if (e.getSource() == sellerExportButton) {
                int result = JOptionPane.showConfirmDialog(frame, "Would you like to export a file with your " +
                        "product information?", "Export Product File", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    ArrayList<Product> sellerProducts = new ArrayList<>();
                    try {
                        oos.writeObject("GETSELLERPRODUCTS");
                        int size = (int) ois.readObject();
                        for (int i = 0; i < size; i++) {
                            sellerProducts.add((Product) ois.readObject());
                        }
                    } catch (IOException | ClassNotFoundException ioe) {
                        ioe.printStackTrace();
                    }

                    File exportFile = new File("product_file.txt");
                    try (FileWriter fw = new FileWriter(exportFile);
                         BufferedWriter bw = new BufferedWriter(fw)) {
                        for (Product sellerProduct : sellerProducts) {
                            bw.write(sellerProduct.getString() + "\n");
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(frame, "Your file has been created under the name" +
                            "\"product_file.txt\"");
                }
            }

            if (e.getSource() == sellerImportButton) {
                JTextField fileNameField = new JTextField(15);
                JLabel message = new JLabel("File format: One product per line,\nEach line has the format:" +
                        " name,store,description,quantity,price\n");
                JPanel myPanel = new JPanel();
                myPanel.add(message);
                myPanel.add(fileNameField);

                int result = JOptionPane.showConfirmDialog(frame, myPanel, "Import Product File",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String filename = fileNameField.getText();

                    File importedFile = new File(filename);
                    ArrayList<String[]> splitLines = new ArrayList<>();
                    try (FileReader fr = new FileReader(importedFile);
                         BufferedReader br = new BufferedReader(fr)) {
                        while (br.ready()) {
                            String line = br.readLine();
                            splitLines.add(line.split(","));
                        }
                    } catch (FileNotFoundException fnf) {
                        JOptionPane.showMessageDialog(frame, "The given file does not exist!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    int count = 0;
                    int quantityAvailable;
                    double price;
                    String response;
                    for (String[] splitLine : splitLines) {
                        try {
                            quantityAvailable = Integer.parseInt(splitLine[3]);
                            price = Double.parseDouble(splitLine[4]);
                            oos.writeObject("ADDPRODUCT");
                            oos.writeObject(splitLine[0]);
                            oos.writeObject(splitLine[1]);
                            oos.writeObject(splitLine[2]);
                            oos.flush();
                            oos.writeInt(quantityAvailable);
                            oos.writeDouble(price);
                            oos.flush();
                            response = (String) ois.readObject();
                            if (response.equals("Product was added successfully")) {
                                count++;
                            }
                        } catch (NumberFormatException ignored) {
                            // empty block
                        } catch (IOException | ClassNotFoundException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                    String resultMessage = String.format("%d of %d product lines were formatted correctly and" +
                            "added", count, splitLines.size());
                    JOptionPane.showMessageDialog(frame, resultMessage);
                }
            }

            if (e.getSource() == searchButton) {
                String query = searchBar.getText();

                //Only throw an error if user attempts to search with no keyword(s)
                if (query.isEmpty() && e.getSource() == searchButton) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid search query!", "Fast Basket",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        oos.writeObject("SEARCHSELLERPRODUCTS");
                        oos.writeObject(query);

                        int size = (int) ois.readObject();
                        ArrayList<Product> searchResults = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            searchResults.add((Product) ois.readObject());
                        }

                        productModel.removeAllElements();
                        if (searchResults.size() == 0) {
                            JOptionPane.showMessageDialog(null, "No products match that query!", "Fast Basket",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            for (Product p : searchResults) {
                                productModel.addElement(p);
                            }
                        }
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(null, "Something went wrong!", "Fast Basket",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }

            if (e.getSource() == salesButton) {
                String[] stores = null;
                try {
                    oos.writeObject("GETSELLERSTORES");
                    int size = (int) ois.readObject();
                    stores = new String[size];
                    for (int i = 0; i < size; i++) {
                        stores[i] = (String) ois.readObject();
                    }
                } catch (IOException | ClassNotFoundException ioe) {
                    ioe.printStackTrace();
                }

                String selectedStore = (String) JOptionPane.showInputDialog(null, "Select Store", "Fast Basket",
                        JOptionPane.PLAIN_MESSAGE, null, stores, null);
                if (selectedStore != null) {
                    String[] sales = null;
                    try {
                        oos.writeObject("GETSALES");
                        oos.writeObject(selectedStore);
                        int size = (int) ois.readObject();
                        sales = new String[size];
                        for (int i = 0; i < size; i++) {
                            sales[i] = (String) ois.readObject();
                        }
                    } catch (IOException | ClassNotFoundException ioe) {
                        ioe.printStackTrace();
                    }
                    if (sales == null) {
                        JOptionPane.showMessageDialog(frame, "You haven't made any sales yet");
                    } else {
                        String output = String.join("\n", sales);
                        JOptionPane.showMessageDialog(frame, output);
                    }
                }
            }

            if (e.getSource() == cartButton) {
                int cartNum = 0;
                try {
                    oos.writeObject("GETCARTNUM");
                    cartNum = (int) ois.readObject();
                } catch (IOException | ClassNotFoundException ioe) {
                    ioe.printStackTrace();
                }
                if (cartNum == 0) {
                    JOptionPane.showMessageDialog(frame, "There are no items in customer carts");
                } else {
                    String message = String.format("There are %d items in customer carts\nSelect Customer", cartNum);
                    String[] customers = null;
                    try {
                        oos.writeObject("GETCUSTOMERNAMES");
                        int size = (int) ois.readObject();
                        customers = new String[size];
                        for (int i = 0; i < size; i++) {
                            customers[i] = (String) ois.readObject();
                        }
                    } catch (IOException | ClassNotFoundException ioe) {
                        ioe.printStackTrace();
                    }

                    String selectedCustomer = (String) JOptionPane.showInputDialog(null, message, "Fast Basket",
                            JOptionPane.PLAIN_MESSAGE, null, customers, null);
                    if (selectedCustomer != null) {
                        String[] cartProducts = null;
                        try {
                            oos.writeObject("GETCUSTOMERCART");
                            oos.writeObject(selectedCustomer);
                            int size = (int) ois.readObject();
                            cartProducts = new String[size];
                            for (int i = 0; i < size; i++) {
                                cartProducts[i] = ((Product) ois.readObject()).toString();
                            }
                        } catch (IOException | ClassNotFoundException ioe) {
                            ioe.printStackTrace();
                        }
                        if (cartProducts == null || cartProducts.length == 0) {
                            JOptionPane.showMessageDialog(frame, "That customer's cart is empty");
                        } else {
                            String output = String.join("\n", cartProducts);
                            JOptionPane.showMessageDialog(frame, output);
                        }
                    }
                }

            }

            if (e.getSource() == stats) {
                JPanel newPanel = new JPanel();
                JTextField newtextfield = new JTextField();
                newPanel.add(newtextfield, BorderLayout.CENTER);

                try {
                    oos.writeObject("SELLERSTATISTICS1");
                    Object s = ois.readObject();

                    JOptionPane.showMessageDialog(null, s, "Fast Basket", JOptionPane.INFORMATION_MESSAGE, null);
                    int choice = 0;
                    /*
                    while (choice ==0) {
                        String[] sortOptions = {"Yes", "No"};
                        choice = JOptionPane.showOptionDialog(null, "Do you want to sort the data?", "Fast Basket",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, sortOptions,
                                sortOptions[0]);
                        if (choice == 0) {
                            String[] sort = {"Store Name", "Quantity"};
                            int options = JOptionPane.showOptionDialog(null, "Sort Options", "Fast Basket",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, sort, sort[0]);
                            oos.writeObject("SORTSELLERSTATISTICS1");
                            oos.writeObject(options);
                            Object str2 = ois.readObject();
                            JOptionPane.showMessageDialog(null, str2, "Fast Basket", JOptionPane.INFORMATION_MESSAGE,
                            null);
                        }
                    }
                     */
                    oos.writeObject("SELLERSTATISTICS2");
                    Object st = ois.readObject();
                    JOptionPane.showMessageDialog(null, st, "Fast Basket", JOptionPane.INFORMATION_MESSAGE, null);
                    int choice2 = 0;
                    while (choice2 == 0) {
                        String[] sortOptions = {"Yes", "No"};
                        choice2 = JOptionPane.showOptionDialog(null, "Do you want to sort the data?",
                                "Fast Basket", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                sortOptions, sortOptions[0]);
                        if (choice2 == 0) {
                            String[] sort = {"Product Name", "Quantity"};
                            int options = JOptionPane.showOptionDialog(null, "Sort Options", "Fast Basket",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, sort, sort[0]);
                            oos.writeObject("SORTSELLERSTATISTICS2");
                            oos.writeObject(options);
                            Object str = ois.readObject();
                            JOptionPane.showMessageDialog(null, str, "Fast Basket",
                                    JOptionPane.INFORMATION_MESSAGE, null);
                        }
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
    };

    //Main action listener that deals with customer GUI buttons
    @SuppressWarnings({"unchecked", "rawtypes"})
    ActionListener customerActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //Since both buttons list products, they can be dealt with together
            if (e.getSource() == allButton || e.getSource() == refreshButton) {
                productModel.removeAllElements();
                ArrayList<Product> tempProducts = new ArrayList<>();
                try {
                    oos.writeObject("GETPRODUCTS");
                    int size = (int) ois.readObject();
                    for (int i = 0; i < size; i++) {
                        tempProducts.add((Product) ois.readObject());
                    }
                } catch (IOException | ClassNotFoundException ioe) {
                    ioe.printStackTrace();
                }

                for (Product tempProduct : tempProducts) {
                    productModel.addElement(tempProduct);
                }
            }

            if (e.getSource() == searchButton) {
                String query = searchBar.getText();

                //Only throw an error if user attempts to search with no keyword(s)
                if (query.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid search query!", "Fast Basket",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        oos.writeObject("SEARCHPRODUCTS");
                        oos.writeObject(query);
                        int size = (int) ois.readObject();
                        ArrayList<Product> searchResults = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            searchResults.add((Product) ois.readObject());
                        }

                        productModel.removeAllElements();
                        if (searchResults.size() == 0) {
                            JOptionPane.showMessageDialog(null, "No products match that query!", "Fast Basket",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            for (Product p : searchResults) {
                                productModel.addElement(p);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Something went wrong!", "Fast Basket",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

            }

            if (e.getSource() == selectButton) {
                Product tempProduct = (Product) productListing.getSelectedValue();
                if (tempProduct == null) {
                    JOptionPane.showMessageDialog(frame, "Click on a product first!");
                } else {
                    String productOutput = String.format("Name: %s\nStore: %s\nDescription: %s\n" +
                                    "Quantity: %d\nPrice: %.2f",
                            tempProduct.getName(), tempProduct.getStore(), tempProduct.getDescription(),
                            tempProduct.getQuantityAvailable(), tempProduct.getPrice());
                    Object[] options1 = { "Add to Cart", "Cancel" };
                    int result = JOptionPane.showOptionDialog(frame, productOutput, "Product Info",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);
                    if (result == 0) {
                        try {
                            oos.writeObject("ADDTOCART");
                            oos.writeObject(tempProduct.getName());
                            oos.writeObject(tempProduct.getStore());

                            if (((String)ois.readObject()).equals("The product is no longer available!")) {
                                throw new Exception();
                            }
                        } catch (Exception exception) {
                            JOptionPane.showMessageDialog(null, "Something went wrong!", "Fast Basket",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }

            if (e.getSource() == shoppingCart) {
                //Shopping cart opens a new window
                JFrame newFrame = new JFrame();

                ArrayList<Product> shoppingCartList = new ArrayList<>();
                try {
                    oos.writeObject("GETCARTPRODUCTS");
                    int listSize = (Integer)ois.readObject();

                    for (int i = 0; i < listSize; i++) {
                        shoppingCartList.add((Product)ois.readObject());
                    }
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Something went wrong!", "Fast Basket",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //Throw error if the user didn't add anything to cart
                if (shoppingCartList.size() == 0) {
                    JOptionPane.showMessageDialog(null, "You need to add items to view your cart!", "Fast Basket",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    DefaultListModel model = new DefaultListModel();
                    for (Product p : shoppingCartList) {
                        model.addElement(p);
                    }

                    JPanel products = new JPanel();

                    JList productList = new JList(model);
                    products.add(productList);
                    newFrame.add(products, BorderLayout.CENTER);

                    JButton deleteProdButton = new JButton("Delete product");
                    deleteProdButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int index = productList.getSelectedIndex();
                            Product p = (Product)model.remove(index);

                            try {
                                oos.writeObject("REMOVEFROMCART");
                                oos.writeObject(p.getName());
                                oos.writeObject(p.getStore());

                                String result = (String)ois.readObject();
                                if (!(result.equals("The product was successfully removed from the cart"))) {
                                    throw new Exception();
                                }

                                if (model.size() == 0) {
                                    newFrame.dispose();
                                }
                            } catch (Exception exception) {
                                JOptionPane.showMessageDialog(null, "Something went wrong!", "Fast Basket",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });

                    JButton purchaseButton = new JButton("Purchase all");
                    purchaseButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                oos.writeObject("PURCHASECART");

                                String result = (String)ois.readObject();

                                if (!(result.equals("successful"))) {
                                    throw new Exception();
                                }

                                newFrame.dispose();
                            } catch (Exception exception) {
                                JOptionPane.showMessageDialog(null, "Something went wrong!", "Fast Basket",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });

                    JPanel options = new JPanel();
                    options.add(deleteProdButton);
                    options.add(purchaseButton);

                    newFrame.add(options, BorderLayout.SOUTH);


                    newFrame.setSize(400, 400);
                    newFrame.setLocationRelativeTo(null);
                    newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    newFrame.setVisible(true);
                }
            }

            if (e.getSource() == customerExportButton) {
                int result = JOptionPane.showConfirmDialog(frame, "Would you like to export a file with your" +
                        "product information?", "Export Product File", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    ArrayList<Product> customerProducts = new ArrayList<>();
                    try {
                        oos.writeObject("GETCUSTOMERPRODUCTS");
                        customerProducts = (ArrayList<Product>) ois.readObject();
                    } catch (IOException | ClassNotFoundException ioe) {
                        ioe.printStackTrace();
                    }

                    File exportFile = new File("product_file.txt");
                    try (FileWriter fw = new FileWriter(exportFile);
                         BufferedWriter bw = new BufferedWriter(fw)) {
                        for (Product customerProduct : customerProducts) {
                            bw.write(customerProduct.getString() + "\n");
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(frame, "Your file has been created under the name" +
                            "\"product_file.txt\"");
                }
            }

            if (e.getSource() == historyButton) {
                try {
                    String resultString = "";
                    oos.writeObject("GETCUSTOMERPRODUCTS");
                    for (Product p : (ArrayList<Product>)ois.readObject()) {
                        resultString += p.toString() + "\n";
                    }
                    JOptionPane.showMessageDialog(null, resultString, "Fast Basket",
                            JOptionPane.INFORMATION_MESSAGE, null);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "Something went wrong!", "Fast Basket",
                            JOptionPane.ERROR_MESSAGE);
                }

            }

            if (e.getSource() == sortPriceButton) {
                ArrayList<Product> tempProducts = new ArrayList<>();
                int n = productModel.getSize();
                for (int i = 0; i < n; i++) {
                    tempProducts.add((Product) productModel.getElementAt(i));
                }

                for (int i = 0; i < n - 1; i++) {
                    int minidx = i;
                    for (int j = i + 1; j < n; j++) {
                        if (tempProducts.get(j).getPrice() < tempProducts.get(minidx).getPrice()) {
                            minidx = j;
                        }
                    }
                    Product temp = tempProducts.get(minidx);
                    tempProducts.set(minidx, tempProducts.get(i));
                    tempProducts.set(i, temp);
                }

                productModel.removeAllElements();
                for (Product tempProduct : tempProducts) {
                    productModel.addElement(tempProduct);
                }
            }

            if (e.getSource() == sortQuantityButton) {
                ArrayList<Product> tempProducts = new ArrayList<>();
                int n = productModel.getSize();
                for (int i = 0; i < n; i++) {
                    tempProducts.add((Product) productModel.getElementAt(i));
                }

                for (int i = 0; i < n - 1; i++) {
                    int minidx = i;
                    for (int j = i + 1; j < n; j++) {
                        if (tempProducts.get(j).getQuantityAvailable() <
                                tempProducts.get(minidx).getQuantityAvailable()) {
                            minidx = j;
                        }
                    }
                    Product temp = tempProducts.get(minidx);
                    tempProducts.set(minidx, tempProducts.get(i));
                    tempProducts.set(i, temp);
                }

                productModel.removeAllElements();
                for (Product tempProduct : tempProducts) {
                    productModel.addElement(tempProduct);
                }
            }

            if (e.getSource() == customerStatsButton) {
                JPanel newPanel = new JPanel();
                JTextField newtextfield = new JTextField();
                newPanel.add(newtextfield, BorderLayout.CENTER);

                try {
                    oos.writeObject("CUSTOMERSTATISTICS");
                    Object s = ois.readObject();

                    JOptionPane.showMessageDialog(null, s, "Fast Basket",
                            JOptionPane.INFORMATION_MESSAGE, null);
                    int choice = 0;
                    while (choice == 0) {
                        String[] sortOptions = {"Yes", "No"};
                        choice = JOptionPane.showOptionDialog(null, "Do you want to sort the data?",
                                "Fast Basket", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                sortOptions, sortOptions[0]);
                        if (choice == 0) {
                            String[] sort = {"Store Name", "Quantity"};
                            int options = JOptionPane.showOptionDialog(null, "Sort Options", "Fast Basket",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, sort, sort[0]);
                            oos.writeObject("SORTCUSTOMERSTATISTICS1");
                            oos.writeObject(options);
                            Object str = ois.readObject();
                            JOptionPane.showMessageDialog(null, str, "Fast Basket",
                                    JOptionPane.INFORMATION_MESSAGE, null);
                        }
                    }
                    oos.writeObject("CUSTOMERSTATISTICS2");
                    Object st = ois.readObject();
                    JOptionPane.showMessageDialog(null, st, "Fast Basket",
                            JOptionPane.INFORMATION_MESSAGE, null);
                    int choice2 = 0;
                    while (choice2 == 0) {
                        String[] sortOptions = {"Yes", "No"};
                        choice2 = JOptionPane.showOptionDialog(null, "Do you want to sort the data?",
                                "Fast Basket", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                sortOptions, sortOptions[0]);
                        if (choice2 == 0) {
                            String[] sort = {"Customer Name", "Store", "Product Name"};
                            int options = JOptionPane.showOptionDialog(null, "Sort Options", "Fast Basket",
                                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, sort, sort[0]);
                            oos.writeObject("SORTCUSTOMERSTATISTICS2");
                            oos.writeObject(options);
                            Object str = ois.readObject();
                            JOptionPane.showMessageDialog(null, str, "Fast Basket",
                                    JOptionPane.INFORMATION_MESSAGE, null);
                        }
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void run() {
        try {
            Socket s = new Socket("localhost", 4242);
            // open input stream and get header from server, then open output stream and send header
            ois = new ObjectInputStream(s.getInputStream());
            oos = new ObjectOutputStream(s.getOutputStream());
            JOptionPane.showMessageDialog(null, "Welcome to Fast Basket",
                    "Fast Basket", JOptionPane.INFORMATION_MESSAGE);
            String response = null;
            do {
                int i = JOptionPane.showConfirmDialog(null, "Do you already have a Fast Basket account?",
                        "Fast Basket", JOptionPane.YES_NO_OPTION);

                if (i == 1) {
                    JOptionPane.showMessageDialog(null, "Lets get you set up with an account!",
                            "Fast Basket", JOptionPane.INFORMATION_MESSAGE);
                    String username = "";
                    String password = "";
                    String type = "";
                    boolean formaterror = true;
                    do {
                        username = JOptionPane.showInputDialog(null, "Enter your email", "Fast Basket",
                                JOptionPane.QUESTION_MESSAGE);
                        if (username == null) {
                            oos.writeObject("EXIT");
                            return;
                        }
                        if (!username.contains("@") || !username.contains(".") ||
                                username.indexOf(".") < username.indexOf("@")) {
                            JOptionPane.showMessageDialog(null,
                                    "Your email should be in the format name@email.host, you should only have @" +
                                            " and . in your email once!",
                                    "Fast Basket", JOptionPane.INFORMATION_MESSAGE);
                            formaterror = true;
                        } else {
                            formaterror = false;
                        }
                    } while (formaterror);

                    password = JOptionPane.showInputDialog(null, "Enter your password", "Fast Basket",
                            JOptionPane.QUESTION_MESSAGE);
                    if (password == null) {
                        oos.writeObject("EXIT");
                        return;
                    }

                    String[] options = {"seller", "customer"};
                    int choice = JOptionPane.showOptionDialog(null, "What type of user do you want to be?",
                            "Fast Basket",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

                    type = choice == 0 ? "seller" : "customer";

                    //Send account creation request to server
                    oos.writeObject("NEWACCOUNT");
                    oos.writeObject(username);
                    oos.writeObject(password);
                    oos.writeObject(type);

                    response = (String)ois.readObject();

                    if (response.equals("unsuccessful")) {
                        JOptionPane.showMessageDialog(null, "That username is already taken!", "Fast Basket",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else if (i == 0) {
                    String username = "";
                    String password = "";
                    username = JOptionPane.showInputDialog(null, "Enter your email", "Fast Basket",
                            JOptionPane.QUESTION_MESSAGE);
                    if (username == null) {
                        oos.writeObject("EXIT");
                        return;
                    }
                    password = JOptionPane.showInputDialog(null, "Enter your password", "Fast Basket",
                            JOptionPane.QUESTION_MESSAGE);
                    if (password == null) {
                        oos.writeObject("EXIT");
                        return;
                    }

                    //Send login request to server
                    oos.writeObject("LOGIN");
                    oos.writeObject(username);
                    oos.writeObject(password);

                    response = (String)ois.readObject();

                    //Show error if there was no user with the matching credentials
                    if (response.equals("unsuccessful")) {
                        JOptionPane.showMessageDialog(null, "That username and password was not valid!",
                                "Fast Basket", JOptionPane.ERROR_MESSAGE);
                    }

                }
            } while (response.equals("unsuccessful"));
            //Receive the response of the user object from the server (need to cast since User now extends serializable)
            user = (User)ois.readObject();
            //Once the user has logged in, the swing gui can be set up
            if (user instanceof Customer) {
                frame = new JFrame("Fast Basket");
                Container content = frame.getContentPane();
                content.setLayout(new BorderLayout());
                client = new Client();
                content.add(client, BorderLayout.CENTER);

                refreshButton = new JButton("Refresh");
                refreshButton.addActionListener(customerActionListener);

                searchButton = new JButton("Search");
                searchButton.addActionListener(customerActionListener);

                allButton = new JButton("All Products");
                allButton.addActionListener(customerActionListener);

                searchBar = new JTextField(15);

                shoppingCart = new JButton("View Shopping Cart");
                shoppingCart.addActionListener(customerActionListener);

                customerExportButton = new JButton("Export Product File");
                customerExportButton.addActionListener(customerActionListener);

                historyButton = new JButton("View Purchase History");
                historyButton.addActionListener(customerActionListener);

                JPanel topPanel = new JPanel();
                topPanel.add(refreshButton);
                topPanel.add(searchBar);
                topPanel.add(allButton);
                topPanel.add(searchButton);

                JPanel productPanel = new JPanel();
                productModel = new DefaultListModel();
                productListing = new JList(productModel);
                productListing.setVisibleRowCount(10);
                productListing.setFixedCellHeight(20);
                productListing.setFixedCellWidth(500);
                productListing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane list1 = new JScrollPane(productListing);

                selectButton = new JButton("Select");
                selectButton.addActionListener(customerActionListener);

                sortPriceButton = new JButton("Sort Price");
                sortPriceButton.addActionListener(customerActionListener);

                sortQuantityButton = new JButton("Sort Quantity");
                sortQuantityButton.addActionListener(customerActionListener);

                customerStatsButton = new JButton("View statistics");
                customerStatsButton.addActionListener(customerActionListener);

                productPanel.add(list1);
                productPanel.add(selectButton);
                productPanel.add(sortPriceButton);
                productPanel.add(sortQuantityButton);
                productPanel.add(customerStatsButton);

                JPanel bottomPanel = new JPanel();
                bottomPanel.add(shoppingCart);
                bottomPanel.add(customerExportButton);
                bottomPanel.add(historyButton);

                content.add(topPanel, BorderLayout.NORTH);
                content.add(productPanel, BorderLayout.CENTER);
                content.add(bottomPanel, BorderLayout.SOUTH);
            } else if (user instanceof Seller) {
                frame = new JFrame("Fast Basket");
                Container content = frame.getContentPane();
                content.setLayout(new BorderLayout());
                client = new Client();
                content.add(client, BorderLayout.CENTER);

                refreshButton = new JButton("Refresh");
                refreshButton.addActionListener(sellerActionListener);

                searchButton = new JButton("Search");
                searchButton.addActionListener(sellerActionListener);
                searchBar = new JTextField(15);

                JPanel topPanel = new JPanel();
                topPanel.add(refreshButton);
                topPanel.add(searchBar);
                topPanel.add(searchButton);

                JPanel productPanel = new JPanel();
                productModel = new DefaultListModel();
                productListing = new JList(productModel);
                productListing.setVisibleRowCount(10);
                productListing.setFixedCellHeight(20);
                productListing.setFixedCellWidth(500);
                productListing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                JScrollPane list1 = new JScrollPane(productListing);
                selectButton = new JButton("Select");
                selectButton.addActionListener(sellerActionListener);
                productPanel.add(list1);
                productPanel.add(selectButton);

                JPanel bottomPanel = new JPanel();

                addButton = new JButton("Add product");
                addButton.addActionListener(sellerActionListener);

                addStoreButton = new JButton("Add store");
                addStoreButton.addActionListener(sellerActionListener);

                stats = new JButton("View Statistics");
                stats.addActionListener(sellerActionListener);

                sellerExportButton = new JButton("Export Product File");
                sellerExportButton.addActionListener(sellerActionListener);

                sellerImportButton = new JButton("Import Product File");
                sellerImportButton.addActionListener(sellerActionListener);

                salesButton = new JButton("View Sales");
                salesButton.addActionListener(sellerActionListener);

                cartButton = new JButton("Cart Info");
                cartButton.addActionListener(sellerActionListener);

                bottomPanel.add(addButton);
                bottomPanel.add(addStoreButton);
                bottomPanel.add(salesButton);
                bottomPanel.add(cartButton);
                bottomPanel.add(stats);

                productPanel.add(sellerExportButton);
                productPanel.add(sellerImportButton);

                content.add(bottomPanel, BorderLayout.SOUTH);
                content.add(topPanel, BorderLayout.NORTH);
                content.add(productPanel, BorderLayout.CENTER);
            }

            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection failed",
                    "Fast Basket", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        Client.ShutDownTask shutDownTask = new Client.ShutDownTask();
        Runtime.getRuntime().addShutdownHook(shutDownTask);
        SwingUtilities.invokeLater(new Client());
    }

    /**
     * ShutDownTask Class
     * This class creates a thread that runs when the Client class is shut down, which writes "EXIT" to the server
     *
     * @author Charlie Schmidt, lab sec 002
     * @version 12/12/2022
     */

    private static class ShutDownTask extends Thread {
        @Override
        public void run() {
            try {
                oos.writeObject("EXIT");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
