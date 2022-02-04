import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class ServerUDP extends Thread {
    boolean running = true;

    public ServerUDP() {
        start();
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            DatagramSocket socket = new DatagramSocket(1977);

            int suma = 0;
            int nrClient = 0;
            InetAddress[] address = new InetAddress[3];
            int[] port = new int[3];

            while (running) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                while (nrClient < 3) {
                    //asteapta client
                    socket.receive(packet);
                    System.out.println(">> " + new String(packet.getData(), 0, packet.getLength()));
                    suma = suma + Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));

                    //citeste adresa si portul clientului
                    address[nrClient] = packet.getAddress();
                    port[nrClient] = packet.getPort();

                    nrClient++;
                }

                String message = String.valueOf(suma);
                for (int i = 0; i < nrClient; i++) {
                    buf = message.getBytes();
                    packet = new DatagramPacket(buf, buf.length, address[i], port[i]);
                    socket.send(packet);
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerUDP udpServer = new ServerUDP();
    }
}
