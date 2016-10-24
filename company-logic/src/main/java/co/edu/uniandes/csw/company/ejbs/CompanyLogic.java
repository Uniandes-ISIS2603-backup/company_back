package co.edu.uniandes.csw.company.ejbs;

import co.edu.uniandes.csw.company.api.ICompanyLogic;
import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.persistence.CompanyPersistence;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class CompanyLogic implements ICompanyLogic {

    @Inject
    private CompanyPersistence persistence;

    /**
     * Obtiene la lista de los registros de Company.
     *
     * @return Colección de objetos de CompanyEntity.
     *
     */
    @Override
    public List<CompanyEntity> getCompanys() {
        return persistence.findAll();
    }

    /**
     * Obtiene los datos de una instancia de Company a partir de su ID.
     *
     * @param id Identificador de la instancia a consultar
     * @return Instancia de CompanyEntity con los datos del Company consultado.
     *
     */
    public CompanyEntity getCompany(Long id) {
        return persistence.find(id);
    }

    /**
     * Se encarga de crear un Company en la base de datos.
     *
     * @param entity Objeto de CompanyEntity con los datos nuevos
     * @return Objeto de CompanyEntity con los datos nuevos y su ID.
     *
     */
    @Override
    public CompanyEntity createCompany(CompanyEntity entity) {
        persistence.create(entity);
        return entity;
    }

    /**
     * Actualiza la información de una instancia de Company.
     *
     * @param entity Instancia de CompanyEntity con los nuevos datos.
     * @return Instancia de CompanyEntity con los datos actualizados.
     *
     */
    @Override
    public CompanyEntity updateCompany(CompanyEntity entity) {
        return persistence.update(entity);
    }

    /**
     * Elimina una instancia de Company de la base de datos.
     *
     * @param id Identificador de la instancia a eliminar.
     *
     */
    @Override
    public void deleteCompany(Long id) {
        persistence.delete(id);
    }

    @Override
    public CompanyEntity getCompanyByName(String name) {
        return persistence.findByName(name);
    }

    @Override
    public Integer getNumberOfEmployeesCompany(Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
