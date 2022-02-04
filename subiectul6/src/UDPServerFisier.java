import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UDPServerFisier extends Thread {
    boolean running = true;

    public UDPServerFisier() {
        start();
    }

    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            DatagramSocket socket = new DatagramSocket(1977);

            String filename = "output.txt";
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));

            int nrClient = 0;
            InetAddress[] address = new InetAddress[2];
            int[] port = new int[2];

            while (running) {
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                while (nrClient < 2) {
                    //asteapta client
                    socket.receive(packet);
                    System.out.println(">> " + new String(packet.getData(), 0, packet.getLength()));

                    //gchar.println(new String(packet.getData(), 0, packet.getLength()));
                    bufferedWriter.write(new String(packet.getData(), 0, packet.getLength()));
                    bufferedWriter.newLine();

                    //citeste adresa si portul clientului
                    address[nrClient] = packet.getAddress();
                    port[nrClient] = packet.getPort();

                    nrClient++;
                }

                bufferedWriter.close();
                //trimite un reply catre client
                //System.out.println("Write message");
                //System.out.println(">> ");
                //String message = scanner.nextLine();

                BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
                String message = bufferedReader.readLine();

                for (int i = 0; i < nrClient; i++) {
                    buf = message.getBytes();
                    packet = new DatagramPacket(buf, buf.length, address[i], port[i]);
                    socket.send(packet);
                    message = bufferedReader.readLine();

                }
                bufferedReader.close();
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UDPServerFisier udpServerFisier = new UDPServerFisier();
    }
}
