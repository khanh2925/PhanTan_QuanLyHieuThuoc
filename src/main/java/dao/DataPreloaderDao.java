package dao;

/**
 * Dao interface for preloading application data into cache.
 */
public interface DataPreloaderDao {

    /**
     * Preloads all data asynchronously to improve application performance.
     * This method is thread-safe and prevents double loading.
     */
    void preloadAllData();

}
