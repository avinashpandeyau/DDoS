package com.sujan;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;

public class Client implements Connectible
{
    private static final int MAX_BUF = 1024 * 16;
    private static final int NO_RESPONSE_TIMEOUT = 3000;
    private ConnectionParameter connectionParameter;
    private SSLSocket socket;
    PrintWriter socketOut = null;
    BufferedReader socketIn = null;
    private String name;
    private long requestTime;
    private long responseTime;
    private char[] charBuffer;

    public String getState() {
        return null!=socket && socket.isConnected() ? "Connected" : "Disconnected";
    }

    public Client(ConnectionParameter connectionParameter, int clientNumber) {
        this.connectionParameter = connectionParameter;
        this.name = "Client "+clientNumber;
        this.charBuffer = new char[MAX_BUF];
        resetBuffer();
    }

    private void resetBuffer() {
        Arrays.fill(charBuffer,'\0');
    }

    public String getName() {
        return name;
    }

    @Override
    public void connect() throws Exception {
        if(null != socket) {
            return;
        }
        socket = SocketFactory.getSslSocket(connectionParameter);
        socket.setKeepAlive(true);
        socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        socketOut = new PrintWriter(socket.getOutputStream(),false);
        socket.setSoTimeout(NO_RESPONSE_TIMEOUT);
        System.out.println(getName()+" started !");
    }

    private void sendRequest(Message message) {
        requestTime = System.currentTimeMillis();
        if(!socket.isConnected()) {
            System.out.println(this.name+" Not Connected !");
            return;
        }
        socketOut.append(message.getMessage());
        socketOut.flush();
    }

    private synchronized void receiveResponse(Message message) throws Exception {
        if(!socket.isConnected()) {
            System.out.println(this.name+" Not Connected!");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        try {
            responseTime = 0;
            for(;;) {
                int i = socketIn.read(charBuffer);
                if(responseTime == 0) {
                    Main.requestCounts.put(this.name,message.getRequestNumber());
                    responseTime = System.currentTimeMillis();
                }
                int bufLength = getLength(charBuffer);
                if(bufLength < MAX_BUF) {
                    buffer.append(Arrays.copyOfRange(charBuffer,0,bufLength));
                    continue;
                }
                buffer.append(charBuffer);
            }
        }catch (Exception ex) {
            responseTime = System.currentTimeMillis() - NO_RESPONSE_TIMEOUT;
        }
        resetBuffer();
        if(buffer.length() <= 0) {
            return;
        }
        System.out.println("Response for "+this.name +" (" + connectionParameter.getPort() + ") Request Sequence: " + message.getRequestNumber() +", after "+(responseTime-requestTime)/1000+"s : "+buffer.toString());
    }

    private int getLength(char[] arr) {
        int len = 0;
        for(char c : arr) {
            if(c!='\0') {
                len++;
            }
        }
        return len;
    }

    public void sendAndReceive(Message message) {

        try {
            sendRequest(message);
            receiveResponse(message);
        }catch (Exception ex) {
            System.out.println(this.name+" Send and Receive : "+ex.getMessage());
        }
    }

    @Override
    public void disconnect() throws Exception {
        socketIn.close();
        socketOut.close();
        socket.close();
        requestTime = 0;
        responseTime = 0;
    }
}
