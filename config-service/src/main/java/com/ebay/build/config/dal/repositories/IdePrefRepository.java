/**
 * 
 */
package com.ebay.build.config.dal.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ebay.build.config.dal.entities.IdePref;

/**
 * @author bishen
 * 
 */
@RepositoryRestResource(collectionResourceRel = "idePref", path = "idePref")
public interface IdePrefRepository extends MongoRepository<IdePref, String> {

	List<IdePref> findByIdeNameAndIdeVersionAllIgnoreCase(
			@Param("ideName") String ideName,
			@Param("ideVersion") String ideVersion);

}
