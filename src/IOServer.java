import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

class IOServer extends Thread {
    private HashMap<String, Board> boards;
    private ArrayList<Boolean> isDraws;
    private ArrayList<Socket> sockets;
    private String name;
    private int id;

    IOServer(ArrayList<Socket> _sockets, ArrayList<Boolean> _isDraws, HashMap<String, Board> _boards) {
        boards = _boards;
        isDraws = _isDraws;
        sockets = _sockets;
        id = sockets.size() - 1;
        name = null;
    }

    private void setup() throws Exception {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sockets.get(id).getOutputStream()));
        for (int i = 0; i < boards.get(name).xArray.size(); ++i) {
            out.write( boards.get(name).colors.get(i).getRGB() + "\n");
            out.write(boards.get(name).strokes.get(i) + "\n");
            for (int j = 0; j < boards.get(name).xArray.get(i).size(); ++j) {
                out.write( boards.get(name).xArray.get(i).get(j) + " " + boards.get(name).yArray.get(i).get(j) + "\n");
            }
            out.write( "\n");
        }
        out.write( "\n");
        out.flush();
    }

    public void run() {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sockets.get(id).getOutputStream()));
            out.write(boards.size() + "\n");
            for (HashMap.Entry<String, Board> e : boards.entrySet()) {
                out.write(e.getKey() + "\n");
            }
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(sockets.get(id).getInputStream()));
            name = in.readLine();
            if (boards.get(name) == null) {
                boards.put(name, new Board());
            }
            out.write(name + "\n");
            out.flush();

            setup();
            isDraws.set(id, true);
            while (true) {
                in = new BufferedReader(new InputStreamReader(sockets.get(id).getInputStream()));
                String str = in.readLine();
                if (str == null) {
                    throw new Exception();
                }
                boards.get(name).addNewLine(str);
                for (int i = 0; i < sockets.size(); ++i) {
                    if (i != id && sockets.get(i) != null && isDraws.get(i)) {
                        out = new BufferedWriter(new OutputStreamWriter(sockets.get(i).getOutputStream()));
                        out.write(str + "\n");
                        out.flush();
                    }
                }
            }
        } catch(Exception ignored) {
            sockets.set(id, null);
            System.out.println("User disconnected!");
        }
    }

}
