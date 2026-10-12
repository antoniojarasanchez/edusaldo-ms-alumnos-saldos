package cl.edusaldo.alumnos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_saldo_bajo")
public class AlertaSaldoBajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_saldo_id", nullable = false)
    private CuentaSaldo cuentaSaldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoAlerta estado = EstadoAlerta.ACTIVA;

    @Column(name = "umbral_aplicado", nullable = false)
    private Long umbralAplicado = 3000L;

    @Column(name = "saldo_al_activar", nullable = false)
    private Long saldoAlActivar;

    @Column(name = "saldo_actual", nullable = false)
    private Long saldoActual;

    @Column(name = "fecha_activacion", nullable = false)
    private LocalDateTime fechaActivacion = LocalDateTime.now();

    @Column(name = "correo_enviado", nullable = false)
    private boolean correoEnviado = false;

    @Column(name = "fecha_envio_correo")
    private LocalDateTime fechaEnvioCorreo;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public AlertaSaldoBajo() {}

    public AlertaSaldoBajo(CuentaSaldo cuentaSaldo, Long saldoActual, Long umbralAplicado) {
        this.cuentaSaldo = cuentaSaldo;
        this.saldoActual = saldoActual;
        this.saldoAlActivar = saldoActual;
        this.umbralAplicado = umbralAplicado != null ? umbralAplicado : 3000L;
        this.estado = EstadoAlerta.ACTIVA;
        this.fechaActivacion = LocalDateTime.now();
    }

    public void actualizarSaldo(Long nuevoSaldo) {
        this.saldoActual = nuevoSaldo;
    }

    public void marcarCorreoEnviado() {
        this.correoEnviado = true;
        this.fechaEnvioCorreo = LocalDateTime.now();
    }

    public void resolver() {
        this.estado = EstadoAlerta.RESUELTA;
        this.fechaResolucion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public CuentaSaldo getCuentaSaldo() { return cuentaSaldo; }
    public EstadoAlerta getEstado() { return estado; }
    public Long getUmbralAplicado() { return umbralAplicado; }
    public Long getSaldoAlActivar() { return saldoAlActivar; }
    public Long getSaldoActual() { return saldoActual; }
    public LocalDateTime getFechaActivacion() { return fechaActivacion; }
    public boolean isCorreoEnviado() { return correoEnviado; }
    public LocalDateTime getFechaEnvioCorreo() { return fechaEnvioCorreo; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
}
