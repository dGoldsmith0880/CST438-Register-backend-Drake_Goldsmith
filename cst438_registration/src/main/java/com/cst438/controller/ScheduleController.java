package com.cst438.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin
public class ScheduleController {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    GradebookService gradebookService;

    /*
     * get current schedule for student.
     */
    @GetMapping("/schedule")
    public ScheduleDTO[] getSchedule(@RequestParam("year") int year, @RequestParam("semester") String semester, Principal principal) {
        System.out.println("/schedule called");

        String student_email = principal.getName(); // Get the authenticated student's email

        Student student = studentRepository.findByEmail(student_email);
        if (student != null) {
            System.out.println("/schedule student " + student.getName() + " " + student.getStudent_id());

            List<Enrollment> enrollments = enrollmentRepository.findStudentSchedule(student_email, year, semester);
            ScheduleDTO[] sched = createSchedule(year, semester, student, enrollments);
            return sched;
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. User does not have 'Student' role.");
        }
    }


    /*
     * add a course for a student
     */
    @PostMapping("/schedule/course/{id}")
    @Transactional
    public ScheduleDTO addCourse(@PathVariable int id, Principal principal) {
        String student_email = principal.getName(); // Get the authenticated student's email
        System.out.println(student_email);

        Student student = studentRepository.findByEmail(student_email);
        Course course = courseRepository.findById(id).orElse(null);

        if (student != null && course != null && student.getStatusCode()==0) {
            // Check if the role is "Student" before allowing the course registration.
            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(student);
            enrollment.setCourse(course);
            enrollment.setYear(course.getYear());
            enrollment.setSemester(course.getSemester());
            enrollmentRepository.save(enrollment);

            ScheduleDTO result = createSchedule(enrollment);
            return result;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course_id invalid, student not allowed to register for the course, or role is not 'Student'. " + id);
        }
    }

    @DeleteMapping("/schedule/{enrollment_id}")
    @Transactional
    public void dropCourse(@PathVariable int enrollment_id, Principal principal) {
        String student_email = principal.getName(); // Get the authenticated student's email
        Enrollment enrollment = enrollmentRepository.findById(enrollment_id).orElse(null);

        if (enrollment != null && enrollment.getStudent().getEmail().equals(student_email)) {
            // Check if the role is "Student" before allowing the course drop.
            enrollmentRepository.delete(enrollment);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Enrollment_id invalid or role is not 'Student'. " + enrollment_id);
        }
    }


    /*
     * helper method to transform course, enrollment, student entities into a an
     * instance of ScheduleDTO to return to the front end. This makes the front end
     * less dependent on the details of the database.
     */
    private ScheduleDTO[] createSchedule(int year, String semester, Student s, List<Enrollment> enrollments) {
        ScheduleDTO[] result = new ScheduleDTO[enrollments.size()];
        for (int i = 0; i < enrollments.size(); i++) {
            ScheduleDTO dto = createSchedule(enrollments.get(i));
            result[i] = dto;
        }
        return result;
    }

    private ScheduleDTO createSchedule(Enrollment e) {
        Course c = e.getCourse();
        ScheduleDTO dto = new ScheduleDTO(e.getEnrollment_id(), c.getCourse_id(), c.getSection(), c.getTitle(),
                c.getTimes(), c.getBuilding(), c.getRoom(), c.getInstructor(), c.getStart().toString(),
                c.getEnd().toString(), e.getCourseGrade());

        return dto;
    }
}
