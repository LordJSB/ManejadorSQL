package sqlmanajer;

public class Reserva {
	private String nombre;
	private String telefono;
	private String fecha_evento;
	private String tipo_evento;
	private int n_personas;
	private String tipo_cocina;
	private int n_jornadas;
	private int n_habitaciones;
	private String tipo_mesa;
	private int n_comensales;
	

	public Reserva() {
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getFecha_evento() {
		return fecha_evento;
	}

	public void setFecha_evento(String fecha_evento) {
		this.fecha_evento = fecha_evento;
	}

	public String getTipo_evento() {
		return tipo_evento;
	}

	public void setTipo_evento(String tipo_evento) {
		this.tipo_evento = tipo_evento;
	}

	public int getN_personas() {
		return n_personas;
	}

	public void setN_personas(int n_personas) {
		this.n_personas = n_personas;
	}

	public String getTipo_cocina() {
		return tipo_cocina;
	}

	public void setTipo_cocina(String tipo_cocina) {
		this.tipo_cocina = tipo_cocina;
	}

	public int getN_jornadas() {
		return n_jornadas;
	}

	public void setN_jornadas(int n_jornadas) {
		this.n_jornadas = n_jornadas;
	}

	public int getN_habitaciones() {
		return n_habitaciones;
	}

	public void setN_habitaciones(int n_habitaciones) {
		this.n_habitaciones = n_habitaciones;
	}

	public String getTipo_mesa() {
		return tipo_mesa;
	}

	public void setTipo_mesa(String tipo_mesa) {
		this.tipo_mesa = tipo_mesa;
	}

	public int getN_comensales() {
		return n_comensales;
	}

	public void setN_comensales(int n_comensales) {
		this.n_comensales = n_comensales;
	}
}