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

import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.persistence.DepartmentPersistence;
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

@RunWith(Arquillian.class)
public class DepartmentPersistenceTest {

    /**
     * @return el jar que se desplegará para la prueba
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(DepartmentEntity.class.getPackage())
                .addPackage(DepartmentPersistence.class.getPackage())
                .addPackage(CompanyEntity.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    /**
     * Compañía que contiene los departamentos. La relación entre Company y
     * Department es "composite"
     */
    CompanyEntity fatherEntity;

    /**
     * Lista de los departamentos que serán utilizados en las pruebas. La
     * relación entre Company y Department es "composite"
     */
    private List<DepartmentEntity> data = new ArrayList<>();

    @Inject
    private DepartmentPersistence departmentPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    /**
     * Configuración inicial de cada método de prueba.
     *
     */
    @Before
    public void configTest() {
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
     */
    private void clearData() {
        em.createQuery("delete  from DepartmentEntity").executeUpdate();
        em.createQuery("delete  from CompanyEntity").executeUpdate();
    }

    /**
     * Para el correcto funcionamiento de las pruebas, inserta los datos
     * iniciales en la base de datos utilizando un manejador de persistencia.
     *
     * Crea una compañía y luego le adiciona tres departamentos.
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        fatherEntity = factory.manufacturePojo(CompanyEntity.class);
        fatherEntity.setId(1L);
        em.persist(fatherEntity);
        for (int i = 0; i < 3; i++) {
            DepartmentEntity entity = factory.manufacturePojo(DepartmentEntity.class);
            entity.setCompany(fatherEntity);
            data.add(entity);
            em.persist(entity);
        }

    }

    /**
     * Prueba para crear un Department.
     *
     *
     */
    @Test
    public void createDepartmentTest() {
        PodamFactory factory = new PodamFactoryImpl();
        DepartmentEntity newEntity = factory.manufacturePojo(DepartmentEntity.class);
        newEntity.setCompany(fatherEntity);
        DepartmentEntity result = departmentPersistence.create(newEntity);

        Assert.assertNotNull(result);

        DepartmentEntity entity = em.find(DepartmentEntity.class, result.getId());

        Assert.assertEquals(newEntity.getName(), entity.getName());
    }

    /**
     * Prueba para consultar la lista de Departments.
     *
     *
     */
    @Test
    public void getDepartmentsInCompanyTest() {
        List<DepartmentEntity> list = departmentPersistence.findAllInCompany(fatherEntity.getId());
        Assert.assertEquals(data.size(), list.size());
        for (DepartmentEntity ent : list) {
            boolean found = false;
            for (DepartmentEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar un Department.
     *
     *
     */
    @Test
    public void getDepartmentTest() {
        DepartmentEntity entity = data.get(0);
        DepartmentEntity newEntity = departmentPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
    }

    /**
     * Prueba para eliminar un Department.
     *
     *
     */
    @Test
    public void deleteDepartmentTest() {
        DepartmentEntity entity = data.get(0);
        departmentPersistence.delete(entity.getId());
        DepartmentEntity deleted = em.find(DepartmentEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para actualizar un Department.
     *
     *
     */
    @Test
    public void updateDepartmentTest() {
        DepartmentEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        DepartmentEntity newEntity = factory.manufacturePojo(DepartmentEntity.class);

        newEntity.setId(entity.getId());

        departmentPersistence.update(newEntity);

        DepartmentEntity resp = em.find(DepartmentEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
    }
}
