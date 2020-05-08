package com.kuhnash.jeremy.poc.rscli.cliworker;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CCloudCliCommandTest {
	
	@Autowired
	CCloudCliCommand command;
	
	@Test
	public void testInit() {
		command.execute();
	}
	
	/**
	 * "Load Test" :) -- Please use jmeter to verify (but should be ok as is synchronized)
	 */
	@Test
	public void testMultiCommands() {
		command.setArgs("environment list");
		command.execute();
		command.setArgs("prompt");
		command.execute();
		command.setArgs("version");
		command.execute();
		command.setArgs("environment list");
		command.execute();
		command.setArgs("prompt");
		command.execute();
		command.setArgs("version");
		command.execute();
		command.setArgs("environment list");
		command.execute();
		command.setArgs("prompt");
		command.execute();
		command.setArgs("version");		
		command.execute();
	}
}
