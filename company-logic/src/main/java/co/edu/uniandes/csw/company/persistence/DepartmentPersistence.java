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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import co.edu.uniandes.csw.company.entities.DepartmentEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Stateless
public class DepartmentPersistence {

    private static final Logger LOGGER = Logger.getLogger(DepartmentPersistence.class.getName());

    @PersistenceContext(unitName = "CompanyPU")
    protected EntityManager em;

    public DepartmentEntity find(Long id) {
        LOGGER.log(Level.INFO, "Consultando department con id={0}", id);
        return em.find(DepartmentEntity.class, id);
    }

    public DepartmentEntity findByName(Long companyId, String departmentName) {
        TypedQuery q = em.createQuery("select d from DepartmentEntity d  where d.company.id = :companyId and d.name = :departmentName", DepartmentEntity.class);
        q = q.setParameter("companyId", companyId);
        q = q.setParameter("departmentName", departmentName);

        List<DepartmentEntity> departmentsSimilarName = q.getResultList();
        if (departmentsSimilarName.isEmpty()) {
            return null;
        } else {
            return departmentsSimilarName.get(0);
        }

    }

    public List<DepartmentEntity> findAll() {
        LOGGER.info("Consultando todos los departments");
        Query q = em.createQuery("select u from DepartmentEntity u");
        return q.getResultList();
    }

    public List<DepartmentEntity> findAllInCompany(Long companyId) {
        LOGGER.log(Level.INFO, "Consultando todos los departments de la company id={0}", companyId);
        TypedQuery q = em.createQuery("select d from DepartmentEntity d  where d.company.id = :companyId", DepartmentEntity.class);
        q = q.setParameter("companyId", companyId);
        return q.getResultList();
    }

    public DepartmentEntity create(DepartmentEntity entity) {
        LOGGER.info("Creando un department nuevo");
        em.persist(entity);
        LOGGER.info("Department creado");
        return entity;
    }

    public DepartmentEntity update(DepartmentEntity entity) {
        LOGGER.log(Level.INFO, "Actualizando department con id={0}", entity.getId());
        return em.merge(entity);
    }

    /**
     *
     * @param id: corresponde a un id válido que existe el deptarment
     * crrespondiente en la base de datos.
     */
    public void delete(Long id) {
        LOGGER.log(Level.INFO, "Borrando department con id={0}", id);
        DepartmentEntity entity = em.find(DepartmentEntity.class, id);
        assert entity != null;
        em.remove(entity);
    }
}
