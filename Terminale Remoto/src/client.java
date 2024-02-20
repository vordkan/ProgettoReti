import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client {
    public static void main(String[] args) {
        try {
            // creazione socket
            Socket socket = new Socket("localhost", 12347);

            // legge da socket -> socket.getInputStream()
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // scrive su socket -> socket.getOutputStream()
            PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true);

            // crea oggetto per leggere da input
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

            // leggi il messaggio di benvenuto dal server
            String welcomeMessage = serverReader.readLine();
            String welcomeMessage1 = serverReader.readLine();
            String welcomeMessage2= serverReader.readLine();
            System.out.println(welcomeMessage + '\n' + welcomeMessage1 + '\n' + welcomeMessage2);

            // Leggi scelta utente
            String choice = userInputReader.readLine();
            serverWriter.println(choice);

            if ("1".equals(choice)) {
                // Login
                String loginUtente = serverReader.readLine();
                System.out.print(loginUtente);
                String username = userInputReader.readLine();
                serverWriter.println(username);

                String loginPassword = serverReader.readLine();
                System.out.print(loginPassword);
                String password = userInputReader.readLine();
                serverWriter.println(password);
            } else if ("2".equals(choice)) {
                // Registrazione
                String registrazioneUtente = serverReader.readLine();
                System.out.print(registrazioneUtente);
                String username = userInputReader.readLine();
                serverWriter.println(username);

                String registrazionePassword = serverReader.readLine();
                System.out.print(registrazionePassword);
                String password = userInputReader.readLine();
                serverWriter.println(password);

                // se si vuole effettuare il login con questi dati
                String risposta = serverReader.readLine();
                System.out.println(risposta);
                String scelta = userInputReader.readLine();
                serverWriter.println(scelta);
            } else {
                String noValid = serverReader.readLine();
                System.out.println(noValid);
                socket.close();
                return;
            }

            // thread per leggere messaggi dal server e visualizzarli sulla console del client
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = serverReader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // thread per inviare comandi al server
            new Thread(() -> {
                try {
                    String userInput;
                    while ((userInput = userInputReader.readLine()) != null) {
                        serverWriter.println(userInput);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
