package cl.edusaldo.alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "apoderados_alumnos")
public class ApoderadoAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @Column(name = "apoderado_id", nullable = false)
    private Long apoderadoId; // Ref escalar a MS Auth

    @Enumerated(EnumType.STRING)
    @Column(name = "parentesco", nullable = false, length = 30)
    private Parentesco parentesco;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAsociacion estado = EstadoAsociacion.ACTIVA;

    @Column(name = "fecha_asociacion", nullable = false)
    private LocalDateTime fechaAsociacion = LocalDateTime.now();

    @Column(name = "asociado_por_id", nullable = false)
    private Long asociadoPorId;

    @Column(name = "fecha_desvinculacion")
    private LocalDateTime fechaDesvinculacion;

    @Column(name = "desvinculado_por_id")
    private Long desvinculadoPorId;

    @Column(name = "motivo_desvinculacion")
    private String motivoDesvinculacion;

    public ApoderadoAlumno() {}

    public ApoderadoAlumno(Alumno alumno, Long apoderadoId, Parentesco parentesco, Long asociadoPorId) {
        this.alumno = alumno;
        this.apoderadoId = apoderadoId;
        this.parentesco = parentesco;
        this.asociadoPorId = asociadoPorId;
        this.estado = EstadoAsociacion.ACTIVA;
        this.fechaAsociacion = LocalDateTime.now();
    }

    public void desvincular(Long usuarioId, String motivo) {
        this.estado = EstadoAsociacion.DESVINCULADA;
        this.fechaDesvinculacion = LocalDateTime.now();
        this.desvinculadoPorId = usuarioId;
        this.motivoDesvinculacion = motivo;
    }

    public Long getId() { return id; }
    public Alumno getAlumno() { return alumno; }
    public Long getApoderadoId() { return apoderadoId; }
    public Parentesco getParentesco() { return parentesco; }
    public EstadoAsociacion getEstado() { return estado; }
    public LocalDateTime getFechaAsociacion() { return fechaAsociacion; }
    public Long getAsociadoPorId() { return asociadoPorId; }
    public LocalDateTime getFechaDesvinculacion() { return fechaDesvinculacion; }
    public Long getDesvinculadoPorId() { return desvinculadoPorId; }
    public String getMotivoDesvinculacion() { return motivoDesvinculacion; }
}
