/**
 * 
 */
package com.vfi.demo.rest.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vfi.demo.rest.entity.Tag;

/**
 * @author athi
 *
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

}
