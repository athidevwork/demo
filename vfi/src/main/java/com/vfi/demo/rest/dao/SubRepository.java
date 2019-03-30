/**
 * 
 */
package com.vfi.demo.rest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vfi.demo.rest.entity.Sub;

/**
 * @author athi
 *
 */
@Repository
public interface SubRepository extends JpaRepository<Sub, Long> {
	Sub findByAdsh(String adsh);
}
