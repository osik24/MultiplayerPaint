import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

class Server {

    public static void main(String[] args) throws Exception {
        System.out.println("Server start!");
        ServerSocket server = new ServerSocket(4004);
        ArrayList<Socket> sockets = new ArrayList<>();
        HashMap<String, Board> boards = new HashMap<>();
        ArrayList<Boolean> isDraws = new ArrayList<>();
        while (true) {
            Socket socket = server.accept();
            sockets.add(socket);
            isDraws.add(false);
            new IOServer(sockets, isDraws, boards).start();
            System.out.println("New user connected!");
        }
    }
}
