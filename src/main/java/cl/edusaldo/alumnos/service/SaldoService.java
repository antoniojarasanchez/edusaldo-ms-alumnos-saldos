package cl.edusaldo.alumnos.service;

import cl.edusaldo.alumnos.dto.AbonarSaldoDTO;
import cl.edusaldo.alumnos.dto.ComprometerSaldoDTO;
import cl.edusaldo.alumnos.dto.FinalizarReservaDTO;
import cl.edusaldo.alumnos.dto.MovimientoSaldoDTO;
import cl.edusaldo.alumnos.model.CuentaSaldo;
import cl.edusaldo.alumnos.model.MovimientoSaldo;
import cl.edusaldo.alumnos.model.TipoMovimientoSaldo;
import cl.edusaldo.alumnos.repository.CuentaSaldoRepository;
import cl.edusaldo.alumnos.repository.MovimientoSaldoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaldoService {

    private final CuentaSaldoRepository cuentaSaldoRepository;
    private final MovimientoSaldoRepository movimientoSaldoRepository;

    public SaldoService(CuentaSaldoRepository cuentaSaldoRepository, MovimientoSaldoRepository movimientoSaldoRepository) {
        this.cuentaSaldoRepository = cuentaSaldoRepository;
        this.movimientoSaldoRepository = movimientoSaldoRepository;
    }

    @Transactional
    public MovimientoSaldoDTO comprometer(Long alumnoId, ComprometerSaldoDTO dto) {
        if (movimientoSaldoRepository.existsByIdempotencyKey(dto.getIdempotencyKey())) {
            MovimientoSaldo prev = movimientoSaldoRepository.findByIdempotencyKey(dto.getIdempotencyKey()).get();
            return new MovimientoSaldoDTO(prev);
        }

        CuentaSaldo cuenta = cuentaSaldoRepository.findByAlumnoId(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de saldo no encontrada para el alumno ID: " + alumnoId));

        cuenta.comprometer(dto.getMonto());
        cuenta = cuentaSaldoRepository.save(cuenta);

        MovimientoSaldo mov = new MovimientoSaldo(
                cuenta, TipoMovimientoSaldo.COMPROMISO_RESERVA, dto.getMonto(), cuenta.getSaldoActual(),
                "RESERVA", dto.getReservaId(), dto.getIdempotencyKey(), dto.getUsuarioId(), "Compromiso de saldo por reserva #" + dto.getReservaId()
        );
        mov = movimientoSaldoRepository.save(mov);
        return new MovimientoSaldoDTO(mov);
    }

    @Transactional
    public MovimientoSaldoDTO finalizarReserva(Long alumnoId, FinalizarReservaDTO dto) {
        if (movimientoSaldoRepository.existsByIdempotencyKey(dto.getIdempotencyKey())) {
            MovimientoSaldo prev = movimientoSaldoRepository.findByIdempotencyKey(dto.getIdempotencyKey()).get();
            return new MovimientoSaldoDTO(prev);
        }

        CuentaSaldo cuenta = cuentaSaldoRepository.findByAlumnoId(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de saldo no encontrada para el alumno ID: " + alumnoId));

        cuenta.finalizarReserva(dto.getMonto());
        cuenta = cuentaSaldoRepository.save(cuenta);

        MovimientoSaldo mov = new MovimientoSaldo(
                cuenta, TipoMovimientoSaldo.DEBITO_VENTA, dto.getMonto(), cuenta.getSaldoActual(),
                "VENTA_RESERVA", dto.getVentaId(), dto.getIdempotencyKey(), dto.getUsuarioId(), "Débito definitivo por entrega de reserva #" + dto.getReservaId() + " (Venta #" + dto.getVentaId() + ")"
        );
        mov = movimientoSaldoRepository.save(mov);
        return new MovimientoSaldoDTO(mov);
    }

    @Transactional
    public MovimientoSaldoDTO liberar(Long alumnoId, Long monto, Long reservaId, Long usuarioId, String idempotencyKey) {
        if (movimientoSaldoRepository.existsByIdempotencyKey(idempotencyKey)) {
            MovimientoSaldo prev = movimientoSaldoRepository.findByIdempotencyKey(idempotencyKey).get();
            return new MovimientoSaldoDTO(prev);
        }

        CuentaSaldo cuenta = cuentaSaldoRepository.findByAlumnoId(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de saldo no encontrada para el alumno ID: " + alumnoId));

        cuenta.liberar(monto);
        cuenta = cuentaSaldoRepository.save(cuenta);

        MovimientoSaldo mov = new MovimientoSaldo(
                cuenta, TipoMovimientoSaldo.LIBERACION_RESERVA, monto, cuenta.getSaldoActual(),
                "RESERVA", reservaId, idempotencyKey, usuarioId, "Liberación de saldo por cancelación de reserva #" + reservaId
        );
        mov = movimientoSaldoRepository.save(mov);
        return new MovimientoSaldoDTO(mov);
    }

    @Transactional
    public MovimientoSaldoDTO abonar(Long alumnoId, AbonarSaldoDTO dto) {
        if (movimientoSaldoRepository.existsByIdempotencyKey(dto.getIdempotencyKey())) {
            MovimientoSaldo prev = movimientoSaldoRepository.findByIdempotencyKey(dto.getIdempotencyKey()).get();
            return new MovimientoSaldoDTO(prev);
        }

        CuentaSaldo cuenta = cuentaSaldoRepository.findByAlumnoId(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de saldo no encontrada para el alumno ID: " + alumnoId));

        cuenta.abonar(dto.getMonto());
        cuenta = cuentaSaldoRepository.save(cuenta);

        MovimientoSaldo mov = new MovimientoSaldo(
                cuenta, TipoMovimientoSaldo.ABONO, dto.getMonto(), cuenta.getSaldoActual(),
                "ABONO", 1L, dto.getIdempotencyKey(), dto.getApoderadoId(), "Aporte de saldo vía Webpay"
        );
        mov = movimientoSaldoRepository.save(mov);
        return new MovimientoSaldoDTO(mov);
    }

    @Transactional(readOnly = true)
    public List<MovimientoSaldoDTO> listarMovimientos(Long alumnoId) {
        CuentaSaldo cuenta = cuentaSaldoRepository.findByAlumnoId(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de saldo no encontrada para el alumno ID: " + alumnoId));

        return movimientoSaldoRepository.findByCuentaSaldoIdOrderByFechaDesc(cuenta.getId())
                .stream()
                .map(MovimientoSaldoDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CuentaSaldo obtenerCuenta(Long alumnoId) {
        return cuentaSaldoRepository.findByAlumnoId(alumnoId)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta de saldo no encontrada para el alumno ID: " + alumnoId));
    }
}
