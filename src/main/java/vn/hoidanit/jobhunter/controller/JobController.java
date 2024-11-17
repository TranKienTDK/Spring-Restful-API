package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {

  private final JobService jobService;

  public JobController(JobService jobService) {
    this.jobService = jobService;
  }

  @PostMapping("/jobs")
  @ApiMessage("Create new job")
  public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job job) {
    return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.handleCreateJob(job));
  }

  @PutMapping("/jobs")
  @ApiMessage("Update a job")
  public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job job) throws IdInvalidException {
    Job reqJob = this.jobService.handleGetJobById(job.getId());
    if (reqJob == null) {
      throw new IdInvalidException("Job có ID = " + job.getId() + " không tồn tại.");
    }
    return ResponseEntity.ok().body(this.jobService.handleUpdateJob(job));
  }

  @DeleteMapping("/jobs/{id}")
  @ApiMessage("Delete a job")
  public ResponseEntity<Void> deleteJob(@PathVariable("id") long id) throws IdInvalidException {
    Job job = this.jobService.handleGetJobById(id);
    if (job == null) {
      throw new IdInvalidException("Job not found to delete");
    }
    this.jobService.handleDeleteJobById(id);
    return ResponseEntity.ok().body(null);
  }

  @GetMapping("/jobs/{id}")
  @ApiMessage("Fetch a job")
  public ResponseEntity<Job> fetchAJob(@PathVariable("id") long id) throws IdInvalidException {
    Job job = this.jobService.handleGetJobById(id);
    if (job == null) {
      throw new IdInvalidException("Job with ID = " + id + " not found");
    }
    return ResponseEntity.ok().body(job);
  }
}
