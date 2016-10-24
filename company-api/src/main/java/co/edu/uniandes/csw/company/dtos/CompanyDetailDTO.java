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

import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CompanyDetailDTO extends CompanyDTO {

    // relación  cero o muchos con departments 
    private List<DepartmentDTO> departments = new ArrayList<>();

    public CompanyDetailDTO() {
        super();
    }

    /**
     * Crea un objeto CompanyDetailDTO a partir de un objeto CompanyEntity
     * incluyendo los atributos de CompanyDTO.
     *
     * @param entity Entidad CompanyEntity desde la cual se va a crear el nuevo
     * objeto.
     *
     */
    public CompanyDetailDTO(CompanyEntity entity) {
        super(entity);
        List<DepartmentEntity> departmentsList = entity.getDepartments();
        for (DepartmentEntity dept : departmentsList) {
            this.departments.add(new DepartmentDTO(dept));
        }
    }

    /**
     * Convierte un objeto CompanyDetailDTO a CompanyEntity incluyendo los
     * atributos de CompanyDTO.
     *
     * @return objeto CompanyEntity.
     *
     */
    @Override
    public CompanyEntity toEntity() {
        CompanyEntity entity = super.toEntity();
         List<DepartmentDTO> departments = this.getDepartments();
        for (DepartmentDTO dept : this.departments) {         
            entity.getDepartments().add(dept.toEntity());
        }
        return entity;
    }

    /**
     * @return the departments
     */
    public List<DepartmentDTO> getDepartments() {
        return departments;
    }

    /**
     * @param departments the departments to set
     */
    public void setDepartments(List<DepartmentDTO> departments) {
        this.departments = departments;
    }

}
