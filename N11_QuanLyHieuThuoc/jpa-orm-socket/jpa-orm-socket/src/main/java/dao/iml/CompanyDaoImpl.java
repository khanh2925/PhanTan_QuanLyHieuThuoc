package dao.iml;

import dao.CompanyDao;
import entity.Company;

public class CompanyDaoImpl extends AbstractGenericDaoImpl<Company, String> implements CompanyDao {

    public CompanyDaoImpl() {
        super(Company.class);
    }

    public static void main(String[] args) {
        CompanyDao companyDao = new CompanyDaoImpl();
        Company company = companyDao.findById("CP3");
        System.out.println(company);

        companyDao.loadAll()
                .forEach(c -> System.out.println(c));
    }
}
