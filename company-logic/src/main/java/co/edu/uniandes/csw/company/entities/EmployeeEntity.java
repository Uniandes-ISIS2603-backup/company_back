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
package co.edu.uniandes.csw.company.entities;

import java.io.Serializable;
import javax.persistence.Entity;

import uk.co.jemos.podam.common.PodamExclude;
import javax.persistence.ManyToOne;

@Entity
public class EmployeeEntity extends BaseEntity implements Serializable {

    private Double salary;

    @PodamExclude
    @ManyToOne
    private DepartmentEntity department;

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
     * Obtiene el atributo department.
     *
     * @return atributo department.
     *
     */
    public DepartmentEntity getDepartment() {
        return department;
    }

    /**
     * Establece el valor del atributo department.
     *
     * @param department nuevo valor del atributo
     *
     */
    public void setDepartment(DepartmentEntity department) {
        this.department = department;
    }
}
