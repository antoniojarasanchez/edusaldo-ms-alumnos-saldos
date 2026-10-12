package cl.edusaldo.alumnos.service;

import cl.edusaldo.alumnos.dto.CrearAlumnoDTO;
import cl.edusaldo.alumnos.model.Alumno;
import cl.edusaldo.alumnos.model.CuentaSaldo;
import cl.edusaldo.alumnos.repository.AlumnoRepository;
import cl.edusaldo.alumnos.repository.CuentaSaldoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final CuentaSaldoRepository cuentaSaldoRepository;

    public AlumnoService(AlumnoRepository alumnoRepository, CuentaSaldoRepository cuentaSaldoRepository) {
        this.alumnoRepository = alumnoRepository;
        this.cuentaSaldoRepository = cuentaSaldoRepository;
    }

    @Transactional
    public Alumno crearAlumno(CrearAlumnoDTO dto) {
        if (alumnoRepository.existsByRut(dto.getRut())) {
            throw new IllegalArgumentException("Ya existe un alumno registrado con el RUT: " + dto.getRut());
        }

        Alumno alumno = new Alumno(dto.getRut(), dto.getPrimerNombre(), dto.getPrimerApellido(), dto.getFechaNacimiento(), dto.getCurso());
        alumno.setSegundoNombre(dto.getSegundoNombre());
        alumno.setSegundoApellido(dto.getSegundoApellido());

        Alumno guardado = alumnoRepository.save(alumno);

        CuentaSaldo cuenta = new CuentaSaldo(guardado, dto.getSaldoInicial());
        cuentaSaldoRepository.save(cuenta);
        guardado.setCuentaSaldo(cuenta);

        return guardado;
    }

    @Transactional(readOnly = true)
    public Alumno obtenerPorId(Long id) {
        return alumnoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Alumno> listarTodos() {
        return alumnoRepository.findAll();
    }
}
