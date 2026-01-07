package dao;

import java.util.*;

class customLinkedList {

    class Node {
        Product data;
        Node next;

        public Node(Product data) {
            this.data = data;
            this.next = null;
        }
    }

    Node head;

    public void add(Product data) {
        Node n = new Node(data);
        if (head == null) {
            head = n;
            return;
        }

        Node current = head;
        while (current.next != null) {
            current = current.next;
        }

        current.next = n;
    }

    public void display() {
        Node current = head;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }

    public Product getProductByCode(int code) {
        Node current = head;
        while (current != null) {
            if (current.data.prodCode == code) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    public void printTableFormat() {

        Node current = head;

        // Header
        System.out.printf(
                "%-10s | %-20s | %-10s | %-20s | %-12s | %-20s | %-15s%n",
                "Code", "Name", "Price", "Usage", "Expiry", "Manufacturer", "Type"
        );
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

        // Rows
        while (current != null) {
            Product p = current.data;
            System.out.printf(
                    "%-10d | %-20s | %-10.2f | %-20s | %-12s | %-20s | %-15s%n",
                    p.getProdCode(),
                    p.getProdName(),
                    p.getProdPrice(),
                    p.getProdUsage(),
                    p.getProdExpiry(),
                    p.getProdManufacturer(),
                    p.getProdType()
            );
            current = current.next;
        }
    }


    public void sortedByName() {
        Node current = head;
        ArrayList<Product> productList = new ArrayList<>();

        while (current != null) {
            productList.add(current.data);
            current = current.next;
        }

        Collections.sort(productList, Comparator.comparing(Product::getProdName, String.CASE_INSENSITIVE_ORDER));

        // Step 4: Print table header
        System.out.printf(
                "%-20s | %-10s | %-20s | %-12s | %-20s | %-15s%n",
                "Product Name", "Price", "Usage", "Expiry", "Manufacturer", "Type"
        );
        System.out.println("---------------------------------------------------------------------------------------------------------------");

        // Step 5: Print sorted rows
        for (Product p : productList) {
            System.out.printf(
                    "%-20s | %-10.2f | %-20s | %-12s | %-20s | %-15s%n",
                    p.getProdName(),
                    p.getProdPrice(),
                    p.getProdUsage(),
                    p.getProdExpiry(),
                    p.getProdManufacturer(),
                    p.getProdType()
            );
        }
    }

    public void productsByManufacturer(String manufacturerName) {
        Node current = head;
        boolean found = false; // Flag to track if any product is matched

        while (current != null) {
            Product p = current.data;

            if (p.getProdManufacturer() != null &&
                    p.getProdManufacturer().equalsIgnoreCase(manufacturerName)) {

                // Print header only once (when first match is found)
                if (!found) {
                    System.out.printf(
                            "%-20s | %-10s | %-20s | %-12s | %-20s | %-15s%n",
                            "Product Name", "Price", "Usage", "Expiry", "Manufacturer", "Type"
                    );
                    System.out.println("-----------------------------------------------------------------------------------------------------------");
                    found = true;
                }

                // Print product row
                System.out.printf(
                        "%-20s | %-10.2f | %-20s | %-12s | %-20s | %-15s%n",
                        p.getProdName(),
                        p.getProdPrice(),
                        p.getProdUsage(),
                        p.getProdExpiry(),
                        p.getProdManufacturer(),
                        p.getProdType()
                );
            }

            current = current.next;
        }

        // If no match was found, show message
        if (!found) {
            System.out.println("No products found for manufacturer: " + manufacturerName);
        }
    }

    public void productsByUsage(String usage) {
        Node current = head;
        boolean found = false; // Flag to track if any product is matched

        while (current != null) {
            Product p = current.data;

            if (p.getProdUsage() != null &&
                    p.getProdUsage().equalsIgnoreCase(usage)) {

                // Print header only once (when first match is found)
                if (!found) {
                    System.out.printf(
                            "%-20s | %-10s | %-20s | %-12s | %-20s | %-15s%n",
                            "Product Name", "Price", "Usage", "Expiry", "Manufacturer", "Type"
                    );
                    System.out.println("-----------------------------------------------------------------------------------------------------------");
                    found = true;
                }

                // Print product row
                System.out.printf(
                        "%-20s | %-10.2f | %-20s | %-12s | %-20s | %-15s%n",
                        p.getProdName(),
                        p.getProdPrice(),
                        p.getProdUsage(),
                        p.getProdExpiry(),
                        p.getProdManufacturer(),
                        p.getProdType()
                );
            }

            current = current.next;
        }

        // If no match was found, show message
        if (!found) {
            System.out.println("No products found for usage: " + usage);
        }
    }

    public void productsByType(String type) {
        Node current = head;
        boolean found = false; // Flag to track if any product is matched

        while (current != null) {
            Product p = current.data;

            if (p.getProdType() != null &&
                    p.getProdType().equalsIgnoreCase(type)) {

                // Print header only once (when first match is found)
                if (!found) {
                    System.out.printf(
                            "%-20s | %-10s | %-20s | %-12s | %-20s | %-15s%n",
                            "Product Name", "Price", "Usage", "Expiry", "Manufacturer", "Type"
                    );
                    System.out.println("-----------------------------------------------------------------------------------------------------------");
                    found = true;
                }

                // Print product row
                System.out.printf(
                        "%-20s | %-10.2f | %-20s | %-12s | %-20s | %-15s%n",
                        p.getProdName(),
                        p.getProdPrice(),
                        p.getProdUsage(),
                        p.getProdExpiry(),
                        p.getProdManufacturer(),
                        p.getProdType()
                );
            }

            current = current.next;
        }

        // If no match was found, show message
        if (!found) {
            System.out.println("No products found for type: " + type);
        }
    }

    public boolean isEmpty()
    {
        return head == null;
    }

}