package sqlmanajer;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		Manayer mg = new Manayer();
		mg.menuPpal();
		mg.closeBD();
	}
}
