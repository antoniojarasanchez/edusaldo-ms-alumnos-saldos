package cl.edusaldo.alumnos.dto;

import cl.edusaldo.alumnos.model.MovimientoSaldo;
import cl.edusaldo.alumnos.model.TipoMovimientoSaldo;

import java.time.LocalDateTime;

public class MovimientoSaldoDTO {
    private Long id;
    private TipoMovimientoSaldo tipo;
    private Long monto;
    private Long saldoResultante;
    private LocalDateTime fecha;
    private String referenciaTipo;
    private Long referenciaId;
    private String descripcion;

    public MovimientoSaldoDTO() {}

    public MovimientoSaldoDTO(MovimientoSaldo m) {
        this.id = m.getId();
        this.tipo = m.getTipo();
        this.monto = m.getMonto();
        this.saldoResultante = m.getSaldoResultante();
        this.fecha = m.getFecha();
        this.referenciaTipo = m.getReferenciaTipo();
        this.referenciaId = m.getReferenciaId();
        this.descripcion = m.getDescripcion();
    }

    public Long getId() { return id; }
    public TipoMovimientoSaldo getTipo() { return tipo; }
    public Long getMonto() { return monto; }
    public Long getSaldoResultante() { return saldoResultante; }
    public LocalDateTime getFecha() { return fecha; }
    public String getReferenciaTipo() { return referenciaTipo; }
    public Long getReferenciaId() { return referenciaId; }
    public String getDescripcion() { return descripcion; }
}
