package com.cst438.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    // Create a new student
    @PostMapping("/")
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // Read all students
    @GetMapping("/")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Read a student by ID
    @GetMapping("/{studentId}")
    public Student getStudentById(@PathVariable int studentId) {
        return studentRepository.findById(studentId)
                .orElse(null);
    }

    // Update a student by ID
    @PutMapping("/{studentId}")
    public Student updateStudent(@PathVariable int studentId, @RequestBody Student updatedStudent) {
        return studentRepository.findById(studentId)
                .map(student -> {
                    student.setName(updatedStudent.getName());
                    student.setEmail(updatedStudent.getEmail());
                    student.setStatusCode(updatedStudent.getStatusCode());
                    student.setStatus(updatedStudent.getStatus());
                    return studentRepository.save(student);
                })
                .orElse(null);
    }

    // Delete a student by ID
    @DeleteMapping("/{studentId}")
    public void deleteStudent(@PathVariable int studentId) {
        studentRepository.deleteById(studentId);
    }
}