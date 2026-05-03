package mapper;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.ApplicationDto;
import entity.Application;
import entity.Candidate;
import entity.Job;

public class Mapper {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <S, T> T map(S source, Class<T> target) {
        return objectMapper.convertValue(source, target);
    }

    public static Application map(ApplicationDto applicationDto) {
        Application application = map(applicationDto, Application.class);
        application.setJob(Job.builder().title(applicationDto.getTitle()).build());
        application.setCandidate(Candidate.builder()
                .id(applicationDto.getCandidateId())
                .name(applicationDto.getCandidateName())
                .build());
        return application;
    }

    public static ApplicationDto map(Application application){
        ApplicationDto applicationDto = map(application, ApplicationDto.class);
        applicationDto.setTitle(application.getJob().getTitle());
        applicationDto.setCandidateId(application.getCandidate().getId());
        applicationDto.setCandidateName(application.getCandidate().getName());
        return applicationDto;
    }
}
