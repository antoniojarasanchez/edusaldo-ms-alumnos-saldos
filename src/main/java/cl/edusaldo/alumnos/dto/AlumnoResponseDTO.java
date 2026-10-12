package cl.edusaldo.alumnos.dto;

import cl.edusaldo.alumnos.model.Alumno;
import cl.edusaldo.alumnos.model.EstadoAlumno;

import java.time.LocalDate;

public class AlumnoResponseDTO {
    private Long id;
    private String rut;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private int edad;
    private String curso;
    private EstadoAlumno estado;
    private Long saldoActual;
    private Long saldoComprometido;
    private Long saldoDisponible;

    public AlumnoResponseDTO() {}

    public AlumnoResponseDTO(Alumno a) {
        this.id = a.getId();
        this.rut = a.getRut();
        this.primerNombre = a.getPrimerNombre();
        this.segundoNombre = a.getSegundoNombre();
        this.primerApellido = a.getPrimerApellido();
        this.segundoApellido = a.getSegundoApellido();
        this.nombreCompleto = a.getNombreCompleto();
        this.fechaNacimiento = a.getFechaNacimiento();
        this.edad = a.calcularEdad();
        this.curso = a.getCurso();
        this.estado = a.getEstado();
        if (a.getCuentaSaldo() != null) {
            this.saldoActual = a.getCuentaSaldo().getSaldoActual();
            this.saldoComprometido = a.getCuentaSaldo().getSaldoComprometido();
            this.saldoDisponible = a.getCuentaSaldo().getSaldoDisponible();
        } else {
            this.saldoActual = 0L;
            this.saldoComprometido = 0L;
            this.saldoDisponible = 0L;
        }
    }

    public Long getId() { return id; }
    public String getRut() { return rut; }
    public String getPrimerNombre() { return primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public String getNombreCompleto() { return nombreCompleto; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public int getEdad() { return edad; }
    public String getCurso() { return curso; }
    public EstadoAlumno getEstado() { return estado; }
    public Long getSaldoActual() { return saldoActual; }
    public Long getSaldoComprometido() { return saldoComprometido; }
    public Long getSaldoDisponible() { return saldoDisponible; }
}
