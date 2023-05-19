### Tests for Login:  

Test 0
1. Open the client without starting the server.

Expected result: this should get you an error GUI message that says connection failed.

Test 1
1. Start the server before the client this time and it should welcome you to Fast Basket
2. You should be asked if you already have a fast basket account, select no
3. Accept the message and then enter a poorly formatted email like “ivan”
4. Read the error message and then enter a properly formatted email like “ivan@gmail.com”
5. Enter a password
6. Select either a seller or customer account  

Expected result: this should get you to either the seller or customer GUI.

Test 2
1. Start the server before the client this time and it should welcome you to fast basket
2. You should be asked if you already have a fast basket account, select yes
3. Enter account details for an account that doesn’t exist
4. It should navigate you back to the page to select whether you want to log in or make a new account
5. Select yes
6. Log in with the account you made previously  

Expected result: this should get you to either the seller or customer GUI.

### Once User is logged in as a seller:

Test 3: Add Store  
1. User clicks “Add Store” button
2. User inputs store name “mystore” and clicks OK  

Expected Result: message is displayed that says “Store was successfully added”  
Test Status: Passed

Test 4: Add Store Again
1. User clicks “Add Store” button again
2. User inputs store name “mystore” and clicks OK  

Expected Result: message is displayed that says “That store already exists!’  
Test Status: Passed

Test 5: Add Product
1. User clicks “Add product” button
2. User inputs the following information in text boxes: “newprod, mystore, d, 10, 12” and clicks OK  

Expected Result: message is displayed that says “Product was added successfully”  
Test Status: Passed

Test 6: Refresh Button
1. User clicks “Refresh” button  

Expected Result: The newly added product information appears in the main menu  
Test Status: Passed

Test 7: Add Product (Invalid input)
1. User clicks “Add product” button
2. User inputs product info but leaves a box blank and clicks OK  

Expected Result: Error message is displayed saying “Please enter values in the correct format”  
Test Status: Passed

Test 8: Add Product (Invalid input cont.)
1. User clicks “Add product” button
2. User inputs product info but gives a negative value for quantity and clicks OK  

Expected Result: Error message is displayed saying “The quantity cannot be less than 0!”  
Test Status: Passed

Test 9: Search Button
1. User adds two different products using the instructions from Test 5 with different input
2. User clicks Refresh
3. User types the name of the new product in the search bar and presses Search  

Expected Result: The new product is displayed in the main menu alone as the only search result (User can then press refresh to see all products)  
Test Status: Passed

Test 10: Select Button
1. User clicks on a product in the menu and presses the Select button  

Expected Result: The full information for the product is displayed in a popup window  
Test Status: Passed

Test 11: Delete Product
1. After Test 10, the user presses the Delete button
2. User presses OK to the new window and presses Refresh  

Expected Result: A window is displayed saying “Product was successfully removed” after Step 1, and the product disappears from the menu after Step 2  
Test Status: Passed

Test 12: Edit Product
1. User clicks on the remaining product and presses Select
2. User presses Edit
3. User gives new input for the product, with no blank fields or negative values
4. User presses OK and closes the new window that pops up
5. User presses Refresh

Expected Result: The product info is updated in the main menu  
Test Status: Passed

Test 13: Import Product File
1. In the same directory as the program, the user creates a new product file following the conventions displayed after pressing the Import Product File button
2. User creates two lines for valid products, and one line with a store the user doesn’t own or negative values
3. User presses the Import Product File button, inputs the name of the file and presses OK to all popup windows
4. User presses Refresh

Expected Result: After inputting the file name in Step 3, a message is displayed saying “2 of the 3 products were formatted correctly and added” (the invalid line was not added). After Step 4, the two products are visible in the main menu.  
Test Status: Passed

Test 14: View Statistics
1. User clicks on “View Statistics” button

Expected Result: A table with the seller name, store name and quantity and the respective data  
Test Status: Passed

1. User gets an option whether they want to sort the data.
2. If the user presses the Yes button, they get option to sort the data based on different columns

Expected Result: A table with the sorted data based on the option selected by the user.  

