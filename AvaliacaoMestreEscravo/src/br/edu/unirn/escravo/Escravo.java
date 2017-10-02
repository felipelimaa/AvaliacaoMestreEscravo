package br.edu.unirn.escravo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.ParseConversionEvent;

import br.edu.unirn.util.PrimoThread;

public class Escravo {

	public static void main(String[] args) throws Exception {

		DatagramPacket data = new DatagramPacket(new byte[512], 512);
		DatagramSocket datagramSocket = new DatagramSocket(2000);

		datagramSocket.receive(data);
		System.out.println(new String(data.getData()));
		data.setData(InetAddress.getLocalHost().getHostAddress().getBytes());
		datagramSocket.send(data);
		datagramSocket.close();

		ServerSocket server = new ServerSocket(2000);

		Socket sc = server.accept();
		
		InetAddress ipMestre = sc.getInetAddress();

		BufferedReader reader = new BufferedReader(new InputStreamReader(sc.getInputStream()));
		String valor = reader.readLine();
		String[] array = valor.split("\\|");
		int valorInicial = Integer.parseInt(array[0]);
		int valorFinal = Integer.parseInt(array[1]);
		int n = 12;

		reader.close();
		sc.close();

		int intervalo = (valorFinal - valorInicial) / n;

		PrimoThread[] threads = new PrimoThread[n];

		// inicia todas as Threads
		for (int i = 0; i < n; i++) {

			PrimoThread p = new PrimoThread(valorInicial + intervalo * i, valorInicial + intervalo * (i + 1));
			p.start();
			threads[i] = p;
		}

		// Espera todas as threads finalizarem
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		int resultado = 0;
		for (int i = 0; i < threads.length; i++) {
			resultado += threads[i].getTotalPrimos();
		}
		
		Socket socket = new Socket(ipMestre, 2000);
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);
		out.println(valorInicial + "|" + valorFinal + "|" + resultado);
		
		out.close();
		socket.close();
	}

}
