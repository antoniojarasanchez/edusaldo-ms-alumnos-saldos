package cl.edusaldo.alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_saldo")
public class MovimientoSaldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_saldo_id", nullable = false)
    private CuentaSaldo cuentaSaldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoMovimientoSaldo tipo;

    @Column(name = "monto", nullable = false)
    private Long monto;

    @Column(name = "saldo_resultante", nullable = false)
    private Long saldoResultante;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "referencia_tipo", nullable = false, length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    public MovimientoSaldo() {}

    public MovimientoSaldo(CuentaSaldo cuentaSaldo, TipoMovimientoSaldo tipo, Long monto, Long saldoResultante,
                           String referenciaTipo, Long referenciaId, String idempotencyKey, Long usuarioId, String descripcion) {
        this.cuentaSaldo = cuentaSaldo;
        this.tipo = tipo;
        this.monto = monto;
        this.saldoResultante = saldoResultante;
        this.referenciaTipo = referenciaTipo;
        this.referenciaId = referenciaId;
        this.idempotencyKey = idempotencyKey;
        this.usuarioId = usuarioId;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public CuentaSaldo getCuentaSaldo() { return cuentaSaldo; }
    public TipoMovimientoSaldo getTipo() { return tipo; }
    public Long getMonto() { return monto; }
    public Long getSaldoResultante() { return saldoResultante; }
    public LocalDateTime getFecha() { return fecha; }
    public String getReferenciaTipo() { return referenciaTipo; }
    public Long getReferenciaId() { return referenciaId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public Long getUsuarioId() { return usuarioId; }
    public String getDescripcion() { return descripcion; }
}
