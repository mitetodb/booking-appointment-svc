package app.web;

import app.model.dto.DoctorDetailsDTO;
import app.model.dto.DoctorListViewDTO;
import app.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public List<DoctorListViewDTO> getAll() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public DoctorDetailsDTO getById(@PathVariable UUID id) {
        return doctorService.getDoctorById(id);
    }
}
