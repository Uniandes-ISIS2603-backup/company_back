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
package co.edu.uniandes.csw.company.ejbs;

import co.edu.uniandes.csw.company.api.IDepartmentLogic;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.persistence.DepartmentPersistence;
import co.edu.uniandes.csw.company.api.ICompanyLogic;
import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import co.edu.uniandes.csw.company.api.IEmployeeLogic;
import co.edu.uniandes.csw.company.exceptions.BusinessLogicException;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

@Stateless
public class DepartmentLogic implements IDepartmentLogic {

    @Inject
    private DepartmentPersistence persistence;

    @Inject
    private ICompanyLogic companyLogic;

    @Inject
    private IEmployeeLogic employeeLogic;

    /**
     * Obtiene la lista de los registros de Department que pertenecen a un
     * Company.
     *
     * @param companyid id del Company el cual es padre de los Departments.
     * @return Colección de objetos de DepartmentEntity.
     *
     */
    @Override
    public List<DepartmentEntity> getDepartments(Long companyid) {
        CompanyEntity company = companyLogic.getCompany(companyid);
        return company.getDepartments();
    }

    /**
     * Obtiene los datos de una instancia de Department a partir de su ID.
     *
     * @param departmentid Identificador del Department a consultar
     * @return Instancia de DepartmentEntity con los datos del Department
     * consultado.
     *
     */
    @Override
    public DepartmentEntity getDepartment(Long departmentId) {
        try {
            return persistence.find(departmentId);
        } catch (NoResultException e) {
            throw new IllegalArgumentException("El Department no existe");
        }
    }

    /**
     * Obtiene los datos de una instancia de Department a partir del
     * identificador de la compañía y el nombre del departmaneto.
     *
     * @param companyId identificador de la compañía sobre la que se quiere
     * buscar un departamento
     * @param departmentName Nombre del Department a consultar dentro de la
     * compañía dada por companyId
     * @return Instancia de DepartmentEntity con los datos del Department
     * consultado o null sino existe
     *
     */
    @Override
    public DepartmentEntity getDepartmentByName(Long companyId, String departmentName) {
        return persistence.findByName(companyId, departmentName);
    }

    /**
     * Se encarga de crear un Department en la base de datos.
     *
     * @param entity Objeto de DepartmentEntity con los datos nuevos
     * @param companyid id del Company el cual sera padre del nuevo Department.
     * @return Objeto de DepartmentEntity con los datos nuevos y su ID.
     *
     */
    @Override
    public DepartmentEntity createDepartment(Long companyid, DepartmentEntity entity) throws BusinessLogicException {
        DepartmentEntity alreadyExist = getDepartmentByName(companyid, entity.getName());
        if (alreadyExist != null) {
            throw new BusinessLogicException("Ya existe un departamento con ese nombre en la compañía ");
        } else {
            CompanyEntity  company = companyLogic.getCompany(companyid);
            entity.setCompany(company);

            entity = persistence.create(entity);
        }
        return entity;
    }

    /**
     * Actualiza la información de una instancia de Department.
     *
     * @param entity Instancia de DepartmentEntity con los nuevos datos.
     * @param companyid id del Company el cual sera padre del Department
     * actualizado.
     * @return Instancia de DepartmentEntity con los datos actualizados.
     *
     */
    @Override
    public DepartmentEntity updateDepartment(Long companyid, DepartmentEntity entity) {
        CompanyEntity company = companyLogic.getCompany(companyid);
        entity.setCompany(company);
        return persistence.update(entity);
    }

    /**
     * Elimina una instancia de Department de la base de datos.
     *
     * @param id Identificador de la instancia a eliminar.
     * @param companyid id del Company el cual es padre del Department.
     *
     */
    @Override
    public void deleteDepartment(Long id) {
        DepartmentEntity old = getDepartment(id);
        persistence.delete(old.getId());
    }

    /**
     * Obtiene una colección de instancias de EmployeeEntity asociadas a una
     * instancia de Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @return Colección de instancias de EmployeeEntity asociadas a la
     * instancia de Department
     *
     */
    @Override
    public List<EmployeeEntity> listEmployees(Long departmentId) {
        return persistence.find(departmentId).getEmployees();
    }

    /**
     * Obtiene una instancia de EmployeeEntity asociada a una instancia de
     * Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @param employeesId Identificador de la instancia de Employee
     *
     */
    @Override
    public EmployeeEntity getEmployee(Long departmentId, Long employeesId) {
        List<EmployeeEntity> list = persistence.find(departmentId).getEmployees();
        EmployeeEntity employeesEntity = new EmployeeEntity();
        employeesEntity.setId(employeesId);
        int index = list.indexOf(employeesEntity);
        if (index >= 0) {
            return list.get(index);
        }
        return null;
    }

    /**
     * Asocia un Employee existente a un Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @param employeesId Identificador de la instancia de Employee
     * @return Instancia de EmployeeEntity que fue asociada a Department
     *
     */
    @Override
    public EmployeeEntity addEmployee(Long departmentId, Long employeesId) {
        DepartmentEntity departmentEntity = persistence.find(departmentId);
        EmployeeEntity employeesEntity = employeeLogic.getEmployee(employeesId);
        employeesEntity.setDepartment(departmentEntity);
        return employeesEntity;
    }

    /**
     * Remplaza las instancias de Employee asociadas a una instancia de
     * Department
     *
     * @param departmentId Identificador de la instancia de Department
     * @param list Colección de instancias de EmployeeEntity a asociar a
     * instancia de Department
     * @return Nueva colección de EmployeeEntity asociada a la instancia de
     * Department
     *
     */
    @Override
    public List<EmployeeEntity> replaceEmployees(Long departmentId, List<EmployeeEntity> list) {
        DepartmentEntity departmentEntity = persistence.find(departmentId);
        List<EmployeeEntity> employeeList = employeeLogic.getEmployees();
        for (EmployeeEntity employee : employeeList) {
            if (list.contains(employee)) {
                employee.setDepartment(departmentEntity);
            } else if (employee.getDepartment() != null && employee.getDepartment().equals(departmentEntity)) {
                employee.setDepartment(null);
            }
        }
        departmentEntity.setEmployees(list);
        return departmentEntity.getEmployees();
    }

    /**
     * Desasocia un Employee existente de un Department existente
     *
     * @param departmentId Identificador de la instancia de Department
     * @param employeesId Identificador de la instancia de Employee
     *
     */
    @Override
    public void removeEmployee(Long departmentId, Long employeesId) {
        EmployeeEntity entity = employeeLogic.getEmployee(employeesId);
        entity.setDepartment(null);
    }
}
