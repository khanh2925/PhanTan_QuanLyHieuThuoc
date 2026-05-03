package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try(Socket socket = new Socket("H81M27", 9090);
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ){

            int choice = 0;
            Request request = null;
            while (true){
                System.out.println("==Menu==");
                System.out.println("1. Find by company's id");
                System.out.println("2. List companies");
                choice = scanner.nextInt();

                switch (choice){
                    case 1 -> {
                        CommandType commandType = CommandType.COMPANY_FIND_BY_ID;
                        String companyId = "CP1";
                        request = Request.builder().commandType(commandType).data(companyId).build();
                    }
                    case 2 -> {
                        CommandType commandType = CommandType.COMPANY_LOAD_ALL;

                    }
                }

                out.writeObject(request);
                out.flush();

                Response response = (Response) in.readObject();
                System.out.println(response);

            }

        }catch (Exception ex){
            throw  new RuntimeException(ex);
        }
    }
}
