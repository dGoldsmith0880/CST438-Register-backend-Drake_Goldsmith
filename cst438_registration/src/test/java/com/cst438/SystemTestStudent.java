package com.cst438;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class SystemTestStudent {
    public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\chromedriver-win64\\chromedriver.exe";
    public static final String URL = "http://localhost:3000/admin";
    public static final int SLEEP_DURATION = 1000; // 1 second.

    WebDriver driver;
    
    @BeforeEach
    public void setup() throws Exception {
        // Set up the Chrome WebDriver
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(ops);
        driver.get(URL);
        Thread.sleep(SLEEP_DURATION);
    }

    @Test
    @Order(1)
    public void addStudent() throws Exception {
        // Locate the "Add Student" button and click it to open the dialog
        WebElement addStudentButton = driver.findElement(By.id("addStudent"));
        addStudentButton.click();
        Thread.sleep(SLEEP_DURATION);

        // Locate and interact with the input fields in the dialog
        WebElement nameInput = driver.findElement(By.name("name"));
        WebElement emailInput = driver.findElement(By.name("email"));
        WebElement statusCodeInput = driver.findElement(By.name("statusCode"));
        WebElement statusInput = driver.findElement(By.name("status"));
        WebElement submitButton = driver.findElement(By.id("add"));

        // Fill in student information
        nameInput.sendKeys("John Doe");
        emailInput.sendKeys("john.doe@example.com");
        statusCodeInput.sendKeys("123");
        statusInput.sendKeys("Active");

        // Submit the form
        submitButton.click();
        Thread.sleep(SLEEP_DURATION);

        // Check if the success message is displayed
        WebElement successMessage = driver.findElement(By.id("message"));
        assertNotNull(successMessage);
        assertEquals("Student added.", successMessage.getText());

        // Fetch the list of students and check if the newly added student is in the list
        List<WebElement> studentList = driver.findElements(By.xpath("//table/tbody/tr"));
        
        boolean isNewStudentFound = false;
        for (WebElement student : studentList) {
            String name = student.findElement(By.xpath("./td[2]")).getText();
            String email = student.findElement(By.xpath("./td[3]")).getText();
            if ("John Doe".equals(name) && "john.doe@example.com".equals(email)) {
                isNewStudentFound = true;
                break;
            }
        }
        
        assertTrue(isNewStudentFound);
    }

    @Test
    @Order(2)
    public void updateStudent() throws Exception {
        // Fetch the list of students and find a specific student to edit
        List<WebElement> studentList = driver.findElements(By.xpath("//table/tbody/tr"));
        WebElement editStudent = null;
        String editName;
        String editEmail;
        
        for (WebElement student : studentList) {
            editName = student.findElement(By.xpath("./td[2]")).getText();
            editEmail = student.findElement(By.xpath("./td[3]")).getText();
            if ("John Doe".equals(editName) && "john.doe@example.com".equals(editEmail)) {
                editStudent = student;
                break;
            }
        }

        // If the student to edit is found, proceed with the update
        if (editStudent != null) {
            // Click the "Edit" button for the specific student
            WebElement editButton = editStudent.findElement(By.xpath("./td[6]"));
            editButton.click();
            Thread.sleep(SLEEP_DURATION);

            // Locate input fields for updating student information
            WebElement nameInput = driver.findElement(By.id("name"));
            WebElement emailInput = driver.findElement(By.id("email"));
            WebElement statusInput = driver.findElement(By.id("status"));
            WebElement statusCodeInput = driver.findElement(By.id("statusCode"));
            WebElement submitButton = driver.findElement(By.id("edit"));

            // Clear the input fields with a process using Keys.BACK_SPACE because .clear() wasn't working
            String currentText = nameInput.getAttribute("value");
            int textLength = currentText.length();
            for (int i = 0; i < textLength; i++) {
                nameInput.sendKeys(Keys.BACK_SPACE);
            }
            currentText = emailInput.getAttribute("value");
            textLength = currentText.length();
            for (int i = 0; i < textLength; i++) {
                emailInput.sendKeys(Keys.BACK_SPACE);
            }
            currentText = statusInput.getAttribute("value");
            textLength = currentText.length();
            for (int i = 0; i < textLength; i++) {
                statusInput.sendKeys(Keys.BACK_SPACE);
            }
            currentText = statusCodeInput.getAttribute("value");
            textLength = currentText.length();
            for (int i = 0; i < textLength; i++) {
                statusCodeInput.sendKeys(Keys.BACK_SPACE);
            }
            
            // Update student information
            nameInput.sendKeys("Updated Name");
            emailInput.sendKeys("Updated@email.com");
            statusCodeInput.sendKeys("456");
            statusInput.sendKeys("Inactive");

            // Submit the updated information
            submitButton.click();
            Thread.sleep(SLEEP_DURATION);

            // Check if the success message is displayed
            WebElement successMessage = driver.findElement(By.id("message"));
            assertNotNull(successMessage);
            assertEquals("Student edited.", successMessage.getText());
            
            // Find newly updated student in student list
            studentList = driver.findElements(By.xpath("//table/tbody/tr"));
            WebElement compareStudent = null;
            for (WebElement student : studentList) {
                editName = student.findElement(By.xpath("./td[2]")).getText();
                editEmail = student.findElement(By.xpath("./td[3]")).getText();
                if ("Updated Name".equals(editName) && "Updated@email.com".equals(editEmail)) {
                    compareStudent = student;
                    break;
                }
            }
            // Check if the updated student got it's attributes updated
            assertThat(compareStudent.findElement(By.xpath("./td[2]")).getText().equals("Updated Name"));
            assertThat(compareStudent.findElement(By.xpath("./td[3]")).getText().equals("Updated@email.com"));
            assertThat(compareStudent.findElement(By.xpath("./td[4]")).getText().equals("456"));
            assertThat(compareStudent.findElement(By.xpath("./td[5]")).getText().equals("Inactive"));
        }
    }

    @Test
    @Order(3)
    public void deleteStudent() throws Exception {
        List<WebElement> studentList = driver.findElements(By.xpath("//table/tbody/tr"));
        WebElement deleteStudent = null;
        String deleteName;
        String deleteEmail;
        // find the specific student to delete in the list
        for (WebElement student : studentList) {
            deleteName = student.findElement(By.xpath("./td[2]")).getText();
            deleteEmail = student.findElement(By.xpath("./td[3]")).getText();
            if ("Updated Name".equals(deleteName) && "Updated@email.com".equals(deleteEmail)) {
                deleteStudent = student;
                break;
            }
        }

        if (deleteStudent != null) {
            // Get the "Delete" button for the specific student
            WebElement deleteButton = deleteStudent.findElement(By.id("deleteStudent"));
            deleteButton.click();

            // Simulate confirming the deletion by accepting the confirmation dialog
            driver.switchTo().alert().accept();
            Thread.sleep(SLEEP_DURATION);

            // Check if the success message is displayed
            WebElement successMessage = driver.findElement(By.id("message"));
            assertNotNull(successMessage);
            assertEquals("Student deleted.", successMessage.getText());

            // Fetch the updated list of students
            List<WebElement> updatedStudentList = driver.findElements(By.xpath("//table/tbody/tr"));

            // Assert that the updated list size is smaller by one
            assertNotEquals(studentList.size(), updatedStudentList.size());
            // Check if student is still in the list
            for (WebElement student: updatedStudentList) {
                deleteName = student.findElement(By.xpath("./td[2]")).getText();
                deleteEmail = student.findElement(By.xpath("./td[3]")).getText();
                assertFalse("test".equals(deleteName) && "test@csumb.edu".equals(deleteEmail));
            }
        }
    }

    @AfterEach
    public void cleanup() {
        // Close the WebDriver after each test
        if (driver != null) {
            driver.close();
            driver.quit();
            driver = null;
        }
    }
}

