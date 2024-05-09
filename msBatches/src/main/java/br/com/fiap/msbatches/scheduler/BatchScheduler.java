package br.com.fiap.msbatches.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job job;

	public BatchScheduler(JobLauncher jobLauncher, Job job) {
		this.jobLauncher = jobLauncher;
		this.job = job;
	}

	@Scheduled(fixedDelay = 1, timeUnit = TimeUnit.HOURS)
	public void executeBatch() throws Exception {
		JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
		jobLauncher.run(job, jobParameters);
	}
}
