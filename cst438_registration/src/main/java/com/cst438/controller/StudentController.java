package com.cst438.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;

@RestController
@CrossOrigin
public class StudentController {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @GetMapping("/student")
    public StudentDTO[] getStudents() {
        Iterable<Student> list = studentRepository.findAll();
        ArrayList<StudentDTO> alist = new ArrayList<>();
        for (Student s : list) {
            StudentDTO sdto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus(), s.getPassword(), s.getRole());
            alist.add(sdto);
        }
        return alist.toArray(new StudentDTO[alist.size()]);
    }

    @GetMapping("/student/{id}")
    public StudentDTO getStudent(@PathVariable("id") int id) {
        Student s = studentRepository.findById(id).orElse(null);
        if (s != null) {
            StudentDTO sdto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus(), s.getPassword(), s.getRole());
            return sdto;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found " + id);
        }
    }

    @PutMapping("/student/{id}")
    public void updateStudent(@PathVariable("id") int id, @RequestBody StudentDTO sdto) {
        Student s = studentRepository.findById(id).orElse(null);
        if (s == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found " + id);
        }
        // Has email been changed? Check that the new email does not exist in the database.
        if (!s.getEmail().equals(sdto.email())) {
            // Update name, email. The new email must not exist in the database.
            Student check = studentRepository.findByEmail(sdto.email());
            if (check != null) {
                // Error. Email exists.
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email already exists " + sdto.email());
            }
        }
        s.setEmail(sdto.email());
        s.setName(sdto.name());
        s.setStatusCode(sdto.statusCode());
        s.setStatus(sdto.status());
        studentRepository.save(s);
    }

    @PostMapping("/student")
    public int createStudent(@RequestBody StudentDTO sdto) {
        Student check = studentRepository.findByEmail(sdto.email());
        if (check != null) {
            // Error. Email exists.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email already exists " + sdto.email());
        }
	    Student s = new Student();
	    s.setEmail(sdto.email());
	    s.setName(sdto.name());
	    s.setStatusCode(sdto.statusCode());
	    s.setStatus(sdto.status());
	    s.setPassword(sdto.password());
	    s.setRole(sdto.role());
	    studentRepository.save(s);
	    // Return the database generated student_id
	    return s.getStudent_id();
    }

    @DeleteMapping("/student/{id}")
    public void deleteStudent(@PathVariable("id") int id, @RequestParam("force") Optional<String> force) {
    	Student s = studentRepository.findById(id).orElse(null);
        if (s != null) {
            // Are there enrollments?
            List<Enrollment> list = enrollmentRepository.findByStudentId(id);
            if (list.size() > 0 && force.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student has enrollments");
            } else {
                studentRepository.deleteById(id);
            }
        } else {
            // If student does not exist, do nothing
            return;
        }
    }
}
