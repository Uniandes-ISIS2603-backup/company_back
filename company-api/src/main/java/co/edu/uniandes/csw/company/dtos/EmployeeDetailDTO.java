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
package co.edu.uniandes.csw.company.dtos;

import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import javax.xml.bind.annotation.XmlRootElement;
import uk.co.jemos.podam.common.PodamExclude;


@XmlRootElement
public class EmployeeDetailDTO extends EmployeeDTO{


    @PodamExclude
    private DepartmentDTO department;

    /**
     * 
     */
    public EmployeeDetailDTO() {
        super();
    }

    /**
     * Crea un objeto EmployeeDetailDTO a partir de un objeto EmployeeEntity incluyendo los atributos de EmployeeDTO.
     *
     * @param entity Entidad EmployeeEntity desde la cual se va a crear el nuevo objeto.
     * 
     */
    public EmployeeDetailDTO(EmployeeEntity entity) {
        super(entity);
        if (entity.getDepartment()!=null){
        this.department = new DepartmentDTO(entity.getDepartment());
        }
        
    }

    /**
     * Convierte un objeto EmployeeDetailDTO a EmployeeEntity incluyendo los atributos de EmployeeDTO.
     *
     * @return  objeto EmployeeEntity.
     * 
     */
    @Override
    public EmployeeEntity toEntity() {
        EmployeeEntity entity = super.toEntity();
        if (this.getDepartment()!=null){
        entity.setDepartment(this.getDepartment().toEntity());
        }
        return entity;
    }

    /**
     * Obtiene el atributo department.
     *
     * @return atributo department.
     * 
     */
    public DepartmentDTO getDepartment() {
        return department;
    }

    /**
     * Establece el valor del atributo department.
     *
     * @param department nuevo valor del atributo
     * 
     */
    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

}
