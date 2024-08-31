package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  // CREATE COMPANY
  @PostMapping("/companies")
  public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company resCompany) {
    Company newCompany = this.companyService.handleCreateCompany(resCompany);
    return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
  }

  // GET ALL COMPANIES
  @GetMapping("/companies")
  public ResponseEntity<ResultPaginationDTO> getAllCompany(
      @Filter Specification<Company> spec, Pageable pageable) {

    return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleGetAllCompanies(spec, pageable));
  }

  // GET A COMPANY
  @GetMapping("/companies/{companyId}")
  public ResponseEntity<Company> getACompany(@PathVariable("companyId") long companyId) {
    Company resCompany = this.companyService.handleGetACompany(companyId);
    return ResponseEntity.status(HttpStatus.OK).body(resCompany);
  }

  // UPDATE COMPANY
  @PutMapping("/companies")
  public ResponseEntity<?> updateCompany(@RequestBody Company reqCompany) {
    Company curCompany = this.companyService.handleUpdateCompany(reqCompany);
    if (curCompany == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID công ty không tồn tại");
    }
    return ResponseEntity.status(HttpStatus.OK).body(curCompany);
  }

  // DELETE COMPANY
  @DeleteMapping("/companies/{companyId}")
  public ResponseEntity<Void> deleteCompany(@PathVariable("companyId") long id) throws IdInvalidException {
    this.companyService.handleDeleteCompany(id);
    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

}
