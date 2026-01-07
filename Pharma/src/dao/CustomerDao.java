package dao;

import util.DBConnection;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static dao.Admin.sc;

public class CustomerDao extends definedRole{

    static definedRole customer=new CustomerDao();

    public static void registerUser(Scanner sc) {
        long phone = 0;
        String password;
        String name;
        String birthCity;

        // Name input
        while (true) {
            System.out.print("Enter your name (letters only): ");
            name = sc.nextLine();

            if (!name.matches("^[A-Za-z ]+$")) {
                System.out.println("Name must contain letters only.");
                continue;
            }
            break;
        }

        // Birth city input
        while (true) {
            System.out.print("Enter your birth city (letters only): ");
            birthCity = sc.nextLine();

            if (!birthCity.matches("^[A-Za-z ]+$")) {
                System.out.println("Birth city must contain letters only.");
                continue;
            }
            break;
        }

        // Phone number input
        while (true) {
            try {
                System.out.print("Enter phone number (10 digits): ");
                String phoneInput = sc.nextLine();

                if (!phoneInput.matches("\\d{10}")) {
                    System.out.println("Invalid phone number.");
                    continue;
                }

                phone = Long.parseLong(phoneInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Please enter numbers only for phone.");
            }
        }

        // Password input
        while (true) {
            System.out.print("Enter password: ");
            password = sc.nextLine();

            if (password.length() < 8) {
                System.out.println("Password must be at least 8 characters long.");
                continue;
            }

            String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$";

            if (!password.matches(regex)) {
                System.out.println("Password must contain at least one letter, one digit, and one special character.");
                continue;
            }

            break;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try {
                Connection conn = DBConnection.getConnection();

                // Check if phone number exists
                String checkPhoneSQL = "SELECT * FROM customer WHERE cust_mobile = ?";
                PreparedStatement checkPhoneStmt = conn.prepareStatement(checkPhoneSQL);
                checkPhoneStmt.setLong(1, phone);
                ResultSet rs = checkPhoneStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("Phone number already exists.");
                    return;
                }

                // Check if password exists
                String checkPasswordSQL = "SELECT * FROM customer WHERE cust_passwd = ?";
                PreparedStatement checkPasswordStmt = conn.prepareStatement(checkPasswordSQL);
                checkPasswordStmt.setString(1, password);
                ResultSet rs1 = checkPasswordStmt.executeQuery();
                if (rs1.next()) {
                    System.out.println("This password is already in use. Please choose a different password.");
                    return;
                }

                // Insert new user with birth city
                String insertSQL = "INSERT INTO customer (cust_name, cust_mobile, cust_passwd, cust_bcity) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
                insertStmt.setString(1, name);
                insertStmt.setLong(2, phone);
                insertStmt.setString(3, password);
                insertStmt.setString(4, birthCity);
                insertStmt.executeUpdate();
                System.out.println("User registered successfully.");

            } catch (SQLIntegrityConstraintViolationException e) {
                System.out.println("Data already exists in the system (duplicate key).");
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during registration.");
            e.printStackTrace();
        }
    }


    public static Long loggedInPhoneNumber = null;

    public static void loginUser(Scanner sc) {
        loggedInPhoneNumber = null;
        String phoneInput;

        try {
            // Validate phone number input
            while (true) {
                System.out.print("Enter phone number: ");
                phoneInput = sc.nextLine();

                if (phoneInput.matches("\\d{10}")) {
                    break;
                } else {
                    System.out.println("Invalid phone number.");
                }
            }

            long phone = Long.parseLong(phoneInput);

            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM customer WHERE cust_mobile = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("cust_passwd");
                String securityAnswer = rs.getString("cust_bcity");
                int attempts = 0;
                boolean isAuthenticated = false;

                while (attempts < 3) {
                    System.out.print("Enter password: ");
                    String passwordInput = sc.nextLine();

                    if (storedPassword.equals(passwordInput)) {
                        loggedInPhoneNumber = phone;
                        System.out.println("Login successful! Welcome, " + rs.getString("cust_name"));
                        isAuthenticated = true;
                        custMenu(sc); // proceed to customer menu
                        break;
                    } else {
                        attempts++;
                        if (attempts < 3) {
                            System.out.println("Incorrect password. Try again " + (3 - attempts) + " attempts left).");
                        }
                    }
                }

                if (!isAuthenticated) {
                    System.out.println("Login failed.");
                    System.out.print("Would you like to reset your password? (yes/no): ");
                    String resetChoice = sc.nextLine().trim().toLowerCase();

                    if (resetChoice.equals("yes")) {
                        System.out.print("Security Question - What is your Birth city? ");
                        String cityInput = sc.nextLine().trim();

                        if (securityAnswer != null && securityAnswer.equalsIgnoreCase(cityInput)) {
                            // Prompt for new password with inline regex check
                            while (true) {
                                System.out.print("Enter new password (min 8 characters, must include letter, digit, special char): ");
                                String newPassword = sc.nextLine();

                                // Inline regex: at least 8 chars, one letter, one digit, one special character
                                String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$";

                                if (newPassword.matches(regex)) {
                                    // Update password in database
                                    String updateSQL = "UPDATE customer SET cust_passwd = ? WHERE cust_mobile = ?";
                                    PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                                    updateStmt.setString(1, newPassword);
                                    updateStmt.setLong(2, phone);
                                    int updated = updateStmt.executeUpdate();

                                    if (updated > 0) {
                                        System.out.println("Password updated successfully. You can now log in with the new password.");
                                    } else {
                                        System.out.println("Failed to update password.");
                                    }
                                    break;
                                } else {
                                    System.out.println("Password must be at least 8 characters long and contain at least one letter, one digit, and one special character.");
                                }
                            }
                        } else {
                            System.out.println("Security answer incorrect. Cannot reset password.");
                        }
                    }
                }

            } else {
                System.out.println("Login failed. Phone number not found.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid phone number format.");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred during login.");
            e.printStackTrace();
        }
    }


    //achieving abstraction and method overriding
    public void viewAllProduct() throws SQLException {
        Connection conn = DBConnection.getConnection();

        customLinkedList cll=new customLinkedList();
        CallableStatement cs = conn.prepareCall("{call viewAllProducts()}");
        ResultSet rs = cs.executeQuery();

        while (rs.next()) {
            Product p = new Product(
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));

            cll.add(p);
        }

        System.out.println("Products:");
        cll.sortedByName();
    }


