package com.sujan;

import java.util.ArrayList;

/**
 * Created by sujan on 4/01/17.
 */
public class ClientTask implements Runnable {

    private Client client;
    private int maxRequest;
    private ArrayList<Message> messages;

    public ClientTask(Client client, int maxRequest) {
        this.client = client;
        this.maxRequest = maxRequest;
        initMessages();
    }

    private void initMessages() {
        this.messages = new ArrayList<>();
        this.messages.add(new Message("17/0/48/5/***")); //handshake
        this.messages.add(new Message("7/0/48/5/299**0")); //strata exchange
    }

    @Override
    public void run() {
        try{
            client.connect();
            for (int request = 1; request <= this.maxRequest; request++) {
                for(Message m: messages) {
                    m.setRequestNumber(request);
                    client.sendAndReceive(m);
                }
            }
            client.disconnect();
        }catch (Exception ex) {
            System.out.println("Error "+client.getName()+": "+ex.getMessage());
        }
    }
}
