package cl.edusaldo.alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devoluciones_saldo")
public class DevolucionSaldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monto", nullable = false)
    private Long monto;

    @Column(name = "apoderado_id", nullable = false)
    private Long apoderadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio", nullable = false, length = 30)
    private MedioDevolucion medio;

    @Column(name = "comprobante", nullable = false, length = 100)
    private String comprobante;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "registrada_por_id", nullable = false)
    private Long registradaPorId;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    public DevolucionSaldo() {}

    public DevolucionSaldo(Long monto, Long apoderadoId, MedioDevolucion medio, String comprobante, String motivo, Long registradaPorId, String idempotencyKey) {
        this.monto = monto;
        this.apoderadoId = apoderadoId;
        this.medio = medio;
        this.comprobante = comprobante;
        this.motivo = motivo;
        this.registradaPorId = registradaPorId;
        this.idempotencyKey = idempotencyKey;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getMonto() { return monto; }
    public Long getApoderadoId() { return apoderadoId; }
    public MedioDevolucion getMedio() { return medio; }
    public String getComprobante() { return comprobante; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getFecha() { return fecha; }
    public Long getRegistradaPorId() { return registradaPorId; }
    public String getIdempotencyKey() { return idempotencyKey; }
}