    public static void viewProductsByType()
    {
        try {
            Connection conn = DBConnection.getConnection();

            customLinkedList cll = new customLinkedList();
            CallableStatement cs = conn.prepareCall("{call viewAllProducts()}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("prod_code"),
                        rs.getString("prod_name"),
                        rs.getDouble("prod_price"),
                        rs.getInt("prod_quantity"),
                        rs.getString("prod_usage"),
                        rs.getDate("prod_expiry").toLocalDate(),
                        rs.getString("prod_manufacturer"),
                        rs.getString("prod_type"));

                cll.add(p);
            }

            // Loop until correct input is given
            String type = "";
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.println("\nSelect Product Usage Type:");
                    System.out.println("1. Generic");
                    System.out.println("2. Non Generic");
                    System.out.println("3. Exit");
                    System.out.print("Enter your choice: ");

                    int choice = Integer.parseInt(sc.nextLine().trim());  // Using nextLine + parse to handle non-integer input

                    switch (choice) {
                        case 1:
                            type = "Generic";
                            validInput = true;
                            break;

                        case 2:
                            type = "Non Generic";
                            validInput = true;
                            break;

                        case 3:
                            System.out.println("Exiting..");
                            return;

                        default:
                            System.out.println("Invalid choice. Please select between 1 and 3.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 3.");
                }
            }

