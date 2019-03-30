/**
 * 
 */
package com.vfi.demo.rest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vfi.demo.rest.entity.Num;

/**
 * @author athi
 *
 */
@Repository
public interface NumRepository extends JpaRepository<Num, Long> {

}
