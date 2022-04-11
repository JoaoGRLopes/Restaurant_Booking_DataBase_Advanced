package Server;

import SQLConnection.ConnectionClass;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClientHandler implements Runnable{
    private final Socket socket;
    private final PrintWriter printWriter;
    private BufferedReader bufferedReader = null;

    private static Connection getConnection;
    private static Statement statement;
    private static ResultSet resultSet;

    private static int connections = 0;
    private final int connectionsNumber;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        printWriter = new PrintWriter(socket.getOutputStream(), true);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        connections++;
        connectionsNumber = connections;
        System.out.println("Connection " + connectionsNumber + " established.");
    }

    @Override
    public void run() {
        try {
            System.out.println("Waiting for client");
            String lineRead;
            while ((lineRead = bufferedReader.readLine()) != null) {
                System.out.println("Reading from client: " + lineRead);

                String[] clientSend = lineRead.split(",");
                System.out.println("Data from client: " + lineRead);
                String wine_id = null;
                String addingWine = null;


                //if the first letter
                if(clientSend[0].equals("add, ")){
                    wine_id = clientSend[1];
                    try {
                        getConnection = ConnectionClass.accessWineDatabase();
                        statement = getConnection.createStatement();
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                    printWriter.println(wine_id);
                }
                //if the first letter
                else if (clientSend[0].equals("select, ")){
                    //response.sendWineList(generateWineList());
                    //System.out.println("It was Sent: "+ response);
                    //printWriter.writeObject(response);
                }

                else if (clientSend[0].equals("delete, ")){
                }
                else{
                    printWriter.println("not recognised");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                System.out.println("We have lost connection to client " + connectionsNumber);
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }
}
