package cl.edusaldo.alumnos.controller;

import cl.edusaldo.alumnos.dto.AbonarSaldoDTO;
import cl.edusaldo.alumnos.dto.ComprometerSaldoDTO;
import cl.edusaldo.alumnos.dto.FinalizarReservaDTO;
import cl.edusaldo.alumnos.dto.MovimientoSaldoDTO;
import cl.edusaldo.alumnos.service.SaldoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saldos")
@Tag(name = "Saldos y Movimientos", description = "Operaciones de saldo, abonos, compromisos y finalización de reservas")
public class SaldoController {

    private final SaldoService saldoService;

    public SaldoController(SaldoService saldoService) {
        this.saldoService = saldoService;
    }

    @PostMapping("/{alumnoId}/comprometer")
    @Operation(summary = "Comprometer saldo por confirmación de reserva")
    public ResponseEntity<MovimientoSaldoDTO> comprometer(
            @PathVariable Long alumnoId,
            @Valid @RequestBody ComprometerSaldoDTO dto) {
        MovimientoSaldoDTO res = saldoService.comprometer(alumnoId, dto);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{alumnoId}/finalizar-reserva")
    @Operation(summary = "Finalizar reserva atómicamente: reduce saldo comprometido y actual sin doble cobro")
    public ResponseEntity<MovimientoSaldoDTO> finalizarReserva(
            @PathVariable Long alumnoId,
            @Valid @RequestBody FinalizarReservaDTO dto) {
        MovimientoSaldoDTO res = saldoService.finalizarReserva(alumnoId, dto);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/{alumnoId}/abonar")
    @Operation(summary = "Registrar aporte de saldo vía Webpay simulado")
    public ResponseEntity<MovimientoSaldoDTO> abonar(
            @PathVariable Long alumnoId,
            @Valid @RequestBody AbonarSaldoDTO dto) {
        MovimientoSaldoDTO res = saldoService.abonar(alumnoId, dto);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{alumnoId}/movimientos")
    @Operation(summary = "Consultar historial de movimientos de saldo del alumno")
    public ResponseEntity<List<MovimientoSaldoDTO>> listarMovimientos(@PathVariable Long alumnoId) {
        List<MovimientoSaldoDTO> list = saldoService.listarMovimientos(alumnoId);
        return ResponseEntity.ok(list);
    }
}
