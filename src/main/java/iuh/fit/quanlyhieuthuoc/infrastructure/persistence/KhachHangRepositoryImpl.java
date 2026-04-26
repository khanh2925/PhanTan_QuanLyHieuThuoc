package iuh.fit.quanlyhieuthuoc.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import iuh.fit.quanlyhieuthuoc.core.entity.KhachHang;
import iuh.fit.quanlyhieuthuoc.core.repository.KhachHangRepository;
import iuh.fit.quanlyhieuthuoc.infrastructure.db.JPAUtil;

public class KhachHangRepositoryImpl extends AbstractGenericRepositoryImpl<KhachHang, String> implements KhachHangRepository {

    public KhachHangRepositoryImpl() {
        super(KhachHang.class);
    }

    @Override
    public ArrayList<KhachHang> layTatCaKhachHang() {
        return new ArrayList<>(super.loadAll());
    }

    @Override
    public boolean themKhachHang(KhachHang kh) {
        try {
            super.create(kh);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean capNhatKhachHang(KhachHang kh) {
        try {
            super.update(kh);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaKhachHang(String maKhachHang) {
        try {
            return super.delete(maKhachHang);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ArrayList<KhachHang> timKhachHang(String tuKhoa) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String key = "%" + tuKhoa.trim() + "%";
            List<KhachHang> list = em.createQuery(
                "FROM KhachHang WHERE maKhachHang LIKE :k OR tenKhachHang LIKE :k OR soDienThoai LIKE :k", 
                KhachHang.class)
                .setParameter("k", key)
                .getResultList();
            return new ArrayList<>(list);
        } finally {
            em.close();
        }
    }

    @Override
    public ArrayList<KhachHang> timKhachHangHoatDong() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            List<KhachHang> list = em.createQuery("FROM KhachHang WHERE hoatDong = true", KhachHang.class).getResultList();
            return new ArrayList<>(list);
        } finally {
            em.close();
        }
    }

    @Override
    public KhachHang timKhachHangTheoMa(String maKhachHang) {
        return super.findById(maKhachHang);
    }

    @Override
    public void refreshCache() {
        // Cache layer removed for direct JPA usage (JPA has Level 1 Cache)
    }

    @Override
    public KhachHang timKhachHangTheoSoDienThoai(String soDienThoai) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("FROM KhachHang WHERE soDienThoai = :sdt AND hoatDong = true", KhachHang.class)
                     .setParameter("sdt", soDienThoai.trim())
                     .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public String phatSinhMaKhachHangTiepTheo() {
        // Giữ nguyên logic phát sinh mã hoặc thay bằng JPQL
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String prefix = "KH-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE) + "-";
            String maxMa = em.createQuery("SELECT MAX(k.maKhachHang) FROM KhachHang k WHERE k.maKhachHang LIKE :prefix", String.class)
                             .setParameter("prefix", prefix + "%")
                             .getSingleResult();
            int next = 1;
            if (maxMa != null) {
                String sttStr = maxMa.substring(maxMa.lastIndexOf('-') + 1).trim();
                try {
                    next = Integer.parseInt(sttStr) + 1;
                } catch (Exception ignored) {}
            }
            return prefix + String.format("%04d", next);
        } finally {
            em.close();
        }
    }

    @Override
    public int demKhachHangMoiTheoThang(int thang, int nam) {
        // Tạm thời fix
        return 0;
    }
}
