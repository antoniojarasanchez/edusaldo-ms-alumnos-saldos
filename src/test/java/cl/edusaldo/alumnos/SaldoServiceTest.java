package cl.edusaldo.alumnos;

import cl.edusaldo.alumnos.dto.AbonarSaldoDTO;
import cl.edusaldo.alumnos.dto.ComprometerSaldoDTO;
import cl.edusaldo.alumnos.dto.FinalizarReservaDTO;
import cl.edusaldo.alumnos.dto.MovimientoSaldoDTO;
import cl.edusaldo.alumnos.model.Alumno;
import cl.edusaldo.alumnos.model.CuentaSaldo;
import cl.edusaldo.alumnos.model.MovimientoSaldo;
import cl.edusaldo.alumnos.model.TipoMovimientoSaldo;
import cl.edusaldo.alumnos.repository.CuentaSaldoRepository;
import cl.edusaldo.alumnos.repository.MovimientoSaldoRepository;
import cl.edusaldo.alumnos.service.SaldoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaldoServiceTest {

    @Mock
    private CuentaSaldoRepository cuentaSaldoRepository;

    @Mock
    private MovimientoSaldoRepository movimientoSaldoRepository;

    @InjectMocks
    private SaldoService saldoService;

    private Alumno alumnoPrueba;
    private CuentaSaldo cuentaPrueba;

    @BeforeEach
    void setUp() {
        alumnoPrueba = new Alumno("12.345.678-5", "Sofía", "Pérez", LocalDate.of(2017, 4, 15), "3° Básico");
        alumnoPrueba.setId(1L);
        cuentaPrueba = new CuentaSaldo(alumnoPrueba, 10000L);
        alumnoPrueba.setCuentaSaldo(cuentaPrueba);
    }

    @Test
    @DisplayName("1. Rechazo de compromiso si el saldo disponible es insuficiente (Invariante HU35)")
    void testRechazoCompromisoSaldoInsuficiente() {
        when(cuentaSaldoRepository.findByAlumnoId(1L)).thenReturn(Optional.of(cuentaPrueba));
        when(movimientoSaldoRepository.existsByIdempotencyKey("KEY-01")).thenReturn(false);

        ComprometerSaldoDTO dto = new ComprometerSaldoDTO();
        dto.setMonto(15000L); // Mayor que saldo de 10.000
        dto.setReservaId(101L);
        dto.setUsuarioId(4L);
        dto.setIdempotencyKey("KEY-01");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> saldoService.comprometer(1L, dto));
        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
        assertEquals(10000L, cuentaPrueba.getSaldoActual());
        assertEquals(0L, cuentaPrueba.getSaldoComprometido());
        assertEquals(10000L, cuentaPrueba.getSaldoDisponible());
    }

    @Test
    @DisplayName("2. Compromiso de saldo incrementa saldo comprometido y reduce disponible (HU28)")
    void testCompromisoSaldoIncrementaComprometidoYReduceDisponible() {
        when(cuentaSaldoRepository.findByAlumnoId(1L)).thenReturn(Optional.of(cuentaPrueba));
        when(movimientoSaldoRepository.existsByIdempotencyKey("KEY-02")).thenReturn(false);
        when(cuentaSaldoRepository.save(any(CuentaSaldo.class))).thenAnswer(i -> i.getArgument(0));

        ComprometerSaldoDTO dto = new ComprometerSaldoDTO();
        dto.setMonto(4000L);
        dto.setReservaId(102L);
        dto.setUsuarioId(4L);
        dto.setIdempotencyKey("KEY-02");

        MovimientoSaldoDTO res = saldoService.comprometer(1L, dto);

        assertNotNull(res);
        assertEquals(TipoMovimientoSaldo.COMPROMISO_RESERVA, res.getTipo());
        assertEquals(4000L, res.getMonto());
        assertEquals(10000L, cuentaPrueba.getSaldoActual());
        assertEquals(4000L, cuentaPrueba.getSaldoComprometido());
        assertEquals(6000L, cuentaPrueba.getSaldoDisponible());
    }

    @Test
    @DisplayName("3. Finalización atómica de reserva reduce saldo comprometido y actual sin doble cobro (HU33)")
    void testFinalizarReservaReduceComprometidoYActualSinDuplicidad() {
        // Estado previo: reserva activa de 4.000 comprometida
        cuentaPrueba.comprometer(4000L, 103L, 4L, "KEY-INIT");
        assertEquals(6000L, cuentaPrueba.getSaldoDisponible());

        when(cuentaSaldoRepository.findByAlumnoId(1L)).thenReturn(Optional.of(cuentaPrueba));
        when(movimientoSaldoRepository.existsByIdempotencyKey("KEY-03")).thenReturn(false);
        when(cuentaSaldoRepository.save(any(CuentaSaldo.class))).thenAnswer(i -> i.getArgument(0));

        FinalizarReservaDTO dto = new FinalizarReservaDTO();
        dto.setMonto(4000L);
        dto.setReservaId(103L);
        dto.setVentaId(501L);
        dto.setUsuarioId(2L);
        dto.setIdempotencyKey("KEY-03");

        MovimientoSaldoDTO res = saldoService.finalizarReserva(1L, dto);

        assertNotNull(res);
        assertEquals(TipoMovimientoSaldo.DEBITO_VENTA, res.getTipo());
        // Invariante atómica: comprometido vuelve a 0, actual baja a 6.000, disponible queda exactamente en 6.000
        assertEquals(0L, cuentaPrueba.getSaldoComprometido());
        assertEquals(6000L, cuentaPrueba.getSaldoActual());
        assertEquals(6000L, cuentaPrueba.getSaldoDisponible());
    }

    @Test
    @DisplayName("4. Control de idempotencia previene doble abono ante reintentos de red (HU21)")
    void testIdempotenciaEvitaDuplicidadDeAbono() {
        MovimientoSaldo movPrevio = new MovimientoSaldo(
                cuentaPrueba, TipoMovimientoSaldo.ABONO, 5000L, 15000L,
                "ABONO", 1L, "KEY-WEBPAY-SAME", 4L, "Aporte previo procesado"
        );

        when(movimientoSaldoRepository.existsByIdempotencyKey("KEY-WEBPAY-SAME")).thenReturn(true);
        when(movimientoSaldoRepository.findByIdempotencyKey("KEY-WEBPAY-SAME")).thenReturn(Optional.of(movPrevio));

        AbonarSaldoDTO dto = new AbonarSaldoDTO();
        dto.setApoderadoId(4L);
        dto.setMonto(5000L);
        dto.setIdempotencyKey("KEY-WEBPAY-SAME");

        MovimientoSaldoDTO res = saldoService.abonar(1L, dto);

        // Retorna la transacción previa sin volver a sumar dinero
        assertNotNull(res);
        assertEquals(5000L, res.getMonto());
        assertEquals(10000L, cuentaPrueba.getSaldoActual()); // Saldo no se mutó por segunda vez
        verify(cuentaSaldoRepository, never()).save(any());
    }
}
