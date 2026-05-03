package network;

import dto.CompanyDto;
import service.CompanyService;
import service.impl.CompanyServiceImpl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {


        ExecutorService pool = Executors.newFixedThreadPool(10);
        try(ServerSocket serverSocket = new ServerSocket(9090);){

            System.out.println("Server is ready...");

            while (true){

                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                pool.submit(clientHandler);


            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}


class ClientHandler implements Runnable{
    private Socket socket;
    private CompanyService companyService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        companyService = new CompanyServiceImpl();
    }

    @Override
    public void run() {
        try(
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                ){

            while (true){
                Request request = (Request) in.readObject();
                CommandType commandType = request.getCommandType();
                Response response = null;
                switch (commandType){
                    case COMPANY_FIND_BY_ID -> {
                        String companyId = (String) request.getData();
                        CompanyDto companyDto = companyService.findById(companyId);
                        response = new Response();
                        response.setSuccess(true);
                        response.setData(companyDto);
                        response.setMessage(companyDto != null ? "Found" : "Not found");
                    }
                    case COMPANY_LOAD_ALL -> {}
                }

                out.writeObject(response);
                out.flush();

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}