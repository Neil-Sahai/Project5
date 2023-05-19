# Project05
## Instructions:
- This program must be run locally, not in Vocareum.
- To run the project, first run the Server class. Then, run as many instances of the Client class as you want. If you are using an IDE such as Intellij, you will need to change the run configuration of the Client class to allow multiple instances.
- To close the Client, just press the X on the main window. To close the Server, you can either ^C in the terminal or press the stop button in the IDE.

## Submissions:
- Zach - Submitted presentation in Brightspace
- Neil - Submitted report in Brightspace
- Charlie - Submitted git repo to Vocareum

## Class Info:
- Server Class: Handles threading for each Client connection, and takes input from the Client and processes it accordingly. The data for all the users is stored in this class as well.
- Client Class: Displays the GUI to the user and handles all user input and most of the input verification. This class writes data to the server and receives the result to display to the user.
- Seller Class: Contains all the information tied to each seller, as well as methods to access, edit, and delete that associated information.
- Customer Class: Contains all the information tied to each customer, as well as methods to access, edit, and delete that associated information.
- Product Class: Contains all the information tied to each Product, and has methods to access and edit that information. The constructor for this class also handles bad input; it throws exceptions with specific messages if the input isn't valid.