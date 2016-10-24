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
package co.edu.uniandes.csw.company.tests;


import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.dtos.DepartmentDTO;
import co.edu.uniandes.csw.company.resources.DepartmentResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@RunWith(Arquillian.class)
public class DepartmentTest {

    private final int Ok = Status.OK.getStatusCode();
    private final int Created = Status.CREATED.getStatusCode();
    private final int OkWithoutContent = Status.NO_CONTENT.getStatusCode();
    private final String departmentPath = "departments";
    private final static List<DepartmentEntity> oraculo = new ArrayList<>();
    private WebTarget target;
    private final String apiPath = "api";
    private final String username = System.getenv("USERNAME_USER");
    private final String password = System.getenv("PASSWORD_USER");    
    private final String companyPath = "companies";
    CompanyEntity fatherCompanyEntity;

    @ArquillianResource
    private URL deploymentURL;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                // Se agrega las dependencias
                .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .importRuntimeDependencies().resolve()
                        .withTransitivity().asFile())
                // Se agregan los compilados de los paquetes de servicios
                .addPackage(DepartmentResource.class.getPackage())
                // El archivo que contiene la configuracion a la base de datos.
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                // El archivo beans.xml es necesario para injeccion de dependencias.
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                 // El archivo web.xml es necesario para el despliegue de los servlets
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    private WebTarget createWebTarget() {
        return ClientBuilder.newClient().target(deploymentURL.toString()).path(apiPath);
    }

    @PersistenceContext(unitName = "CompanyPU")
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private void clearData() {
        
        em.createQuery("delete from DepartmentEntity").executeUpdate();
        em.createQuery("delete from CompanyEntity").executeUpdate();
        oraculo.clear();
    }

  

   /**
     * Datos iniciales para el correcto funcionamiento de las pruebas.
     *
     * 
     */
    public void insertData() {
        PodamFactory factory = new PodamFactoryImpl();
        fatherCompanyEntity = factory.manufacturePojo(CompanyEntity.class);
        fatherCompanyEntity.setId(1L);
        em.persist(fatherCompanyEntity);
        
        for (int i = 0; i < 3; i++) {            
            DepartmentEntity department = factory.manufacturePojo(DepartmentEntity.class);
            department.setId(i + 1L);
            department.setCompany(fatherCompanyEntity);
            em.persist(department);
            oraculo.add(department);
        }
    }

 

    /**
     * Configuración inicial de la prueba.
     *
     * 
     */
    @Before
    public void setUpTest() {
        target = createWebTarget();
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
     * Prueba para crear un Department
     *
     * 
     */
    @Test
    public void createDepartmentTest() throws IOException {
        PodamFactory factory = new PodamFactoryImpl();
        DepartmentDTO department = factory.manufacturePojo(DepartmentDTO.class);
   
        Response response = target
            .path(companyPath).path(fatherCompanyEntity.getId().toString())
          .path(departmentPath)
            .request()
            .post(Entity.entity(department, MediaType.APPLICATION_JSON));
        
        DepartmentDTO  departmentTest = (DepartmentDTO) response.readEntity(DepartmentDTO.class);
        Assert.assertEquals(department.getName(), departmentTest.getName());
        Assert.assertEquals(Ok, response.getStatus());
        DepartmentEntity entity = em.find(DepartmentEntity.class, departmentTest.getId());
        Assert.assertNotNull(entity);
    }

    /**
     * Prueba para consultar un Department
     *
     * 
     */
    @Test
    public void getDepartmentById() {
    
        DepartmentDTO departmentTest = target
            .path(companyPath).path(fatherCompanyEntity.getId().toString())
          .path(departmentPath)
            .path(oraculo.get(0).getId().toString())
            .request().get(DepartmentDTO.class);
        
        Assert.assertEquals(departmentTest.getName(), oraculo.get(0).getName());
        Assert.assertEquals(departmentTest.getId(), oraculo.get(0).getId());
    }

    /**
     * Prueba para consultar la lista de Departments
     *
     * 
     */
    @Test
    public void listDepartmentTest() throws IOException {
    
        Response response = target
            .path(companyPath).path(fatherCompanyEntity.getId().toString())
          .path(departmentPath)
            .request().get();
        
        String listDepartment = response.readEntity(String.class);
        List<DepartmentDTO> listDepartmentTest = new ObjectMapper().readValue(listDepartment, List.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(3, listDepartmentTest.size());
    }

    /**
     * Prueba para actualizar un Department
     *
     * 
     */
    @Test
    public void updateDepartmentTest() throws IOException {
        
        DepartmentDTO department = new DepartmentDTO(oraculo.get(0));
        PodamFactory factory = new PodamFactoryImpl();
        DepartmentDTO departmentChanged = factory.manufacturePojo(DepartmentDTO.class);
        department.setName(departmentChanged.getName());
        Response response = target
            .path(companyPath).path(fatherCompanyEntity.getId().toString())
          .path(departmentPath)
            .path(department.getId().toString())
            .request().put(Entity.entity(department, MediaType.APPLICATION_JSON));
        
        DepartmentDTO departmentTest = (DepartmentDTO) response.readEntity(DepartmentDTO.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(department.getName(), departmentTest.getName());
    }
    
    /**
     * Prueba para eliminar un Department
     *
     * 
     */
    @Test
    public void deleteDepartmentTest() {
       
        DepartmentDTO department = new DepartmentDTO(oraculo.get(0));
        Response response = target
            .path(companyPath).path(fatherCompanyEntity.getId().toString())
          .path(departmentPath)
            .path(department.getId().toString())
            .request().delete();
        
        Assert.assertEquals(OkWithoutContent, response.getStatus());
    }
    
}
