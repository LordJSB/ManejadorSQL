package sqlmanajer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Manayer {
	private String URL = "jdbc:mysql://localhost/prueba";
	private String USER = "root";
	private String PASSWORD = "admin";
	private Scanner scan = new Scanner(System.in);

	private Connection conex;

	public Manayer() {
		this.startBD(scan);
	}

	public void startBD(Scanner sc) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.print("Inserte la base de datos a la que desea entrar: ");
			String bbdd = sc.nextLine();
			URL = "jdbc:mysql://localhost/" + bbdd;
			conex = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("Conexión exitosa a la base " + bbdd + ".");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void menuPpal() throws IOException {
		boolean bucle = true;
		while (bucle) {
			System.out.print("\nEscoja entre las distintas opciones: " + "\n1: Ejecutar una sentencia SQL"
					+ "\n2: Leer un XML o un JSON" + "\n3: Inserta las reservas de un hotel en un XML o un JSON"
					+ "\n0: Salir" + "\nInserte un número de los establecidos anteriormente: ");
			switch (scan.nextLine().toString()) {
			case "0":
				System.out.println("Gracias por participar");
				bucle = false;
				break;
			case "1":
				System.out.print("\nInserte la sentencia a ejecutar: ");
				String query = scan.nextLine();
				ejecutarSentencia(query);
				break;
			case "2":
				opcionLectura(scan);
				break;
			case "3":
				leerTablaReservas(scan);
				break;
			default:
				System.out.println(
						"ERROR: Escoja un número entre el 0 y el 3 acorde a las opciones anteriormente listadas");
				break;
			}
		}
	}

////////////////OPCIÓN DE LECTURA DE ARCHIVOS////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void opcionLectura(Scanner sc) {
		System.out.print("Inserte la ruta del archivo de entrada"
				+ "\nSi es un archivo dentro de la carpeta del proyecto, inserte únicamente el nombre: ");
		String nombreArchivo = sc.nextLine().trim();
		try {
			File archivoEntrada = leeArchivo(nombreArchivo);
			if (nombreArchivo.endsWith(".xml")) {
				lectorXML(archivoEntrada, sc);
			} else if (nombreArchivo.endsWith(".json")) {
				lectorJSON(archivoEntrada, sc);
			} else {
				System.out.println("ERROR: El documento inicial debe ser un JSON o un XML.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File leeArchivo(String nombre) throws IOException {
		File archivo = new File(nombre);
		if (archivo.exists()) {
			return archivo;
		} else {
			System.err.println("El archivo " + nombre + " no existe");
			return null;
		}
	}

	public void lectorXML(File archivo, Scanner sc) throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(archivo);
		BufferedReader lector = new BufferedReader(fr);
		boolean dentroDeReservas = false;
		boolean dentroDeReserva = false;
		String linea, query = null;

		while ((linea = lector.readLine()) != null) {
			if (linea.contains("<reservas>")) {
				dentroDeReservas = true;
			}
			if (linea.contains("<reserva>") && dentroDeReservas) {
				dentroDeReserva = true;
			}
			if (dentroDeReserva && dentroDeReservas) {
				query = creaQueryXML(linea, query);
			}

			if (linea.contains("</reserva>") && dentroDeReserva) {
				dentroDeReserva = false;
				ejecutarSentencia(query);
			} else if (linea.contains("</reservas>") && !dentroDeReserva) {
				dentroDeReservas = false;
			}
		}
		lector.close();
	}

	public String creaQueryXML(String linea, String query) throws IOException {
		String nombre = pillaCampoXML(linea, "<nombre>", "</nombre>");
		String telefono = pillaCampoXML(linea, "<telefono>", "</telefono>");
		String fecha_evento = pillaCampoXML(linea, "<fecha_evento>", "</fecha_evento>");
		if (linea.toLowerCase().contains("banquete")) {
			String tipo_evento = pillaCampoXML(linea, "<tipo_evento>", "</tipo_evento>");
			String n_personas = pillaCampoXML(linea, "<n_personas>", "</n_personas>");
			String tipo_cocina = pillaCampoXML(linea, "<tipo_cocina>", "</tipoCocina>");
			String tipo_mesa = pillaCampoXML(linea, "<tipo_mesa>", "</tipo_mesa>");
			String n_comensales = pillaCampoXML(linea, "<n_comensales>", "</n_comensales>");
			query = "INSERT INTO reservas (nombre, telefono, fecha_evento, tipo_evento, n_personas, tipo_cocina, tipo_mesa, n_comensales) "
					+ "VALUES (" + nombre + "," + telefono + "," + fecha_evento + "," + tipo_evento + "," + n_personas
					+ "," + tipo_cocina + "," + tipo_mesa + "," + n_comensales + ");";
		} else if (linea.toLowerCase().contains("congreso")) {
			String tipo_evento = pillaCampoXML(linea, "<tipo_evento>", "</tipo_evento>");
			String n_personas = pillaCampoXML(linea, "<n_personas>", "</n_personas>");
			String tipo_cocina = pillaCampoXML(linea, "<tipo_cocina>", "</tipoCocina>");
			String n_jornadas = pillaCampoXML(linea, "<n_jornadas>", "</n_jornadas>");
			String n_habitaciones = pillaCampoXML(linea, "<n_habitaciones>", "</n_habitaciones>");
			query = "INSERT INTO reservas (nombre, telefono, fecha_evento, tipo_evento, n_personas, tipo_cocina, n_jornadas, n_habitaciones) "
					+ "VALUES (" + nombre + "," + telefono + "," + fecha_evento + "," + tipo_evento + "," + n_personas
					+ "," + tipo_cocina + "," + n_jornadas + "," + n_habitaciones + ");";
		} else if (linea.toLowerCase().contains("jornada")) {
			String tipo_evento = pillaCampoXML(linea, "<tipo_evento>", "</tipo_evento>");
			String n_personas = pillaCampoXML(linea, "<n_personas>", "</n_personas>");
			String tipo_cocina = pillaCampoXML(linea, "<tipo_cocina>", "</tipo_cocina>");
			query = "INSERT INTO reservas (nombre, telefono, fecha_evento, tipo_evento, n_personas, tipo_cocina) "
					+ "VALUES (" + nombre + "," + telefono + "," + fecha_evento + "," + tipo_evento + "," + n_personas
					+ "," + tipo_cocina + ");";
		}
		return query;
	}

	public String pillaCampoXML(String linea, String entrada, String salida) throws IOException {
		boolean condicion = linea.contains(entrada) && linea.endsWith(salida);
		int inicioValor = linea.indexOf(">");
		int finValor = linea.lastIndexOf("<");
		String valor = linea.substring(inicioValor + 1, finValor);
		condicion = validParam(entrada, valor, condicion);
		if (condicion) {
			return valor;
		}
		return "VOID";
	}

	public void lectorJSON(File archivo, Scanner sc) throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(archivo);
		BufferedReader lector = new BufferedReader(fr);
		String linea, query = null;
		boolean dentroDeReservas = false;
		boolean dentroDeReserva = false;

		while ((linea = lector.readLine()) != null) {
			if (linea.contains("\"reservas\":{")) {
				dentroDeReservas = true;
			}
			if (linea.contains("\"reserva\":{") && dentroDeReservas) {
				dentroDeReserva = true;
			}
			if (dentroDeReserva && dentroDeReservas) {
				query = creaQueryJSON(linea, query);
			}

			if (linea.contains("}") && dentroDeReserva) {
				dentroDeReserva = false;
				ejecutarSentencia(query);
			} else if (linea.contains("}") && !dentroDeReserva) {
				dentroDeReservas = false;
			}
		}
		lector.close();
	}

	public String creaQueryJSON(String linea, String query) throws IOException {
		String nombre = pillaCampoJSON(linea, "\"nombre\":");
		String telefono = pillaCampoJSON(linea, "\"telefono\":");
		String fecha_evento = pillaCampoJSON(linea, "\"fecha_evento\":");
		if (linea.toLowerCase().contains("banquete")) {
			String tipo_evento = pillaCampoJSON(linea, "\"tipo\":");
			String n_personas = pillaCampoJSON(linea, "\"asistentes\":");
			String tipo_cocina = pillaCampoJSON(linea, "\"tipo_cocina\":");
			String tipo_mesa = pillaCampoJSON(linea, "\"tipo_mesa\":");
			String n_comensales = pillaCampoJSON(linea, "\"n_comensales\":");
			query = "INSERT INTO reservas (nombre, telefono, fecha_evento, tipo_evento, n_personas, tipo_cocina, tipo_mesa, n_comensales) "
					+ "VALUES (" + nombre + "," + telefono + "," + fecha_evento + "," + tipo_evento + "," + n_personas
					+ "," + tipo_cocina + "," + tipo_mesa + "," + n_comensales + ");";
		} else if (linea.toLowerCase().contains("congreso")) {
			String tipo_evento = pillaCampoJSON(linea, "\"tipo\":");
			String n_personas = pillaCampoJSON(linea, "\"asistentes\":");
			String tipo_cocina = pillaCampoJSON(linea, "\"tipo_cocina\":");
			String n_jornadas = pillaCampoJSON(linea, "\"n_jornadas\":");
			String n_habitaciones = pillaCampoJSON(linea, "\"n_habitaciones\":");
			query = "INSERT INTO reservas (nombre, telefono, fecha_evento, tipo_evento, n_personas, tipo_cocina, n_jornadas, n_habitaciones) "
					+ "VALUES (" + nombre + "," + telefono + "," + fecha_evento + "," + tipo_evento + "," + n_personas
					+ "," + tipo_cocina + "," + n_jornadas + "," + n_habitaciones + ");";
		} else if (linea.toLowerCase().contains("jornada")) {
			String tipo_evento = pillaCampoJSON(linea, "\"tipo\":");
			String n_personas = pillaCampoJSON(linea, "\"asistentes\":");
			String tipo_cocina = pillaCampoJSON(linea, "\"tipo_cocina\":");
			query = "INSERT INTO reservas (nombre, telefono, fecha_evento, tipo_evento, n_personas, tipo_cocina) "
					+ "VALUES (" + nombre + "," + telefono + "," + fecha_evento + "," + tipo_evento + "," + n_personas
					+ "," + tipo_cocina + ");";
		}
		return query;
	}

	public String pillaCampoJSON(String linea, String campo) throws IOException {
		boolean condicion = linea.contains(campo);
		String valor = quitaComillas(campo);
		condicion = validParam(campo, valor, condicion);
		if (condicion) {
			return valor;
		}
		return "VOID";
	}

	public String quitaComillas(String linea) {
		int inicio = linea.indexOf("\"") + 1;
		int fin = linea.lastIndexOf("\"");
		return linea.substring(inicio, fin);
	}

	public boolean validParam(String campo, String valor, boolean condicion) throws IOException {
		boolean isNum = false;
		boolean validMesa = false;
		boolean validFecha = false;
		boolean validCocina = false;
		if (campo.contains("asistentes") || campo.contains("n_comensales") || campo.contains("n_jornadas")
				|| campo.contains("telefono")) {
			if (valor.matches("\\d")) {
				isNum = true;
			}
		}
		if (campo.contains("tipo_mesa")) {
			if (valor.contains("redonda") || valor.contains("cuadrada")) {
				validMesa = true;
			}
		}
		if (campo.contains("fecha_evento")) {
			if (valor.matches("^(3[01]|[12][0-9]|0?[1-9])/(1[0-2]|0?[1-9])/(?:[0-9]{2})?[0-9]{2}$")) {
				validFecha = true;
			}
		}

		if (campo.contains("tipo_cocina")) {
			if (valor.toLowerCase().contains("bufe") || valor.toLowerCase().contains("carta")
					|| valor.toLowerCase().contains("pedir cita") || valor.toLowerCase().contains("no precisa")) {
				validCocina = true;
			}
		}

		condicion = condicionalNoNum(isNum, validMesa, validFecha, validCocina, campo, condicion);
		return condicion;
	}

	public boolean condicionalNoNum(boolean isNum, boolean validMesa, boolean validFecha, boolean validCocina,
			String campo, boolean condicion) {
		if (!isNum && campo.contains("n_personas")) { // PUEDE ENTRAR UNA CONDICION FALSA DE UNA ETIQUETA RANDOM Y COMO
														// CUMPLE LO VALIDA
			condicion = false;
		}
		if (!isNum && campo.contains("telefono")) {
			condicion = false;
		}
		if (!isNum && campo.contains("n_comensales")) {
			condicion = false;
		}
		if (!isNum && campo.contains("n_jornadas")) {
			condicion = false;
		}
		if (!validMesa && campo.contains("tipo_mesa")) {
			condicion = false;
		}
		if (!validFecha && campo.contains("fecha_evento")) {
			condicion = false;
		}
		if (!validCocina && campo.contains("tipo_cocina")) {
			condicion = false;
		}
		return condicion;
	}

////////////////OPCIÓN DE LECTURA DE ARCHIVOS////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////OPCIÓN DE EJECUTAR QUERY////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void ejecutarSentencia(String query) {
		try {
			Statement sentencia = conex.createStatement();
			boolean isSelect = sentencia.execute(query);

			if (isSelect) {
				selectQuery(sentencia);
			} else {
				int cuentaFilas = sentencia.getUpdateCount();
				System.out.println("Número de filas afectadas: " + cuentaFilas);
				System.out.println("Sentencia ejecutada correctamente.");
			}

			sentencia.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void selectQuery(Statement st) throws SQLException {
		ResultSet res = st.getResultSet(); // DE AQUI SACAS LA INFO
		java.sql.ResultSetMetaData metaData = res.getMetaData(); // SIN ESTAS DOS
		int numCampo = metaData.getColumnCount(); // NO PUEDES SACAR EL NUM DE COLUMNAS
		int valorActual = 0;

		while (res.next()) {
			int nuevoValor = res.getInt(1);
			if (valorActual != nuevoValor) {
				valorActual = nuevoValor;
				System.out.println("\nValor " + valorActual);
			}
			for (int i = 1; i <= numCampo; i++) {
				System.out.print("Campo " + i + ": " + res.getString(i) + "\t");
			}
			System.out.println();
		}
		System.out.println("\nSentencia SELECT ejecutada correctamente.");
	}

	public void leerTablaReservas(Scanner scan) throws IOException {
		boolean bucle = true;
		System.out.print("\nInserte el nombre de la tabla: ");
		String nombreTabla = scan.nextLine();
		String query = "SELECT * FROM " + nombreTabla + ";";
		try {
			Statement sentencia = conex.createStatement();
			selectQuery(sentencia);
			ResultSet rS = sentencia.executeQuery(query);

			while (rS.next()) {
				List<Reserva> hotel = leerReservas(rS);
				while (bucle) {
					System.out.println("Seleccione el tipo de archivo que desea crear: " + "\n1: XML" + "\n2: JSON");
					switch (scan.nextLine()) {
					case "1":
						creaXML(scan, hotel);
						bucle = false;
						break;
					case "2":
						creaJSON(scan, hotel);
						bucle = false;
						break;
					default:
						System.out.println("ERROR: Escoga entre las 2 opciones dadas anteriormente");
						break;
					}
				}
			}

			sentencia.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Reserva> leerReservas(ResultSet resultSet) throws SQLException {
		List<Reserva> reservas = new ArrayList<>();

		while (resultSet.next()) {
			Reserva r = new Reserva();
			String nombre = resultSet.getString("nombre");
			String telefono = resultSet.getString("telefono");
			String fecha_evento = resultSet.getString("fecha_evento");
			String tipo_evento = resultSet.getString("tipo_evento");
			int n_personas = resultSet.getInt("n_personas");
			String tipo_cocina = resultSet.getString("tipo_cocina");
			int n_jornadas = resultSet.getInt("n_jornadas");
			int n_habitaciones = resultSet.getInt("n_habitaciones");
			String tipo_mesa = resultSet.getString("tipo_mesa");
			int n_comensales = resultSet.getInt("n_comensales");

			r.setNombre(nombre);
			r.setTelefono(telefono);
			r.setFecha_evento(fecha_evento);
			r.setTipo_evento(tipo_evento);
			r.setN_personas(n_personas);
			r.setTipo_cocina(tipo_cocina);

			if (tipo_evento.contains("banquete")) {
				r.setTipo_mesa(tipo_mesa);
				r.setN_comensales(n_comensales);
			} else if (tipo_evento.contains("congreso")) {
				r.setN_jornadas(n_jornadas);
				r.setN_habitaciones(n_habitaciones);
			}

			reservas.add(r);
		}

		return reservas;
	}

	public File creaArchivo(Scanner sc, String extension) throws IOException {
		File archivo = null;
		String nombre;
		System.out.print("\nIntroduzca el nombre del archivo: ");
		nombre = sc.nextLine();
		if (nombre.endsWith(".txt")) {
			nombre = nombre.substring(0, nombre.length() - 4);
		}
		archivo = new File(nombre + extension);

		if (archivo.createNewFile()) {
			System.out.println("Se ha creado: " + archivo.getName());
		} else {
			System.out.println("Ya existe.");
		}

		return archivo;
	}

	public void creaXML(Scanner sc, List<Reserva> hotel) throws IOException {
		File archivo = creaArchivo(sc, ".xml");
		FileWriter fW = new FileWriter(archivo);

		fW.write("<reservas>\n");

		for (Reserva reserva : hotel) {
			fW.write("<reserva>\n");
			fW.write("\t<nombre>" + reserva.getNombre() + "</nombre>\n");
			fW.write("\t<telefono>" + reserva.getTelefono() + "</telefono>\n");
			fW.write("\t<fechaEvento>" + reserva.getFecha_evento() + "</fechaEvento>\n");
			fW.write("\t<tipo>" + reserva.getTipo_evento() + "</tipo>\n");
			fW.write("\t<asistentes>" + reserva.getN_personas() + "</asistentes>\n");
			fW.write("\t<tipoCocina>" + reserva.getTipo_cocina() + "</tipoCocina>\n");

			if (reserva.getTipo_evento().contains("banquete")) {
				fW.write("\t<tipoMesa>" + reserva.getTipo_mesa() + "</tipoMesa>\n");
				fW.write("\t<comensalesMesa>" + reserva.getN_comensales() + "</comensalesMesa>\n");
			} else if (reserva.getTipo_evento().contains("congreso")) {
				fW.write("\t<numeroJornadas>" + reserva.getN_jornadas() + "</numeroJornadas>\n");
				fW.write("\t<habitaciones>" + reserva.getN_habitaciones() + "</habitaciones>\n");
			}

			fW.write("</reserva>\n");
		}
		fW.write("</reservas>\n");
		fW.close();
		System.out.println("Archivo XML creado exitosamente: " + archivo.getName());
	}

	public void creaJSON(Scanner sc, List<Reserva> hotel) throws IOException {
		File archivo = creaArchivo(sc, ".json");
		FileWriter fW = new FileWriter(archivo);

		fW.write("{\n\t\"reservas\": {\n");

		for (Reserva reserva : hotel) {
			fW.write("\t\t\"reserva\": {\n");
			fW.write("\t\t\t\"nombre\": \"" + reserva.getNombre() + "\",\n");
			fW.write("\t\t\t\"telefono\": \"" + reserva.getTelefono() + "\",\n");
			fW.write("\t\t\t\"fecha_evento\": \"" + reserva.getFecha_evento() + "\",\n");
			fW.write("\t\t\t\"tipo\": \"" + reserva.getTipo_evento() + "\",\n");
			fW.write("\t\t\t\"asistentes\": " + reserva.getN_personas() + ",\n");
			fW.write("\t\t\t\"tipo_cocina\": \"" + reserva.getTipo_cocina() + "\",\n");

			if (reserva.getTipo_evento().contains("banquete")) {
				fW.write("\t\t\t\"tipo_mesa\": \"" + reserva.getTipo_mesa() + "\",\n");
				fW.write("\t\t\t\"n_comensales\": " + reserva.getN_comensales() + "\n");
			} else if (reserva.getTipo_evento().contains("congreso")) {
				fW.write("\t\t\t\"n_jornadas\": " + reserva.getN_jornadas() + ",\n");
				fW.write("\t\t\t\"n_habitaciones\": " + reserva.getN_habitaciones() + "\n");
			}
			fW.write("\t\t}\n");
		}
		fW.write("\t}\n}\n");
		fW.close();
		System.out.println("Archivo JSON creado exitosamente: " + archivo.getName());
	}

////////////////OPCIÓN DE EJECUTAR QUERY////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void closeBD() {
		try {
			if (conex != null && !conex.isClosed()) {
				conex.close();
				System.out.println("Conexión cerrada.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
