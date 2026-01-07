package dao;

import util.DBConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

class Product {
    int prodCode;
    String prodName;
    double prodPrice;
    int prodQuantity;
    String prodUsage;
    LocalDate prodExpiry;
    String prodManufacturer;
    String prodType;

    public Product(int prodCode, String prodName, double prodPrice, int prodQuantity,
                   String prodUsage, LocalDate prodExpiry, String prodManufacturer, String prodType) {
        this.prodCode = prodCode;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodQuantity = prodQuantity;
        this.prodUsage = prodUsage;
        this.prodExpiry = prodExpiry;
        this.prodManufacturer = prodManufacturer;
        this.prodType = prodType;
    }

    public int getProdQuantity() {
        return prodQuantity;
    }

    public int getProdCode() {
        return prodCode;
    }

    public String getProdName() {
        return prodName;
    }

    public double getProdPrice() {
        return prodPrice;
    }

    public String getProdUsage() {
        return prodUsage;
    }

    public String getProdManufacturer() {
        return prodManufacturer;
    }

    public String getProdType() {
        return prodType;
    }

    @Override
    public String toString() {
        return "Product{" +
                "prodCode=" + prodCode +
                ", prodName='" + prodName + '\'' +
                ", prodPrice=" + prodPrice +
                ", prodQuantity=" + prodQuantity +
                ", prodUsage='" + prodUsage + '\'' +
                ", prodExpiry=" + prodExpiry +
                ", prodManufacturer='" + prodManufacturer + '\'' +
                ", prodType='" + prodType + '\'' +
                '}';
    }

    public LocalDate getProdExpiry() {
        return prodExpiry;
    }
}

class Customer {
    private int custCode;
    private String custName;
    private String custMobile;
    private String custPasswd;
    private Double custRewardPoints;
    private int custOrder;
    private String custBCity;

    // Constructor
    public Customer(int custCode, String custName, String custMobile, String custPasswd, Double custRewardPoints, int custOrder,String bCity) {
        this.custCode = custCode;
        this.custName = custName;
        this.custMobile = custMobile;
        this.custPasswd = custPasswd;
        this.custRewardPoints = custRewardPoints;
        this.custOrder = custOrder;
        this.custBCity = bCity;
    }

    public int getCustCode() {
        return custCode;
    }

    public String getCustName() {
        return custName;
    }

    public String getCustMobile() {
        return custMobile;
    }

    public String getCustPasswd() {
        return custPasswd;
    }

    public double getCustRewardPoints(){
        return custRewardPoints;
    }

    public int getCustOrder() {
        return custOrder;
    }

    public String getCustBCity() {
        return custBCity;
    }
}



public class Admin extends definedRole {

    static definedRole admin=new Admin();
    static Scanner sc=new Scanner(System.in);


    public static void loginAdmin(Scanner sc) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM admin";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("admin_passwd");

                int attempts = 0;
                boolean authenticated = false;

                while (attempts < 3) {
                    System.out.print("Enter password: ");
                    String passwordInput = sc.nextLine();

                    if (storedPassword.equals(passwordInput)) {
                        System.out.println("Login successful! Welcome");
                        authenticated = true;
                        adminMenu(sc); // Redirect to admin menu
                        break;
                    } else {
                        attempts++;
                        if (attempts < 3) {
                            System.out.println("Incorrect password. Try again " + (3 - attempts) + " attempts left");
                        }
                    }
                }

                if (!authenticated) {
                    System.out.println("Login failed.");
                }

            } else {
                System.out.println("No admin record found.");
            }

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


