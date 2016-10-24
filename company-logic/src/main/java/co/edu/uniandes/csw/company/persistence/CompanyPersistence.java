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
import co.edu.uniandes.csw.company.entities.CompanyEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

@Stateless
public class CompanyPersistence {

    private static final Logger LOGGER = Logger.getLogger(CompanyPersistence.class.getName());

    @PersistenceContext(unitName = "CompanyPU")
    protected EntityManager em;

    public CompanyEntity find(Long id) {
        LOGGER.log(Level.INFO, "Consultando company con id={0}", id);
        return em.find(CompanyEntity.class, id);
    }

    public CompanyEntity findByName(String name) {
        LOGGER.log(Level.INFO, "Consultando company con name = {0}", name);
        TypedQuery<CompanyEntity> q
                = em.createQuery("select u from CompanyEntity u where u.name = :name", CompanyEntity.class);
        q = q.setParameter("name", name);
        
       List<CompanyEntity> companiesSimilarName = q.getResultList();
        if (companiesSimilarName.isEmpty() ) {
            return null; 
        } else {
            return companiesSimilarName.get(0);
        }
    }

    public List<CompanyEntity> findAll() {
        LOGGER.info("Consultando todos los companys");
        Query q = em.createQuery("select u from CompanyEntity u");
        return q.getResultList();
    }

    public CompanyEntity create(CompanyEntity entity) {
        LOGGER.info("Creando un company nuevo " + entity.getName());
        em.persist(entity);
        
        return entity;
    }

    public CompanyEntity update(CompanyEntity entity) {
        LOGGER.log(Level.INFO, "Actualizando company con id={0}", entity.getId());
        return em.merge(entity);
    }

    public void delete(Long id) {
        LOGGER.log(Level.INFO, "Borrando company con id={0}", id);
        CompanyEntity entity = em.find(CompanyEntity.class, id);
        em.remove(entity);
    }
}
