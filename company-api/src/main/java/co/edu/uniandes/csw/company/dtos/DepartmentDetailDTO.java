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

import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import uk.co.jemos.podam.common.PodamExclude;


@XmlRootElement
public class DepartmentDetailDTO extends DepartmentDTO {

    @PodamExclude
    private CompanyDTO company;

     // relación  cero o muchos con departments 
    private List<EmployeeDTO> employees = new ArrayList<>();
    /**
     *
     */
    public DepartmentDetailDTO() {
        super();
    }

    /**
     * Crea un objeto DepartmentDetailDTO a partir de un objeto DepartmentEntity
     * incluyendo los atributos de DepartmentDTO.
     *
     * @param entity Entidad DepartmentEntity desde la cual se va a crear el
     * nuevo objeto.
     *
     */
    public DepartmentDetailDTO(DepartmentEntity entity) {
        super(entity);
        if (entity.getCompany() != null) {
            this.company = new CompanyDTO(entity.getCompany());
        }

    }

    /**
     * Convierte un objeto DepartmentDetailDTO a DepartmentEntity incluyendo los
     * atributos de DepartmentDTO.
     *
     * @return  objeto DepartmentEntity.
     *
     */
    @Override
    public DepartmentEntity toEntity() {
        DepartmentEntity entity = super.toEntity();
        if (this.getCompany() != null) {
            entity.setCompany(this.getCompany().toEntity());
        }
        return entity;
    }

    /**
     * Obtiene el atributo company.
     *
     * @return atributo company.
     *
     */
    public CompanyDTO getCompany() {
        return company;
    }

    /**
     * Establece el valor del atributo company.
     *
     * @param company nuevo valor del atributo
     *
     */
    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    /**
     * @return the employees
     */
    public List<EmployeeDTO> getEmployees() {
        return employees;
    }

    /**
     * @param employees the employees to set
     */
    public void setEmployees(List<EmployeeDTO> employees) {
        this.employees = employees;
    }

}
