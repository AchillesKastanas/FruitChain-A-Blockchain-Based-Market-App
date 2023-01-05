package org.bmarket;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class Block {
    private String hash;
    private String previousHash;
    private Timestamp timeStamp;
    private int nonce;
    private String[] data;
    static int PREFIX = 6;

    //Used to create a new Block
    public Block(String previousHash, String[] data, Timestamp timeStamp) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = timeStamp;
        this.hash = calculateBlockHash();
    }

    //Used to Initialise an already created block (pulled from the database)
    public Block(String previousHash, String hash, String[] data, Timestamp timeStamp, int nonce) {
        this.previousHash = previousHash;
        this.data = data;
        this.timeStamp = timeStamp;
        this.hash = hash;
        this.nonce = nonce;
    }

    public String calculateBlockHash(){
        String dataToHash = previousHash + timeStamp.toString()
                +data+Integer.toString(nonce);
        MessageDigest digest = null;
        byte[] bytes = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes){
            builder.append(String.format("%02x",b));
        }
        return builder.toString();
    }

    public String mineBlock(){
        String prefixString =
                new String(new char[PREFIX]).replace('\0','0');
        while (!hash.substring(0,PREFIX).equals(prefixString)){
            nonce++;
            hash = calculateBlockHash();
        }
        System.out.println("MINING ENDED AT: " + new Timestamp(System.currentTimeMillis()));
        return hash;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public Timestamp getTimestamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }

    public String[] getData() {
        return data;
    }

    public String[] toArray(){
        return new String[]{this.hash, this.previousHash, timeStamp.toString(), String.valueOf(nonce), data[0], data[1], data[2], data[3], data[4], data[5], data[6]};
    }
}