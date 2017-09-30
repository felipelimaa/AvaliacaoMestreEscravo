package br.edu.unirn.util;


public class PrimoThread extends Thread {

	int x1, x2, totalPrimos;
	
	public PrimoThread(int x1, int x2) {
		this.x1 = x1;
		this.x2 = x2;
	}
	
	@Override
	public void run() {
	
		for (int i = x1; i < x2; i++) {
			if ( isPrimo(i) ) {
				totalPrimos++;
			}
		}
	
	}
	
	public static boolean isPrimo(int x) {
		
		for (int i = 2; i <= (int) Math.sqrt(x); i++) {
			if ( x % i == 0 ) return false;
		}
		return true;		
	}
	
	public int getTotalPrimos() {
		return totalPrimos;
	}
	
}