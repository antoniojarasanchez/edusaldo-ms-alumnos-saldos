package cl.edusaldo.alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "abonos")
public class Abono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_id", nullable = false)
    private Long alumnoId;

    @Column(name = "apoderado_id", nullable = false)
    private Long apoderadoId;

    @Column(name = "monto", nullable = false)
    private Long monto;

    @Column(name = "token_transaccion", nullable = false, unique = true, length = 100)
    private String tokenTransaccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAbono estado = EstadoAbono.PENDIENTE;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public Abono() {}

    public Abono(Long alumnoId, Long apoderadoId, Long monto, String tokenTransaccion) {
        this.alumnoId = alumnoId;
        this.apoderadoId = apoderadoId;
        this.monto = monto;
        this.tokenTransaccion = tokenTransaccion;
        this.estado = EstadoAbono.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void aprobar() {
        this.estado = EstadoAbono.APROBADO;
        this.fechaResolucion = LocalDateTime.now();
    }

    public void rechazar() {
        this.estado = EstadoAbono.RECHAZADO;
        this.fechaResolucion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getAlumnoId() { return alumnoId; }
    public Long getApoderadoId() { return apoderadoId; }
    public Long getMonto() { return monto; }
    public String getTokenTransaccion() { return tokenTransaccion; }
    public EstadoAbono getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
}