1. If the user presses the No button, it displays the next dashboard with product name and quantity and the respective data
2. User gets an option whether they want to sort the data.
3. If the user presses the Yes button, they get option to sort the data based on different columns

Expected Result: A table with the sorted data based on the option selected by the user.

### Tests for Concurrency:
Run a new instance of client with the seller menu open
This time, user logs in as a customer to display the customer menu, repeating the login process from above

### Tests for Customer GUI

Test 1: Refresh
1. User clicks the refresh button

Expected Result: A list of available items should appear  
Test Status: Passed

Test 2: Viewing Items (Invalid input)
1. User presses the “Select” button without first choosing an item

Expected Result: An pop-up saying “Click on a product first!”  
Test Status: Passed

Test 2: Viewing Items (Valid input)
1. User chooses an item displayed in the menu and it appears highlighted blue
2. User presses the “Select” button

Expected Result: A pop-up should appear with more detailed information  
Test Status: Passed

Test 3: Searching items (Invalid input)
1. User leaves search bar empty (or removes what is there)
2. User presses “Search”

Expected Result: A pop-up should appear that says “Please enter a valid search query!”  
Test Status: Passed

Test 3: Searching items (Valid input, no results)
1. User enters “product query” into the search bar
2. User presses “Search”

Expected Result: A pop-up should appear that says “No products match that query!”  
Test Status: Passed

Test 4: Sorting items (by price)
1. User presses “All products” to view every product on the marketplace
2. User presses “Sort price”

Expected Result: The products should now be listed in price order  
Test Status: Passed

Test 4: Sorting items (by quantity)
1. User presses “Sort quantity”

Expected Result: The products should now be listed in quantity order  
Test Status: Passed

Test 5: Adding items to shopping cart
1. User selects an item from the menu
2. After pressing "Select", the user presses “Add to cart”
3. User then clicks “View shopping cart”

Expected Result: A new window with the item that was added to the shopping cart  
Test Status: Passed

Test 6: Deleting items from shopping cart (One item)
1. The User selects the product from the shopping cart list and presses delete product
2. Shopping cart window closes
3. User presses “View shopping cart”

Expected Result: A pop-up indicating the user has no items in cart  
Test Status: Passed

Test 7: Deleting items from shopping cart (Multiple items)
1. User adds a product twice to shopping cart using methods from Test 3
2. User presses “View shopping cart” and selects one item
3. User presses “Delete product”

Expected Result: There is one item remaining in the shopping cart  
Test Status: Passed

Test 8: Purchasing items from shopping cart and purchase history
1. Inside the shopping cart menu the user presses purchase all
2. User is taken back to the main menu
3. User presses view purchase history

Expected Result: The purchased product appears in purchase history  
Test Status: Passed

Test 9: Refresh button and concurrency (Customer side)
1. User presses “Refresh”
2. User selects the item they recently purchased in Test 8

Expected Result: The quantity decreased by the amount the user purchased  
Test Status: Passed

Test 10:  Refresh button and concurrency (Seller side)
1. User goes back to the seller menu from the seller tests
2. User presses “Refresh”

Expected Result: The recently purchased product's quantity should decrease by one (shown after selecting the product)  
Test Status: Passed

Test 11: Export product file
1. User goes back to the customer GUI
2. User presses “Export Product File”
3. User presses ok (if not, no file should be created)

Expected Result: A file called “product_file.txt” should be created that contains the users purchase history in the CSV format  
Test Status: Passed

Test 12: View Statistics
1. User clicks on “View Statistics” button

Expected Result: A table with the seller name, store name and quantity and the respective data  
Test Status: Passed

1. User gets an option whether they want to sort the data.
2. If the user presses the Yes button, they get option to sort the data based on different columns

Expected Result: A table with the sorted data based on the option selected by the user.  
Test Status: Passed

1. If the user presses the No button, it displays the next dashboard with customer name, store, and product name and the respective data
2. User gets an option whether they want to sort the data.
3. If the user presses the Yes button, they get option to sort the data based on different columns

Expected Result: A table with the sorted data based on the option selected by the user.  
Test Status: Passed


