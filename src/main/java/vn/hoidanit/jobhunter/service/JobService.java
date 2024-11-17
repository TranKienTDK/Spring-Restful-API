package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {

  private final JobRepository jobRepository;

  private final SkillRepository skillRepository;

  public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
    this.jobRepository = jobRepository;
    this.skillRepository = skillRepository;
  }

  public ResCreateJobDTO handleCreateJob(Job job) {
    // check skills
    if (job.getSkills() != null) {
      List<Long> reqSkills = job.getSkills()
          .stream().map(x -> x.getId())
          .collect(Collectors.toList());

      List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
      job.setSkills(dbSkills);
    }

    // create job
    Job currentJob = this.jobRepository.save(job);

    // convert response
    ResCreateJobDTO dto = new ResCreateJobDTO();

    dto.setId(currentJob.getId());
    dto.setName(currentJob.getName());
    dto.setSalary(currentJob.getSalary());
    dto.setQuantity(currentJob.getQuantity());
    dto.setLocation(currentJob.getLocation());
    dto.setLevel(currentJob.getLevel());
    dto.setStartDate(currentJob.getStartDate());
    dto.setEndDate(currentJob.getEndDate());
    dto.setActive(currentJob.isActive());
    dto.setCreatedAt(currentJob.getCreatedAt());
    dto.setCreatedBy(currentJob.getCreatedBy());

    if (currentJob.getSkills() != null) {
      List<String> skills = currentJob.getSkills()
          .stream().map(item -> item.getName())
          .collect(Collectors.toList());
      dto.setSkills(skills);
    }

    return dto;
  }

  public ResUpdateJobDTO handleUpdateJob(Job job) {
    // check skills
    if (job.getSkills() != null) {
      List<Long> reqSkills = job.getSkills()
          .stream().map(x -> x.getId())
          .collect(Collectors.toList());

      List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
      job.setSkills(dbSkills);
    }

    // update job
    Job currentJob = this.jobRepository.save(job);

    // convert response
    ResUpdateJobDTO dto = new ResUpdateJobDTO();

    dto.setId(currentJob.getId());
    dto.setName(currentJob.getName());
    dto.setSalary(currentJob.getSalary());
    dto.setQuantity(currentJob.getQuantity());
    dto.setLocation(currentJob.getLocation());
    dto.setLevel(currentJob.getLevel());
    dto.setStartDate(currentJob.getStartDate());
    dto.setEndDate(currentJob.getEndDate());
    dto.setActive(currentJob.isActive());
    dto.setUpdatedAt(currentJob.getUpdatedAt());
    dto.setUpdatedBy(currentJob.getUpdatedBy());

    if (currentJob.getSkills() != null) {
      List<String> skills = currentJob.getSkills()
          .stream().map(item -> item.getName())
          .collect(Collectors.toList());
      dto.setSkills(skills);
    }

    return dto;
  }

  public Job handleGetJobById(long id) {
    Optional<Job> jobOptional = this.jobRepository.findById(id);
    if (jobOptional.isPresent()) {
      return jobOptional.get();
    }
    return null;
  }

  public void handleDeleteJobById(long id) {
    this.jobRepository.deleteById(id);
  }

}
