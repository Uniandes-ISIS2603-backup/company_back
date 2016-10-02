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
package co.edu.uniandes.csw.company.test.persistence;
import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import co.edu.uniandes.csw.company.persistence.EmployeePersistence;
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
public class EmployeePersistenceTest {

    /**
     * 
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(EmployeeEntity.class.getPackage())
                .addPackage(EmployeePersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * 
     */

    /**
     * 
     */
    @Inject
    private EmployeePersistence employeePersistence;

    /**
     * 
     */
    @PersistenceContext
    private EntityManager em;

    /**
     * 
     */
    @Inject
    UserTransaction utx;

    /**
     * Configuración inicial de la prueba.
     *
     * 
     */
    @Before
    public void setUp() {
        try {
            utx.begin();
            em.joinTransaction();
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
    }

    /**
     * 
     */
    private List<EmployeeEntity> data = new ArrayList<EmployeeEntity>();

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
     *
     * 
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            EmployeeEntity entity = factory.manufacturePojo(EmployeeEntity.class);
            
            em.persist(entity);
            data.add(entity);
        }
    }
    /**
     * Prueba para crear un Employee.
     *
     * 
     */
    @Test
    public void createEmployeeTest() {
        PodamFactory factory = new PodamFactoryImpl();
        EmployeeEntity newEntity = factory.manufacturePojo(EmployeeEntity.class);
        EmployeeEntity result = employeePersistence.create(newEntity);

        Assert.assertNotNull(result);

        EmployeeEntity entity = em.find(EmployeeEntity.class, result.getId());

        Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getSalary(), entity.getSalary());
    }

    /**
     * Prueba para consultar la lista de Employees.
     *
     * 
     */
    @Test
    public void getEmployeesTest() {
        List<EmployeeEntity> list = employeePersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (EmployeeEntity ent : list) {
            boolean found = false;
            for (EmployeeEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar un Employee.
     *
     * 
     */
    @Test
    public void getEmployeeTest() {
        EmployeeEntity entity = data.get(0);
        EmployeeEntity newEntity = employeePersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
        Assert.assertEquals(entity.getSalary(), newEntity.getSalary());
    }

    /**
     * Prueba para eliminar un Employee.
     *
     * 
     */
    @Test
    public void deleteEmployeeTest() {
        EmployeeEntity entity = data.get(0);
        employeePersistence.delete(entity.getId());
        EmployeeEntity deleted = em.find(EmployeeEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para actualizar un Employee.
     *
     * 
     */
    @Test
    public void updateEmployeeTest() {
        EmployeeEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        EmployeeEntity newEntity = factory.manufacturePojo(EmployeeEntity.class);

        newEntity.setId(entity.getId());

        employeePersistence.update(newEntity);

        EmployeeEntity resp = em.find(EmployeeEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
        Assert.assertEquals(newEntity.getSalary(), resp.getSalary());
    }
}
