import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class UDPServer {
    private static List<InetAddress> clientAddresses = new ArrayList<>();
    private static List<Integer> clientPorts = new ArrayList<>();

    public static void main(String[] args) {
        DatagramSocket serverSocket = null;

        try {
            // 创建服务器端Socket，指定端口
            serverSocket = new DatagramSocket(9876);

            // 创建接收数据的字节数组
            byte[] receiveData = new byte[1024];

            while (true) {
                // 创建接收数据的DatagramPacket对象
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                // 接收数据
                serverSocket.receive(receivePacket);

                // 获取客户端地址和端口
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();


                // 从DatagramPacket中获取客户端发送的数据
                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());


                // 如果是初始化消息，则将客户端添加到列表中
                if (clientMessage.equals("INIT")||clientMessage.equals("INIT2")) {
                    if (!clientPorts.contains(clientPort)) {
                        clientAddresses.add(clientAddress);
                        clientPorts.add(clientPort);
                        System.out.println("客户端加入 (" + clientAddress + ":" + clientPort + ")");
                        String m="";
                        byte[] sendData = m.getBytes();
                        DatagramPacket replyToSenderPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                        serverSocket.send(replyToSenderPacket);
                    }
                } else {
                    // 输出客户端发送的数据
                    System.out.println("收到客户端消息 (" + clientAddress + ":" + clientPort + "): " + clientMessage);
                    // 向所有客户端广播消息
                    broadcastMessage(clientMessage, clientAddress, clientPort);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }

    private static void broadcastMessage(String message, InetAddress senderAddress, int senderPort) {
        DatagramSocket broadcastSocket = null;

        try {
            broadcastSocket = new DatagramSocket();

            byte[] sendData = message.getBytes();

            for (int i = 0; i < clientAddresses.size(); i++) {
                InetAddress clientAddress = clientAddresses.get(i);
                int clientPort = clientPorts.get(i);

                // 将消息发送给消息发送者
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                broadcastSocket.send(sendPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (broadcastSocket != null && !broadcastSocket.isClosed()) {
                broadcastSocket.close();
            }
        }
    }
}
