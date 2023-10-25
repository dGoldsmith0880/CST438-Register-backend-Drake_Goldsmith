package com.cst438;



import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.StudentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestStudent {
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void createStudent() throws Exception {
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null, null, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		// retrieve the student
		response = mvc.perform(
				MockMvcRequestBuilders
				 .get("/student/"+student_id)
				 .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		StudentDTO actual = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertEquals(sdto.name(), actual.name());
		assertEquals(sdto.email(), actual.email());
		assertEquals(sdto.statusCode(), actual.statusCode());
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
	}
	
	@Test
	public void createStudentDupEmail() throws Exception {
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null, null, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		// try to create another student with same email
		sdto = new StudentDTO(0, "name2 test2", "ntest@csumb.edu", 0, null, null, null);
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(400, response.getStatus()); // BAD_REQUEST
		assertTrue(response.getErrorMessage().contains("student email already exists"));
		
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
	}
	
	@Test
	public void updateStudent() throws Exception  {
		
		MockHttpServletResponse response;

		// retrieve the student id = 2
		response = mvc.perform(
				MockMvcRequestBuilders
				 .get("/student/2")
				 .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		StudentDTO original = fromJsonString(response.getContentAsString(), StudentDTO.class);
		// modify name, email and statusCode
		StudentDTO mod = new StudentDTO(original.studentId(), "new name", "newname@csumb.edu", 1, "balance outstanding", null, null);
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/2")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(mod)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
		// retrieve again and check updated fields
		response = mvc.perform(
				MockMvcRequestBuilders
				 .get("/student/2")
				 .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		StudentDTO actual = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertEquals(mod, actual);
	}
	
	@Test
	public void updateStudentDupEmail() throws Exception {
		// create 2 students 
		
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null, null, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		StudentDTO sdto2 = new StudentDTO(0, "name test2", "ntest2@csumb.edu", 0, null, null, null);
		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto2)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id2 = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id2 > 0);
		
		// attempt to change email of student #1 to student #2
		StudentDTO sdto3 = new StudentDTO(student_id, "name test", "ntest2@csumb.edu", 0, null, null, null);
		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/"+student_id)
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto3)))
				.andReturn().getResponse();
		assertEquals(400, response.getStatus());
		assertTrue(response.getErrorMessage().contains("student email already exists"));
		
	}
	
	@Test
	public void updateStudentNotFound() throws Exception {
		StudentDTO sdto = new StudentDTO(99, "namenew test", "ntestnew@csumb.edu", 0, null, null, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .put("/student/99")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(404, response.getStatus());
		
	}
	
	@Test
	public void deleteStudentNoEnrollments() throws Exception {
		StudentDTO sdto = new StudentDTO(0, "name test", "ntest@csumb.edu", 0, null, null, null);
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/student")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)
			      .content(asJsonString(sdto)))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		int  student_id = Integer.parseInt(response.getContentAsString());
		assertTrue(student_id > 0);
		
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
		// another delete should be OK.
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/"+student_id))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());
		
	}
	
	@Test
	public void deleteStudentWithEnrollment() throws Exception {
		MockHttpServletResponse response;
		// delete the new student
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/1"))
				.andReturn().getResponse();
		assertEquals(400, response.getStatus()); // BAD_REQUEST
		assertTrue(response.getErrorMessage().contains("student has enrollments"));
		
		// now do a force delete
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/1?force=yes"))
				.andReturn().getResponse();
		assertEquals(200, response.getStatus());	
		
	}

	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
//package com.cst438;
//import com.cst438.domain.Student;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import static org.junit.Assert.assertEquals;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class JunitTestStudent {
//
//    @Autowired
//    private MockMvc mvc;
//
//    // Test to retrieve all students "getAllStudents"
//    @Test
//    public void getAllStudents() throws Exception {
//        // Perform a GET request to retrieve all students
//        MockHttpServletResponse response = mvc.perform(
//                MockMvcRequestBuilders
//                        .get("/students/")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//        
//        // Verify that return status is OK (value 200)
//        assertEquals(200, response.getStatus());
//        Student[] studentList = fromJsonString(response.getContentAsString(), Student[].class);
//        assertEquals(4, studentList.length);
//        
//        for (Student student : studentList) {
//            System.out.println(student.toString());
//        }
//    }
//    
//    @Test
//    public void updateStudentStatus() throws Exception {
//        Student student = new Student();
//        student.setName("Drake Goldsmith");
//        student.setEmail("dGoldsmith@csumb.edu");
//        student.setStatusCode(1);
//        student.setStatus("Active");
//
//        // Perform a POST request to create a student
//        MockHttpServletResponse response = mvc.perform(
//                MockMvcRequestBuilders
//                        .post("/students/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(student))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify that return status is OK (value 200)
//        assertEquals(200, response.getStatus());
//
//        Student createdStudent = fromJsonString(response.getContentAsString(), Student.class);
//        int createdStudentId = createdStudent.getStudent_id();
//
//        // Perform a PUT request to update the student's status
//        Student updatedStudent = new Student();
//        updatedStudent.setStatusCode(0); // Updated status code
//        updatedStudent.setStatus("Inactive"); // Updated status
//        response = mvc.perform(
//                MockMvcRequestBuilders
//                        .put("/students/" + createdStudentId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(updatedStudent))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify that return status is OK (value 200) when updating the student's status
//        assertEquals(200, response.getStatus());
//        
//        // Perform a GET request to retrieve student data based off of ID
//        response = mvc.perform(
//                MockMvcRequestBuilders
//                        .get("/students/" + createdStudentId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify that return status is OK (value 200) when retrieving the student by ID
//        assertEquals(200, response.getStatus());
//
//        Student retrievedStudent = fromJsonString(response.getContentAsString(), Student.class);
//        assertEquals(createdStudentId, retrievedStudent.getStudent_id());
//        assertEquals(0, retrievedStudent.getStatusCode()); // Updated status code
//        assertEquals("Inactive", retrievedStudent.getStatus()); // Updated status
//    }
//
//    @Test
//    public void deleteStudent() throws Exception {
//        Student student = new Student();
//        student.setName("Drake Goldsmith");
//        student.setEmail("dGoldsmith@csumb.edu");
//        student.setStatusCode(1);
//        student.setStatus("Active");
//
//        // Perform a POST request to create a student
//        MockHttpServletResponse response = mvc.perform(
//                MockMvcRequestBuilders
//                        .post("/students/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(student))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify that return status is OK (value 200)
//        assertEquals(200, response.getStatus());
//
//        // Retrieve the created student
//        Student createdStudent = fromJsonString(response.getContentAsString(), Student.class);
//        int createdStudentId = createdStudent.getStudent_id();
//
//        // Perform a DELETE request to delete the student by ID
//        response = mvc.perform(
//                MockMvcRequestBuilders
//                        .delete("/students/" + createdStudentId))
//                .andReturn().getResponse();
//
//        // Verify that return status is OK (value 200) when deleting the student by ID
//        assertEquals(200, response.getStatus());
//    }
//    
//
//    private static String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static <T> T fromJsonString(String str, Class<T> valueType) {
//        try {
//            return new ObjectMapper().readValue(str, valueType);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
