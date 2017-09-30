package br.edu.unirn.mestre;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Mestre {

	public static void main(String[] args) throws Exception {

		int valorInicial = 1;
		int valorFinal = 999999;
		int quantidadeTentativa = 1;

		ArrayList<InetAddress> ipsescravos = new ArrayList<>();

		//////// PRIMEIRA PARTE //////////
		for (int i = 0; i < quantidadeTentativa; i++) {
			DatagramSocket datagramSocket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(new byte[512], 512, InetAddress.getByName("10.14.21.255"), 2000);
			packet.setData("OK".getBytes());
			System.out.println("Aguardando mensagem...");
			datagramSocket.send(packet); // método bloqueante até receber uma mensagem
			packet = new DatagramPacket(new byte[512], 512);
			datagramSocket.receive(packet);
			InetAddress addrEscravo = InetAddress.getByName(new String(packet.getData()));
			System.out.println("IP: " + addrEscravo.toString());
			if (!ipsescravos.contains(addrEscravo)) {
				ipsescravos.add(addrEscravo);
			}
			Thread.sleep(1000);
		}
		/////// FIM PRIMEIRA PARTE /////////
		System.out.println(ipsescravos.size());
		///// PRIMO THREADS //////

		int intervalo = (valorFinal - valorInicial) / ipsescravos.size();

		for (int i = 0; i < ipsescravos.size(); i++) {

			Socket socket = new Socket(ipsescravos.get(i), 2000);
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			out.println((valorInicial + intervalo * i) + "|" + (valorInicial + intervalo * (i + 1)));
			out.close();
			socket.close();
		}
		////
		ServerSocket server = new ServerSocket(2000);

		for (int i = 0; i < ipsescravos.size(); i++) {
			Socket sc = server.accept();
			BufferedReader reader = new BufferedReader(new InputStreamReader(sc.getInputStream()));
			System.out.println(reader.readLine());
			sc.close();
		}
		server.close();

	}

}
