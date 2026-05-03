package network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Simple client-side socket wrapper for sending Request and receiving Response.
 */
public class ClientSocket implements AutoCloseable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientSocket(String host, int port, int timeoutMs) throws Exception {
        this.socket = new Socket(host, port);
        this.socket.setSoTimeout(timeoutMs);
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
        this.in = new ObjectInputStream(this.socket.getInputStream());
    }

    public Response sendRequest(Request req) throws Exception {
        synchronized (out) {
            out.writeObject(req);
            out.flush();
        }
        Object resp = in.readObject();
        if (resp instanceof Response) {
            return (Response) resp;
        }
        throw new IllegalStateException("Unexpected response type: " + resp.getClass());
    }

    @Override
    public void close() throws Exception {
        try { if (in != null) in.close(); } catch (Exception e) {}
        try { if (out != null) out.close(); } catch (Exception e) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception e) {}
    }
}
