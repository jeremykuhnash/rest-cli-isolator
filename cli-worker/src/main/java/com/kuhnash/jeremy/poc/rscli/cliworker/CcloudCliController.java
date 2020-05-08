package com.kuhnash.jeremy.poc.rscli.cliworker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CcloudCliController {
	
	@Autowired
	CCloudCliCommand clco;
	
	@PostMapping("/ccloud")
	public CCloudCliCommand cliCommand (@RequestParam(value = "args", defaultValue = "environment list") String args) {
		clco.setArgs(args);
		clco.execute();
		return clco;
	}

	/**
	 * Implementation based on the Kubernetes "Liveness" feature. 
	 * https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/#define-a-liveness-command
	 * 
	 * @return
	 */
	@GetMapping("/healthz")
	public ResponseEntity<HttpStatus> readinessCheck () {
		ResponseEntity<HttpStatus> re;
		if (CCloudCliCommand.isReady) {
			re = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		else {
			re = new ResponseEntity<>(HttpStatus.OK);
		}
		return re;
	}
}
