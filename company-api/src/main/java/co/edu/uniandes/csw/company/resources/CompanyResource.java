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
package co.edu.uniandes.csw.company.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import co.edu.uniandes.csw.company.api.ICompanyLogic;
import co.edu.uniandes.csw.company.dtos.CompanyDetailDTO;
import co.edu.uniandes.csw.company.entities.CompanyEntity;
import co.edu.uniandes.csw.company.exceptions.BusinessLogicException;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

@Path("companies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CompanyResource {

    @Inject
    private ICompanyLogic companyLogic;

    /**
     * Convierte una lista de CompanyEntity a una lista de CompanyDetailDTO.
     *
     * @param entityList Lista de CompanyEntity a convertir.
     * @return Lista de CompanyDetailDTO convertida.
     *
     */
    private List<CompanyDetailDTO> listEntity2DTO(List<CompanyEntity> entityList) {
        List<CompanyDetailDTO> list = new ArrayList<>();
        for (CompanyEntity entity : entityList) {
            list.add(new CompanyDetailDTO(entity));
        }
        return list;
    }

    /**
     * Obtiene la lista de los registros de Company
     *
     * @return Colección de objetos de CompanyDetailDTO
     *
     */
    @GET
    public List<CompanyDetailDTO> getCompanys() {

        return listEntity2DTO(companyLogic.getCompanys());
    }

    /**
     * Obtiene los datos de una instancia de Company a partir de su ID
     *
     * @param id Identificador de la instancia a consultar
     * @return Instancia de CompanyDetailDTO con los datos del Company
     * consultado
     *
     */
    @GET
    @Path("{id: \\d+}")
    public CompanyDetailDTO getCompany(@PathParam("id") Long id) {
        return new CompanyDetailDTO(companyLogic.getCompany(id));
    }

    /**
     * Obtiene los datos de una instancia de Company a partir de su ID
     *
     * @param id Identificador de la instancia a consultar
     * @return Instancia de CompanyDetailDTO con los datos del Company
     * consultado
     *
     */
    @GET
    @Path("name")
    public CompanyDetailDTO getCompanyByName(@QueryParam("name") String name) {
        CompanyEntity companyE = companyLogic.getCompanyByName(name);
        if (companyE == null) {
            throw new WebApplicationException("La compañía no existe", 404);
        } else {
            return new CompanyDetailDTO(companyE);
        }
    }

    /**
     * Se encarga de crear un Company en la base de datos
     *
     * @param dto Objeto de CompanyDetailDTO con los datos nuevos
     * @return Objeto de CompanyDetailDTOcon los datos nuevos y su ID
     *
     */
    @POST
    public CompanyDetailDTO createCompany(CompanyDetailDTO dto) throws BusinessLogicException {
        return new CompanyDetailDTO(companyLogic.createCompany(dto.toEntity()));
    }

    /**
     * Actualiza la información de una instancia de Company
     *
     * @param id Identificador de la instancia de Company a modificar
     * @param dto Instancia de CompanyDetailDTO con los nuevos datos
     * @return Instancia de CompanyDetailDTO con los datos actualizados
     *
     */
    @PUT
    @Path("{id: \\d+}")
    public CompanyDetailDTO updateCompany(@PathParam("id") Long id, CompanyDetailDTO dto) {
        CompanyEntity entity = dto.toEntity();
        entity.setId(id);
        return new CompanyDetailDTO(companyLogic.updateCompany(entity));
    }

    /**
     * Elimina una instancia de Company de la base de datos
     *
     * @param id Identificador de la instancia a eliminar
     *
     */
    @DELETE
    @Path("{id: \\d+}")
    public void deleteCompany(@PathParam("id") Long id) {
        companyLogic.deleteCompany(id);
    }

    @GET
    @Path("{id: \\d+}/numberofemployees")
    public Integer getNumberOfEmployeesCompany(@PathParam("id") Long id) {
        return companyLogic.getNumberOfEmployeesCompany(id);
    }
}
