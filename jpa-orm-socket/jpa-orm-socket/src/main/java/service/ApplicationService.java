package service;

import dto.ApplicationDto;
import entity.Application;

import java.util.List;

public interface ApplicationService {

    ApplicationDto findById(Application.ApplicationId applicationId);
    List<ApplicationDto> loadAll();
    ApplicationDto findBySkillInOpenJobs(String skill);


}
