package com.example.rest.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.rest.dao.EmployeeDAO;
import com.example.rest.model.Employee;
import com.example.rest.model.Employees;
import com.google.common.util.concurrent.RateLimiter;

@RestController
@RequestMapping(path = "/employees")
public class EmployeeController 
{
	
	final RateLimiter rateLimiter = RateLimiter.create(0.25);
	
    @Autowired
    private EmployeeDAO employeeDao;
    
    @GetMapping(path="/ratelimiter", produces = "application/json")
    public Employees getEmployeesRateLimiter() 
    {
    	rateLimiter.acquire();
        return employeeDao.getAllEmployees();
    }
    
    @GetMapping(path="/weddini", produces = "application/json")
    public Employees getEmployeesWeddini() 
    {
    	rateLimiter.acquire();
        return employeeDao.getAllEmployees();
    }
    
    @PostMapping(path= "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> addEmployee(
                        @RequestHeader(name = "X-COM-PERSIST", required = true) String headerPersist,
                        @RequestHeader(name = "X-COM-LOCATION", required = false, defaultValue = "ASIA") String headerLocation,
                        @RequestBody Employee employee) 
                 throws Exception 
    {       
        //Generate resource id
        Integer id = employeeDao.getAllEmployees().getEmployeeList().size() + 1;
        employee.setId(id);
        
        //add resource
        employeeDao.addEmployee(employee);
        
        //Create resource location
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                                    .path("/{id}")
                                    .buildAndExpand(employee.getId())
                                    .toUri();
        
        //Send location in response
        return ResponseEntity.created(location).build();
    }
}
