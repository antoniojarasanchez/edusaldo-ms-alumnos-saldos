package cl.edusaldo.alumnos.controller;

import cl.edusaldo.alumnos.dto.AlumnoResponseDTO;
import cl.edusaldo.alumnos.dto.CrearAlumnoDTO;
import cl.edusaldo.alumnos.model.Alumno;
import cl.edusaldo.alumnos.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alumnos")
@Tag(name = "Alumnos", description = "Gestión de nómina de estudiantes y perfiles")
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @PostMapping
    @Operation(summary = "Registrar nuevo alumno y crear su cuenta de saldo")
    public ResponseEntity<AlumnoResponseDTO> crear(@Valid @RequestBody CrearAlumnoDTO dto) {
        Alumno a = alumnoService.crearAlumno(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AlumnoResponseDTO(a));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar datos y saldo de un alumno por ID")
    public ResponseEntity<AlumnoResponseDTO> obtenerPorId(@PathVariable Long id) {
        Alumno a = alumnoService.obtenerPorId(id);
        return ResponseEntity.ok(new AlumnoResponseDTO(a));
    }

    @GetMapping
    @Operation(summary = "Listar todos los alumnos con sus saldos")
    public ResponseEntity<List<AlumnoResponseDTO>> listarTodos() {
        List<AlumnoResponseDTO> list = alumnoService.listarTodos().stream()
                .map(AlumnoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
