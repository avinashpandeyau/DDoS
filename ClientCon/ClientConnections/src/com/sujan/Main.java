package com.sujan;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static HashMap<String, Integer> requestCounts = new HashMap<>();

    private static int getRandomDelay(int start, int max) {
        Random random = new Random();
        return random.nextInt(max) + start;
    }

    public static void main(String[] args) throws Exception {
	// write your code here
        if(args.length < 4) {
            System.out.println("Not enough arguments\n {host} {ports} {numberOfClient} {totalRequests} must be supplied\n{ports} : comma separated list of port numbers");
            return;
        }

        String host = args[0]; //"192.168.1.102"
        String[] ports = args[1].split(",");
        int totalRequests = 1;
         try{
             totalRequests = Integer.parseInt(args[3]);
         } catch (Exception ex) {}
        int numberOfClients = 1;
        try{
            numberOfClients = Integer.parseInt(args[2]);
        } catch (Exception ex) {}
        ExecutorService executorService = Executors.newCachedThreadPool();
        System.out.println("Pre request Information :");
        System.out.println("Host  :"+host);
        System.out.println("Ports :"+args[1]);
        System.out.println("Number of Clients :"+numberOfClients);
        System.out.println("------------------------------------------");

        int portStartIndex = 0;
        //ArrayList<ClientTask> tasks = new ArrayList<ClientTask>();
        for(int i= 1; i <= numberOfClients; i++) {
            if(portStartIndex > ports.length - 1) {
               portStartIndex = 0;
            }
            try{
                int portNumber = Integer.parseInt(ports[portStartIndex]);
                ConnectionParameter parameter = new ConnectionParameter();
                parameter.setHostname(host);
                parameter.setPort(portNumber);
                requestCounts.put("Client "+i,0);
                ClientTask task = new ClientTask(new Client(parameter,i),totalRequests);
                executorService.execute(task);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
            portStartIndex++;
        }
        executorService.shutdown();
        boolean finished = executorService.awaitTermination(20, TimeUnit.MINUTES);
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Post request statistics");
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Client Name\t\t\t|\t\tRequests");
        System.out.println("------------------------------------------------------------------------------------");
        for(Map.Entry<String, Integer> entry: requestCounts.entrySet()) {
            System.out.println(entry.getKey() + "\t\t\t|\t\t"+entry.getValue());
        }
    }
}
