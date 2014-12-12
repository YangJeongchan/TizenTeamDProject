/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.emremote.Empeople;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.naming.Context;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 * REST Web Service
 *
 * @author JHS-Home
 */
@Path("JoinService")
public class JoinServiceResource {

    private EmpeopleFacadeRemote peopleFacade;

    FaceManagerRemote faceManager;

    FacebookManagerRemote facebookManager;

    /**
     * Creates a new instance of JoinServiceResource
     */
    public JoinServiceResource() {
        Context context = null;
        try {
            context = (Context) new InitialContext();

            peopleFacade = (EmpeopleFacadeRemote) context.lookup("com.mycompany.EmpeopleFacadeRemote");
            facebookManager = (FacebookManagerRemote) context.lookup("com.mycompany.FacebookManagerRemote");
            faceManager = (FaceManagerRemote) context.lookup("com.mycompany.FaceManagerRemote");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@QueryParam("id") String idName, @QueryParam("pw") String pw, @QueryParam("token") String token, @QueryParam("fileName") String fileName) {
        //TODO return proper representation object
        System.out.println("Join Get Success id : " + idName + " pw =" + pw + "token =" + token + "filename =" + fileName);
        Empeople newP = new Empeople();
        newP.setIdname(idName);
        newP.setPw(pw);

        // String longToken = facebookManager.getLongAccessToken(token);
        String longToken = facebookManager.getLongAccessToken(token);
        newP.setToken(longToken);
        newP.setFilename(fileName);
        
        peopleFacade.create(newP);
        int id = peopleFacade.findResultWithID_PW(idName, pw);
        //return "<h3> SUccess " +idName + pw+ "</h3>";
        //return (peopleFacade.findResultWithID_PW(idName, pw)) ? "success" : "false";
        return Integer.toString(id);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String joinMember(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        //TODO return proper representation object
        System.out.println("File Post !!!");
        
        String fileLocation = "C://JoinFile/" + fileDetail.getFileName();
        String [] sp = fileDetail.getFileName().split("-");
        String idName =(sp[0]);
        Empeople updateP = peopleFacade.getPeopleById(idName);
        
        String[] ssplit = fileDetail.getFileName().split("\\.");
        String combineFileName = "C://JoinFile/" + ssplit[0]+"combined.jpg";
        updateP.setFilename(combineFileName);
        
        
        peopleFacade.edit(updateP);
        
        
        saveToFile(uploadedInputStream, fileLocation);
        try {
            faceManager.makeCompositePic(fileLocation);
        } catch (IOException ex) {
            Logger.getLogger(JoinServiceResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Post Success");
        
//        try {
//            facebookManager.postMessageOnFacebook(updateP.getToken(), updateP.getFilename());
//        } catch (IOException ex) {
//            Logger.getLogger(JoinServiceResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        return "success";
        
    }

    private void saveToFile(InputStream uploadedInputStream,
            String uploadedFileLocation) {

        try {
            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
