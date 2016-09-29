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

import co.edu.uniandes.csw.company.api.IEmployeeLogic;
import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import co.edu.uniandes.csw.company.persistence.EmployeePersistence;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;


@Stateless
public class EmployeeLogic implements IEmployeeLogic {

    @Inject private EmployeePersistence persistence;


    /**
     * Obtiene la lista de los registros de Employee.
     *
     * @return Colección de objetos de EmployeeEntity.
     * 
     */
    @Override
    public List<EmployeeEntity> getEmployees() {
        return persistence.findAll();
    }


    /**
     * Obtiene los datos de una instancia de Employee a partir de su ID.
     *
     * @param id Identificador de la instancia a consultar
     * @return Instancia de EmployeeEntity con los datos del Employee consultado.
     * 
     */
    public EmployeeEntity getEmployee(Long id) {
        return persistence.find(id);
    }

    /**
     * Obtiene los datos de una instancia de Employee a partir de su name.
     *
     * @param name nombre del empleado de la instancia a consultar
     * @return el primer empleado con ese nombre .
     * 
     */
    public EmployeeEntity getEmployeeByName(String name) {
        return persistence.findByName(name);
    }
    /**
     * Se encarga de crear un Employee en la base de datos.
     *
     * @param entity Objeto de EmployeeEntity con los datos nuevos
     * @return Objeto de EmployeeEntity con los datos nuevos y su ID.
     * 
     */
    @Override
    public EmployeeEntity createEmployee(EmployeeEntity entity) {
        persistence.create(entity);
        return entity;
    }

    /**
     * Actualiza la información de una instancia de Employee.
     *
     * @param entity Instancia de EmployeeEntity con los nuevos datos.
     * @return Instancia de EmployeeEntity con los datos actualizados.
     * 
     */
    @Override
    public EmployeeEntity updateEmployee(EmployeeEntity entity) {
        return persistence.update(entity);
    }

    /**
     * Elimina una instancia de Employee de la base de datos.
     *
     * @param id Identificador de la instancia a eliminar.
     * 
     */
    @Override
    public void deleteEmployee(Long id) {
        persistence.delete(id);
    }
  
}
