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
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestStudent {

    @Autowired
    private MockMvc mvc;

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
        Student[] studentList = fromJsonString(response.getContentAsString(), Student[].class);
        assertEquals(4, studentList.length);
        
        for (Student student : studentList) {
            System.out.println(student.toString());
        }
    }
    
    @Test
    public void updateStudentStatus() throws Exception {
        Student student = new Student();
        student.setName("Drake Goldsmith");
        student.setEmail("dGoldsmith@csumb.edu");
        student.setStatusCode(1);
        student.setStatus("Active");

        // Perform a POST request to create a student
        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/students/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200)
        assertEquals(200, response.getStatus());

        Student createdStudent = fromJsonString(response.getContentAsString(), Student.class);
        int createdStudentId = createdStudent.getStudent_id();

        // Perform a PUT request to update the student's status
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
        
        // Perform a GET request to retrieve student data based off of ID
        response = mvc.perform(
                MockMvcRequestBuilders
                        .get("/students/" + createdStudentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200) when retrieving the student by ID
        assertEquals(200, response.getStatus());

        Student retrievedStudent = fromJsonString(response.getContentAsString(), Student.class);
        assertEquals(createdStudentId, retrievedStudent.getStudent_id());
        assertEquals(0, retrievedStudent.getStatusCode()); // Updated status code
        assertEquals("Inactive", retrievedStudent.getStatus()); // Updated status
    }

    @Test
    public void deleteStudent() throws Exception {
        Student student = new Student();
        student.setName("Drake Goldsmith");
        student.setEmail("dGoldsmith@csumb.edu");
        student.setStatusCode(1);
        student.setStatus("Active");

        // Perform a POST request to create a student
        MockHttpServletResponse response = mvc.perform(
                MockMvcRequestBuilders
                        .post("/students/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(student))
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200)
        assertEquals(200, response.getStatus());

        // Retrieve the created student
        Student createdStudent = fromJsonString(response.getContentAsString(), Student.class);
        int createdStudentId = createdStudent.getStudent_id();

        // Perform a DELETE request to delete the student by ID
        response = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/students/" + createdStudentId))
                .andReturn().getResponse();

        // Verify that return status is OK (value 200) when deleting the student by ID
        assertEquals(200, response.getStatus());
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
