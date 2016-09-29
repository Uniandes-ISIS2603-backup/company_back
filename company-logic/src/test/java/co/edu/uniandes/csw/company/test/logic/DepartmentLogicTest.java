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

import co.edu.uniandes.csw.company.ejbs.DepartmentLogic;
import co.edu.uniandes.csw.company.api.IDepartmentLogic;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.persistence.DepartmentPersistence;
import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.entities.EmployeeEntity;
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
public class DepartmentLogicTest {

    /**
     *
     */
    CompanyEntity fatherEntity;

    /**
     *
     */
    private PodamFactory factory = new PodamFactoryImpl();

    /**
     *
     */
    @Inject
    private IDepartmentLogic departmentLogic;

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
    private List<DepartmentEntity> data = new ArrayList<DepartmentEntity>();

    /**
     *
     */
    private List<CompanyEntity> companyData = new ArrayList<>();

    /**
     *
     */
    private List<EmployeeEntity> employeesData = new ArrayList<>();

    /**
     *
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(DepartmentEntity.class.getPackage())
                .addPackage(DepartmentLogic.class.getPackage())
                .addPackage(IDepartmentLogic.class.getPackage())
                .addPackage(DepartmentPersistence.class.getPackage())
                .addPackage(CompanyEntity.class.getPackage())
                .addPackage(EmployeeEntity.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Configuración inicial de la prueba.
     *
     *
     */
    @Before
    public void configTest() {
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
        em.createQuery("delete from CompanyEntity").executeUpdate();
    }

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las
     * pruebas.
     *
     *
     */
    private void insertData() {

        for (int i = 0; i < 3; i++) {
            EmployeeEntity employees = factory.manufacturePojo(EmployeeEntity.class);
            em.persist(employees);
            employeesData.add(employees);
        }

        fatherEntity = factory.manufacturePojo(CompanyEntity.class);
        fatherEntity.setId(1L);
        em.persist(fatherEntity);
        for (int i = 0; i < 3; i++) {
            DepartmentEntity entity = factory.manufacturePojo(DepartmentEntity.class);
            entity.setCompany(fatherEntity);
            em.persist(entity);
            data.add(entity);

            if (i == 0) {
                employeesData.get(i).setDepartment(entity);
            }
        }
    }

    /**
     * Prueba para crear un Department
     *
     *
     */
    @Test
    public void createDepartmentTest() {
        DepartmentEntity newEntity = factory.manufacturePojo(DepartmentEntity.class);
        DepartmentEntity result = departmentLogic.createDepartment(fatherEntity.getId(), newEntity);
        Assert.assertNotNull(result);
        DepartmentEntity entity = em.find(DepartmentEntity.class, result.getId());
        Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getId(), entity.getId());
    }

    /**
     * Prueba para consultar la lista de Departments
     *
     *
     */
    @Test
    public void getDepartmentsTest() {
        List<DepartmentEntity> list = departmentLogic.getDepartments(fatherEntity.getId());
        Assert.assertEquals(data.size(), list.size());
        for (DepartmentEntity entity : list) {
            boolean found = false;
            for (DepartmentEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar un Department
     *
     *
     */
    @Test
    public void getDepartmentTest() {
        DepartmentEntity entity = data.get(0);
        DepartmentEntity resultEntity = departmentLogic.getDepartment(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getName(), resultEntity.getName());
        Assert.assertEquals(entity.getId(), resultEntity.getId());
    }

    /**
     * Prueba para eliminar un Department
     *
     *
     */
    @Test
    public void deleteDepartmentTest() {
        DepartmentEntity entity = data.get(1);
        departmentLogic.deleteDepartment(entity.getId());
        DepartmentEntity deleted = em.find(DepartmentEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para actualizar un Department
     *
     *
     */
    @Test
    public void updateDepartmentTest() {
        DepartmentEntity entity = data.get(0);
        DepartmentEntity pojoEntity = factory.manufacturePojo(DepartmentEntity.class);

        pojoEntity.setId(entity.getId());

        departmentLogic.updateDepartment(fatherEntity.getId(), pojoEntity);

        DepartmentEntity resp = em.find(DepartmentEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getName(), resp.getName());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
    }

    /**
     * Prueba para obtener una instancia de Employees asociada a una instancia
     * Department
     *
     *
     */
    @Test
    public void getEmployeesTest() {
        DepartmentEntity entity = data.get(0);
        EmployeeEntity employeeEntity = employeesData.get(0);
        EmployeeEntity response = departmentLogic.getEmployee(entity.getId(), employeeEntity.getId());

        Assert.assertEquals(employeeEntity.getName(), response.getName());
        Assert.assertEquals(employeeEntity.getSalary(), response.getSalary());
        Assert.assertEquals(employeeEntity.getId(), response.getId());
    }

    /**
     * Prueba para obtener una colección de instancias de Employees asociadas a
     * una instancia Department
     *
     *
     */
    @Test
    public void listEmployeesTest() {
        List<EmployeeEntity> list = departmentLogic.listEmployees(data.get(0).getId());
        Assert.assertEquals(1, list.size());
    }

    /**
     * Prueba para asociar un Employees existente a un Department
     *
     *
     */
    @Test
    public void addEmployeesTest() {
        DepartmentEntity entity = data.get(0);
        EmployeeEntity employeeEntity = employeesData.get(1);
        EmployeeEntity response = departmentLogic.addEmployee(entity.getId(), employeeEntity.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(employeeEntity.getId(), response.getId());
    }

    /**
     * Prueba para remplazar las instancias de Employees asociadas a una
     * instancia de Department
     *
     *
     */
    @Test
    public void replaceEmployeesTest() {
        DepartmentEntity entity = data.get(0);
        List<EmployeeEntity> list = employeesData.subList(1, 3);
        departmentLogic.replaceEmployees(entity.getId(), list);

        entity = departmentLogic.getDepartment(entity.getId());
        Assert.assertFalse(entity.getEmployees().contains(employeesData.get(0)));
        Assert.assertTrue(entity.getEmployees().contains(employeesData.get(1)));
        Assert.assertTrue(entity.getEmployees().contains(employeesData.get(2)));
    }

    /**
     * Prueba para desasociar un Employees existente de un Department existente
     *
     *
     */
    @Test
    public void removeEmployeesTest() {
        departmentLogic.removeEmployee(data.get(0).getId(), employeesData.get(0).getId());
        EmployeeEntity response = departmentLogic.getEmployee(data.get(0).getId(), employeesData.get(0).getId());
        Assert.assertNull(response);
    }
}
