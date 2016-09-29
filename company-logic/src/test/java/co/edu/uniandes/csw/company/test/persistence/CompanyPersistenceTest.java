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
import co.edu.uniandes.csw.company.persistence.CompanyPersistence;
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
public class CompanyPersistenceTest {

    /**
     *
     * @return el jar que va a desplegar para la prueba
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(CompanyEntity.class.getPackage())
                .addPackage(CompanyPersistence.class.getPackage())
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml")
                .addAsManifestResource("META-INF/beans.xml", "beans.xml");
    }

    @Inject
    private CompanyPersistence companyPersistence;

    @PersistenceContext
    private EntityManager em;

    @Inject
    UserTransaction utx;

    private List<CompanyEntity> data = new ArrayList<CompanyEntity>();

    /**
     * Configuración inicial de la prueba.
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
        em.createQuery("delete from DepartmentEntity").executeUpdate();
        em.createQuery("delete from CompanyEntity").executeUpdate();
    }

    /**
     * Inserta los datos iniciales para el correcto funcionamiento de las
     * pruebas.
     *
     */
    private void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        for (int i = 0; i < 3; i++) {
            CompanyEntity entity = factory.manufacturePojo(CompanyEntity.class);
            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Prueba para crear un Company.
     */
    @Test
    public void createCompanyTest() {
        PodamFactory factory = new PodamFactoryImpl();
        CompanyEntity newEntity = factory.manufacturePojo(CompanyEntity.class);
        CompanyEntity result = companyPersistence.create(newEntity);

        Assert.assertNotNull(result);

        CompanyEntity entity = em.find(CompanyEntity.class, result.getId());

        Assert.assertEquals(newEntity.getName(), entity.getName());
    }

    /**
     * Prueba para consultar la lista de Companys.
     *
     *
     */
    @Test
    public void getCompaniesTest() {
        List<CompanyEntity> list = companyPersistence.findAll();
        Assert.assertEquals(data.size(), list.size());
        for (CompanyEntity ent : list) {
            boolean found = false;
            for (CompanyEntity entity : data) {
                if (ent.getId().equals(entity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar una Company.
     */
    @Test
    public void getCompanyTest() {
        CompanyEntity entity = data.get(0);
        CompanyEntity newEntity = companyPersistence.find(entity.getId());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
    }

    /**
     * Prueba para consultar una Company.
     */
    @Test
    public void getCompanyByNameTest() {
        CompanyEntity entity = data.get(0);
        CompanyEntity newEntity = companyPersistence.findByName(entity.getName());
        Assert.assertNotNull(newEntity);
        Assert.assertEquals(entity.getName(), newEntity.getName());
    }

    /**
     * Prueba para eliminar un Company.
     */
    @Test
    public void deleteCompanyTest() {
        CompanyEntity entity = data.get(0);
        companyPersistence.delete(entity.getId());
        CompanyEntity deleted = em.find(CompanyEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para actualizar un Company.
     */
    @Test
    public void updateCompanyTest() {
        CompanyEntity entity = data.get(0);
        PodamFactory factory = new PodamFactoryImpl();
        CompanyEntity newEntity = factory.manufacturePojo(CompanyEntity.class);

        newEntity.setId(entity.getId());

        companyPersistence.update(newEntity);

        CompanyEntity resp = em.find(CompanyEntity.class, entity.getId());

        Assert.assertEquals(newEntity.getName(), resp.getName());
    }
}
