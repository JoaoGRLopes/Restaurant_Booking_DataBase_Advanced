package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadedServer {
    private void connectClient() {
        System.out.println("Starting Server");
        try(ServerSocket serverSocket = new ServerSocket(2000)){
            while(true){
                System.out.println("Waiting for connection with clients");

                try{
                    Socket socket = serverSocket.accept();
                    ClientHandler clientHandlerThread = new ClientHandler(socket);
                    Thread connectionThread = new Thread(clientHandlerThread);
                    connectionThread.start();
                } catch (IOException ex) {
                    System.out.println("Server: Could not start connection to a client.");
                }
            }
        }
        catch(IOException ex) {
            System.out.println("Server: Could not start connection to a client.");
        }
    }

    public static void main(String[] args) {
        ThreadedServer threadedServer = new ThreadedServer();
        threadedServer.connectClient();
    }
}
