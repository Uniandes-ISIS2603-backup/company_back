/*
The MIT License (MIT)

Copyright (c) 2015 Los Andes University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package co.edu.uniandes.csw.company.resources;

import co.edu.uniandes.csw.company.api.ICompanyLogic;
import java.util.List;
import javax.inject.Inject;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import co.edu.uniandes.csw.company.api.IDepartmentLogic;
import co.edu.uniandes.csw.company.dtos.CompanyDetailDTO;
import co.edu.uniandes.csw.company.dtos.DepartmentDetailDTO;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.company.persistence.CompanyPersistence;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/companies/{companyId: \\d+}/")
public class DepartmentResource {

    private static final Logger LOGGER = Logger.getLogger(DepartmentResource.class.getName());

    @Inject
    private IDepartmentLogic departmentLogic;

    @Inject
    private ICompanyLogic companyLogic;

    @PathParam("companyId")
    private Long companyId;

    /**
     * Convierte una lista de DepartmentEntity a una lista de
     * DepartmentDetailDTO
     *
     * @param entityList Lista de DepartmentEntity a convertir
     * @return Lista de DepartmentDetailDTO convertida
     *
     */
    private List<DepartmentDetailDTO> listEntity2DTO(List<DepartmentEntity> entityList) {
        List<DepartmentDetailDTO> list = new ArrayList<>();
        for (DepartmentEntity entity : entityList) {
            list.add(new DepartmentDetailDTO(entity));
        }
        return list;
    }

    public void existsCompany(Long companyId) {
        CompanyDetailDTO company = new CompanyDetailDTO(companyLogic.getCompany(companyId));
        if (company == null) {
            throw new WebApplicationException("La compañía no existe", 404);
        }
    }

    public void existsDepartment(Long departmentId) {
        DepartmentDetailDTO department = new DepartmentDetailDTO(departmentLogic.getDepartment(departmentId));
        if (department == null) {
            throw new WebApplicationException("El Departamento no existe", 404);
        }
    }

    /**
     * Obtiene los datos de los Departments de una compañía a partir del ID de
     * la Company
     *
     *
     * @return Lista de DepartmentDetailDTO con los datos del Department
     * consultado
     *
     */
    @GET
    public List<DepartmentDetailDTO> getDepartments() {
        existsCompany(companyId);

        List<DepartmentEntity> departments = departmentLogic.getDepartments(companyId);

        return listEntity2DTO(departments);
    }

    /**
     * Obtiene los datos de una instancia de Department a partir de su ID
     * asociado a un Company
     *
     * @param departmentId Identificador de la instancia a consultar
     * @return Instancia de DepartmentDetailDTO con los datos del Department
     * consultado
     *
     */
    @GET
    @Path("departments/{departmentId: \\d+}")
    public DepartmentDetailDTO getDepartment(@PathParam("departmentId") Long departmentId) {
        existsCompany(companyId);
        LOGGER.log(Level.INFO, "Consultando company con companyId = {0}", companyId);
        DepartmentEntity entity = departmentLogic.getDepartment(departmentId);
        LOGGER.log(Level.INFO, "Consultando company con id = {0}", entity.getCompany().getId());
        if (entity.getCompany() != null && !companyId.equals(entity.getCompany().getId())) {
            throw new WebApplicationException(404);
        }

        return new DepartmentDetailDTO(entity);
    }

    /**
     * Asocia un Department existente a un Company
     *
     * @param dto Objeto de DepartmentDetailDTO con los datos nuevos
     * @return Objeto de DepartmentDetailDTOcon los datos nuevos y su ID.
     *
     */
    @POST
    @Path("departments")
    public DepartmentDetailDTO createDepartment(DepartmentDetailDTO dto) throws BusinessLogicException {
        existsCompany(companyId);
        return new DepartmentDetailDTO(departmentLogic.createDepartment(companyId, dto.toEntity()));
    }

    /**
     * Actualiza la información de una instancia de Department.
     *
     * @param departmentId Identificador de la instancia de Department a
     * modificar
     * @param dto Instancia de DepartmentDetailDTO con los nuevos datos.
     * @return Instancia de DepartmentDetailDTO con los datos actualizados.
     *
     */
    @PUT
    @Path("departments/{departmentId: \\d+}")
    public DepartmentDetailDTO updateDepartment(@PathParam("departmentId") Long departmentId, DepartmentDetailDTO dto) {
        existsCompany(companyId);
        existsDepartment(departmentId);
        DepartmentEntity entity = dto.toEntity();
        entity.setId(departmentId);
        DepartmentEntity oldEntity = departmentLogic.getDepartment(departmentId);
        entity.setEmployees(oldEntity.getEmployees());
        return new DepartmentDetailDTO(departmentLogic.updateDepartment(companyId, entity));
    }

    /**
     * Elimina una instancia de Department de la base de datos.
     *
     * @param departmentId Identificador de la instancia a eliminar.
     *
     */
    @DELETE
    @Path("departments/{departmentId: \\d+}")
    public void deleteDepartment(@PathParam("departmentId") Long departmentId) {

        existsCompany(companyId);
        existsDepartment(departmentId);
        departmentLogic.deleteDepartment(departmentId);
    }

}
