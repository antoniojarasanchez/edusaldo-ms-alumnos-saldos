package cl.edusaldo.alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alumnos")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "primer_nombre", nullable = false, length = 100)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 100)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 100)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 100)
    private String segundoApellido;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "curso", nullable = false, length = 50)
    private String curso;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAlumno estado = EstadoAlumno.ACTIVO;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApoderadoAlumno> apoderados = new ArrayList<>();

    @OneToOne(mappedBy = "alumno", cascade = CascadeType.ALL)
    private CuentaSaldo cuentaSaldo;

    public Alumno() {}

    public Alumno(String rut, String primerNombre, String primerApellido, LocalDate fechaNacimiento, String curso) {
        this.rut = rut;
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.fechaNacimiento = fechaNacimiento;
        this.curso = curso;
        this.estado = EstadoAlumno.ACTIVO;
    }

    public int calcularEdad() {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public boolean puedeComprarDirecto() {
        return calcularEdad() >= 12;
    }

    public boolean puedeReservar() {
        return true;
    }

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder(primerNombre);
        if (segundoNombre != null && !segundoNombre.isBlank()) {
            sb.append(" ").append(segundoNombre);
        }
        sb.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.isBlank()) {
            sb.append(" ").append(segundoApellido);
        }
        return sb.toString();
    }

    public ApoderadoAlumno asociarApoderado(Long apoderadoId, Parentesco parentesco, Long asociadoPorId) {
        ApoderadoAlumno ap = new ApoderadoAlumno(this, apoderadoId, parentesco, asociadoPorId);
        this.apoderados.add(ap);
        return ap;
    }

    public void inactivar() {
        this.estado = EstadoAlumno.INACTIVO;
    }

    public void reactivar() {
        this.estado = EstadoAlumno.ACTIVO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public EstadoAlumno getEstado() { return estado; }
    public void setEstado(EstadoAlumno estado) { this.estado = estado; }
    public List<ApoderadoAlumno> getApoderados() { return apoderados; }
    public void setApoderados(List<ApoderadoAlumno> apoderados) { this.apoderados = apoderados; }
    public CuentaSaldo getCuentaSaldo() { return cuentaSaldo; }
    public void setCuentaSaldo(CuentaSaldo cuentaSaldo) { this.cuentaSaldo = cuentaSaldo; }
}
