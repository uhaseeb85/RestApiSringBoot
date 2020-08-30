/**
 * 
 */
package com.example.rest.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 * @author Haseeb
 *
 */
@Component
public class InMemoryFileSystem {
	
	public static FileSystem fs;
	
	public static Path auditFile;

	public InMemoryFileSystem() {
		fs = Jimfs.newFileSystem(Configuration.unix());
		Path foo = fs.getPath("/foo");
		try {
			Files.createDirectory(foo);
			auditFile = foo.resolve("hello.txt");
			Files.write(auditFile, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
