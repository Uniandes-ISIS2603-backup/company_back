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
package co.edu.uniandes.csw.company.persistence;

import co.edu.uniandes.csw.company.entities.EmployeeEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Stateless
public class EmployeePersistence {

    private static final Logger LOGGER = Logger.getLogger(EmployeePersistence.class.getName());

    @PersistenceContext(unitName = "CompanyPU")
    protected EntityManager em;

    public EmployeeEntity find(Long id) {
        LOGGER.log(Level.INFO, "Consultando employee con id={0}", id);
        return em.find(EmployeeEntity.class, id);
    }

    public EmployeeEntity findByName(String name) {
        LOGGER.log(Level.INFO, "Consultando employee con name= ", name);
        TypedQuery<EmployeeEntity> q
                = em.createQuery("select u from EmployeeEntity u where u.name = :name", EmployeeEntity.class);
        q = q.setParameter("name", name);
        return q.getSingleResult();
    }

    public List<EmployeeEntity> findAll() {
        LOGGER.info("Consultando todos los employees");
        Query q = em.createQuery("select u from EmployeeEntity u");
        return q.getResultList();
    }

    public EmployeeEntity create(EmployeeEntity entity) {
        LOGGER.info("Creando un employee nuevo");
        em.persist(entity);
        LOGGER.info("Employee creado");
        return entity;
    }

    public EmployeeEntity update(EmployeeEntity entity) {
        LOGGER.log(Level.INFO, "Actualizando employee con id={0}", entity.getId());
        return em.merge(entity);
    }

    public void delete(Long id) {
        LOGGER.log(Level.INFO, "Borrando employee con id={0}", id);
        EmployeeEntity entity = em.find(EmployeeEntity.class, id);
        em.remove(entity);
    }
}
