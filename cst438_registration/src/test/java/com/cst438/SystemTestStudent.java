package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class SystemTestStudent {
    public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\chromedriver-win64\\chromedriver.exe";
    public static final String URL = "http://localhost:3000/admin";
    public static final int SLEEP_DURATION = 1000; // 1 second.

    WebDriver driver;
    
    @BeforeEach
    public void setup() throws Exception{
        System.setProperty(
            "webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(ops);
        driver.get(URL);
        Thread.sleep(SLEEP_DURATION);
    }


    @Test
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
        List<WebElement> studentList = driver.findElements(By.xpath("//tr[@class='students']"));
        
        boolean isNewStudentFound = false;
        for (WebElement student : studentList) {
            String name = student.findElement(By.className("name")).getText();
            String email = student.findElement(By.className("email")).getText();
            if ("John Doe".equals(name) && "john.doe@example.com".equals(email)) {
                isNewStudentFound = true;
                break;
            }
        }
        
        assertTrue(isNewStudentFound);
    }



    @Test
    public void updateStudent() throws Exception {
        // Fetch the list of students
        List<WebElement> studentList = driver.findElements(By.xpath("//tr[@class='students']"));

        // Find the specific student to edit based on their characteristics (e.g., name and email)
        WebElement studentToEdit = null;

        for (WebElement student : studentList) {
            String name = student.findElement(By.className("name")).getText();
            String email = student.findElement(By.className("email")).getText();

            // Check if this is the student you want to edit
            if ("John Doe".equals(name) && "john.doe@example.com".equals(email)) {
                studentToEdit = student;
                break;
            }
        }

        // If the student to edit is found, proceed with the update
        if (studentToEdit != null) {
            // Click the "Edit" button for the specific student
            WebElement editButton = studentToEdit.findElement(By.id("editStudent"));
            editButton.click();
            Thread.sleep(SLEEP_DURATION);

            // Locate input fields for updating student information
            WebElement nameInput = driver.findElement(By.name("name"));
            WebElement emailInput = driver.findElement(By.name("email"));
            WebElement statusInput = driver.findElement(By.name("status"));
            WebElement statusCodeInput = driver.findElement(By.name("statusCode"));
            WebElement submitButton = driver.findElement(By.name("submit"));

            // Update student information
            nameInput.clear();
            nameInput.sendKeys("Updated Name");
            emailInput.clear();
            emailInput.sendKeys("updated.email@example.com");
            statusCodeInput.clear();
            statusCodeInput.sendKeys("456");
            statusInput.clear();
            statusInput.sendKeys("Inactive");

            // Submit the updated information
            submitButton.click();
            Thread.sleep(SLEEP_DURATION);

            // Check if the success message is displayed
            WebElement successMessage = driver.findElement(By.id("message"));
            assertNotNull(successMessage);
            assertEquals("Student edited.", successMessage.getText());
        }
    }



    @Test
    public void deleteStudent() throws Exception {
        // Find the specific student to delete based on their characteristics (e.g., name and email)
        WebElement studentToDelete = null;
        
        List<WebElement> studentList = driver.findElements(By.xpath("//tr[@class='students']"));
        
        for (WebElement student : studentList) {
            String name = student.findElement(By.className("Name")).getText();
            String email = student.findElement(By.className("Email")).getText();

            // Check if this is the student you want to delete
            if ("test".equals(name) && "test@csumb.edu".equals(email)) {
                studentToDelete = student;
                break;
            }
        }

        // If the student was found, proceed to delete
        if (studentToDelete != null) {
            // Get the "Delete" button for the specific student
            WebElement deleteButton = studentToDelete.findElement(By.id("deleteStudent"));
            deleteButton.click();

            // Simulate confirming the deletion by accepting the confirmation dialog
            driver.switchTo().alert().accept();
            Thread.sleep(SLEEP_DURATION);

            // Check if the success message is displayed
            WebElement successMessage = driver.findElement(By.id("message"));
            assertNotNull(successMessage);
            assertEquals("Student deleted.", successMessage.getText());

            // Fetch the updated list of students
            List<WebElement> updatedStudentList = driver.findElements(By.xpath("//tr[@class='students']"));

            // Assert that the updated list size is smaller by one
            assertEquals(studentList.size() - 1, updatedStudentList.size());
        }
    }


    @AfterEach
    public void cleanup() {
        if (driver != null) {
            driver.close();
            driver.quit();
            driver = null;
        }
    }
}

