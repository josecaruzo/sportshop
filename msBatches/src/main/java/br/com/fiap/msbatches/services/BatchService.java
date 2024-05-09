package br.com.fiap.msbatches.services;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
public class BatchService {
	private final JobLauncher jobLauncher;
	private final Job job;

	public BatchService(JobLauncher jobLauncher, Job job) {
		this.jobLauncher = jobLauncher;
		this.job = job;
	}

	public void runProductBatch() {
		JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
		try {
			jobLauncher.run(job, jobParameters);
		} catch (Exception e) {
			throw new RuntimeException("Error running batch job", e);
		}
	}
}
