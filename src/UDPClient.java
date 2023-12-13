import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 创建客户端Socket
            DatagramSocket clientSocket = new DatagramSocket();

            // 获取服务器地址
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 9876;

            // 发送初始化消息给服务器
            String initMessage = "INIT";
            byte[] initSendData = initMessage.getBytes();
            DatagramPacket initSendPacket = new DatagramPacket(initSendData, initSendData.length, serverAddress, serverPort);
            clientSocket.send(initSendPacket);

            // 创建接收数据的字节数组
            byte[] receiveData = new byte[1024];

            // 启动接收消息的线程
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);

                        String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.print("("+serverResponse+")\nusr1请输入消息: ");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // 发送用户消息
            while (true) {
                String message = scanner.nextLine();
                message = "usr1:" + message;
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);

                if (message.equals("usr1:exit")) {
                    break;
                }
            }

            // 等待接收线程结束
            receiveThread.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
