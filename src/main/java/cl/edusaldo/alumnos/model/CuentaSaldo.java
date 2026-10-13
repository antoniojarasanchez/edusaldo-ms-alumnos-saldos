package cl.edusaldo.alumnos.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cuentas_saldo")
public class CuentaSaldo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumno_id", nullable = false, unique = true)
    private Alumno alumno;

    @Column(name = "saldo_actual", nullable = false)
    private Long saldoActual = 0L;

    @Column(name = "saldo_comprometido", nullable = false)
    private Long saldoComprometido = 0L;

    @OneToMany(mappedBy = "cuentaSaldo", cascade = CascadeType.ALL)
    private List<MovimientoSaldo> movimientos = new ArrayList<>();

    public CuentaSaldo() {}

    public CuentaSaldo(Alumno alumno, Long saldoInicial) {
        this.alumno = alumno;
        this.saldoActual = saldoInicial != null ? saldoInicial : 0L;
        this.saldoComprometido = 0L;
    }

    public Long getSaldoDisponible() {
        return this.saldoActual - this.saldoComprometido;
    }

    public boolean tieneSaldoDisponible(Long monto) {
        return getSaldoDisponible() >= monto;
    }

    public void comprometer(Long monto) {
        if (!tieneSaldoDisponible(monto)) {
            throw new IllegalStateException("Saldo insuficiente. Saldo disponible: $" + getSaldoDisponible() + " - Monto requerido: $" + monto);
        }
        this.saldoComprometido += monto;
    }

    public void liberar(Long monto) {
        this.saldoComprometido = Math.max(0L, this.saldoComprometido - monto);
    }

    public void finalizarReserva(Long monto) {
        this.saldoComprometido = Math.max(0L, this.saldoComprometido - monto);
        this.saldoActual = Math.max(0L, this.saldoActual - monto);
    }

    public void debitar(Long monto) {
        if (!tieneSaldoDisponible(monto)) {
            throw new IllegalStateException("Saldo disponible insuficiente para compra presencial.");
        }
        this.saldoActual -= monto;
    }

    public void abonar(Long monto) {
        this.saldoActual += monto;
    }

    public void reintegrar(Long monto) {
        this.saldoActual += monto;
    }

    public DevolucionSaldo devolverSaldo(Long adminId, Long apoderadoId, MedioDevolucion medio, String comprobante, String motivo, String idempotencyKey) {
        if (this.saldoComprometido > 0) {
            throw new IllegalStateException("No se puede devolver saldo: el alumno tiene reservas pendientes retenidas ($" + this.saldoComprometido + ").");
        }
        Long montoTotal = this.saldoActual;
        this.saldoActual = 0L;
        return new DevolucionSaldo(montoTotal, apoderadoId, medio, comprobante, motivo, adminId, idempotencyKey);
    }

    public Long getId() { return id; }
    public Alumno getAlumno() { return alumno; }
    public Long getSaldoActual() { return saldoActual; }
    public Long getSaldoComprometido() { return saldoComprometido; }
}
