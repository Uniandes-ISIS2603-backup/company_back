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
import co.edu.uniandes.csw.company.dtos.EmployeeDetailDTO;
import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import java.util.ArrayList;

@Path("/departments/{departmentId: \\d+}/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DepartmentEmployeeResource {

    @Inject
    private IDepartmentLogic departmentLogic;

    /**
     * Convierte una lista de EmployeeEntity a una lista de EmployeeDetailDTO.
     *
     * @param entityList Lista de EmployeeEntity a convertir.
     * @return Lista de EmployeeDetailDTO convertida.
     *
     */
    private List<EmployeeDetailDTO> employeesListEntity2DTO(List<EmployeeEntity> entityList) {
        List<EmployeeDetailDTO> list = new ArrayList<>();
        for (EmployeeEntity entity : entityList) {
            list.add(new EmployeeDetailDTO(entity));
        }
        return list;
    }

    /**
     * Convierte una lista de EmployeeDetailDTO a una lista de EmployeeEntity.
     *
     * @param dtos Lista de EmployeeDetailDTO a convertir.
     * @return Lista de EmployeeEntity convertida.
     *
     */
    private List<EmployeeEntity> employeesListDTO2Entity(List<EmployeeDetailDTO> dtos) {
        List<EmployeeEntity> list = new ArrayList<>();
        for (EmployeeDetailDTO dto : dtos) {
            list.add(dto.toEntity());
        }
        return list;
    }

    /**
     * Obtiene una colección de instancias de EmployeeDetailDTO asociadas a una
     * instancia de Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @return Colección de instancias de EmployeeDetailDTO asociadas a la
     * instancia de Department
     *
     */
    @GET
    @Path("employees")
    public List<EmployeeDetailDTO> listEmployees(@PathParam("departmentId") Long departmentId) {
        return employeesListEntity2DTO(departmentLogic.listEmployees(departmentId));
    }

    /**
     * Obtiene una instancia de Employee asociada a una instancia de Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @param employeeId Identificador de la instancia de Employee
     * @return la instancia de EmployeeDetailDTO 
     *
     */
    @GET
    @Path("employees/{employeeId: \\d+}")
    public EmployeeDetailDTO getEmployees(@PathParam("departmentId") Long departmentId, @PathParam("employeeId") Long employeeId) {
        return new EmployeeDetailDTO(departmentLogic.getEmployee(departmentId, employeeId));
    }

    /**
     * Asocia un Employee existente a un Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @param employeeId Identificador de la instancia de Employee
     * @return Instancia de EmployeeDetailDTO que fue asociada a Department
     *
     */
    @POST
    @Path("employees/{employeeId: \\d+}")
    public EmployeeDetailDTO addEmployees(@PathParam("departmentId") Long departmentId, @PathParam("employeeId") Long employeeId) {
        return new EmployeeDetailDTO(departmentLogic.addEmployee(departmentId, employeeId));
    }

    /**
     * Remplaza las instancias de Employee asociadas a una instancia de
     * Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @param employees Colección de instancias de EmployeeDTO a asociar a
     * instancia de Department
     * @return Nueva colección de EmployeeDTO asociada a la instancia de
     * Department
     *
     */
    @PUT
    @Path("employees")
    public List<EmployeeDetailDTO> replaceEmployees(@PathParam("departmentId") Long departmentId, List<EmployeeDetailDTO> employees) {
        return employeesListEntity2DTO(departmentLogic.replaceEmployees(departmentId, employeesListDTO2Entity(employees)));
    }

    /**
     * Desasocia un Employee existente de un Department existente
     *
     * @param departmentId Identificador de la instancia de Department
     * @param employeeId Identificador de la instancia de Employee
     *
     */
    @DELETE
    @Path("employees/{employeeId: \\d+}")
    public void removeEmployees(@PathParam("departmentId") Long departmentId, @PathParam("employeeId") Long employeeId) {
        departmentLogic.removeEmployee(departmentId, employeeId);
    }
}
