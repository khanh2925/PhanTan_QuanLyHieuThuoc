import jakarta.persistence.Persistence;

public class CreateDbSchema {
    public static void main(String[] args) {
        Persistence.createEntityManagerFactory("mariadb-pu")
                .createEntityManager();
    }
}
