package ${packageDir}.service;

import ${packageDir}.domain.${entity};
import ${packageDir}.model.${entity}EditModel;
import ${packageDir}.model.${entity}SearchModel;
import ${packageDir}.model.${entity}FetchModel;
import ${packageDir}.model.Pager;
import ${packageDir}.repository.${entity}Repository;
import ${packageDir}.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author TODO 
 * TODO 
 */
@Service
public class ${entity}Service {

    @Autowired
    ${entity}Repository ${entityVar}Repository;
 

    public Response edit(${entity}EditModel ${entityVar}EditModel) {

        //TODO 新增操作
        return Response.SUCCESS();
    }

    public Response delete(${idType} id) {
        ${entityVar}Repository.delete(id);
        return Response.SUCCESS();
    }

    public Response<${entity}FetchModel> fetch(${idType} id) {
        ${entity} entity = ${entityVar}Repository.findOne(id);
        if (null == entity) {
            return Response.FAILURE("记录不存在");
        }
        ${entity}FetchModel ${entityVar}FetchModel = new ${entity}FetchModel();
        return Response.SUCCESS(${entityVar}FetchModel);
    }


    public Pager<${entity}FetchModel> list(${entity}SearchModel ${entityVar}SearchModel) {

        //TODO  查询列表

       return null;
    }
}
