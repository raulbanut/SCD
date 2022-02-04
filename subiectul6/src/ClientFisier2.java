import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientFisier2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("Write message");
                System.out.println(">> ");
                String message = scanner.nextLine();

                DatagramSocket socket = new DatagramSocket();
                byte[] buf = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length,
                        InetAddress.getByName("localhost"), 1977);
                socket.send(packet);
                //pac15ket = new DatagramPacket(buf, buf.length);

                System.out.println("Waiting for the message from server");

                buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length,
                        InetAddress.getByName("localhost"), 1977);
                socket.receive(packet);
                System.out.println(">> " + new String(new String(packet.getData(), 0, packet.getLength())));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
