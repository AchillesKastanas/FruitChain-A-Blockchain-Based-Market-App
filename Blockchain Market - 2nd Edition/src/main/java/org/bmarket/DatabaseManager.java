package org.bmarket;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DatabaseManager {
    // establish connection with the database
    private static String DATABASE_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "/database";
    private ArrayList<Block> blockChain = new ArrayList<>();

    //Load all the stored blocks in the blockchain arraylist
    public DatabaseManager(){
        updateBlockChain();
    }

    private Connection doConnect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // create new tables if they don't exist
    public void createTable() {
        // query for creating the Users table
        String sql = "CREATE TABLE IF NOT EXISTS blocks (hash text NOT NULL, previousHash, blockTimestamp, nonce text NOT NULL, productCode text NOT NULL,title text NOT NULL,timeOfCreation text NOT NULL,price text NOT NULL,description text NOT NULL,category text NOT NULL,prevRecord text NOT NULL);";

        try (Connection conn = this.doConnect(); Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // insert user details in the User table
    public void insertBlock(Block block) {
        //Synchronized is used to ensure that only one thread can access this at once
        synchronized (DatabaseManager.class){
            System.out.println("IM INSERTING A BLOCK");
            String[] data = block.toArray();
            String sql = "INSERT OR IGNORE INTO blocks(hash, previousHash, blockTimestamp, nonce, productCode,title,timeOfCreation,price,description,category,prevRecord) VALUES(?,?,?,?,?,?,?,?,?,?,?)";

            try (Connection conn = this.doConnect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, data[0]);
                pstmt.setString(2, data[1]);
                pstmt.setString(3, data[2]);
                pstmt.setString(4, data[3]);
                pstmt.setString(5, data[4]);
                pstmt.setString(6, data[5]);
                pstmt.setString(7, data[6]);
                pstmt.setString(8, data[7]);
                pstmt.setString(9, data[8]);
                pstmt.setString(10, data[9]);
                pstmt.setString(11, data[10]);

                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            //Update the blockchain
            updateBlockChain();
        }
    }

    public void getAllProducts() {
        for(Block block: blockChain){
            String[] blockData = block.getData();
            System.out.println(
                    "BLOCK " +
                            blockChain.indexOf(block) + ":\n" +
                            " | Hash: " + block.getHash() + "\n" +
                            " | Previous Hash: " + block.getPreviousHash() + "\n" +
                            " | Block Timestamp: " + block.getTimestamp() + "\n" +
                            " | Nonce: " + block.getNonce() + "\n\n" +
                            "  PRODUCT: \n" +
                            "  | Product Code: " + blockData[0] + "\n" +
                            "  | Title: " + blockData[1] + "\n" +
                            "  | Time of Creation: " + blockData[2] + "\n" +
                            "  | Price: " + blockData[3] + "\n" +
                            "  | Description: " + blockData[4] + "\n" +
                            "  | Category: " + blockData[5] + "\n" +
                            "  | Previous Record: " + blockData[6] + "\n" +
                            "-------------------------------------------\n"
            );
        }
    }

    //Gets the latest product code of the product of the same title
    public String getLatestProductCode(String title) {
        String query = "SELECT productCode FROM blocks WHERE title= '" + title + "' ORDER BY rowid DESC LIMIT 1";
        try (Connection conn = this.doConnect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                return rs.getString("productCode");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "-";
    }

    //Gets the latest hash code of the product of the same title
    public String getLatestBlockHash() {
        String query = "SELECT hash FROM blocks ORDER BY rowid DESC LIMIT 1";
        try (Connection conn = this.doConnect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                return rs.getString("hash");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //Returns empty if no other blocks where found in the db
        return "";
    }

    public void searchProduct(String title) {
        ArrayList<String[]> products = new ArrayList<>();
        for(Block block: blockChain){
            String[] blockData = block.getData();

            if(Arrays.asList(blockData).contains(title)){
                products.add(blockData);
            }
        }

        if(products.isEmpty()){
            System.out.println("No Products with Title: '" + title + "' where found");
        }
        else if(products.size() == 1){
            System.out.println(
                    "  PRODUCT: \n" +
                            "  | Product Code: " + products.get(0)[0] + "\n" +
                            "  | Title: " + products.get(0)[1] + "\n" +
                            "  | Time of Creation: " + products.get(0)[2] + "\n" +
                            "  | Price: " + products.get(0)[3] + "\n" +
                            "  | Description: " + products.get(0)[4] + "\n" +
                            "  | Category: " + products.get(0)[5] + "\n" +
                            "  | Previous Record: " + products.get(0)[6] + "\n" +
                            "-------------------------------------------\n"
            );
        }
        //Multiple products found
        else{
            System.out.println("Multiple Records of Product: '" + title + "' where found. Would you like to: \n");
            System.out.println("[1] -> Show the latest Record of the Product '" + title + "'");
            System.out.println("[2] -> Show the first Record of the Product '" + title + "'");
            System.out.println("[3] -> Show all the Records of the Product '" + title + "'\n");

            Scanner scanner=new Scanner(System.in);
            int personChoice = scanner.nextInt();

            switch(personChoice) {
                case 1:
                    System.out.println(
                            "  PRODUCT: \n" +
                                    "  | Product Code: " + products.get(products.size()-1)[0] + "\n" +
                                    "  | Title: " + products.get(products.size()-1)[1] + "\n" +
                                    "  | Time of Creation: " + products.get(products.size()-1)[2] + "\n" +
                                    "  | Price: " + products.get(products.size()-1)[3] + "\n" +
                                    "  | Description: " + products.get(products.size()-1)[4] + "\n" +
                                    "  | Category: " + products.get(products.size()-1)[5] + "\n" +
                                    "  | Previous Record: " + products.get(products.size()-1)[6] + "\n" +
                                    "-------------------------------------------\n"
                    );
                    break;
                case 2:
                    System.out.println(
                            "  PRODUCT: \n" +
                                    "  | Product Code: " + products.get(0)[0] + "\n" +
                                    "  | Title: " + products.get(0)[1] + "\n" +
                                    "  | Time of Creation: " + products.get(0)[2] + "\n" +
                                    "  | Price: " + products.get(0)[3] + "\n" +
                                    "  | Description: " + products.get(0)[4] + "\n" +
                                    "  | Category: " + products.get(0)[5] + "\n" +
                                    "  | Previous Record: " + products.get(0)[6] + "\n" +
                                    "-------------------------------------------\n"
                    );
                    break;
                case 3:
                    for (String[] product : products) {
                        System.out.println(
                                "PRODUCT: \n" +
                                        "| Product Code: " + product[0] + "\n" +
                                        "| Title: " + product[1] + "\n" +
                                        "| Time of Creation: " + product[2] + "\n" +
                                        "| Price: " + product[3] + "\n" +
                                        "| Description: " + product[4] + "\n" +
                                        "| Category: " + product[5] + "\n" +
                                        "| Previous Record: " + product[6] + "\n" +
                                        "-------------------------------------------\n"
                        );
                    }
                    break;
            }
        }
    }

    //Gets the latest hash code of the product of the same title
    public void getProductStatistics(String title) {
        ArrayList<String[]> products = new ArrayList<>();
        for(Block block: blockChain){
            String[] blockData = block.getData();

            if(Arrays.asList(blockData).contains(title)){
                products.add(blockData);
            }
        }

        if(products.isEmpty()) {
            System.out.println("No Products Found");
        }
        else if(products.size() == 1){
            System.out.println("Given product has a single Record. Please provide a product that has more records in order to show Statistics");
        }
        else {
            System.out.println("--[ '" + title + "' STATISTICS ]--\n");
            System.out.println("Times Updated: " + products.size());
            System.out.println("Price Change Graph: ");

            String graph = "";
            for (String[] product : products) {
                graph += "[" + product[3] + "] -> ";
            }
            System.out.println(graph.substring(0, graph.length()-3));
        }
    }

    public void updateBlockChain(){
        String query = "SELECT * FROM blocks";
        ArrayList<Block> tempBlockChain = new ArrayList<>();

        try (Connection conn = this.doConnect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            // loop through the result set
            while (rs.next()) {

                Product product = new Product(rs.getString("productCode"), rs.getString("title"), rs.getString("timeOfCreation"),
                        rs.getString("price"), rs.getString("description"), rs.getString("category"), rs.getString("prevRecord"));

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = new java.sql.Date(dateFormat.parse(rs.getString("blockTimestamp")).getTime());
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

                Block block = new Block(rs.getString("previousHash"), rs.getString("hash"), product.toArray(), timestamp, rs.getInt("nonce"));
                tempBlockChain.add(block);
            }

            //Check if the tempBlockChain has more items than the blockChain Array list. This means that there are new items in the db, and should
            //be added to the blockChain Array List as well
            if(blockChain.isEmpty()){
                //Copy all data from the tempBlockChain (first init)
                blockChain = (ArrayList)tempBlockChain.clone();
            }
            else if(tempBlockChain.size() == blockChain.size()){
                //Do nothing
            }
            else if(tempBlockChain.size() > blockChain.size()){
                //Copy all the new data from the tempBlockChain to the blockChain ArrayList
                for(int i = blockChain.size(); i <= tempBlockChain.size() - 1; i++){
                    blockChain.add(tempBlockChain.get(i));
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void printBlockChain(){
        for(Block block: blockChain){
            System.out.println("-------------");
            String[] bData = block.toArray();
            for(String s: bData){
                System.out.println(s);
            }
            System.out.println("-------------");
        }
    }
}