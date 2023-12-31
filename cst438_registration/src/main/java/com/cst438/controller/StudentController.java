package com.cst438.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
			StudentDTO sdto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus());
			alist.add(sdto);
		}
		return alist.toArray(new StudentDTO[alist.size()]);
	}
	
	@GetMapping("/student/{id}")
	public StudentDTO getStudent(@PathVariable("id") int id) {
		Student s = studentRepository.findById(id).orElse(null);
		if (s!=null) {
			StudentDTO sdto = new StudentDTO(s.getStudent_id(), s.getName(), s.getEmail(), s.getStatusCode(), s.getStatus());
			return sdto;
		} else {
			throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "student not found "+id);
		}
	}
	
	@PutMapping("/student/{id}") 
	public void updateStudent(@PathVariable("id")int id, @RequestBody StudentDTO sdto) {
		Student s = studentRepository.findById(id).orElse(null);
		if (s==null) {
			throw  new ResponseStatusException( HttpStatus.NOT_FOUND, "student not found "+id);
		}
		// has email been changed, check that new email does not exist in database
		if (!s.getEmail().equals(sdto.email())) {
		// update name, email.  new email must not exist in database
			Student check = studentRepository.findByEmail(sdto.email());
			if (check != null) {
				// error.  email exists.
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+sdto.email());
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
			// error.  email exists.
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student email already exists "+sdto.email());
		}
		Student s = new Student();
		s.setEmail(sdto.email());
		s.setName(sdto.name());
		s.setStatusCode(sdto.statusCode());
		s.setStatus(sdto.status());
		studentRepository.save(s);
		// return the database generated student_id 
		return s.getStudent_id();
	}
	
	@DeleteMapping("/student/{id}")
	public void deleteStudent(@PathVariable("id") int id, @RequestParam("force") Optional<String> force) {
		Student s = studentRepository.findById(id).orElse(null);
		if (s!=null) {
			// are there enrollments?
			List<Enrollment> list = enrollmentRepository.findByStudentId(id);
			if (list.size()>0 && force.isEmpty()) {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "student has enrollments");
			} else {
				studentRepository.deleteById(id);
			}
		} else {
			// if student does not exist.  do nothing
			return;
		}
		
	}

}
//package com.cst438.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import com.cst438.domain.Student;
//import com.cst438.domain.StudentRepository;
//
//@RestController
//@RequestMapping("/students")
//public class StudentController {
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    // Create a new student
//    @PostMapping("/")
//    public Student createStudent(@RequestBody Student student) {
//        return studentRepository.save(student);
//    }
//
//    // Read all students
//    @GetMapping("/")
//    public List<Student> getAllStudents() {
//        return studentRepository.findAll();
//    }
//
//    // Read a student by ID
//    @GetMapping("/{student_id}")
//    public Student getStudentById(@PathVariable int student_id) {
//        return studentRepository.findById(student_id)
//                .orElse(null);
//    }
//
//    // Update a student's status and statusCode by ID
//    @PutMapping("/{student_id}")
//    public Student updateStudentStatusAndStatusCode(@PathVariable int student_id, @RequestBody Student updatedStudent) {
//        return studentRepository.findById(student_id)
//                .map(student -> {
//                    student.setStatus(updatedStudent.getStatus());
//                    student.setStatusCode(updatedStudent.getStatusCode());
//                    return studentRepository.save(student);
//                })
//                .orElse(null);
//    }
//
//
//    // Delete a student by ID
//    @DeleteMapping("/{student_id}")
//    public void deleteStudent(@PathVariable int student_id) {
//        studentRepository.deleteById(student_id);
//    }
//}