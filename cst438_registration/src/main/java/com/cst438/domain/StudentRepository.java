package com.cst438.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository <Student, Integer> {
	
	 Student findByEmail(String email); 
	
	 Student[] findByNameStartsWith(String name);

	 List<Student> findAll();
}
