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

    // Invariante: comprometer saldo para reserva
    public MovimientoSaldo comprometer(Long monto, Long reservaId, Long usuarioId, String idempotencyKey) {
        if (!tieneSaldoDisponible(monto)) {
            throw new IllegalStateException("Saldo insuficiente. Saldo disponible: $" + getSaldoDisponible() + " - Monto requerido: $" + monto);
        }
        this.saldoComprometido += monto;
        MovimientoSaldo mov = new MovimientoSaldo(
                this, TipoMovimientoSaldo.COMPROMISO_RESERVA, monto, this.saldoActual,
                "RESERVA", reservaId, idempotencyKey, usuarioId, "Compromiso de saldo por reserva #" + reservaId
        );
        this.movimientos.add(mov);
        return mov;
    }

    // Invariante: liberar saldo de reserva cancelada
    public MovimientoSaldo liberar(Long monto, Long reservaId, Long usuarioId, String idempotencyKey) {
        this.saldoComprometido = Math.max(0L, this.saldoComprometido - monto);
        MovimientoSaldo mov = new MovimientoSaldo(
                this, TipoMovimientoSaldo.LIBERACION_RESERVA, monto, this.saldoActual,
                "RESERVA", reservaId, idempotencyKey, usuarioId, "Liberación de saldo por cancelación de reserva #" + reservaId
        );
        this.movimientos.add(mov);
        return mov;
    }

    // Invariante atómica: finalizar reserva al retirar en librería (entrega/venta)
    public MovimientoSaldo finalizarReserva(Long monto, Long reservaId, Long ventaId, Long usuarioId, String idempotencyKey) {
        this.saldoComprometido = Math.max(0L, this.saldoComprometido - monto);
        this.saldoActual = Math.max(0L, this.saldoActual - monto);
        MovimientoSaldo mov = new MovimientoSaldo(
                this, TipoMovimientoSaldo.DEBITO_VENTA, monto, this.saldoActual,
                "VENTA_RESERVA", ventaId, idempotencyKey, usuarioId, "Débito definitivo por entrega de reserva #" + reservaId + " (Venta #" + ventaId + ")"
        );
        this.movimientos.add(mov);
        return mov;
    }

    // Venta presencial directa (alumnos >= 12 años)
    public MovimientoSaldo debitar(Long monto, Long ventaId, Long usuarioId, String idempotencyKey) {
        if (!tieneSaldoDisponible(monto)) {
            throw new IllegalStateException("Saldo disponible insuficiente para compra presencial.");
        }
        this.saldoActual -= monto;
        MovimientoSaldo mov = new MovimientoSaldo(
                this, TipoMovimientoSaldo.DEBITO_VENTA, monto, this.saldoActual,
                "VENTA_DIRECTA", ventaId, idempotencyKey, usuarioId, "Compra presencial directa en librería #" + ventaId
        );
        this.movimientos.add(mov);
        return mov;
    }

    // Abono Webpay
    public MovimientoSaldo abonar(Long monto, Long abonoId, Long usuarioId, String idempotencyKey) {
        this.saldoActual += monto;
        MovimientoSaldo mov = new MovimientoSaldo(
                this, TipoMovimientoSaldo.ABONO, monto, this.saldoActual,
                "ABONO", abonoId, idempotencyKey, usuarioId, "Aporte de saldo vía Webpay"
        );
        this.movimientos.add(mov);
        return mov;
    }

    // Reintegro por devolución de venta
    public MovimientoSaldo reintegrar(Long monto, Long devolucionId, Long usuarioId, String idempotencyKey) {
        this.saldoActual += monto;
        MovimientoSaldo mov = new MovimientoSaldo(
                this, TipoMovimientoSaldo.DEVOLUCION_VENTA, monto, this.saldoActual,
                "DEVOLUCION", devolucionId, idempotencyKey, usuarioId, "Reintegro de saldo por devolución #" + devolucionId
        );
        this.movimientos.add(mov);
        return mov;
    }

    // HU18: Devolución total de saldo por egreso del alumno
    public DevolucionSaldo devolverSaldo(Long adminId, Long apoderadoId, MedioDevolucion medio, String comprobante, String motivo, String idempotencyKey) {
        if (this.saldoComprometido > 0) {
            throw new IllegalStateException("No se puede devolver saldo: el alumno tiene reservas pendientes retenidas ($" + this.saldoComprometido + ").");
        }
        Long montoTotal = this.saldoActual;
        this.saldoActual = 0L;

        MovimientoSaldo mov = new MovimientoSaldo(
                this, TipoMovimientoSaldo.DEVOLUCION_SALDO, montoTotal, 0L,
                "EGRESO_ALUMNO", null, idempotencyKey, adminId, "Devolución de saldo total por retiro del alumno: " + motivo
        );
        this.movimientos.add(mov);

        return new DevolucionSaldo(montoTotal, apoderadoId, medio, comprobante, motivo, adminId, idempotencyKey);
    }

    public Long getId() { return id; }
    public Alumno getAlumno() { return alumno; }
    public Long getSaldoActual() { return saldoActual; }
    public Long getSaldoComprometido() { return saldoComprometido; }
    public List<MovimientoSaldo> getMovimientos() { return movimientos; }
}
