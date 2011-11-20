package com.tkraus.greenball.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JenkinsResult {
	private Map<String, BallEnum> jobs = new HashMap<String, BallEnum>();
	private JenkinsFailEnum status;

	public JenkinsFailEnum getStatus() {
		return status;
	}

	public void setStatus(JenkinsFailEnum status) {
		this.status = status;
	}

	public void addJob(String jobName, BallEnum jobStatus) {
		jobs.put(jobName, jobStatus);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		for (String jobName : jobs.keySet()) {
			buf.append("Job: ").append(jobName).append(": ")
					.append(jobs.get(jobName)).append("\n");
		}
		return buf.toString();
	}

	public Set<String> getJobs() {
		return jobs.keySet();
	}

	public BallEnum getJobStatus(String job) {
		return jobs.get(job);
	}
}
