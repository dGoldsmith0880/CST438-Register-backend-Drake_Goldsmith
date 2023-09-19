package com.cst438;
import com.cst438.domain.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestStudent {

    @Autowired
    private MockMvc mvc;

    // Example test to create a new student
    @Test
    public void createStudent() throws Exception {
        // Create a new Student object with required data
        Student student = new Student();
        student.setName("John Doe");
        student.setEmail("john@example.com");
        student.setStatusCode(1);
        student.setStatus("Active");

        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/students/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200)
        assertEquals(200, response.getStatus());

        // Verify that the created student has a non-zero ID
        Student createdStudent = fromJsonString(response.getContentAsString(), Student.class);
        assertNotEquals(0, createdStudent.getStudent_id());

        // You can add more assertions to validate the created student's properties
        assertEquals("John Doe", createdStudent.getName());
        assertEquals("john@example.com", createdStudent.getEmail());
        assertEquals(1, createdStudent.getStatusCode());
        assertEquals("Active", createdStudent.getStatus());

        
        // Test retrieving the created student by ID
        int createdStudentId = createdStudent.getStudent_id();
        response = mvc.perform(
                MockMvcRequestBuilders
                        .get("/students/" + createdStudentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200) when retrieving the student by ID
        assertEquals(200, response.getStatus());

        // Verify that the retrieved student matches the created student
        Student retrievedStudent = fromJsonString(response.getContentAsString(), Student.class);
        assertEquals(createdStudentId, retrievedStudent.getStudent_id());
        assertEquals("John Doe", retrievedStudent.getName());
        assertEquals("john@example.com", retrievedStudent.getEmail());
        assertEquals(1, retrievedStudent.getStatusCode());
        assertEquals("Active", retrievedStudent.getStatus());
        
        
        // Test deleting the created student by ID
        response = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/students/" + createdStudentId))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200) when deleting the student by ID
        assertEquals(200, response.getStatus());

        // Attempt to retrieve the deleted student by ID
        response = mvc.perform(
                MockMvcRequestBuilders
                        .get("/students/" + createdStudentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is NOT FOUND (value 404) when attempting to retrieve the deleted student
        assertEquals(404, response.getStatus());
    }
    
    @Test
    public void getAllStudents() throws Exception {
        // Perform a GET request to retrieve all students
        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders
                        .get("/students/")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200)
        assertEquals(200, response.getStatus());

        // Parse the response into an array of Student objects
        Student[] studentList = fromJsonString(response.getContentAsString(), Student[].class);

        // Verify that the response contains the expected number of students (based on your provided data)
        assertEquals(3, studentList.length);
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
