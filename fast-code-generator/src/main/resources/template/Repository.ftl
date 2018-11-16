package ${packageDir}.repository;

import ${packageDir}.domain.${entity};
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ${entity}Repository extends CrudRepository<${entity}, ${idType}> {

}
