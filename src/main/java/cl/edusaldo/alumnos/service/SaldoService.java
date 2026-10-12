package cl.edusaldo.alumnos.service;

import cl.edusaldo.alumnos.dto.AbonarSaldoDTO;
import cl.edusaldo.alumnos.dto.ComprometerSaldoDTO;
import cl.edusaldo.alumnos.dto.FinalizarReservaDTO;
import cl.edusaldo.alumnos.dto.MovimientoSaldoDTO;
import cl.edusaldo.alumnos.model.CuentaSaldo;
import cl.edusaldo.alumnos.model.MovimientoSaldo;
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

        MovimientoSaldo mov = cuenta.comprometer(dto.getMonto(), dto.getReservaId(), dto.getUsuarioId(), dto.getIdempotencyKey());
        cuentaSaldoRepository.save(cuenta);
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

        MovimientoSaldo mov = cuenta.finalizarReserva(dto.getMonto(), dto.getReservaId(), dto.getVentaId(), dto.getUsuarioId(), dto.getIdempotencyKey());
        cuentaSaldoRepository.save(cuenta);
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

        MovimientoSaldo mov = cuenta.liberar(monto, reservaId, usuarioId, idempotencyKey);
        cuentaSaldoRepository.save(cuenta);
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

        MovimientoSaldo mov = cuenta.abonar(dto.getMonto(), 1L, dto.getApoderadoId(), dto.getIdempotencyKey());
        cuentaSaldoRepository.save(cuenta);
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
