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

    // test to create a new student "createStudent"
    @Test
    public void createStudent() throws Exception {
        // Create a new Student object with required data
        Student student = new Student();
        student.setName("Drake Goldsmith");
        student.setEmail("dGoldsmith@csumb.edu");
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
        assertEquals("Drake Goldsmith", createdStudent.getName());
        assertEquals("dGoldsmith@csumb.edu", createdStudent.getEmail());
        assertEquals(1, createdStudent.getStatusCode());
        assertEquals("Active", createdStudent.getStatus());
        
        
        
        // Test updating the student's status by ID "updateStudentStatusAndStatusCode"
        int createdStudentId = createdStudent.getStudent_id();
        Student updatedStudent = new Student();
        updatedStudent.setStatusCode(0); // Updated status code
        updatedStudent.setStatus("Inactive"); // Updated status
        response = mvc.perform(
                MockMvcRequestBuilders
                        .put("/students/" + createdStudentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedStudent))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200) when updating the student's status
        assertEquals(200, response.getStatus());

        
        
        // Test retrieving the updated student by ID "getStudentById"
        response = mvc.perform(
                MockMvcRequestBuilders
                        .get("/students/" + createdStudentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200) when retrieving the student by ID
        assertEquals(200, response.getStatus());

        // Verify that the retrieved student matches the updated status
        Student retrievedStudent1 = fromJsonString(response.getContentAsString(), Student.class);
        assertEquals(createdStudentId, retrievedStudent1.getStudent_id());
        assertEquals(0, retrievedStudent1.getStatusCode()); // Updated status code
        assertEquals("Inactive", retrievedStudent1.getStatus()); // Updated status
        
        
        
        // Test deleting the created student by ID "deleteStudent"
        response = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/students/" + createdStudentId))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200) when deleting the student by ID
        assertEquals(200, response.getStatus());
    }
    
    
    // Test to retrieve all students "getAllStudents"
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
