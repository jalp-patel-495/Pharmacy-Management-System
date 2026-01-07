package main;

import dao.Admin;
import dao.CustomerDao;
import dao.CustomerDao;
//import model.Customer;

import java.util.InputMismatchException;
import java.util.Scanner;

public class mainApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        CustomerDao customerDAO = new CustomerDao();
        Admin adminDAO = new Admin();


        while (true) {
            try {
                System.out.println("1. Register as Customer");
                System.out.println("2. Login as Customer");
                System.out.println("3. Admin Access");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        customerDAO.registerUser(sc);
                        break;

                    case 2:
                        customerDAO.loginUser(sc);
                        break;

                    case 3:
                        //adminDAO.InsertPassword();
                        adminDAO.loginAdmin(sc);
                        break;

                    case 4:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number (1, 2, or 3).");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("An unexpected error occurred.");
                e.printStackTrace();
            }
        }
    }
}