package dao.iml;

import dao.ApplicationDao;
import entity.Application;
import entity.JobStatus;

import java.util.List;

public class ApplicationDaoImpl extends AbstractGenericDaoImpl<Application, Application.ApplicationId> implements ApplicationDao {

    public ApplicationDaoImpl(){
        super(Application.class);
    }

    @Override
    public List<Object[]> findBySkillInOpenJobs(String skill) {

        String query = "select c, j.title, app.appliedDate " +
                "from Candidate c " +
                "join c.applications app " +
                "join app.job j " +
                "join j.skills jsk " +
                "join c.skills csk " +
                "where j.status = :status " +
                "and jsk.name = :skill " +
                "and csk.name = :skill ";

        return doInTransaction(em -> {
            return em.createQuery(query)
                    .setParameter("status", JobStatus.OPEN)
                    .setParameter("skill", skill)
                    .getResultList();
        });
    }

    public static void main(String[] args) {
        ApplicationDao applicationDao = new ApplicationDaoImpl();

        List<Object[]> list = applicationDao.findBySkillInOpenJobs("java");
        for(Object[] arr : list){
            System.out.println(arr[0]);
            System.out.println(arr[1]);
            System.out.println(arr[2]);
            System.out.println("=====");
        }


//        Application application = applicationDao.findById(Application.ApplicationId.builder().candidate("C2").job("J2").build());
//        System.out.println(application);
//
//        applicationDao.loadAll()
//                .forEach(app -> System.out.println(app));
    }

}