            cll.productsByType(type);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void viewProductsByManufacturer() throws SQLException
    {
        Connection conn = DBConnection.getConnection();

        customLinkedList cll=new customLinkedList();
        CallableStatement cs = conn.prepareCall("{call viewAllProducts()}");
        ResultSet rs = cs.executeQuery();

        while (rs.next()) {
            Product p = new Product(
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));

            cll.add(p);
        }
        System.out.print("Enter Manufacturer name: ");
        cll.productsByManufacturer(sc.nextLine());
    }

    public static void viewProductsByUsage(Scanner sc)
    {
        try {
            Connection conn = DBConnection.getConnection();

            customLinkedList cll = new customLinkedList();
            CallableStatement cs = conn.prepareCall("{call viewAllProducts()}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("prod_code"),
                        rs.getString("prod_name"),
                        rs.getDouble("prod_price"),
                        rs.getInt("prod_quantity"),
                        rs.getString("prod_usage"),
                        rs.getDate("prod_expiry").toLocalDate(),
                        rs.getString("prod_manufacturer"),
                        rs.getString("prod_type"));

                cll.add(p);
            }

            // Loop until correct input is given
            String usage = "";
            boolean validInput = false;

            while (!validInput) {
                try {
                    System.out.println("\nSelect Product Usage Type:");
                    System.out.println("1. Medical");
                    System.out.println("2. Cosmetic");
                    System.out.println("3. Nutrition");
                    System.out.println("4. Household");
                    System.out.println("5. Baby Care");
                    System.out.println("6. Assistive Devices");
                    System.out.println("7. Exit");
                    System.out.print("Enter your choice (1-7): ");

                    int choice = Integer.parseInt(sc.nextLine().trim());

                    switch (choice) {
                        case 1:
                            usage = "Medical";
                            validInput = true;
                            break;

                        case 2:
                            usage = "Cosmetic";
                            validInput = true;
                            break;

                        case 3:
                            usage = "Nutrition";
                            validInput = true;
                            break;

                        case 4:
                            usage = "Household";
                            validInput = true;
                            break;

                        case 5:
                            usage = "Baby Care";
                            validInput = true;
                            break;

                        case 6:
                            usage = "Assistive Devices";
                            validInput = true;
                            break;

                        case 7:
                            System.out.println("Exiting..");
                            return;

                        default:
                            System.out.println("Invalid choice. Please select between 1 and 7.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number between 1 and 7.");
                }
            }

            cll.productsByUsage(usage);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //achieving abstraction and method overriding
    public void viewUserProfile() throws SQLException {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM customer WHERE cust_mobile = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, loggedInPhoneNumber);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                System.out.println("\n========= Your Profile =========");
                System.out.printf("%-10s %-20s %-15s %-15s %-15s %-10s %-15s%n",
                        "Code", "Name", "Mobile", "Password", "Reward Points", "Orders", "Birth City");


                System.out.println("----------------------------------------------------------------------------------------------------------");

                System.out.printf("%-10d %-20s %-15d %-15s %-15.2f %-10d %-15s%n",
                        rs.getInt("cust_code"),
                        rs.getString("cust_name"),
                        rs.getLong("cust_mobile"),
                        rs.getString("cust_passwd"),
                        rs.getDouble("cust_rewardpoint"),
                        rs.getInt("cust_order"),
                        rs.getString("cust_bcity"));
            } else {
                System.out.println("User profile not found.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while fetching profile: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static customLinkedList fetchSearchedProducts(String keyword) {
        customLinkedList filteredList = new customLinkedList();

        String query = "SELECT * FROM products WHERE LOWER(prod_name) LIKE ?";
        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, "%" + keyword.toLowerCase() + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("prod_code"),
                        rs.getString("prod_name"),
                        rs.getDouble("prod_price"),
                        rs.getInt("prod_quantity"),
                        rs.getString("prod_usage"),
                        rs.getDate("prod_expiry").toLocalDate(),
                        rs.getString("prod_manufacturer"),
                        rs.getString("prod_type")
                );
                filteredList.add(p);
            }

        } catch (SQLException e) {
            System.out.println("DB Error while filtering products: " + e.getMessage());
        }

        return filteredList;
    }


    public static void searchProduct() throws SQLException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter product name or letter to search (or type 'exit' to cancel): ");
        String keyword = sc.nextLine().trim();

        // Allow user to exit search early
        if (keyword.equalsIgnoreCase("exit")) {
            System.out.println("Search cancelled.");
            return;
        }

        // Fetch filtered products directly from DB
        customLinkedList filteredList = fetchSearchedProducts(keyword);
        if (filteredList.isEmpty()) {
            System.out.println("No matching products found.");
            return;
        }

        // Show filtered products
        System.out.println("\nMatching Products:");
        filteredList.printTableFormat();

        // Prompt user to enter product codes
        while (true) {
            System.out.print("\nEnter product codes to add to cart (comma-separated, or type 'exit' to cancel): ");
            String inputLine = sc.nextLine().trim();

            if (inputLine.equalsIgnoreCase("exit")) {
                System.out.println("Add to cart cancelled.");
                return;
            }

            String[] codeInputs = inputLine.split(",");
            boolean allValid = true;

            // Validate all inputs first
            for (String codeStr : codeInputs) {
                codeStr = codeStr.trim();
                if (!codeStr.matches("\\d+")) {
                    System.out.println("Invalid product code. Enter only numbers.");
                    allValid = false;
                    break;
                }
            }

            if (!allValid) {
                continue; // re-prompt the user for product codes
            }

            Connection conn = DBConnection.getConnection();
            for (String codeStr : codeInputs) {
                int prodCode = Integer.parseInt(codeStr.trim());

                Product selectedProduct = filteredList.getProductByCode(prodCode);
                if (selectedProduct == null) {
                    System.out.println("Invalid product code: " + prodCode);
                    continue;
                }

                // Check if product is already in the cart
                String checkSQL = "SELECT COUNT(*) FROM cart WHERE prod_code = ? AND cust_mobile = ?";
                try{
                    PreparedStatement checkStmt = conn.prepareStatement(checkSQL);
                    checkStmt.setInt(1, selectedProduct.prodCode);
                    checkStmt.setLong(2, loggedInPhoneNumber);

                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Product already in cart: " + selectedProduct.prodName);
                        continue;
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                // Insert into cart
                String insertSQL = "INSERT INTO cart (prod_code, prod_name, prod_price, cust_mobile) VALUES (?, ?, ?, ?)";
                try{
                    PreparedStatement stmt = conn.prepareStatement(insertSQL);
                    stmt.setInt(1, selectedProduct.prodCode);
                    stmt.setString(2, selectedProduct.prodName);
                    stmt.setDouble(3, selectedProduct.prodPrice);
                    stmt.setLong(4, loggedInPhoneNumber);
                    stmt.executeUpdate();

                    System.out.println("Added to cart: " + selectedProduct.prodName);
                } catch (SQLException e) {
                    System.out.println("DB Error while adding product: " + e.getMessage());
                }
            }

            break; // exit the loop after successful add
        }
    }


    public static void viewMyBills() throws SQLException {

        Connection conn = DBConnection.getConnection();

        String select="select cust_name from customer where cust_mobile=?";
        PreparedStatement pst= conn.prepareStatement(select);
        pst.setLong(1,loggedInPhoneNumber);
        ResultSet rs = pst.executeQuery();
        String name=null;
        while (rs.next()) {
            name= rs.getString("cust_name");
        }

        String fileName = "bill_customer.txt";

        String targetCustomer = name.trim().toLowerCase();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            boolean isCapturing = false;
            boolean isMatchingCustomer = false;
            StringBuilder currentBill = new StringBuilder();
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                // Detect start of a new bill
                if (line.startsWith("======")) {
                    if (isCapturing && isMatchingCustomer) {
                        // Print the matched bill
                        System.out.println(currentBill.toString());
                        found = true;
                    }
                    // Reset for next bill
                    currentBill.setLength(0);
                    isCapturing = true;
                    isMatchingCustomer = false;
                }

                if (isCapturing) {
                    currentBill.append(line).append("\n");

                    // Check for customer name line
                    if (line.toLowerCase().startsWith("customer name: ")) {
                        String customerName = line.substring(15).trim().toLowerCase();
                        if (customerName.equals(targetCustomer)) {
                            isMatchingCustomer = true;
                        }
                    }
                }
            }

            // Handle last bill
            if (isCapturing && isMatchingCustomer) {
                System.out.println(currentBill.toString());
                found = true;
            }

            if (!found) {
                System.out.println("No bills found for customer: " + targetCustomer);
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public static void viewCartAndPurchase() {
        ArrayList<Product> cartProducts = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            String cartQuery = "SELECT prod_code, prod_name, prod_price FROM cart WHERE cust_mobile = ?";
            try{
                PreparedStatement ps = con.prepareStatement(cartQuery);
                ps.setLong(1, loggedInPhoneNumber);
                try{
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int code = rs.getInt("prod_code");
                        String name = rs.getString("prod_name");
                        double price = rs.getDouble("prod_price");

                        cartProducts.add(new Product(code, name, price, 0, "", null, "", ""));
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            if (cartProducts.isEmpty()) {
                System.out.println("Your cart is empty.");
                return;
            }

            System.out.println("\nYour Cart:");
            for (Product p : cartProducts) {
                System.out.printf("Code: %d, Name: %s, Price: %.2f\n", p.prodCode, p.prodName, p.prodPrice);
            }

            List<Product> selectedProducts = new ArrayList<>();
            double subtotal = 0;

            while (true) {
                System.out.print("\nEnter product codes to purchase (comma-separated or type 'exit' to cancel): ");
                String codeLine = sc.nextLine().trim();

                if (codeLine.equalsIgnoreCase("exit")) {
                    System.out.println("Purchase cancelled by user.");
                    return;
                }

                String[] selectedCodesStr = codeLine.split(",");
                boolean allValid = true;
                selectedProducts.clear();  // Reset on re-entry
                subtotal = 0;

                for (String codeStr : selectedCodesStr) {
                    int code;
                    try {
                        code = Integer.parseInt(codeStr.trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid product code. Enter integer only");
                        allValid = false;
                        break;
                    }

                    Product selectedProduct = null;
                    for (Product p : cartProducts) {
                        if (p.prodCode == code) {
                            selectedProduct = p;
                            break;
                        }
                    }

                    if (selectedProduct == null) {
                        System.out.println("Product code not found in cart: " + code);
                        allValid = false;
                        break;
                    }

                    int qty = 0;
                    while (true) {
                        System.out.print("Enter quantity for " + selectedProduct.prodName + " (or type 'exit' to cancel): ");
                        String qtyStr = sc.nextLine().trim();

                        if (qtyStr.equalsIgnoreCase("exit")) {
                            System.out.println("Purchase cancelled by user.");
                            return;
                        }

                        try {
                            qty = Integer.parseInt(qtyStr);
                            if (qty <= 0) {
                                System.out.println("Quantity must be positive.");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Enter a valid number.");
                        }
                    }

                    int availableQty = 0;
                    String stockQuery = "SELECT prod_quantity FROM products WHERE prod_code = ?";
                    try {
                        PreparedStatement ps = con.prepareStatement(stockQuery);
                        ps.setInt(1, selectedProduct.prodCode);
                        try{
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                availableQty = rs.getInt("prod_quantity");
                            } else {
                                System.out.println("Product not found in products table.");
                                allValid = false;
                                break;
                            }
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }

                    if (qty > availableQty) {
                        System.out.println("Insufficient stock. Available only:" + availableQty);
                        allValid = false;
                        break;
                    }

                    selectedProduct.prodQuantity = qty;
                    selectedProducts.add(selectedProduct);
                    subtotal += selectedProduct.prodPrice * qty;
                }

                if (allValid) break;
            }


            double tax = subtotal * 0.18;
            double grandTotal = subtotal + tax;

            String custName = "";
            double custReward = 0;
            int custOrder = 0;

            try{
                PreparedStatement ps = con.prepareStatement("SELECT cust_name, cust_rewardpoint, cust_order FROM customer WHERE cust_mobile = ?");
                ps.setLong(1, loggedInPhoneNumber);
                try{
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        custName = rs.getString("cust_name");
                        custReward = rs.getDouble("cust_rewardpoint");
                        custOrder = rs.getInt("cust_order");
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            double discount = 0;
            if (custOrder == 0 || custOrder == 1) {
                discount = grandTotal * 0.10;
            } else if (custOrder >= 10) {
                discount = grandTotal * 0.07;
            }

            grandTotal -= discount;

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Print bill
            System.out.println("\n===== Pharmacy Store =====");
            System.out.println("Customer: " + custName);
            System.out.println("Mobile: " + loggedInPhoneNumber);
            System.out.println("Date: " + now.format(formatter));
            System.out.printf("%-10s %-20s %-10s %-10s\n", "Code", "Name", "Qty", "Price");

            for (Product p : selectedProducts) {
                System.out.printf("%-10d %-20s %-10d %-10.2f\n", p.prodCode, p.prodName, p.prodQuantity, p.prodPrice * p.prodQuantity);
            }

            System.out.println("-----------------------------------");
            System.out.printf("Subtotal: %.2f\n", subtotal);
            System.out.printf("Tax (18%%): %.2f\n", tax);
            System.out.printf("Discount: %.2f\n", discount);
            System.out.printf("Grand Total: %.2f\n", grandTotal);

            // Payment
            System.out.println("\nChoose Payment Method:\n1. UPI\n2. Reward Points\n3. Cancel");
            int choice = 0;
            while (true) {
                System.out.print("Enter choice: ");
                try {
                    choice = Integer.parseInt(sc.nextLine());
                    if (choice >= 1 && choice <= 3) break;
                    else System.out.println("Invalid choice.");
                } catch (NumberFormatException e) {
                    System.out.println("Enter valid number.");
                }
            }

            if (choice == 3) {
                System.out.println("Transaction cancelled.");
                return;
            }

            String payType = (choice == 1) ? "UPI" : "Reward Points";

            if (choice == 2 && custReward < grandTotal) {
                System.out.println("Insufficient reward points.");
                return;
            }

            // Generation of otp
            Random random = new Random();
            int otp = 100000 + random.nextInt(900000); // 6-digit OTP
            System.out.println("\nYour OTP is: " + otp);

            System.out.print("Enter the OTP to confirm payment: ");
            int enteredOtp;
            try {
                enteredOtp = sc.nextInt();
                sc.nextLine();
                if (enteredOtp != otp) {
                    System.out.println("Incorrect OTP. Payment failed.");
                    return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Incorrect OTP. Payment failed.");
                sc.nextLine();
                return;
            }

            if (choice == 2) {
                try{
                    PreparedStatement ps = con.prepareStatement("UPDATE customer SET cust_rewardpoint = cust_rewardpoint - ? WHERE cust_mobile = ?");
                    ps.setDouble(1, grandTotal);
                    ps.setLong(2, loggedInPhoneNumber);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }

            try {
                con.setAutoCommit(false);

                int saleId = 0;
                try{
                    PreparedStatement ps = con.prepareStatement("INSERT INTO sales (cust_name, cust_mobile, sale_date, total, pay_type) VALUES (?, ?, ?, ?, ?)");
                    ps.setString(1, custName);
                    ps.setLong(2, loggedInPhoneNumber);
                    ps.setDate(3, Date.valueOf(now.toLocalDate()));
                    ps.setDouble(4, grandTotal);
                    ps.setString(5, payType);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }


                try{
                    PreparedStatement ps = con.prepareStatement("SELECT MAX(sale_code) AS sale_id FROM sales WHERE cust_mobile = ?");
                    ps.setLong(1, loggedInPhoneNumber);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            saleId = rs.getInt("sale_id");
                        }
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                try{
                    PreparedStatement ps = con.prepareStatement("INSERT INTO saleitems (sale_code, prod_code, prod_name, prod_quantity, prod_price) VALUES (?, ?, ?, ?, ?)");
                    for (Product p : selectedProducts) {
                        ps.setInt(1, saleId);
                        ps.setInt(2, p.prodCode);
                        ps.setString(3, p.prodName);
                        ps.setInt(4, p.prodQuantity);
                        ps.setDouble(5, p.prodPrice);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                try{
                    PreparedStatement ps = con.prepareStatement("UPDATE products SET prod_quantity = prod_quantity - ? WHERE prod_code = ?");
                    for (Product p : selectedProducts) {
                        ps.setInt(1, p.prodQuantity);
                        ps.setInt(2, p.prodCode);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                try{
                    PreparedStatement ps = con.prepareStatement("DELETE FROM cart WHERE cust_mobile = ? AND prod_code = ?");
                    for (Product p : selectedProducts) {
                        ps.setLong(1, loggedInPhoneNumber);
                        ps.setInt(2, p.prodCode);
                        ps.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                // Update rewards
                double rewardEarned = (grandTotal > 200) ? grandTotal * 0.05 : 0;
                try{
                    PreparedStatement ps = con.prepareStatement("UPDATE customer SET cust_order = cust_order + 1, cust_rewardpoint = cust_rewardpoint + ? WHERE cust_mobile = ?");
                    ps.setDouble(1, rewardEarned);
                    ps.setLong(2, loggedInPhoneNumber);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                con.commit();
                System.out.printf("\nPayment successful via %s. Reward Earned: %.2f\n", payType, rewardEarned);

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error during transaction: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }

            // Write to file
            String fileName = "bill_customer.txt";
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
                bw.write("====== Pharmacy Store ======\n");
                bw.write("Customer Name: " + custName + "\n");
                bw.write("Customer Mobile: " + loggedInPhoneNumber + "\n");
                bw.write("Date & Time: " + now.format(formatter) + "\n");
                bw.write("Payment Type: " + payType + "\n");
                bw.write("-------------------------------------------\n");
                bw.write(String.format("%-10s %-20s %-10s %-10s\n", "Code", "Name", "Qty", "Price"));
                for (Product p : selectedProducts) {
                    bw.write(String.format("%-10d %-20s %-10d %-10.2f\n",
                            p.prodCode, p.prodName, p.prodQuantity, p.prodPrice * p.prodQuantity));
                }
                bw.write("-------------------------------------------\n");
                bw.write(String.format("Subtotal: %.2f\n", subtotal));
                bw.write(String.format("Tax (18%%): %.2f\n", tax));
                bw.write(String.format("Discount: %.2f\n", discount));
                bw.write(String.format("Grand Total: %.2f\n", grandTotal));
                bw.write("===========================================\n\n");

                System.out.println("\nBill saved to file: " + fileName);
            } catch (IOException e) {
                System.out.println("Failed to write bill to file: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
        }
    }


    public static void custMenu(Scanner sc)
    {
        while (true) {
            try {
                System.out.println("1. View Product");
                System.out.println("2. Search Product");
                System.out.println("3. View Cart & Purchase");
                System.out.println("4. View Previous Bills");
                System.out.println("5. My Profile");
                System.out.println("6. Logout");
                System.out.print("Choose an option: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        while (true) {
                        try {
                            System.out.println("\nProduct Menu");
                            System.out.println("1. View All Products");
                            System.out.println("2. View Product By Usage");
                            System.out.println("3. View Product By Type");
                            System.out.println("4. View Product By Manufacturer");
                            System.out.println("5. Return to Main Menu");

                            System.out.print("Enter your choice: ");
                            int c5 = sc.nextInt();
                            sc.nextLine();  // Clear newline

                            switch (c5) {
                                case 1:
                                    customer.viewAllProduct(); // method overriding and achieving abstraction
                                    break;

                                case 2:
                                    viewProductsByUsage(sc);
                                    break;

                                case 3:
                                    viewProductsByType();
                                    break;

                                case 4:
                                    viewProductsByManufacturer();
                                    break;

                                case 5:
                                    System.out.println("Returning to Main Menu...");
                                    break;

                                default:
                                    System.out.println("Enter valid choice");
                                    break;
                            }


                            if (c5 == 5) {
                                break;
                            }

                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number.");
                            sc.nextLine();  // Clear invalid input
                        } catch (Exception e) {
                            System.out.println("An unexpected error occurred.");
                            e.printStackTrace();
                        }
                    }
                        break;

                    case 2:
                        searchProduct();
                        break;

                    case 3:
                        viewCartAndPurchase();
                        break;

                    case 4:
                        viewMyBills();
                        break;

                    case 5:
                        customer.viewUserProfile(); // method overriding and achieving abstraction
                        break;

                    case 6:
                        System.out.println("Thanks for your visit \nVisit again...");
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number between 1 to 6.");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("An unexpected error occurred.");
                e.printStackTrace();
            }
        }
    }
}