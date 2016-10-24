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

import co.edu.uniandes.csw.company.ejbs.CompanyLogic;
import co.edu.uniandes.csw.company.api.ICompanyLogic;
import co.edu.uniandes.csw.company.api.IDepartmentLogic;
import co.edu.uniandes.csw.company.ejbs.DepartmentLogic;
import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.persistence.CompanyPersistence;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.persistence.DepartmentPersistence;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
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
public class CompanyLogicTest {

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
    private ICompanyLogic companyLogic;
    /**
     *
     */
    @Inject
    private DepartmentPersistence departmentPersistence;
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
    private List<CompanyEntity> data = new ArrayList<CompanyEntity>();

    /**
     *
     */
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackage(CompanyEntity.class.getPackage())
                .addPackage(CompanyLogic.class.getPackage())
                .addPackage(ICompanyLogic.class.getPackage())
                .addPackage(CompanyPersistence.class.getPackage())
                .addPackage(DepartmentPersistence.class.getPackage())
                .addPackage(DepartmentEntity.class.getPackage())
                .addPackage(DepartmentLogic.class.getPackage())
                .addPackage(IDepartmentLogic.class.getPackage())
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
            CompanyEntity entity = factory.manufacturePojo(CompanyEntity.class);

            em.persist(entity);
            data.add(entity);
        }
    }

    /**
     * Prueba para crear un Company
     *
     *
     */
    @Test
    public void createCompanyTest() throws NotSupportedException, Exception {
        CompanyEntity newEntity = factory.manufacturePojo(CompanyEntity.class);
        System.out.println("1 " + newEntity.getDepartments().size());
        CompanyEntity result = companyLogic.createCompany(newEntity);
        Assert.assertNotNull(result);
        System.out.println("2 " + result.getDepartments().size());

        CompanyEntity entity = em.find(CompanyEntity.class, result.getId());
        System.out.println("3 " + entity.getDepartments().size());

        TypedQuery q = em.createQuery("select d from DepartmentEntity d  where d.company.id = :companyId", DepartmentEntity.class);
        q = q.setParameter("companyId", result.getId());
        List<DepartmentEntity> list = (List<DepartmentEntity>) q.getResultList();

        System.out.println("4 " + list.size());
        list = departmentPersistence.findAll();
        
        for (DepartmentEntity d: list){
            System.out.println(d.getName()+" "+d.getCompany().getId());
            
        }
        Assert.assertEquals(newEntity.getName(), entity.getName());
        Assert.assertEquals(newEntity.getId(), entity.getId());

        entity.setDepartments(list);

        Assert.assertNotNull(entity.getDepartments());
        Assert.assertNotNull(result.getDepartments());
        Assert.assertEquals(result.getDepartments().size(), entity.getDepartments().size());

    }

    /**
     * Prueba para consultar la lista de Companys
     *
     *
     */
    @Test
    public void getCompanysTest() {
        List<CompanyEntity> list = companyLogic.getCompanys();
        Assert.assertEquals(data.size(), list.size());
        for (CompanyEntity entity : list) {
            boolean found = false;
            for (CompanyEntity storedEntity : data) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            Assert.assertTrue(found);
        }
    }

    /**
     * Prueba para consultar un Company
     *
     *
     */
    @Test
    public void getCompanyTest() {
        CompanyEntity entity = data.get(0);
        CompanyEntity resultEntity = companyLogic.getCompany(entity.getId());
        Assert.assertNotNull(resultEntity);
        Assert.assertEquals(entity.getName(), resultEntity.getName());
        Assert.assertEquals(entity.getId(), resultEntity.getId());
    }

    /**
     * Prueba para eliminar un Company
     *
     *
     */
    @Test
    public void deleteCompanyTest() {
        CompanyEntity entity = data.get(1);
        companyLogic.deleteCompany(entity.getId());
        CompanyEntity deleted = em.find(CompanyEntity.class, entity.getId());
        Assert.assertNull(deleted);
    }

    /**
     * Prueba para actualizar un Company
     *
     *
     */
    @Test
    public void updateCompanyTest() {
        CompanyEntity entity = data.get(0);
        CompanyEntity pojoEntity = factory.manufacturePojo(CompanyEntity.class);

        pojoEntity.setId(entity.getId());

        companyLogic.updateCompany(pojoEntity);

        CompanyEntity resp = em.find(CompanyEntity.class, entity.getId());

        Assert.assertEquals(pojoEntity.getName(), resp.getName());
        Assert.assertEquals(pojoEntity.getId(), resp.getId());
    }
}
