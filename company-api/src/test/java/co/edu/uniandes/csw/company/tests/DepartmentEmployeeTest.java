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
import co.edu.uniandes.csw.company.dtos.DepartmentDTO;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.dtos.EmployeeDTO;
import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import co.edu.uniandes.csw.company.resources.DepartmentResource;
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
import org.codehaus.jackson.map.ObjectMapper;

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
public class DepartmentEmployeeTest {

    private final int Ok = Status.OK.getStatusCode();
    private final int Created = 200; // Status.CREATED.getStatusCode();
    private final int OkWithoutContent = Status.NO_CONTENT.getStatusCode();
    private final String departmentPath = "departments";
    private final static List<DepartmentEntity> oraculo = new ArrayList<>();
    private final String employeesPath = "employees";
    private final static List<EmployeeEntity> oraculoEmployees = new ArrayList<>();
    private WebTarget target;
    private final String apiPath = "api";  
    private final String companyPath = "companies";
    CompanyEntity fatherEntity;

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
        em.createQuery("delete from EmployeeEntity").executeUpdate();
        em.createQuery("delete from CompanyEntity").executeUpdate();
        em.createQuery("delete from DepartmentEntity").executeUpdate();
        oraculoEmployees.clear();
        oraculo.clear();
    }

  

   /**
     * Datos iniciales para el correcto funcionamiento de las pruebas.
     *
     * 
     */
    public void insertData() {
        try{
            PodamFactory factory = new PodamFactoryImpl();
            fatherEntity = factory.manufacturePojo(CompanyEntity.class);
            fatherEntity.setId(1L);
            utx.begin();
            em.persist(fatherEntity);
            utx.commit();
            for (int i = 0; i < 3; i++) {   
                DepartmentEntity department = factory.manufacturePojo(DepartmentEntity.class);
                department.setId(i + 1L);
                department.setCompany(fatherEntity);
                utx.begin();
                em.persist(department);
                utx.commit();
                oraculo.add(department);

                EmployeeEntity employees = factory.manufacturePojo(EmployeeEntity.class);
                employees.setId(i + 1L);
                employees.setDepartment(department);    
                utx.begin();
                em.persist(employees);
                utx.commit();
                oraculoEmployees.add(employees);                     
                
                
            }
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
            utx.commit();
            insertData();            
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
     *Prueba para asociar un Employees existente a un Department
     *
     * 
     */
    @Test
    public void addEmployeesTest() {
       

        EmployeeDTO employees = new EmployeeDTO(oraculoEmployees.get(1));
        DepartmentDTO department = new DepartmentDTO(oraculo.get(0));

        Response response = target.path(departmentPath).path(department.getId().toString())
                .path(employeesPath).path(employees.getId().toString())
                .request()
                .post(Entity.entity(employees, MediaType.APPLICATION_JSON));

        EmployeeDTO employeesTest = (EmployeeDTO) response.readEntity(EmployeeDTO.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(employees.getId(), employeesTest.getId());
    }

    /**
     * Prueba para obtener una colección de instancias de Employees asociadas a una instancia Department
     *
     * 
     */
    @Test
    public void listEmployeesTest() throws IOException {
   
        DepartmentDTO department = new DepartmentDTO(oraculo.get(0));

        Response response = target.path(departmentPath)
                .path(department.getId().toString())
                .path(employeesPath)
                .request().get();

        String employeesList = response.readEntity(String.class);
        List<EmployeeDTO> employeesListTest = new ObjectMapper().readValue(employeesList, List.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(1, employeesListTest.size());
    }

    /**
     * Prueba para obtener una instancia de Employees asociada a una instancia Department
     *
     * 
     */
    @Test
    public void getEmployeesTest() throws IOException {
      
        EmployeeDTO employees = new EmployeeDTO(oraculoEmployees.get(0));
        DepartmentDTO department = new DepartmentDTO(oraculo.get(0));

        EmployeeDTO employeesTest = target.path(departmentPath)
                .path(department.getId().toString()).path(employeesPath)
                .path(employees.getId().toString())
                .request().get(EmployeeDTO.class);

        Assert.assertEquals(employees.getName(), employeesTest.getName());
        Assert.assertEquals(employees.getSalary(), employeesTest.getSalary());
        Assert.assertEquals(employees.getId(), employeesTest.getId());
    }

    /**
     * Prueba para desasociar un Employees existente de un Department existente
     *
     * 
     */
    @Test
    public void removeEmployeesTest() {
    
        EmployeeDTO employees = new EmployeeDTO(oraculoEmployees.get(0));
        DepartmentDTO department = new DepartmentDTO(oraculo.get(0));

        Response response = target.path(departmentPath).path(department.getId().toString())
                .path(employeesPath).path(employees.getId().toString())
                .request().delete();
        Assert.assertEquals(OkWithoutContent, response.getStatus());
    }
}
