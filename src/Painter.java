import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class Painter extends JFrame implements MouseListener, MouseMotionListener {
    private Board board = new Board();
    private String name;
    private Socket socket;
    private int stroke;
    private Color color;
    private int cur;
    private int curPaint;

    Painter(Socket _socket, String _name) {
        socket = _socket;
        name = _name;
        color = Color.BLACK;
        stroke = 3;
        curPaint = 0;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setTitle("Painter");
        setVisible(true);
        JButton button = new JButton("Ластик");
        button.setSize(100, 50);

        JButton redButton = new JButton("Красный");
        JButton greenButton = new JButton("Зеленый");
        JButton blueButton = new JButton("Синий");

        redButton.setSize(100, 50);
        greenButton.setSize(100, 50);
        blueButton.setSize(100, 50);

        class ButtonActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                if (stroke == 3) {
                    stroke = 20;
                    color = new Color(239, 238, 239);
                } else {
                    stroke = 3;
                    color = Color.BLACK;
                }
            }
        }

        class ColorListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                stroke = 3;
                if (e.getActionCommand().equals("Красный")) {
                    color = Color.RED;
                } else if (e.getActionCommand().equals("Зеленый")) {
                    color = Color.GREEN;
                } else if (e.getActionCommand().equals("Синий")) {
                    color = Color.BLUE;
                } else {
                    color = Color.BLACK;
                }
            }
        }

        ActionListener actionListener = new ButtonActionListener();
        ActionListener colorListener = new ColorListener();




        JMenuBar menu = new JMenuBar();

        button.addActionListener(actionListener);
        redButton.addActionListener(colorListener);
        greenButton.addActionListener(colorListener);
        blueButton.addActionListener(colorListener);

        menu.add(button);
        menu.add(redButton);
        menu.add(greenButton);
        menu.add(blueButton);


        setJMenuBar(menu);
        setSize(800, 600);
        revalidate();

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void paint(Graphics g) {
        for (int i = curPaint; i < board.xArray.size(); ++i) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(board.strokes.get(i)));
            g2.setColor(board.colors.get(i));
            for (int j = 0; j < board.xArray.get(i).size() - 1; ++j) {
                g2.drawLine(board.xArray.get(i).get(j), board.yArray.get(i).get(j),
                            board.xArray.get(i).get(j + 1), board.yArray.get(i).get(j + 1));
            }
            int xBack = board.xArray.get(i).get(board.xArray.get(i).size() - 1);
            int yBack = board.yArray.get(i).get(board.xArray.get(i).size() - 1);
            g2.drawLine(xBack, yBack, xBack, yBack);
            if (i > curPaint) {
                curPaint = i;
            }
        }
    }

    void setup() throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String str = in.readLine();
        while (!str.equals("")) {
            board.colors.add(new Color(Integer.parseInt(str)));
            board.strokes.add(Integer.parseInt(in.readLine()));
            board.xArray.add(new ArrayList<>());
            board.yArray.add(new ArrayList<>());
            str = in.readLine();
            while (!str.equals("")) {
                board.xArray.get(board.xArray.size() - 1).add(Integer.parseInt(str.split(" ")[0]));
                board.yArray.get(board.yArray.size() - 1).add(Integer.parseInt(str.split(" ")[1]));
                str = in.readLine();
            }
            str = in.readLine();
        }
        repaint();
    }

    void pullRepaint(String str) {
        board.addNewLine(str);
        repaint();
    }

    private void pushRepaint(MouseEvent e) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            out.write(cur + " " + e.getX() + " " + e.getY() + " " + color.getRGB() + " " + name + " " + stroke + "\n");
            out.flush();
        } catch (IOException ignored) {}
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        cur = board.xArray.size();
        board.colors.add(color);
        board.strokes.add(stroke);
        board.xArray.add(new ArrayList<>());
        board.yArray.add(new ArrayList<>());
        board.xArray.get(cur).add(e.getX());
        board.yArray.get(cur).add(e.getY());
        pushRepaint(e);
    }

    public void mouseDragged(MouseEvent e) {
        board.xArray.get(cur).add(e.getX());
        board.yArray.get(cur).add(e.getY());
        pushRepaint(e);
    }

    public void mouseClicked(MouseEvent e) {
        cur = board.xArray.size();
        board.colors.add(color);
        board.strokes.add(stroke);
        board.xArray.add(new ArrayList<>());
        board.yArray.add(new ArrayList<>());
        board.xArray.get(cur).add(e.getX());
        board.yArray.get(cur).add(e.getY());
        pushRepaint(e);
    }

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {}
}
