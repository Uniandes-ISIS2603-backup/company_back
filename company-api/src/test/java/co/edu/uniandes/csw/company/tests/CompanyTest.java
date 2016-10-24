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


import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.dtos.CompanyDTO;
import co.edu.uniandes.csw.company.resources.CompanyResource;
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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@RunWith(Arquillian.class)
public class CompanyTest {

    private final int Ok = Status.OK.getStatusCode();
    private final int Created = Status.CREATED.getStatusCode();
    private final int OkWithoutContent = Status.NO_CONTENT.getStatusCode();
    private final String companyPath = "companies";
    private final static List<CompanyEntity> oraculo = new ArrayList<>();
    private WebTarget target;
    private final String apiPath = "api";
    
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
                .addPackage(CompanyResource.class.getPackage())
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
        for (int i = 0; i < 3; i++) {            
            CompanyEntity company = factory.manufacturePojo(CompanyEntity.class);
            company.setId(i + 1L);
            em.persist(company);
            oraculo.add(company);
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
     * Prueba para crear un Company
     *
     * 
     */
    @Test
    public void createCompanyTest() throws IOException {
        PodamFactory factory = new PodamFactoryImpl();
        CompanyDTO company = factory.manufacturePojo(CompanyDTO.class);
 
        Response response = target.path(companyPath)
            .request()
            .post(Entity.entity(company, MediaType.APPLICATION_JSON));
        
        CompanyDTO  companyTest = (CompanyDTO) response.readEntity(CompanyDTO.class);
        Assert.assertEquals(company.getName(), companyTest.getName());
        Assert.assertEquals(Created, response.getStatus());
        CompanyEntity entity = em.find(CompanyEntity.class, companyTest.getId());
        Assert.assertNotNull(entity);
    }

    /**
     * Prueba para consultar un Company
     *
     * 
     */
    @Test
    public void getCompanyById() {
        
        CompanyDTO companyTest = target.path(companyPath)
                .path(oraculo.get(0).getId().toString())
                .request().get(CompanyDTO.class);
        
        Assert.assertEquals(companyTest.getName(), oraculo.get(0).getName());
        Assert.assertEquals(companyTest.getId(), oraculo.get(0).getId());
    }

    /**
     * Prueba para consultar la lista de Companys
     *
     * 
     */
    @Test
    public void listCompanyTest() throws IOException {
       
        Response response = target.path(companyPath)
                .request().get();
        
        String listCompany = response.readEntity(String.class);
        List<CompanyDTO> listCompanyTest = new ObjectMapper().readValue(listCompany, List.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(3, listCompanyTest.size());
    }

    /**
     * Prueba para actualizar un Company
     *
     * 
     */
    @Test
    public void updateCompanyTest() throws IOException {
       
        CompanyDTO company = new CompanyDTO(oraculo.get(0));
        PodamFactory factory = new PodamFactoryImpl();
        CompanyDTO companyChanged = factory.manufacturePojo(CompanyDTO.class);
        company.setName(companyChanged.getName());
        Response response = target.path(companyPath).path(company.getId().toString())
                .request().put(Entity.entity(company, MediaType.APPLICATION_JSON));
        
        CompanyDTO companyTest = (CompanyDTO) response.readEntity(CompanyDTO.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(company.getName(), companyTest.getName());
    }
    
    /**
     * Prueba para eliminar un Company
     *
     * 
     */
    @Test
    public void deleteCompanyTest() {
      
        CompanyDTO company = new CompanyDTO(oraculo.get(0));
        Response response = target.path(companyPath).path(company.getId().toString())
                .request().delete();
        
        Assert.assertEquals(OkWithoutContent, response.getStatus());
    }
}
