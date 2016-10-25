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


import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import co.edu.uniandes.csw.company.dtos.EmployeeDTO;
import co.edu.uniandes.csw.company.resources.EmployeeResource;
import org.codehaus.jackson.map.ObjectMapper;
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
public class EmployeeTest {

    private final int Ok = Status.OK.getStatusCode();
    private final int Created = 200; // Status.CREATED.getStatusCode();
    private final int OkWithoutContent = Status.NO_CONTENT.getStatusCode();
    private final String employeePath = "employees";
    private final static List<EmployeeEntity> oraculo = new ArrayList<>();
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
                .addPackage(EmployeeResource.class.getPackage())
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
        
        em.createQuery("delete from EmployeeEntity").executeUpdate();    
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
            EmployeeEntity employee = factory.manufacturePojo(EmployeeEntity.class);
            employee.setId(i + 1L);
            em.persist(employee);
            oraculo.add(employee);
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
     * Prueba para crear un Employee
     *
     * 
     */
    @Test
    public void createEmployeeTest() throws IOException {
        PodamFactory factory = new PodamFactoryImpl();
        EmployeeDTO employee = factory.manufacturePojo(EmployeeDTO.class);
    
        Response response = target.path(employeePath)
            .request()
            .post(Entity.entity(employee, MediaType.APPLICATION_JSON));
        
        EmployeeDTO  employeeTest = (EmployeeDTO) response.readEntity(EmployeeDTO.class);
        Assert.assertEquals(employee.getName(), employeeTest.getName());
        Assert.assertEquals(employee.getSalary(), employeeTest.getSalary());
        Assert.assertEquals(Created, response.getStatus());
        EmployeeEntity entity = em.find(EmployeeEntity.class, employeeTest.getId());
        Assert.assertNotNull(entity);
    }

    /**
     * Prueba para consultar un Employee
     *
     * 
     */
    @Test
    public void getEmployeeById() {
    
        EmployeeDTO employeeTest = target.path(employeePath)
                .path(oraculo.get(0).getId().toString())
                .request().get(EmployeeDTO.class);
        
        Assert.assertEquals(employeeTest.getName(), oraculo.get(0).getName());
        Assert.assertEquals(employeeTest.getSalary(), oraculo.get(0).getSalary());
        Assert.assertEquals(employeeTest.getId(), oraculo.get(0).getId());
    }

    /**
     * Prueba para consultar la lista de Employees
     *
     * 
     */
    @Test
    public void listEmployeeTest() throws IOException {
    
        Response response = target.path(employeePath)
                .request().get();
        
        String listEmployee = response.readEntity(String.class);
        List<EmployeeDTO> listEmployeeTest = new ObjectMapper().readValue(listEmployee, List.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(3, listEmployeeTest.size());
    }

    /**
     * Prueba para actualizar un Employee
     *
     * 
     */
    @Test
    public void updateEmployeeTest() throws IOException {
        
        EmployeeDTO employee = new EmployeeDTO(oraculo.get(0));
        PodamFactory factory = new PodamFactoryImpl();
        EmployeeDTO employeeChanged = factory.manufacturePojo(EmployeeDTO.class);
        employee.setName(employeeChanged.getName());
        employee.setSalary(employeeChanged.getSalary());
        Response response = target.path(employeePath).path(employee.getId().toString())
                .request().put(Entity.entity(employee, MediaType.APPLICATION_JSON));
        
        EmployeeDTO employeeTest = (EmployeeDTO) response.readEntity(EmployeeDTO.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(employee.getName(), employeeTest.getName());
        Assert.assertEquals(employee.getSalary(), employeeTest.getSalary());
    }
    
    /**
     * Prueba para eliminar un Employee
     *
     * 
     */
    @Test
    public void deleteEmployeeTest() {
    
        EmployeeDTO employee = new EmployeeDTO(oraculo.get(0));
        Response response = target.path(employeePath).path(employee.getId().toString())
                .request().delete();
        
        Assert.assertEquals(OkWithoutContent, response.getStatus());
    }
}
