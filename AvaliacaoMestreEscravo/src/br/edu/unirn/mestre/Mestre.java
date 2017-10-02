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
		int quantidadeTentativa = 3;

		ArrayList<InetAddress> ipsescravos = new ArrayList<>();

		//////// ENVIO DE BROADCAST USANDO PROTOCOLO UDP PARA ENCONTRAR ESCRAVOS DISPONÍVEIS //////////
		for (int i = 0; i < quantidadeTentativa; i++) { //Quantidade de tentativas para enxergar escravos
			DatagramSocket datagramSocket = new DatagramSocket(); //criando pacote TCP para ouvir conexões
			DatagramPacket packet = new DatagramPacket(new byte[512], 512, InetAddress.getByName("10.14.21.255"), 2000); //criação do pacote UDP configurado para enviar por broadcast
			packet.setData("MESTRE: Requisitando escravos...".getBytes());
			System.out.println("MESTRE: Aguardando retorno de escravos disponíveis...");
			datagramSocket.send(packet); // envio do pacote UDP
			packet = new DatagramPacket(new byte[512], 512); //criação de um novo pacote para garantir que todos os bytes que compoem o endereço IP serão enviados
			datagramSocket.receive(packet); //recepção do pacote enviado pelo escravo em TCP
			InetAddress addrEscravo = InetAddress.getByName(new String(packet.getData())); //lista de IP's, onde cada escravo disponível tem seu ip adicionado a lista
			System.out.println("IP: " + addrEscravo.toString());
			if (!ipsescravos.contains(addrEscravo)) {
				ipsescravos.add(addrEscravo);
			}
			Thread.sleep(1000);
		}
		/////// FIM PRIMEIRA PARTE /////////
		
		System.out.println("Escravos disponíveis: ");
		for ( int j = 0; j <= ipsescravos.size(); j++) { 
			System.out.println(ipsescravos.get(j)); //Impressão de lista com todos os escravos disponíveis para execução de rotinas
		}
		System.out.println("Há: " + ipsescravos.size() + " escravo(s) disponível(is) para realizar operações.");
		
		///// CALCULAR VALOR PARA CADA ESCRAVO PROCESSAR //////

		int intervalo = (valorFinal - valorInicial) / ipsescravos.size(); //Divisão de valores por escravo

		for (int i = 0; i < ipsescravos.size(); i++) { //envio dos valores para cada escravo

			Socket socket = new Socket(ipsescravos.get(i), 2000); //criação de pacote TCP, passando IP do escravo e porta
			PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			out.println((valorInicial + intervalo * i) + "|" + (valorInicial + intervalo * (i + 1))); //Envio dos valores iniciais e finais, com pipe separando-os
			out.close(); //fechamento da mensagem
			socket.close(); //fechamento do pacote
		}
		
		///// SERVIDOR TCP ESCUTANDO RESPOSTA DE CADA ESCRAVO CONFORME FINALIZAÇÃO DE CADA UM /////	
		ServerSocket server = new ServerSocket(2000); //Criação do Pacote TCP escutando a porta 2000

		for (int i = 0; i < ipsescravos.size(); i++) { //resposta de cada escravo
			Socket sc = server.accept(); //aceitação do pacote
			BufferedReader reader = new BufferedReader(new InputStreamReader(sc.getInputStream())); //leitura do pacote
			System.out.println(reader.readLine()); //impressão do conteúdo do pacote
			sc.close(); //fechamento de comunicação com o escravo
		}
		server.close(); //fechamento de escuta TCP na porta 2000

	}

}
