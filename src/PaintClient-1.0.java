import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(InetAddress.getByName(args[0]), 4004);
        String name = MainMenu(socket);
        Painter painter = new Painter(socket, name);
        painter.setup();

        try {
            while (true) {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str = in.readLine();
                if (str == null) {
                    throw new Exception();
                }
                painter.pullRepaint(str);
            }
        } catch(Exception ignored) {
            socket.close();
        }
    }

    static String MainMenu(Socket socket) throws Exception {
        JFrame mm = new JFrame();
        mm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mm.setTitle("Main Menu");
        mm.setVisible(true);
        JPanel contents = new JPanel(new FlowLayout());
        JTextField smallField = new JTextField(15);

        class ButtonActionListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                String str;
                if (e.getActionCommand().equals("Создать новую доску")) {
                    str = smallField.getText();
                } else {
                    str = e.getActionCommand();
                }
                try {
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    out.write(str + "\n");
                    out.flush();
                } catch (IOException ignored) {}
            }
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        int size = Integer.parseInt(in.readLine());
        Box box = Box.createVerticalBox();

        if (size > 0) {
            box.add(new JLabel("Присоединиться к существующей доске"));
        }

        JButton button;
        ActionListener actionListener = new ButtonActionListener();
        for (int i = 0; i < size; ++i) {
            button = new JButton(in.readLine());
            button.addActionListener(actionListener);
            box.add(button);
        }
        contents.add(box, BorderLayout.NORTH);
        box = Box.createVerticalBox();
        box.add(smallField);
        button = new JButton("Создать новую доску");
        button.setActionCommand(null);
        button.addActionListener(actionListener);
        box.add(button);

        contents.add(box, BorderLayout.SOUTH);
        mm.setContentPane(contents);

        mm.setSize(400, 600);
        String name = in.readLine();
        mm.setVisible(false);
        return name;
    }
}
