package service.impl;

import dao.CompanyDao;
import dao.iml.CompanyDaoImpl;
import dto.CompanyDto;
import entity.Company;
import mapper.Mapper;
import service.CompanyService;

import java.util.List;

public class CompanyServiceImpl implements CompanyService {

    private CompanyDao companyDao;

    public CompanyServiceImpl(){
        companyDao = new CompanyDaoImpl();
    }

    @Override
    public CompanyDto create(CompanyDto companyDto) {
        return null;
    }

    @Override
    public CompanyDto update(CompanyDto companyDto) {
        return null;
    }

    @Override
    public boolean delete(String companyId) {
        return companyDao.delete(companyId);
    }

    @Override
    public CompanyDto findById(String companyId) {
        Company company = companyDao.findById(companyId);
        return Mapper.map(company, CompanyDto.class);
    }

    @Override
    public List<CompanyDto> loadAll() {
        List<Company> companies = companyDao.loadAll();
        return companies
                .stream()
                .map(company -> Mapper.map(company, CompanyDto.class))
                .toList();
    }

    public static void main(String[] args) {
        CompanyService companyService = new CompanyServiceImpl();
        companyService
                .loadAll()
                .forEach(c -> System.out.println(c));
    }
}
