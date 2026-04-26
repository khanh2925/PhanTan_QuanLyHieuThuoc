package iuh.fit.quanlyhieuthuoc.core.repository;

/**
 * Repository interface for preloading application data into cache.
 */
public interface DataPreloaderRepository {

    /**
     * Preloads all data asynchronously to improve application performance.
     * This method is thread-safe and prevents double loading.
     */
    void preloadAllData();

}
