package com.example.rest.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.UUID;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.util.concurrent.RateLimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

// TODO: Auto-generated Javadoc
/**
 * The Class EmployeeController.
 */
@RestController
@RequestMapping(path = "/employees")
public class EmployeeController {

	/** The bucket. */
	private final Bucket bucket;

	/** The fs. */
	private static FileSystem fs;

	/** The audit file. */
	private static Path auditFile;

	/** The rate limiter. */
	private final RateLimiter rateLimiter = RateLimiter.create(100);

	static {
		System.out.println("Executing static block.");
		fs = Jimfs.newFileSystem(Configuration.unix());
		Path foo = fs.getPath("/foo");
		try {
			Files.createDirectory(foo);
			auditFile = foo.resolve("hello.txt");
			Files.write(auditFile, ImmutableList.of("Audit START"), StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * On exit.
	 */
	@PreDestroy
	public void onExit() {
		System.out.println("###STOPing###");
		CreateFile();
		System.out.println("###STOP FROM THE LIFECYCLE###");
	}

	/**
	 * Instantiates a new employee controller.
	 */
	public EmployeeController() {
		long capacity = 10;
		Bandwidth limit = Bandwidth.simple(capacity, Duration.ofMinutes(1));
		this.bucket = Bucket4j.builder().addLimit(limit).build();
	}

	/** The employee dao. */
	@Autowired
	private EmployeeDAO employeeDao;

	/**
	 * Gets the employees rate limiter.
	 *
	 * @return the employees rate limiter
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GetMapping(path = "/ratelimiter", produces = "application/json")
	public synchronized Employees getEmployeesRateLimiter() throws IOException {
		System.out.println("File Name :: " + auditFile.getFileName());
		Files.write(auditFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		System.out.println(new String(Files.readAllBytes(auditFile)));
		rateLimiter.acquire();
		return employeeDao.getAllEmployees();
	}
	
	/**
	 * Exit.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GetMapping(path = "/exit", produces = "application/json")
	public synchronized void exit() throws IOException {
		System.exit(0);
	}

	/**
	 * Gets the employees weddini.
	 *
	 * @param response the response
	 * @return the employees weddini
	 */
	@GetMapping(path = "/weddini", produces = "application/json")
	public ResponseEntity<Employees> getEmployeesWeddini(HttpServletResponse response) {
		if (bucket.tryConsume(1)) {
			Employees body = employeeDao.getAllEmployees();
			return ResponseEntity.ok().body(body);
		}
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
	}

	/**
	 * Gets the employees bucket 4 j.
	 *
	 * @return the employees bucket 4 j
	 */
	@GetMapping(path = "/bucket4j", produces = "application/json")
	public Employees getEmployeesBucket4j() {
		return employeeDao.getAllEmployees();
	}

	/**
	 * Gets the employees bucket 4 j.
	 *
	 * @return the employees bucket 4 j
	 */
	@GetMapping(path = "/bucket4j_v2", produces = "application/json")
	public Employees getEmployeesBucket4j_v2() {
		return employeeDao.getAllEmployees();
	}

	/**
	 * Adds the employee.
	 *
	 * @param headerPersist  the header persist
	 * @param headerLocation the header location
	 * @param employee       the employee
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@PostMapping(path = "/", consumes = "application/json", produces = "application/json")
	public ResponseEntity<Object> addEmployee(
			@RequestHeader(name = "X-COM-PERSIST", required = true) String headerPersist,
			@RequestHeader(name = "X-COM-LOCATION", required = false, defaultValue = "ASIA") String headerLocation,
			@RequestBody Employee employee) throws Exception {
		// Generate resource id
		Integer id = employeeDao.getAllEmployees().getEmployeeList().size() + 1;
		employee.setId(id);

		// add resource
		employeeDao.addEmployee(employee);

		// Create resource location
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(employee.getId())
				.toUri();

		// Send location in response
		return ResponseEntity.created(location).build();
	}

	/**
	 * Creates the file.
	 */
	public void CreateFile() {
		try {
			UUID gfg1 = UUID.randomUUID();
			Files.copy(
					auditFile,
	                Paths.get("C:\\Users\\Haseeb\\git\\RestApiSringBoot\\src\\main\\resources\\" + auditFile.getFileName()+"_"+gfg1),
	                StandardCopyOption.REPLACE_EXISTING
	        );
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}
