package com.cst438.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.FinalGradeDTO;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@Service
@ConditionalOnProperty(prefix = "gradebook", name = "service", havingValue = "rest")
@RestController
public class GradebookServiceREST implements GradebookService {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	private static String gradebook_url;

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		System.out.println("Start Message "+ student_email +" " + course_id); 
		// TODO use RestTemplate to send message to gradebook service
		// Create an EnrollmentDTO to send to the Gradebook service
		EnrollmentDTO enrollmentDTO = new EnrollmentDTO(course_id, student_email, student_name, course_id);
		
		// Use RestTemplate to send a POST request to the Gradebook service
		restTemplate.postForObject(gradebook_url, enrollmentDTO, EnrollmentDTO.class);
	}
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	/*
	 * endpoint for final course grades
	 */
	@PutMapping("/course/{course_id}")
	@Transactional
	public void updateCourseGrades( @RequestBody FinalGradeDTO[] grades, @PathVariable("course_id") int course_id) {
		System.out.println("Grades received "+grades.length);
		//TODO update grades in enrollment records with grades received from gradebook service
		//Iterate over the grades and update enrollment records
		for (FinalGradeDTO gradeDTO : grades) {
			
			String studentEmail = gradeDTO.studentEmail();
			// Find the enrollment record based on student email and course_id
			Enrollment enrollment = enrollmentRepository.findByEmailAndCourseId(studentEmail, course_id);
					
			if (enrollment != null) {
				// Update the course grade in the enrollment record
			    enrollment.setCourseGrade(gradeDTO.grade());
			    // Save the updated enrollment record to the database
			    enrollmentRepository.save(enrollment);
			}
		}
	}
}
