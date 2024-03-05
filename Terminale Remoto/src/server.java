import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";


    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12347);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // per inviare output al client
                PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                // per leggere input dal client
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Messaggio di benvenuto
                clientWriter.println("Benvenuto! Vuoi loggarti o registrarti?");
                clientWriter.println("1. Login");
                clientWriter.println("2. Registrati");

                // Scelta del cliente
                String choice = clientReader.readLine();

                if ("1".equals(choice)) {
                    // Login
                    clientWriter.println("Inserisci username:");
                    String username = clientReader.readLine();
                    clientWriter.println("Inserisci password:");
                    String password = clientReader.readLine();

                    // Controllo dell'utente
                    if (!funzioni_server.isValidUser(username, password)) {
                        clientWriter.println(RED + "Errore: Utente non valido" + RESET);
                        clientSocket.close();
                        continue;
                    } else {
                        clientWriter.println(GREEN + "Sei loggato!" + RESET);

                        // legge da input del client
                        new Thread(() -> {
                            try {
                                // Testo preso dal client
                                String clientMessage;

                                // Help comandi
                                clientWriter.println("Elenco dei comandi:");
                                clientWriter.println("passwd -> Cambia la password ");
                                clientWriter.println("ls -> Visualizza i contenuti della directory ");
                                clientWriter.println("cd -> Cambia directory");
                                clientWriter.println("mkdir <nome cartella> -> crea una cartella");
                                clientWriter.println("touch <nome file> -> crea un file");
                                clientWriter.println("cp <nome file> <directory per copiarlo>/<nome file> -> crea una copia di questo file nella cartella");
                                clientWriter.println("mv <nome file> <directory per spostarlo>/<nome file> -> sposta un file in un altra cartella");
                                clientWriter.println("rm <nome file o cartella> -> rimuove il file o la cartella" + RESET);

                                // Finquando ci sono cose scritte esegui
                                while ((clientMessage = clientReader.readLine()) != null) {

                                    // Richiamo delle funzioni per implementare i comandi del terminale
                                    if (clientMessage.startsWith("ls")) clientWriter.println(funzioni_server.ls());
                                    else if(clientMessage.startsWith("cd ")) clientWriter.println(funzioni_server.cd(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("mkdir ")) clientWriter.println(funzioni_server.mkdir(clientMessage.substring(6)));
                                    else if(clientMessage.startsWith("touch ")) clientWriter.println(funzioni_server.touch(clientMessage.substring(6)));
                                    else if(clientMessage.startsWith("cp ")) clientWriter.println(funzioni_server.cp(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("mv ")) clientWriter.println(funzioni_server.mv(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("rm ")) clientWriter.println(funzioni_server.rm(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("passwd")) {
                                        clientWriter.println("Inserisci nuova password:");
                                        String newPassword = clientReader.readLine();
                                        funzioni_server.passwd(username, newPassword, clientWriter);
                                    }
                                    else if(clientMessage.startsWith("exit")) funzioni_server.exit();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                System.out.println("fine");
                            }
                        }).start();
                    }
                } else if ("2".equals(choice)) {
                    // Registrazione
                    clientWriter.println("Inserisci username:");
                    String username = clientReader.readLine();
                    clientWriter.println("Inserisci password:");
                    String password = clientReader.readLine();

                    funzioni_server.registerUser(username,password);

                    clientWriter.println("Vuoi accedere con questi dati? (si, no)");
                    String risposta = clientReader.readLine();

                    if(risposta.equals("si")){
                        clientWriter.println(GREEN + "Sei loggato!" + RESET);

                        // legge da input del client
                        new Thread(() -> {
                            try {
                                // Testo preso dal client
                                String clientMessage;

                                // Help comandi
                                clientWriter.println("Elenco dei comandi:");
                                clientWriter.println("passwd -> Cambia la password ");
                                clientWriter.println("ls -> Visualizza i contenuti della directory ");
                                clientWriter.println("cd -> Cambia directory");
                                clientWriter.println("mkdir <nome cartella> -> crea una cartella");
                                clientWriter.println("touch <nome file> -> crea un file");
                                clientWriter.println("cp <nome file> <directory per copiarlo>/<nome file> -> crea una copia di questo file nella cartella");
                                clientWriter.println("mv <nome file> <directory per spostarlo>/<nome file> -> sposta un file in un altra cartella");
                                clientWriter.println("rm <nome file o cartella> -> rimuove il file o la cartella" + RESET);

                                // Finquando ci sono cose scritte esegui
                                while ((clientMessage = clientReader.readLine()) != null) {

                                    // Richiamo delle funzioni per implementare i comandi del terminale
                                    if (clientMessage.startsWith("ls")) clientWriter.println(funzioni_server.ls());
                                    else if(clientMessage.startsWith("cd ")) clientWriter.println(funzioni_server.cd(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("mkdir ")) clientWriter.println(funzioni_server.mkdir(clientMessage.substring(6)));
                                    else if(clientMessage.startsWith("touch ")) clientWriter.println(funzioni_server.touch(clientMessage.substring(6)));
                                    else if(clientMessage.startsWith("cp ")) clientWriter.println(funzioni_server.cp(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("mv ")) clientWriter.println(funzioni_server.mv(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("rm ")) clientWriter.println(funzioni_server.rm(clientMessage.substring(3)));
                                    else if(clientMessage.startsWith("passwd")) {
                                        clientWriter.println("Inserisci nuova password:");
                                        String newPassword = clientReader.readLine();
                                        funzioni_server.passwd(username, newPassword, clientWriter);
                                    }
                                    else if(clientMessage.startsWith("exit")) funzioni_server.exit();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try{
                                    System.out.println("fine");
                                    clientWriter.close();
                                    clientReader.close();
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else funzioni_server.exit();
                } else {
                    clientWriter.println("Scelta non valida. Disconnessione.");
                    clientSocket.close();
                    continue;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
