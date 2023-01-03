package org.bmarket;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        //Creating a thread pool of 4 Threads
        ExecutorService executor = Executors.newFixedThreadPool(4);

        //INIT
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.createTable();
        boolean exit = true;

        while(exit){
            System.out.println("\n\n--[  PERFECT STORE MAIN PAGE  ]--\n\n");

            System.out.println("[1] -> Product List");
            System.out.println("[2] -> Add a Product [Creates a new Thread]");
            System.out.println("[3] -> Add Multiple Products [Creates a new Thread for every new Product]");
            System.out.println("[4] -> Search for a Product");
            System.out.println("[5] -> Database Statistics");
            System.out.println("[6] -> Exit\n");

            Scanner scanner=new Scanner(System.in);
            int personChoice=scanner.nextInt();

            switch(personChoice){
                case 1:
                    databaseManager.getAllProducts();
                    break;
                case 2:
                    Scanner scanner2 = new Scanner(System.in);
                    String title2, price2, description2, category2;

                    System.out.println("Title: ");
                    title2 = scanner2.nextLine();
                    System.out.println("Price: ");
                    price2 = scanner2.nextLine();
                    System.out.println("Description: ");
                    description2 = scanner2.nextLine();
                    System.out.println("Category: ");
                    category2 = scanner2.nextLine();

                    Product product2 = new Product("#" + String.valueOf( new Random().nextInt(99999) ),title2, String.valueOf( new Timestamp(System.currentTimeMillis())), price2, description2, category2, String.valueOf(databaseManager.getLatestProductCode(title2)));
                    Block block2 = new Block(databaseManager.getLatestBlockHash(), product2.toArray(), new Timestamp(System.currentTimeMillis()));

                    Runnable worker2 = block2;
                    executor.execute(worker2);
                    break;
                case 3:
                    Scanner scanner3 = new Scanner(System.in);
                    String title3, price3, description3, category3;
                    int counter = -1;

                    while (true) {
                        System.out.println("How many Products would you like to add?\n");
                        String count = scanner3.nextLine();
                        try{
                            counter = Integer.parseInt(count);
                            break;
                        }
                        catch(Exception e){
                            System.out.println("Please enter a valid number");
                        }
                    }

                    //Created a temp array to store the threads, in order to run them together.
                    //This was made to demonstrate the functionality of the threads
                    ArrayList<Block> queue = new ArrayList<>();

                    for(int i = 0; i < counter; i++){
                        System.out.println("Title: ");
                        title3 = scanner3.nextLine();
                        System.out.println("Price: ");
                        price3 = scanner3.nextLine();
                        System.out.println("Description: ");
                        description3 = scanner3.nextLine();
                        System.out.println("Category: ");
                        category3 = scanner3.nextLine();

                        Product product3 = new Product("#" + String.valueOf( new Random().nextInt(99999) ),title3, String.valueOf( new Timestamp(System.currentTimeMillis())), price3, description3, category3, String.valueOf(databaseManager.getLatestProductCode(title3)));
                        Block block3;
                        //previousHash is -1 since it is not mined yet
                        block3 = new Block("-1", product3.toArray(), new Timestamp(System.currentTimeMillis()));
                        queue.add(block3);
                    }

                    for (Block block: queue) {
                        Runnable worker = block;
                        executor.execute(worker);
                    }

                    break;
                case 4:
                    Scanner scanner4 = new Scanner(System.in);
                    System.out.println("Please enter the product title: ");
                    String title4 = scanner4.nextLine();
                    databaseManager.searchProduct(title4);
                    break;
                case 5:
                    Scanner scanner5 = new Scanner(System.in);
                    System.out.println("Please enter the product title: ");

                    String title5 = scanner5.nextLine();
                    databaseManager.getProductStatistics(title5);
                    break;
                case 6:
                    exit = false;
                    System.out.println("Adios Muchachos");
            }
        }
    }
}