package com.kuhnash.jeremy.poc.rscli.cliworker;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.annotation.PostConstruct;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import expectj.ExpectJ;
import expectj.ExpectJException;
import expectj.Spawn;
import expectj.TimeoutException;

/**
 * Encapsulates any command on the system as a minimalist bean.
 * @author jkuhnash@confluent.io
 *
 */
@Component
public class CCloudCliCommand {
	// set this in applicaton.properties
	@Value("${CLI_COMMAND}")
	private String cli_command;
	
	@Value("${USERNAME}")
	private String username;
	
	@Value("${PASSWORD}")
	private String password;

	private String stderr;
	public String getStderr() {
		return stderr;
	}

	String full_command;
	
	public void setStderr(String stderr) {
		this.stderr = stderr;
	}
	
	private String stdout;
	public String getStdout() {
		return stdout;
	}

	public void setStdout(String stdout) {
		this.stdout = stdout;
	}
	
	private int retval;
	public int getRetval() {
		return retval;
	}

	public void setRetval(int retval) {
		this.retval = retval;
	}
	
	private String args;
	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}
	
	public synchronized void execute() {
		// assumes being run from top level of container
		Project p = new Project();
		p.setBaseDir(new File("/"));
		p.setBasedir("/");
		ExecTask etask = new ExecTask();
		etask.setDir(new File("/"));
		for (int i = 0; i < args.split(" ").length; i++ ) {
			etask.createArg().setValue(args.split(" ")[i]);
		}
		String command = cli_command;
		etask.setExecutable(command);
		etask.execute();
	}

	/**
	 * true after init() has completed. 
	 * 
	 * Used from the Controller to offer a 'Readiness' / 'Liveness' indicator of when the container can be put in service.
	 */
	public static boolean isReady = false;

	/**
	 * Perform any setup by extending this method.
	 * 
	 * In Spring, instances are a Singleton by default, so this execs just once, specified by @PostConstruct
	 */
	@PostConstruct
	public void init() throws Exception {
		// 1 sec timeout, then throws Exception.
		ExpectJ expectinator = new ExpectJ(3);
		String login_command = cli_command + " login --save";
		Spawn shell = expectinator.spawn(login_command);;
		try {
			System.out.print("Gunna send username:" + username);
			shell.expect("mail:");
			shell.send(username + "\r\n");
			shell.expect("assword:");
			shell.send(password + "\r\n");
			shell.expectClose(3);
			int exitVal = shell.getExitValue();
			if (exitVal != 0)
				throw new Exception("[[ Process did not exit within timeout or returned non-zero result ]]: '" + full_command + '"');
			isReady = true;
		}
		catch (ExpectJException EJE) {
			String msg = getStackTrace(EJE) + " -- command: '" + login_command + "'  -- stdout: '" + shell.getCurrentStandardOutContents() + "'" + "'  -- stderr: '" + shell.getCurrentStandardErrContents() + "'";
			System.out.println(msg);
			throw new Exception(msg, EJE);
		}
		catch (IOException IOE) {
			String msg = getStackTrace(IOE) + " -- command: '" + login_command + "'  -- stdout: '" + shell.getCurrentStandardOutContents() + "'  -- stderr: '" + shell.getCurrentStandardErrContents() + "'";
			System.out.println(msg);
			throw new Exception(msg, IOE);
		}    
		catch (TimeoutException TOE) {
			String msg = getStackTrace(TOE) + " -- command: '" + login_command + "'  -- stdout: '" + shell.getCurrentStandardOutContents() + "'  -- stderr: '" + shell.getCurrentStandardErrContents() + "'";
			System.out.println(msg);
			throw new Exception(msg, TOE);
		}
		catch (Exception E) {
			throw E;
		}
	}

    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
      }
}
