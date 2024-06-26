package Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import File.Receive;
import File.SendFile;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuGui extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField date;
    private static DatagramSocket socket = null;
    private static int serverPort = 9876;
    private static InetAddress serverAddress;
    private JTextField username_1;
    private static DatagramPacket receivePacket;
    private JLabel lblHTnSinh;
    private JTextField time_1;
    private JButton ntNopBai;
    private SendFile file;

    private volatile boolean listening = true;

    private static void sendDiemDanhRequestToServer(String username, String time) {
        String request = "DIEMDANH " + username + "," + time;
        statClient(request);
    }

    public static void statClient(String data) {
        try {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serverAddress = InetAddress.getByName("192.168.96.1");

                        String request = data;
                        byte[] sendData = request.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress,
                                serverPort);
                        socket.send(sendPacket);

                        byte[] receiveData = new byte[1024];
                        receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);

                        String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        handleDiemDanhResponse(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleDiemDanhResponse(String response) {
        switch (response) {
            case "OK":
                JOptionPane.showMessageDialog(null, "Đã điểm danh", response, JOptionPane.OK_OPTION);
                break;
            case "INVALID":
                JOptionPane.showMessageDialog(null, "Invalid username or password!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                break;
            case "FAILE":
                JOptionPane.showMessageDialog(null, "Bạn Đã điểm danh không thể điểm danh tiếp", response,
                        JOptionPane.OK_OPTION);
                break;
            case "ERROR":
                JOptionPane.showMessageDialog(null, "Bạn đang điểm danh giúp người khác đấy", response,
                        JOptionPane.OK_OPTION);
                break;
            case "FILE":
                int option = JOptionPane.showConfirmDialog(null, "Do you want to save the received file?", "File Received", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                	Receive.handleFileRequest(socket, null);
                }
                break;

            default:
                break;
        }
    }
    
    private static void saveReceivedFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(selectedFile);
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                fileOutputStream.write(receivePacket.getData(), 0, receivePacket.getLength());

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Lưu file thành công", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Lưu file thất bại", "Error", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = dateFormat.format(new Date());
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String dateTime1 = dateFormat1.format(new Date());
        date.setText(dateTime);
        time_1.setText(dateTime1);
    }

    public MenuGui(String username, DatagramSocket socket) {
        this.socket = socket;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 402, 282);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        date = new JTextField();
        date.setBounds(159, 88, 197, 40);
        contentPane.add(date);
        date.setColumns(10);

        JLabel lblNewLabel = new JLabel("Ngày");
        lblNewLabel.setForeground(Color.RED);
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 19));
        lblNewLabel.setBounds(21, 88, 128, 40);
        contentPane.add(lblNewLabel);

        JButton btDiemDanh = new JButton("Điểm danh");
        btDiemDanh.setBounds(199, 195, 157, 40);
        contentPane.add(btDiemDanh);

        username_1 = new JTextField();
        username_1.setText(username);
        username_1.setColumns(10);
        username_1.setBounds(159, 31, 197, 40);
        contentPane.add(username_1);

        lblHTnSinh = new JLabel("Username");
        lblHTnSinh.setForeground(Color.RED);
        lblHTnSinh.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 19));
        lblHTnSinh.setBounds(21, 31, 128, 40);
        contentPane.add(lblHTnSinh);

        JLabel lblGi = new JLabel("Giờ");
        lblGi.setForeground(Color.RED);
        lblGi.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 19));
        lblGi.setBounds(21, 138, 128, 40);
        contentPane.add(lblGi);

        time_1 = new JTextField();
        time_1.setColumns(10);
        time_1.setBounds(159, 138, 197, 40);
        contentPane.add(time_1);

        ntNopBai = new JButton("Nộp bài");
        ntNopBai.setBounds(32, 195, 157, 40);
        contentPane.add(ntNopBai);

        ntNopBai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });

        btDiemDanh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = username_1.getText();
                String time = date.getText();
                sendDiemDanhRequestToServer(username_1.getText(), time);
                dispose();
            }
        });

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDateTime();
            }
        });
        timer.start();

        // Add a window listener to call closeWindow() when the window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        // Start the listening thread
        startListeningThread();
    }

    private void startListeningThread() {
        Thread listenerThread = new Thread(() -> {
            while (listening) {
                try {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);

                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    handleDiemDanhResponse(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        listenerThread.start();
    }

    private void stopListening() {
        listening = false;
    }

    private void closeWindow() {
        stopListening();
        dispose();
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            file = new SendFile();
            try {
                file.sendFileRequest(selectedFile, serverAddress, serverPort,"FILECLIENT");
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Nộp thành công", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Nộp thất bại", "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }
    }
}
