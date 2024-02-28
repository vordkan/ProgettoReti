import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class funzioni_server extends server {

    // Settaggio colori
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";

    // Variabile per memorizzare la directory corrente
    private static String currentDirectory = ".";
    /* ---------------------------------------------------------------------------------------------------------------*/
    // FUNZIONI DI REGISTRAZIONE E LOGIN
    // Metodo verifica utente
    public static boolean isValidUser(String username, String password) {
        // cripto la password per vedere se corrisponde a quella salvata
        password = hashPassword(password);
        // leggo dal file i nomi degli utenti
        try (BufferedReader fileReader = new BufferedReader(new FileReader("user.txt"))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                // Divide la riga in due parti separate dal carattere ':'
                String[] parts = line.split(":");
                // Verifica se la riga è stata divisa correttamente in due parti
                if (parts.length == 2) {
                    // mi divido il nome utente e la password in due variabili
                    String storedUsername = parts[0].trim();
                    String storedPassword = parts[1].trim();
                    // Confronta lo username e la password forniti con quelli memorizzati
                    if (username.equals(storedUsername) && password.equals(storedPassword)) {
                        return true; // Restituisce true se lo username e la password corrispondono
                    }
                }
            }
        } catch (IOException e) {
            // Gestisce eventuali eccezioni di input/output
            e.printStackTrace();
        }
        // Restituisce false se lo username e la password non corrispondono a nessun utente
        return false;
    }

