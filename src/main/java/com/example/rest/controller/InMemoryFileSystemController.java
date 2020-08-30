/**
 * 
 */
package com.example.rest.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import javax.annotation.PreDestroy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 * @author Haseeb
 *
 */
@RestController
@RequestMapping(path = "/file")
public class InMemoryFileSystemController {

	/** The fs. */
	private static FileSystem fs;

	/** The audit file. */
	private static Path auditFile;

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
	 * Gets the employees rate limiter.
	 *
	 * @return the employees rate limiter
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@GetMapping(path = "/write", produces = "application/json")
	public synchronized String getEmployeesRateLimiter() throws IOException {
		System.out.println("File Name :: " + auditFile.getFileName());
		Files.write(auditFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		return new String(Files.readAllBytes(auditFile));
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
	 * Creates the file.
	 */
	public void CreateFile() {
		try {
			UUID gfg1 = UUID.randomUUID();
			Files.copy(auditFile, Paths.get("C:\\Users\\Haseeb\\git\\RestApiSringBoot\\src\\main\\resources\\"
					+ auditFile.getFileName() + "_" + gfg1), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

}