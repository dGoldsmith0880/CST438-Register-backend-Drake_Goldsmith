package com.cst438.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @GetMapping("/{student_id}")
    public Student getStudentById(@PathVariable int student_id) {
        return studentRepository.findById(student_id)
                .orElse(null);
    }

    // Update a student's status and statusCode by ID
    @PutMapping("/{student_id}")
    public Student updateStudentStatusAndStatusCode(@PathVariable int student_id, @RequestBody Student updatedStudent) {
        return studentRepository.findById(student_id)
                .map(student -> {
                    student.setStatus(updatedStudent.getStatus());
                    student.setStatusCode(updatedStudent.getStatusCode());
                    return studentRepository.save(student);
                })
                .orElse(null);
    }


    // Delete a student by ID
    @DeleteMapping("/{student_id}")
    public void deleteStudent(@PathVariable int student_id) {
        studentRepository.deleteById(student_id);
    }
}