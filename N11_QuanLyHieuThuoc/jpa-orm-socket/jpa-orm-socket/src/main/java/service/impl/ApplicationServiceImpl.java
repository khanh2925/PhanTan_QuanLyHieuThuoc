package service.impl;

import dao.ApplicationDao;
import dao.iml.ApplicationDaoImpl;
import dto.ApplicationDto;
import entity.Application;
import mapper.Mapper;
import service.ApplicationService;

import java.util.List;

public class ApplicationServiceImpl implements ApplicationService {

    private ApplicationDao applicationDao;

    public ApplicationServiceImpl(){
        applicationDao = new ApplicationDaoImpl();
    }

    @Override
    public ApplicationDto findById(Application.ApplicationId applicationId) {
        Application application = applicationDao.findById(applicationId);
        return Mapper.map(application);
    }

    @Override
    public List<ApplicationDto> loadAll() {
        List<Application> applications = applicationDao.loadAll();
        return applications
                .stream()
                .map(application -> Mapper.map(application))
                .toList();
    }

    @Override
    public ApplicationDto findBySkillInOpenJobs(String skill) {
        return null;
    }


    public static void main(String[] args) {
        ApplicationService applicationService = new ApplicationServiceImpl();
//        ApplicationDto applicationDto = applicationService.findById(Application.ApplicationId.builder().candidate("C2").job("J2").build());
//        System.out.println(applicationDto);
        applicationService
                .loadAll()
                .forEach(a -> System.out.println(a));
    }
}
