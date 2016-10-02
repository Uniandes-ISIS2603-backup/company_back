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
package co.edu.uniandes.csw.company.test.logic;

import co.edu.uniandes.csw.company.ejbs.EmployeeLogic;
import co.edu.uniandes.csw.company.api.IEmployeeLogic;
import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import co.edu.uniandes.csw.company.persistence.EmployeePersistence;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.junit.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * 
 */
@RunWith(Arquillian.class)
public class EmployeeLogicTest {

    /**
     * 
     */

    /**
     * 
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     * 
     */
    @Inject
    private IEmployeeLogic employeeLogic;

    /**
     * 
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * 
     */
    @Inject
    private UserTransaction utx;

    /**
     * 
     */
    private List<EmployeeEntity> data = new ArrayList<EmployeeEntity>();

    /**
     * 
     */
    private List<DepartmentEntity> departmentData = new ArrayList<>();

    /**
     * 
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EmployeeEntity.class.getPackage())
                .addPackage(EmployeeLogic.class.getPackage())
                .addPackage(IEmployeeLogic.class.getPackage())
                .addPackage(EmployeePersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Configuración inicial de la prueba.
     *
     * 
     */
    @Before
    public void setUp() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Limpia las tablas que están implicadas en la prueba.
     *
     * 
     */
    private void clearData() {
        em.createQuery("delete from EmployeeEntity").executeUpdate();
        em.createQuery("delete from DepartmentEntity").executeUpdate();
    }

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
     *
     * 
     */
    private void insertData() {
        
        
        for (int i = 0; i < 3; i++) {
            DepartmentEntity department = factory.manufacturePojo(DepartmentEntity.class);
            em.persist(department);
            departmentData.add(department);
        }
        for (int i = 0; i < 3; i++) {
            EmployeeEntity entity = factory.manufacturePojo(EmployeeEntity.class);
            entity.setDepartment(departmentData.get(0));
            

            em.persist(entity);
            data.add(entity);
        }
    }
    /**
     * Prueba para crear un Employee
     *
     * 
     */
    @Test
    public void createEmployeeTest() {
        EmployeeEntity newEntity = factory.manufacturePojo(EmployeeEntity.class);
        EmployeeEntity result = employeeLogic.createEmployee(newEntity);
        Assert.assertNotNull(result);
        EmployeeEntity entity = em.find(EmployeeEntity.class, result.getId());
        Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getSalary(), entity.getSalary());
        Assert.assertEquals(newEntity.getId(), entity.getId());
    }

    /**
     * Prueba para consultar la lista de Employees
     *
     * 
     */
    @Test
    public void getEmployeesTest() {
        List<EmployeeEntity> list = employeeLogic.getEmployees();
        Assert.assertEquals(data.size(), list.size());
        for (EmployeeEntity entity : list) {
            boolean found = false;
            for (EmployeeEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    
    /**
     * Prueba para consultar un Employee
     *
     * 
     */
    @Test
    public void getEmployeeTest() {
        EmployeeEntity entity = data.get(0);
        EmployeeEntity resultEntity = employeeLogic.getEmployee(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getName(), resultEntity.getName());
        Assert.assertEquals(entity.getSalary(), resultEntity.getSalary());
        Assert.assertEquals(entity.getId(), resultEntity.getId());
    }

    /**
     * Prueba para eliminar un Employee
     *
     * 
     */
    @Test
    public void deleteEmployeeTest() {
        EmployeeEntity entity = data.get(1);
        employeeLogic.deleteEmployee(entity.getId());
        EmployeeEntity deleted = em.find(EmployeeEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para actualizar un Employee
     *
     * 
     */
    @Test
    public void updateEmployeeTest() {
        EmployeeEntity entity = data.get(0);
        EmployeeEntity pojoEntity = factory.manufacturePojo(EmployeeEntity.class);

        pojoEntity.setId(entity.getId());

        employeeLogic.updateEmployee(pojoEntity);

        EmployeeEntity resp = em.find(EmployeeEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getName(), resp.getName());
        Assert.assertEquals(pojoEntity.getSalary(), resp.getSalary());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
    }
}

