/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.emremote.Empeople;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.enterprise.context.RequestScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author JHS-Home
 */
@Path("Cal")
@RequestScoped
public class CalResource {

    private EmpeopleFacadeRemote peopleFacade;
    private FacebookManagerRemote facebookManager;

    /**
     * Creates a new instance of CalResource
     */
    public CalResource() {
        Context context = null;
        try {
            context = (Context) new InitialContext();

            peopleFacade = (EmpeopleFacadeRemote) context.lookup("com.mycompany.EmpeopleFacadeRemote");
            facebookManager = (FacebookManagerRemote) context.lookup("com.mycompany.FacebookManagerRemote");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves representation of an instance of com.mycompany.CalResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@QueryParam("id") String idName, @QueryParam("cal") String cal) {
        //TODO return proper representation object
        List<Empeople> list = peopleFacade.findAll();
        Empeople up = null;
        for (Empeople p : list) {
            if (p.getId().equals((long)Integer.parseInt(idName))) {
                up = p;
                break;
            }
        }
        up.setCalorie(cal);
        peopleFacade.edit(up);

        try {
            facebookManager.postMessageOnFacebook(up.getToken(), up.getFilename());
        } catch (IOException ex) {
            Logger.getLogger(JoinServiceResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        //return (peopleFacade.findResultWithID_PW(idName, pw)) ? "success" : "false";
        return "success";
    }
    /**
     * PUT method for updating or creating an instance of CalResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */

}
