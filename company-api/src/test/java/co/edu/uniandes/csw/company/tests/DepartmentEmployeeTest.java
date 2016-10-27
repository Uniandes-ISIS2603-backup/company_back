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

import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import co.edu.uniandes.csw.company.dtos.EmployeeDTO;
import co.edu.uniandes.csw.company.dtos.EmployeeDetailDTO;
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

    private WebTarget target;
    private PodamFactory factory = new PodamFactoryImpl();

    private final int Ok = Status.OK.getStatusCode();
    private final int OkWithoutContent = Status.NO_CONTENT.getStatusCode();

    private final static List<EmployeeEntity> employeeList = new ArrayList<>();
    private final String apiPath = "api";
    private final String companyPath = "companies";
    private final String departmentPath = "departments";
    private final String employeesPath = "employees";

    private CompanyEntity fatherCompanyEntity;
    private DepartmentEntity fatherDepartmentEntity;

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
        List<EmployeeEntity> records = em.createQuery("SELECT u FROM EmployeeEntity u").getResultList();
        for (EmployeeEntity record : records) {
            em.remove(record);
        }
        em.createQuery("delete from DepartmentEntity").executeUpdate();
        em.createQuery("delete from CompanyEntity").executeUpdate();
        
        employeeList.clear();
        
    }

    /**
     * Datos iniciales para el correcto funcionamiento de las pruebas.
     *
     */
    private void insertData() {
        fatherCompanyEntity = factory.manufacturePojo(CompanyEntity.class);
        em.persist(fatherCompanyEntity);
        fatherDepartmentEntity = factory.manufacturePojo(DepartmentEntity.class);
        fatherDepartmentEntity.setCompany(fatherCompanyEntity);
        em.persist(fatherDepartmentEntity);

        for (int i = 0; i < 3; i++) {
            EmployeeEntity employees = factory.manufacturePojo(EmployeeEntity.class);
            em.persist(employees);
            if (i < 2) {
                employees.setDepartment(fatherDepartmentEntity);
            }
            employeeList.add(employees);
        }
    }
/**
     * Configuración inicial de la prueba.
     *
     * @generated
     */
    @Before
    public void setUpTest() {
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
        target = createWebTarget()
                .path(companyPath)
                .path(fatherCompanyEntity.getId().toString())
                .path(departmentPath)
                .path(fatherDepartmentEntity.getId().toString())
                .path(employeesPath);
    }
    /**
     * Prueba para asociar un Employees existente a un Department
     *
     *
     */
    @Test
    public void addEmployeesTest() {
        EmployeeDetailDTO employee = new EmployeeDetailDTO(employeeList.get(1));
       

        Response response = target
                .request()
                .post(Entity.entity(employee, MediaType.APPLICATION_JSON));

        EmployeeDetailDTO employeesTest = (EmployeeDetailDTO) response.readEntity(EmployeeDetailDTO.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(employee.getId(), employeesTest.getId());
    }

    /**
     * Prueba para obtener una colección de instancias de Employees asociadas a
     * una instancia Department
     *
     *
     */
    @Test
    public void listEmployeesTest() throws IOException {

         Response response = target
                .request().get();

        String employeesList = response.readEntity(String.class);
        List<EmployeeDTO> employeesListTest = new ObjectMapper().readValue(employeesList, List.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(2, employeesListTest.size());
    }

    /**
     * Prueba para obtener una instancia de Employees asociada a una instancia
     * Department
     *
     *
     */
    @Test
    public void getEmployeesTest() throws IOException {

        EmployeeDTO employees = new EmployeeDTO(employeeList.get(0));
      

        EmployeeDTO employeesTest = target
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

        EmployeeDTO employees = new EmployeeDTO(employeeList.get(0));
        

        Response response = target
               .path(employees.getId().toString())
                .request().delete();

        Assert.assertEquals(OkWithoutContent, response.getStatus());
    }
}
