package com.cst438.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.JwtService;


@RestController
public class LoginController {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private StudentRepository repository;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<?> getToken(@RequestBody StudentDTO student) throws Exception {
        // Find the student by email using the repository
        Student foundStudent = repository.findByEmail(student.email());

        if (foundStudent == null) {
            // Student with the provided email doesn't exist
            throw new Exception("Student not found");
        }

        // Now, you have the foundStudent and can proceed with authentication
        UsernamePasswordAuthenticationToken creds = new UsernamePasswordAuthenticationToken(
            foundStudent.getEmail(),
            student.password()
        );

        Authentication auth;

        try {
            auth = authenticationManager.authenticate(creds);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Student not found", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        }

        // Get the user's role
        String userRole = foundStudent.getRole();

        // Generate token with the user's role included in the payload
        String jwts = jwtService.getToken(auth.getName(), userRole);

        // Build the response with the generated token
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwts)
            .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Authorization")
            .build();
    }
}