// Metodo per registrare un nuovo utente
    public static void registerUser(String username, String password) {
        // Scrive nel file degli utenti
        try (PrintWriter writer = new PrintWriter(new FileWriter("user.txt", true))) {
            // crittografare la password
            password = hashPassword(password);
            // Scrive il nome utente e la password su una nuova riga
            writer.println(username + ":" + password);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------------------------------------------------------------------------------------------------------------*/
    //hasing pass
    public static String hashPassword(String password) {
        try {
            // Ottiene un'istanza di MessageDigest per SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Aggiunge la password come input al digest
            md.update(password.getBytes());

            // Ottiene l'array di byte dell'hash della password
            byte[] hashedPasswordBytes = md.digest();

            // Converti l'array di byte in una rappresentazione esadecimale
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPasswordBytes) {
                sb.append(String.format("%02x", b));
            }

            // Restituisce l'hash della password come stringa esadecimale
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Gestisce il caso in cui l'algoritmo SHA-256 non sia supportato
            e.printStackTrace();
            return null;
        }
    }
    /* ---------------------------------------------------------------------------------------------------------------*/
    // FUNZIONI PER IL TERMINALE

    public static void passwd(String username, String newPassword, PrintWriter clientWriter) {
        try {

            newPassword = hashPassword(newPassword);
            // Apri il file degli utenti in modalità di lettura e scrittura
            File userFile = new File("user.txt");
            BufferedReader fileReader = new BufferedReader(new FileReader(userFile));
            StringBuilder fileContent = new StringBuilder();

            String line;
            boolean userFound = false;

            // Scansiona il file degli utenti
            while ((line = fileReader.readLine()) != null) {
                // Divide la riga in nome utente e password
                String[] parts = line.split(":");
                String storedUsername = parts[0].trim();

                // Se l'utente corrente è quello per cui vogliamo cambiare la password, aggiorna la riga
                if (storedUsername.equals(username)) {
                    fileContent.append(username).append(":").append(newPassword).append("\n");
                    userFound = true;
                } else {
                    // Altrimenti, aggiungi la riga originale al nuovo contenuto del file
                    fileContent.append(line).append("\n");
                }
            }

            fileReader.close();

            // Se l'utente è stato trovato e la password è stata cambiata, sovrascrivi il file con il nuovo contenuto
            if (userFound) {
                PrintWriter fileWriter = new PrintWriter(new FileWriter(userFile));
                fileWriter.print(fileContent);
                fileWriter.close();
                clientWriter.println(GREEN + "Password cambiata con successo" + RESET);
            } else {
                clientWriter.println(RED + "Utente non trovato: " + username + RESET);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Funzione per il comando ls
    public static String ls() {
        // Ottiene il riferimento alla directory corrente
        File directory = new File(currentDirectory);
        // Ottiene un array di file e directory all'interno della directory corrente
        File[] files = directory.listFiles();
        // Verifica se l'array non è nullo
        if (files != null) {
            // Inizializza una stringa per contenere il contenuto della directory
            StringBuilder content = new StringBuilder("Contenuto della directory: \n");
            // Itera attraverso ogni file o directory all'interno della directory corrente
            for (File file : files) {
                // Aggiunge il nome del file o della directory alla stringa di contenuto
                content.append(file.getName()).append("\t");
            }
            // Restituisce la stringa contenente il contenuto della directory
            return content.toString();
        }
        // Se non ci sono file o directory, restituisce un messaggio di avviso
        return "Nessun file o directory trovati.";
    }

    // Funzione per il comando cd
    public static String cd(String newDirectory) {
        // Verifica se il comando è "cd .."
        if (newDirectory.equals("..")) {
            // Ottiene la directory padre della directory corrente
            File parentDirectory = new File(currentDirectory).getParentFile();
            // Verifica se la directory padre esiste
            if (parentDirectory != null && parentDirectory.exists()) {
                // Imposta la directory corrente alla directory padre
                currentDirectory = parentDirectory.getAbsolutePath();
                // Restituisce un messaggio confermando il cambio di directory
                return "Directory corrente: " + currentDirectory;
            } else {
                // Se la directory padre non esiste, restituisce un messaggio di errore
                return RED + "Directory padre non trovata" + RESET;
            }
        } else {
            // Altrimenti, gestisce il caso normale cambiando la directory come prima
            File directory = new File(currentDirectory, newDirectory);
            // Verifica se il percorso corrisponde a una directory esistente
            if (directory.isDirectory() && directory.exists()) {
                // Imposta la directory corrente
                currentDirectory = directory.getAbsolutePath();
                // Restituisce un messaggio confermando il cambio di directory
                return "Directory corrente: " + currentDirectory;
            } else {
                // Se la directory non esiste, restituisce un messaggio di errore
                return RED + "Directory non trovata" + RESET;
            }
        }
    }

// Funzione per il comando mkdir
    public static String mkdir(String newDirectoryName) {
        // Costruisci il percorso completo della nuova directory utilizzando il path corrente
        String directoryPath = currentDirectory + File.separator + newDirectoryName;

        // Crea un oggetto File per la nuova directory
        File newDir = new File(directoryPath);

        // Verifica se la directory non esiste
        if (!newDir.exists()) {
            // Prova a creare la directory e restituisce un messaggio di conferma
            if (newDir.mkdirs()) {
                return GREEN + "Directory creata con successo" + RESET;
            } else {
                // Se si verifica un errore durante la creazione della directory, restituisce un messaggio di errore
                return RED + "Errore durante la creazione della directory" + RESET;
            }
        } else {
            // Se la directory esiste già, restituisce un messaggio informativo
            return RED + "La directory esiste già" + RESET;
        }
    }

// Funzione per il comando touch
    public static String touch(String newFileName) {
        // Costruisci il percorso completo del nuovo file utilizzando il path corrente
        String filePath = currentDirectory + File.separator + newFileName;

        // Crea un oggetto File per il nuovo file
        File newFile = new File(filePath);

        try {
            // Prova a creare il nuovo file
            if (newFile.createNewFile()) {
                // Restituisce un messaggio di conferma se il file è stato creato con successo
                return GREEN + "File creato con successo" + RESET;
            } else {
                // Se il file esiste già, restituisce un messaggio di errore
                return RED + "Errore durante la creazione del file" + RESET;
            }
        } catch (IOException e) {
            // Gestisce eventuali eccezioni durante la creazione del file
            e.printStackTrace();
            return RED + "Errore durante la creazione del file: " + e.getMessage() + RESET;
        }
    }

    // Funzione per il comando cp (copy)
    public static String cp(String message) {
        // Divide il messaggio in due parti utilizzando lo spazio come delimitatore
        String[] parts = message.split(" ", 2);

        // Verifica se ci sono esattamente due parti nel messaggio
        if (parts.length == 2) {
            // Estrae il percorso del file sorgente e il percorso del file destinazione
            String sourceFilePath = parts[0]; // Percorso del file sorgente
            String destinationFilePath = parts[1]; // Percorso del file destinazione

            // Crea oggetti File per il file sorgente e il file destinazione
            File sourceFile = new File(sourceFilePath);
            File destinationFile = new File(destinationFilePath);

            try (FileInputStream fis = new FileInputStream(sourceFile);
                 FileOutputStream fos = new FileOutputStream(destinationFile)) {

                byte[] buffer = new byte[1024];
                int length;

                // Legge dal file sorgente e scrive nel file destinazione
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }

                // Restituisce un messaggio di conferma se la copia è avvenuta con successo
                return GREEN + "File copiato con successo" + RESET;
            } catch (IOException e) {
                // Gestisce eventuali eccezioni durante la copia del file
                e.printStackTrace();
                return RED + "Errore durante la copia del file: " + e.getMessage() + RESET;
            }
        } else {
            // Se il messaggio non è nel formato corretto, restituisce un messaggio informativo
            return RED + "Formato del comando non corretto. Utilizzo: cp <file_origine> <file_destinazione>" + RESET;
        }
    }

    // Funzione per il comando mv
    public static String mv(String message) {
        // Divide il messaggio in due parti usando lo spazio come delimitatore
        String[] parts = message.split(" ", 2);

        // Assicurati che ci siano due parti nel messaggio
        if (parts.length == 2) {
            // Estrae il percorso di origine e il percorso di destinazione
            String sourcePath = parts[0]; // Percorso di origine
            String destinationPath = parts[1]; // Percorso di destinazione

            // Crea oggetti File per il file di origine e il file di destinazione
            File sourceFile = new File(sourcePath);
            File destinationFile = new File(destinationPath);

            try {
                // Sposta il file dalla posizione di origine a quella di destinazione
                boolean success = sourceFile.renameTo(destinationFile);
                // Verifica se lo spostamento ha avuto successo
                if (success) {
                    // Restituisce un messaggio di conferma se il file è stato spostato con successo
                    return GREEN + "File spostato con successo" + RESET;
                } else {
                    // Se lo spostamento non ha avuto successo, restituisce un messaggio di errore
                    return RED + "Impossibile spostare il file" + RESET;
                }
            } catch (Exception e) {
                // Gestisce eventuali eccezioni durante lo spostamento del file
                e.printStackTrace();
                return RED + "Errore durante lo spostamento del file: " + e.getMessage() + RESET;
            }
        } else {
            // Se il messaggio non è nel formato corretto, restituisce un messaggio informativo
            return RED + "Formato del comando non corretto. Utilizzo: mv <file_origine> <file_destinazione>" + RESET;
        }
    }

    // Funzione per il comando rm
    public static String rm(String message) {
        // Costruisci il percorso completo del file o della directory da rimuovere utilizzando il path corrente
        String filePath = currentDirectory + File.separator + message;

        // Crea un oggetto File per il file o la directory da rimuovere
        File fileOrDirectory = new File(filePath);

        // Verifica se il file o la directory esistono
        if (fileOrDirectory.exists()) {
            // Se il file o la directory esistono, prova a eliminarli
            if (fileOrDirectory.delete()) {
                // Restituisce un messaggio di conferma se il file o la directory sono stati rimossi con successo
                return GREEN + "File o directory rimossi con successo" + RESET;
            } else {
                // Se si verifica un errore durante l'eliminazione, restituisce un messaggio di errore
                return RED + "Impossibile rimuovere il file o la directory" + RESET;
            }
        } else {
            // Se il file o la directory non esistono, restituisce un messaggio di errore
            return RED + "Il file o la directory specificati non esistono" + RESET;
        }
    }

    // Funzione per il comando exit
    public static void exit() {
        // Esce dal programma
        System.exit(0);
    }
}

