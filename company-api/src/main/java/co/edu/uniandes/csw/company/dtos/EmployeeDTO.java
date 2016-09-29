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


@XmlRootElement
public class EmployeeDTO {

    private String name;
    private Double salary;
    private Long id;

    /**
     *
     */
    public EmployeeDTO() {
    }

    /**
     * Crea un objeto EmployeeMinimumDTO a partir de un objeto EmployeeEntity.
     *
     * @param entity Entidad EmployeeEntity desde la cual se va a crear el nuevo
     * objeto.
     *
     */
    public EmployeeDTO(EmployeeEntity entity) {
        if (entity != null) {
            this.name = entity.getName();
            this.salary = entity.getSalary();
            this.id = entity.getId();
        }
    }

    /**
     * Convierte un objeto EmployeeDTO a EmployeeEntity.
     *
     * @return Nueva objeto EmployeeEntity.
     *
     */
    public EmployeeEntity toEntity() {
        EmployeeEntity entity = new EmployeeEntity();
        entity.setName(this.getName());
        entity.setSalary(this.getSalary());
        entity.setId(this.getId());
        return entity;
    }

    /**
     * Obtiene el atributo name.
     *
     * @return atributo name.
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el valor del atributo name.
     *
     * @param name nuevo valor del atributo
     *
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene el atributo salary.
     *
     * @return atributo salary.
     *
     */
    public Double getSalary() {
        return salary;
    }

    /**
     * Establece el valor del atributo salary.
     *
     * @param salary nuevo valor del atributo
     *
     */
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    /**
     * Obtiene el atributo id.
     *
     * @return atributo id.
     *
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el valor del atributo id.
     *
     * @param id nuevo valor del atributo
     *
     */
    public void setId(Long id) {
        this.id = id;
    }

}