/*
    public static void InsertPassword() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try{
            Connection conn = DBConnection.getConnection();

                String insertSQL = "INSERT INTO admin VALUES (?)";
                 PreparedStatement pst = conn.prepareStatement(insertSQL);

                String userPassword = "Admin@123";
                pst.setString(1, userPassword);

                int rowsInserted = pst.executeUpdate();
                System.out.println(rowsInserted + " rows inserted.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 */

    public static void addProduct(Scanner sc) throws SQLException {
        Connection conn = DBConnection.getConnection();
        int id=0;
        try {
            while (true) {
                try {
                    System.out.print("Enter product code: ");
                    id = sc.nextInt();
                    sc.nextLine();

                    if (id <= 0) {
                        System.out.println("Product ID must be greater than 0.");
                        continue;
                    }

                    String checkSql = "SELECT COUNT(*) FROM products WHERE prod_code = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setInt(1, id);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Product ID already exists. Please enter a unique ID.");
                        continue;
                    }

                    break; // valid and unique ID
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer for ID.");
                    sc.nextLine(); // clear buffer
                }
            }

            System.out.print("Enter product name: ");
            String name = sc.nextLine();

            double price = 0;
            while (true) {
                try {
                    System.out.print("Enter product price: ");
                    price = sc.nextDouble();
                    sc.nextLine();

                    if (price <= 0) {
                        System.out.println("Price must be greater than 0.");
                        continue;
                    }

                    break; // valid price
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number for price.");
                    sc.nextLine();
                }
            }

            int quantity = 0;
            while (true) {
                try {
                    System.out.print("Enter product quantity : ");
                    quantity = sc.nextInt();
                    sc.nextLine();

                    if (quantity <= 0) {
                        System.out.println("Quantity must be greater than 0.");
                        continue;
                    }

                    break; // valid quantity
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid integer for quantity.");
                    sc.nextLine(); // Clear the invalid input
                }
            }

            System.out.print("Enter product usage: ");
            String usage = sc.nextLine();

            LocalDate date = getValidDateFromUser("Enter product expiry_date (YYYY-MM-DD): ");

            System.out.print("Enter product manufacturer: ");
            String mfg = sc.nextLine();

            System.out.print("Enter product type: ");
            String type = sc.nextLine();

            String insertSql = "INSERT INTO products VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(insertSql);
            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setDouble(3, price);
            pst.setInt(4, quantity);
            pst.setString(5, usage);
            pst.setDate(6, java.sql.Date.valueOf(date));
            pst.setString(7, mfg);
            pst.setString(8, type);

            pst.executeUpdate();
            System.out.println("Product added successfully.");
            //break; // Exit loop after successful insert

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input type. Please try again.");
            sc.nextLine(); // Clear the buffer
        }
    }


    public static void updateProduct(Scanner sc) throws SQLException {
        Connection conn = DBConnection.getConnection();

        int uid;
        while (true) {
            System.out.print("Enter product ID to update: ");
            String input = sc.nextLine();
            try {
                uid = Integer.parseInt(input);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid numeric product ID.");
            }
        }

        String checkSql = "SELECT COUNT(*) FROM products WHERE prod_code = ?";
        try {
            PreparedStatement pst = conn.prepareStatement(checkSql);
            pst.setInt(1, uid);
            ResultSet rs = pst.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                System.out.println("Product ID " + uid + " not found.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error checking product ID: " + e.getMessage());
            return;
        }

        boolean b1 = true;
        while (b1) {
            System.out.println("\n1. Update Name");
            System.out.println("2. Update Price");
            System.out.println("3. Update Quantity");
            System.out.println("4. Update Usage");
            System.out.println("5. Update Expiry Date");
            System.out.println("6. Update Manufacturer");
            System.out.println("7. Update Type");
            System.out.println("8. Return to Main Menu");
            System.out.print("Enter choice: ");

            int ch;
            try {
                ch = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 8.");
                continue;
            }

            switch (ch) {
                case 1:
                    System.out.print("Enter new product name: ");
                    String newName = sc.nextLine();
                    try {
                        PreparedStatement pst = conn.prepareStatement("UPDATE products SET prod_name = ? WHERE prod_code = ?");
                        pst.setString(1, newName);
                        pst.setInt(2, uid);
                        pst.executeUpdate();
                        System.out.println("Name updated successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error updating name: " + e.getMessage());
                    }
                    break;

                case 2:
                    double newPrice;
                    while (true) {
                        System.out.print("Enter new product price: ");
                        String input = sc.nextLine();
                        try {
                            newPrice = Double.parseDouble(input);
                            if (newPrice > 0) break;
                            else System.out.println("Price must be greater than zero.");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Enter a numeric price.");
                        }
                    }
                    try {
                        PreparedStatement pst = conn.prepareStatement("UPDATE products SET prod_price = ? WHERE prod_code = ?");
                        pst.setDouble(1, newPrice);
                        pst.setInt(2, uid);
                        pst.executeUpdate();
                        System.out.println("Price updated successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error updating price: " + e.getMessage());
                    }
                    break;

                case 3:
                    double newQty;
                    while (true) {
                        System.out.print("Enter new product quantity: ");
                        String input = sc.nextLine();
                        try {
                            newQty = Double.parseDouble(input);
                            if (newQty > 0) break;
                            else System.out.println("Quantity must be greater than zero.");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Enter a numeric quantity.");
                        }
                    }
                    try {
                        PreparedStatement pst = conn.prepareStatement("UPDATE products SET prod_quantity = ? WHERE prod_code = ?");
                        pst.setDouble(1, newQty);
                        pst.setInt(2, uid);
                        pst.executeUpdate();
                        System.out.println("Quantity updated successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error updating quantity: " + e.getMessage());
                    }
                    break;

                case 4:
                    System.out.print("Enter new product usage: ");
                    String newUsage = sc.nextLine();
                    try {
                        PreparedStatement pst = conn.prepareStatement("UPDATE products SET prod_usage = ? WHERE prod_code = ?");
                        pst.setString(1, newUsage);
                        pst.setInt(2, uid);
                        pst.executeUpdate();
                        System.out.println("Usage updated successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error updating usage: " + e.getMessage());
                    }
                    break;

                case 5:
                    LocalDate newExpiry = getValidDateFromUser("Enter new expiry date (YYYY-MM-DD): ");
                    try {
                        PreparedStatement pst = conn.prepareStatement("UPDATE products SET prod_expiry = ? WHERE prod_code = ?");
                        pst.setDate(1, java.sql.Date.valueOf(newExpiry));
                        pst.setInt(2, uid);
                        pst.executeUpdate();
                        System.out.println("Expiry date updated successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error updating expiry date: " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.print("Enter new product manufacturer: ");
                    String newMfg = sc.nextLine();
                    try {
                        PreparedStatement pst = conn.prepareStatement("UPDATE products SET prod_manufacturer = ? WHERE prod_code = ?");
                        pst.setString(1, newMfg);
                        pst.setInt(2, uid);
                        pst.executeUpdate();
                        System.out.println("Manufacturer updated successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error updating manufacturer: " + e.getMessage());
                    }
                    break;

                case 7:
                    System.out.print("Enter new product type: ");
                    String newType = sc.nextLine();
                    try {
                        PreparedStatement pst = conn.prepareStatement("UPDATE products SET prod_type = ? WHERE prod_code = ?");
                        pst.setString(1, newType);
                        pst.setInt(2, uid);
                        pst.executeUpdate();
                        System.out.println("Product type updated successfully.");
                    } catch (SQLException e) {
                        System.out.println("Error updating product type: " + e.getMessage());
                    }
                    break;

                case 8:
                    System.out.println("Returning to Main Menu..");
                    b1 = false;
                    break;

                default:
                    System.out.println("Invalid choice. Enter a number between 1 and 8.");
            }
        }
    }


    //abstraction and method overriding
    public void viewAllProduct() throws SQLException
    {
        Connection conn = DBConnection.getConnection();

        CallableStatement cs = conn.prepareCall("{call viewAllProducts()}");
        ResultSet rs = cs.executeQuery();

        boolean headerPrinted = false;

        while (rs.next()) {
            if (!headerPrinted) {
                System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                        "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                headerPrinted = true;
            }

            System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));
        }

        if (!headerPrinted) {
            System.out.println("No products found.");
        }

    }

    public static void viewProduct(int productId) throws SQLException //method overloading
    {
        Connection conn = DBConnection.getConnection();

        CallableStatement cs = conn.prepareCall("{call viewProductByCode(?)}");
        cs.setInt(1, productId);

        ResultSet rs = cs.executeQuery();

        boolean found = false;

        while (rs.next()) {
            if (!found) {
                System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                        "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                found = true;
            }

            System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));
        }

        if (!found) {
            System.out.println("No product found with ID: " + productId);
        }
    }

    public static void viewProductOnQuantity() throws SQLException
    {
        Connection conn = DBConnection.getConnection();

        String sql = "select * from products";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        ArrayList<Product> productList = new ArrayList<>();

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
            productList.add(p);
        }

        productList.sort(Comparator.comparingInt(Product::getProdQuantity));

        if (productList.isEmpty()) {
            System.out.println("No products found in the database.");
        } else {
            System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                    "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");

            for (Product p : productList) {
                System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                        p.prodCode, p.prodName, p.prodPrice, p.prodQuantity, p.prodUsage,
                        p.prodExpiry, p.prodManufacturer, p.prodType);
            }
        }

    }

    public static void viewProductOnEdate() throws SQLException
    {
        Connection conn = DBConnection.getConnection();

        String sql = "select * from products";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        List<Product> productList = new ArrayList<>();

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
            productList.add(p);
        }

        productList.sort(Comparator.comparing(Product::getProdExpiry));

        if (productList.isEmpty()) {
            System.out.println("No products found in the database.");
        } else {
            System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                    "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry", "Manufacturer", "Type");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");

            for (Product p : productList) {
                System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                        p.prodCode, p.prodName, p.prodPrice, p.prodQuantity, p.prodUsage,
                        p.prodExpiry, p.prodManufacturer, p.prodType);
            }
        }
    }

    public static void viewProductOnType(Scanner sc) throws SQLException
    {
        Connection conn = DBConnection.getConnection();

        System.out.print("Enter Product Type to view: ");
        String productType = sc.nextLine();

        CallableStatement cs = conn.prepareCall("{call viewProductByType(?)}");
        cs.setString(1, productType);

        ResultSet rs = cs.executeQuery();

        boolean found = false;

        while (rs.next()) {
            if (!found) {
                System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                        "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                found = true;
            }

            System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));
        }

        if (!found) {
            System.out.println("No product found with " + productType+" Type");
        }
    }

    public static void viewProduct(String productmfg) throws SQLException //method overloading
    {
        Connection conn = DBConnection.getConnection();

        CallableStatement cs = conn.prepareCall("{call viewProductByMfg(?)}");
        cs.setString(1, productmfg);

        ResultSet rs = cs.executeQuery();

        boolean found = false;

        while (rs.next()) {
            if (!found) {
                System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                        "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                found = true;
            }

            System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));
        }

        if (!found) {
            System.out.println("No product found with Manufacturer named "+ productmfg);
        }
    }

    public static void deleteProductOnProductCode(Scanner sc) throws SQLException
    {
        Connection conn = DBConnection.getConnection();
        System.out.print("Enter product code to delete:");
        int did = sc.nextInt();

        String delSql = "delete from products where prod_code = ?";
        PreparedStatement Pst = conn.prepareStatement(delSql);
        Pst.setInt(1, did);

        int r= Pst.executeUpdate();
        if (r>0)
        {
            System.out.println("Successfully deleted");
        }
        else {
            System.out.println("No Product found for Product code:"+did);
        }
    }

    public static void deleteProductOnEdate() throws SQLException
    {
        Connection conn = DBConnection.getConnection();

        LocalDate today = LocalDate.now();

        String selectSQL = "select * from products where prod_expiry <= ?";
        String deleteSQL = "delete from products where prod_expiry <= ?";

        PreparedStatement psSelect = conn.prepareStatement(selectSQL);
        psSelect.setDate(1, java.sql.Date.valueOf(today));
        ResultSet rs = psSelect.executeQuery();

        boolean headerPrinted = false;
        while (rs.next()) {
            if (!headerPrinted) {
                System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                        "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                headerPrinted = true;
            }

            System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));
        }

        PreparedStatement pst = conn.prepareStatement(deleteSQL);
        pst.setDate(1, java.sql.Date.valueOf(today));

        int r= pst.executeUpdate();
        if (r>0)
        {
            System.out.println("Successfully deleted");
        }
        else {
            System.out.println("No product found expired");
        }
    }


    public static void viewRemainderOnStock() throws SQLException
    {
        Connection conn = DBConnection.getConnection();

        CallableStatement cs = conn.prepareCall("{call checkStock()}");
        ResultSet rs = cs.executeQuery();

        boolean headerPrinted = false;

        while (rs.next()) {
            if (!headerPrinted) {
                System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                        "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
                headerPrinted = true;
            }

            System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                    rs.getInt("prod_code"),
                    rs.getString("prod_name"),
                    rs.getDouble("prod_price"),
                    rs.getInt("prod_quantity"),
                    rs.getString("prod_usage"),
                    rs.getDate("prod_expiry").toLocalDate(),
                    rs.getString("prod_manufacturer"),
                    rs.getString("prod_type"));
        }

        if (!headerPrinted) {
            System.out.println("Sufficient Quantity of all products.");
        }

    }

    public static void viewRemainderOnExpiry(){
        try{
                Connection conn = DBConnection.getConnection();

                CallableStatement expiredStmt = conn.prepareCall("{CALL getExpiredProducts()}");
                ResultSet rs = expiredStmt.executeQuery();

                System.out.println("\nEXPIRED MEDICINES (Expiry date before today):");

                boolean found1 = false;
                while (rs.next()) {
                    if (!found1) {
                        printHeader();
                        found1 = true;
                    }
                    printTable(rs);
                }

                if (!found1) {
                    System.out.println("No expired medicines found.");
                }

                CallableStatement expiringStmt = conn.prepareCall("{CALL getExpiringProducts()}");
                ResultSet rs1 = expiringStmt.executeQuery();

                System.out.println("\nMEDICINES EXPIRING WITHIN 7 DAYS:");

                boolean found2 = false;
                while (rs1.next()) {
                    if (!found2) {
                        printHeader();
                        found2 = true;
                    }
                    printTable(rs1);
                }

                if (!found2) {
                    System.out.println("No medicines are expiring within the next 7 days.");
                }


        } catch (SQLException e) {
            System.err.println("Error fetching medicine data: " + e.getMessage());
        }
    }

    private static void printHeader() {
        System.out.printf("%-12s %-20s %-12s %-10s %-20s %-15s %-25s %-15s\n",
                "Prod Code", "Product Name", "Price", "Quantity", "Usage", "Expiry Date", "Manufacturer", "Type");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
    }

    private static void printTable(ResultSet rs) throws SQLException {
        System.out.printf("%-12d %-20s %-12.2f %-10d %-20s %-15s %-25s %-15s\n",
                rs.getInt("prod_code"),
                rs.getString("prod_name"),
                rs.getDouble("prod_price"),
                rs.getInt("prod_quantity"),
                rs.getString("prod_usage"),
                rs.getDate("prod_expiry").toLocalDate(),
                rs.getString("prod_manufacturer"),
                rs.getString("prod_type"));
    }

    public void viewUserProfile() throws SQLException //abstraction achieved and method overriding
    {
        Connection conn = DBConnection.getConnection();

        CallableStatement cs = conn.prepareCall("{call viewAllCustomers()}");
        ResultSet rs = cs.executeQuery();

        boolean headerPrinted = false;

        while (rs.next()) {
            if (!headerPrinted) {
                System.out.printf("%-12s %-20s %-15s %-15s %-18s %-12s %-15s\n",
                        "Cust Code", "Customer Name", "Mobile", "Password", "Reward Points", "Orders", "Birth City");
                System.out.println("--------------------------------------------------------------------------------------------------------------");
                headerPrinted = true;
            }

            System.out.printf("%-12d %-20s %-15s %-15s %-18.2f %-12d %-15s\n",
                    rs.getInt("cust_code"),
                    rs.getString("cust_name"),
                    rs.getString("cust_mobile"),
                    rs.getString("cust_passwd"),
                    rs.getDouble("cust_rewardpoint"),
                    rs.getInt("cust_order"),
                    rs.getString("cust_bcity"));
        }

        if (!headerPrinted) {
            System.out.println("No customer record found.");
        }
    }


    public static void viewOnOrders() throws SQLException {
        ArrayList<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM customer";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int custCode = rs.getInt("cust_code");
                String custName = rs.getString("cust_name");
                String custMobile = rs.getString("cust_mobile");
                String custPasswd = rs.getString("cust_passwd");
                Double custRewardPoints = rs.getDouble("cust_rewardpoint");
                int custOrder = rs.getInt("cust_order");
                String custBCity = rs.getString("cust_bcity");

                Customer customer = new Customer(custCode, custName, custMobile, custPasswd, custRewardPoints, custOrder, custBCity);
                customerList.add(customer);
            }

            Collections.sort(customerList, Comparator.comparing(dao.Customer::getCustOrder).reversed());


            System.out.printf("%-10s %-20s %-15s %-15s %-15s %-10s %-15s%n",
                    "Cust Code", "Name", "Mobile", "Password", "Reward Points", "Orders", "Birth City");
            System.out.println("----------------------------------------------------------------------------------------------------------");


            for (Customer customer : customerList) {
                System.out.printf("%-10d %-20s %-15s %-15s %-15.2f %-10d %-15s%n",
                        customer.getCustCode(),
                        customer.getCustName(),
                        customer.getCustMobile(),
                        customer.getCustPasswd(),
                        customer.getCustRewardPoints(),
                        customer.getCustOrder(),
                        customer.getCustBCity());
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    public static void viewOnRewardPoints() throws SQLException {
        ArrayList<Customer> customerList = new ArrayList<>();
        String sql = "SELECT * FROM customer";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int custCode = rs.getInt("cust_code");
                String custName = rs.getString("cust_name");
                String custMobile = rs.getString("cust_mobile");
                String custPasswd = rs.getString("cust_passwd");
                double custRewardPoints = rs.getDouble("cust_rewardpoint");
                int custOrder = rs.getInt("cust_order");
                String custBCity = rs.getString("cust_bcity");

                Customer customer = new Customer(custCode, custName, custMobile, custPasswd, custRewardPoints, custOrder, custBCity);
                customerList.add(customer);
            }

            // Sort by Reward Points in descending order
            Collections.sort(customerList, Comparator.comparing(dao.Customer::getCustRewardPoints).reversed());


            System.out.printf("%-10s %-20s %-15s %-15s %-15s %-10s %-15s%n",
                    "Cust Code", "Name", "Mobile", "Password", "Reward Points", "Orders", "Birth City");
            System.out.println("----------------------------------------------------------------------------------------------------------");


            for (Customer customer : customerList) {
                System.out.printf("%-10d %-20s %-15s %-15s %-15.2f %-10d %-15s%n",
                        customer.getCustCode(),
                        customer.getCustName(),
                        customer.getCustMobile(),
                        customer.getCustPasswd(),
                        customer.getCustRewardPoints(),
                        customer.getCustOrder(),
                        customer.getCustBCity());
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }


    public static void viewAllBills()
    {
        String fileName = "bill_customer.txt";

        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public static void viewBillByName()
    {
        String fileName = "bill_customer.txt";
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter customer name to search: ");
        String targetCustomer = scanner.nextLine().trim().toLowerCase();

        try{
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            boolean isCapturing = false;
            boolean isMatchingCustomer = false;

            boolean anyMatchFound = false;

            while ((line = reader.readLine()) != null) {
                // Start of a new bill
                if (line.startsWith("======")) {

                    if (isCapturing && isMatchingCustomer) {
                        System.out.println();
                    }

                    // Reset state for the new bill
                    isCapturing = true;
                    isMatchingCustomer = false;
                }

                if (isCapturing) {
                    // Check if this line has the customer name
                    if (line.toLowerCase().startsWith("customer name: ")) {
                        String customerName = line.substring(15).trim().toLowerCase();
                        if (customerName.equals(targetCustomer)) {
                            isMatchingCustomer = true;
                            anyMatchFound = true;
                        }
                    }

                    // Print the line only if the bill matches the customer
                    if (isMatchingCustomer) {
                        System.out.println(line);
                    }
                }
            }

            if (!anyMatchFound) {
                System.out.println("No bills found for customer: " + targetCustomer);
            }

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
    }

    public static void viewAllSales()
    {
        try{
            Connection con = DBConnection.getConnection();
            CallableStatement cs = con.prepareCall("{CALL getAllSales()}");

                ResultSet rs = cs.executeQuery();

                boolean foundResults = false;

                while (rs.next()) {
                    if (!foundResults) {
                        // Print header only once when the first result is found
                        System.out.printf("%-10s %-15s %-15s %-10s %-20s %-10s %-10s %-12s %-10s %-15s\n",
                                "SaleCode", "CustName", "CustMobile", "ProdCode", "ProdName",
                                "Quantity", "Price", "Date", "Total", "PayType");
                        foundResults = true;
                    }
                    int saleCode = rs.getInt("sale_code");
                    String custName = rs.getString("cust_name");
                    long custMobile = rs.getLong("cust_mobile");
                    int prodCode = rs.getInt("prod_code");
                    String prodName = rs.getString("prod_name");
                    int quantity = rs.getInt("prod_quantity");
                    double price = rs.getDouble("prod_price");
                    LocalDate saleDate = rs.getDate("sale_date").toLocalDate();
                    double total = rs.getDouble("total");
                    String payType = rs.getString("pay_type");

                    System.out.printf("%-10d %-15s %-15d %-10d %-20s %-10d %-10.2f %-12s %-10.2f %-15s\n",
                            saleCode, custName, custMobile, prodCode, prodName,
                            quantity, price, saleDate.toString(), total, payType);

                }

                if (!foundResults) {
                    System.out.println("No sales records found.");
                }

                CallableStatement st2 = con.prepareCall("{? = call sumOfAmtGainByUPI()}");

                st2.registerOutParameter(1, Types.DOUBLE);
                st2.execute();

                double totalSalesByUPI = st2.getDouble(1);
                System.out.println("\nTotal Amount Received by UPI: " + totalSalesByUPI);

                CallableStatement st3 = con.prepareCall("{? = call sumOfAmtGainByRp()}");

                st3.registerOutParameter(1, Types.DOUBLE);
                st3.execute();

                double totalSalesByRp = st3.getDouble(1);
                System.out.println("Total Amount Received by RewardPoints: " + totalSalesByRp);

                CallableStatement st1 = con.prepareCall("{? = call sumOfAmtGain()}");

                st1.registerOutParameter(1, Types.DOUBLE);
                st1.execute();

                double totalSales = st1.getDouble(1);
                System.out.println("Total Amount Received: " + totalSales);



        } catch (SQLException e) {
            System.out.println("Error fetching sales details: " + e.getMessage());
        }
    }

    public static void viewSalesByPaymentMethod(Scanner sc) {
        String selectedPayType = null;

        while (true) {
            System.out.println("Select Payment Type to View Sales:");
            System.out.println("1. UPI");
            System.out.println("2. Reward Points");
            System.out.print("Enter your choice: ");

            try {
                int choice = Integer.parseInt(sc.nextLine().trim());

                switch (choice) {
                    case 1:
                        selectedPayType = "UPI";
                        break;
                    case 2:
                        selectedPayType = "Reward Points";
                        break;
                    default:
                        System.out.println("Invalid option. Please enter 1 or 2.\n");
                        continue; // back to loop
                }
                break; // valid input; exit loop
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.\n");
            }
        }

        try{
            Connection con = DBConnection.getConnection();
            CallableStatement cs = con.prepareCall("{CALL getSalesByPayment(?)}");

            cs.setString(1, selectedPayType);

            ResultSet rs = cs.executeQuery();
                boolean foundResults = false;

                while (rs.next()) {
                    if (!foundResults) {
                        System.out.printf("%-10s %-15s %-15s %-10s %-20s %-10s %-10s %-12s %-10s %-15s\n",
                                "SaleCode", "CustName", "CustMobile", "ProdCode", "ProdName",
                                "Quantity", "Price", "Date", "Total", "PayType");
                        foundResults = true;
                    }

                    int saleCode = rs.getInt("sale_code");
                    String custName = rs.getString("cust_name");
                    long custMobile = rs.getLong("cust_mobile");
                    int prodCode = rs.getInt("prod_code");
                    String prodName = rs.getString("prod_name");
                    int quantity = rs.getInt("prod_quantity");
                    double price = rs.getDouble("prod_price");
                    LocalDate saleDate = rs.getDate("sale_date").toLocalDate();
                    double total = rs.getDouble("total");
                    String payType = rs.getString("pay_type");

                    System.out.printf("%-10d %-15s %-15d %-10d %-20s %-10d %-10.2f %-12s %-10.2f %-15s\n",
                            saleCode, custName, custMobile, prodCode, prodName,
                            quantity, price, saleDate.toString(), total, payType);
                }

                if (!foundResults) {
                    System.out.println("No sales records found for payment type: " + selectedPayType);
                }

        } catch (SQLException e) {
            System.out.println("Error fetching sales: " + e.getMessage());
        }
    }


    public static void viewSalesByDate(Scanner sc) throws SQLException {

        LocalDate startDate = getValidDateFromUser("Enter start date (YYYY-MM-DD): ");
        LocalDate endDate;

        while (true) {
            endDate = getValidDateFromUser("Enter end date (YYYY-MM-DD): ");

            if (endDate.isBefore(startDate)) {
                System.out.println("End date cannot be before start date. Please try again.");
            } else {
                break;
            }
        }

        try{
            Connection con = DBConnection.getConnection();
            CallableStatement cs = con.prepareCall("{CALL getSalesByDate(?, ?)}");

            cs.setDate(1, Date.valueOf(startDate));
            cs.setDate(2, Date.valueOf(endDate));


                ResultSet rs = cs.executeQuery();
                boolean foundResults = false;

                while (rs.next()) {
                    if (!foundResults) {
                        System.out.printf("%-10s %-15s %-15s %-10s %-20s %-10s %-10s %-12s %-10s %-15s\n",
                                "SaleCode", "CustName", "CustMobile", "ProdCode", "ProdName",
                                "Quantity", "Price", "Date", "Total", "PayType");
                        foundResults = true;
                    }

                    int saleCode = rs.getInt("sale_code");
                    String custName = rs.getString("cust_name");
                    long custMobile = rs.getLong("cust_mobile");
                    int prodCode = rs.getInt("prod_code");
                    String prodName = rs.getString("prod_name");
                    int quantity = rs.getInt("prod_quantity");
                    double price = rs.getDouble("prod_price");
                    LocalDate saleDate = rs.getDate("sale_date").toLocalDate();
                    double total = rs.getDouble("total");
                    String payType = rs.getString("pay_type");

                    System.out.printf("%-10d %-15s %-15d %-10d %-20s %-10d %-10.2f %-12s %-10.2f %-15s\n",
                            saleCode, custName, custMobile, prodCode, prodName,
                            quantity, price, saleDate.toString(), total, payType);
                }

                if (!foundResults) {
                    System.out.println("No sales found between " + startDate + " and " + endDate + ".");
                }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    public static void viewLog() {

        String query = "SELECT * FROM log";

        try{
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            System.out.printf("%-10s %-15s %-15s %-25s\n", "Log ID", "Product Code", "Operation", "Timestamp");
            System.out.println("------------------------------------------------------------------");

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;
                int logCode = rs.getInt("log_code");
                int productCode = rs.getInt("product_code");
                String operation = rs.getString("operation");
                Timestamp timestamp = rs.getTimestamp("log_timestamp");

                System.out.printf("%-10d %-15d %-15s %-25s\n", logCode, productCode, operation, timestamp.toString());
            }

            if (!hasData) {
                System.out.println("No logs found.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving log table: " + e.getMessage());
        }
    }


    static LocalDate getValidDateFromUser(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.next();
            try {
                return LocalDate.parse(input);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    public static void adminMenu(Scanner sc) {
        while (true) {
            try {
                System.out.println();
                System.out.println("1. Add Product");
                System.out.println("2. Update Product");
                System.out.println("3. View Product");
                System.out.println("4. Delete Product");
                System.out.println("5. View Remainders");
                System.out.println("6. View Bills");
                System.out.println("7. View Users");
                System.out.println("8. View Sales");
                System.out.println("9. View Log");
                System.out.println("10. Logout");
                System.out.print("Choose an option: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        addProduct(sc);
                        break;

                    case 2:
                        updateProduct(sc);
                        break;

                    case 3:
                        while (true) {
                            try {
                                System.out.println("\n1. View all Products");
                                System.out.println("2. View on basis of Product Code");
                                System.out.println("3. View on basis of Product Quantity");
                                System.out.println("4. View on basis of Expiry Date");
                                System.out.println("5. View on basis of Product Type");
                                System.out.println("6. View on basis of Manufacturer Type");
                                System.out.println("7. Return to Main Menu");
                                int c1 = sc.nextInt();
                                sc.nextLine();
                                switch (c1) {
                                    case 1:
                                        admin.viewAllProduct(); // method overriding and achieving abstraction
                                        break;

                                    case 2:
                                        int id = -1;
                                        boolean valid = false;

                                        while (!valid) {
                                            try {
                                                System.out.print("Enter Product Code to view: ");
                                                id = sc.nextInt();
                                                sc.nextLine();
                                                valid = true;
                                            } catch (InputMismatchException e) {
                                                System.out.println("Invalid input. Please enter a numeric Product Code.");
                                                sc.nextLine();
                                            }
                                        }
                                        viewProduct(id);
                                        break;

                                    case 3:
                                        viewProductOnQuantity();
                                        break;

                                    case 4:
                                        viewProductOnEdate();
                                        break;

                                    case 5:
                                        viewProductOnType(sc);
                                        break;

                                    case 6:
                                        System.out.print("Enter Product Manufacturer to view: ");
                                        String mfg = sc.nextLine();
                                        viewProduct(mfg);
                                        break;

                                    case 7:
                                        System.out.println("Returning to Main Menu..");
                                        break;

                                    default:
                                        System.out.println("Invalid choice.");
                                        break;
                                }
                                if (c1 == 7) {
                                    break;
                                }

                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input.");
                                sc.nextLine();
                            } catch (Exception e) {
                                System.out.println("An unexpected error occurred.");
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 4:
                        while (true) {
                            try {
                                System.out.println("\n1. Delete on basis of Product Code");
                                System.out.println("2. Delete on basis of Expiry Date");
                                System.out.println("3. Return to Main Menu");

                                int c2 = sc.nextInt();
                                sc.nextLine();

                                switch (c2) {
                                    case 1:
                                        deleteProductOnProductCode(sc);
                                        break;

                                    case 2:
                                        deleteProductOnEdate();
                                        break;

                                    case 3:
                                        System.out.println("Returning to Main Menu...");
                                        break;

                                    default:
                                        System.out.println("Invalid choice.");
                                        break;
                                }
                                if (c2 == 3) {
                                    break;
                                }

                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input.");
                                sc.nextLine();
                            } catch (Exception e) {
                                System.out.println("An unexpected error occurred.");
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 5:
                        while (true) {
                            try {
                                System.out.println("\n1. View Remainder on Stock");
                                System.out.println("2. View Remainder on Expiry Date");
                                System.out.println("3. Return to Main Menu");

                                int c4 = sc.nextInt();
                                sc.nextLine();

                                switch (c4) {
                                    case 1:
                                        viewRemainderOnStock();
                                        break;

                                    case 2:
                                        viewRemainderOnExpiry();
                                        break;

                                    case 3:
                                        System.out.println("Returning to Main Menu..");
                                        break;

                                    default:
                                        System.out.println("Invalid choice.");
                                        break;
                                }

                                if (c4 == 3) {
                                    break;
                                }

                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input.");
                                sc.nextLine();
                            } catch (Exception e) {
                                System.out.println("An unexpected error occurred.");
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 6:
                        while (true) {
                            try {
                                System.out.println("\n1. View All Bills");
                                System.out.println("2. View Bills by Customer Name");
                                System.out.println("3. Return to Main Menu");

                                int c6 = sc.nextInt();
                                sc.nextLine();

                                switch (c6) {
                                    case 1:
                                        viewAllBills();
                                        break;

                                    case 2:
                                        viewBillByName();
                                        break;

                                    case 3:
                                        System.out.println("Returning to Main Menu..");
                                        break;

                                    default:
                                        System.out.println("Invalid choice.");
                                        break;
                                }

                                if (c6 == 3) {
                                    break;
                                }

                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input.");
                                sc.nextLine();
                            } catch (Exception e) {
                                System.out.println("An unexpected error occurred.");
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 7:
                        while (true) {
                            try {
                                System.out.println("\n1. View all Users");
                                System.out.println("2. View on basis of Orders");
                                System.out.println("3. View on basis of Reward Points");
                                System.out.println("4. Return to Main Menu");

                                int c3 = sc.nextInt();
                                sc.nextLine();

                                switch (c3) {
                                    case 1:
                                        admin.viewUserProfile(); // method overriding and achieving abstraction
                                        break;

                                    case 2:
                                        viewOnOrders();
                                        break;

                                    case 3:
                                        viewOnRewardPoints();
                                        break;

                                    case 4:
                                        System.out.println("Returning to Main Menu..");
                                        break;

                                    default:
                                        System.out.println("Invalid choice.");
                                }

                                if (c3 == 4) {
                                    break;
                                }

                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input.");
                                sc.nextLine();
                            } catch (Exception e) {
                                System.out.println("An unexpected error occurred.");
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 8:
                        while (true) {
                            try {
                                System.out.println("\n1. View all Sales");
                                System.out.println("2. View Sales by Payment Method");
                                System.out.println("3. View Sales by Date");
                                System.out.println("4. Return to Main Menu");

                                int c3 = sc.nextInt();
                                sc.nextLine();

                                switch (c3) {
                                    case 1:
                                        viewAllSales();
                                        break;

                                    case 2:
                                        viewSalesByPaymentMethod(sc);
                                        break;

                                    case 3:
                                        viewSalesByDate(sc);
                                        break;

                                    case 4:
                                        System.out.println("Returning to Main Menu..");
                                        break;

                                    default:
                                        System.out.println("Invalid choice.");
                                }

                                if (c3 == 4) {
                                    break;
                                }

                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input.");
                                sc.nextLine();
                            } catch (Exception e) {
                                System.out.println("An unexpected error occurred.");
                                e.printStackTrace();
                            }
                        }
                        break;

                    case 9:
                        viewLog();
                        break;

                    case 10:
                        System.out.println("Logging out...");
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input.");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("An unexpected error occurred.");
                e.printStackTrace();
            }
        }
    }
}