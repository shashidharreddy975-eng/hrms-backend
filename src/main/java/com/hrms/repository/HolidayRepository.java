
package com.hrms.repository;

import com.hrms.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByAdminId(Long adminId);
}
