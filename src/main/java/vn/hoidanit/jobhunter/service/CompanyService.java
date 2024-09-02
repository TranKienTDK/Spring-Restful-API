package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

  private final CompanyRepository companyRepository;

  public CompanyService(CompanyRepository companyRepository) {
    this.companyRepository = companyRepository;
  }

  public Company handleCreateCompany(Company company) {
    return this.companyRepository.save(company);
  }

  public void handleDeleteCompany(Long id) {
    this.companyRepository.deleteById(id);
  }

  public Company handleGetACompany(Long id) {
    Optional<Company> company = this.companyRepository.findById(id);
    if (company.isPresent()) {
      return company.get();
    }
    return null;
  }

  public ResultPaginationDTO handleGetAllCompanies(Specification<Company> spec, Pageable pageable) {
    Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
    ResultPaginationDTO rs = new ResultPaginationDTO();
    ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

    mt.setPage(pageable.getPageNumber() + 1);
    mt.setPageSize(pageable.getPageSize());

    mt.setPages(pageCompany.getTotalPages());
    mt.setTotal(pageCompany.getTotalElements());

    rs.setMeta(mt);
    rs.setResult(pageCompany.getContent());

    return rs;
  }

  public Company handleUpdateCompany(Company resCompany) {
    Optional<Company> curCompany = this.companyRepository.findById(resCompany.getId());
    if (curCompany.isPresent()) {
      curCompany.get().setName(resCompany.getName());
      curCompany.get().setDescription(resCompany.getDescription());
      curCompany.get().setAddress(resCompany.getAddress());
      curCompany.get().setLogo(resCompany.getLogo());

      this.companyRepository.save(curCompany.get());
      return curCompany.get();
    }
    return null;
  }

}
