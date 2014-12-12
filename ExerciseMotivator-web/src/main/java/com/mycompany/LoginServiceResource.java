/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
@Path("LoginService")
public class LoginServiceResource {

    private EmpeopleFacadeRemote peopleFacade;
    private FaceManagerRemote faceManager;
    /**
     * Creates a new instance of LoginServiceResource
     */
    public LoginServiceResource() {
        Context context = null;
        if (peopleFacade == null) {
            try {
                
                context = (javax.naming.Context) new InitialContext();
                peopleFacade = (EmpeopleFacadeRemote) context.lookup("com.mycompany.EmpeopleFacadeRemote");
                faceManager = (FaceManagerRemote)context.lookup("com.mycompany.FaceManagerRemote");
            } catch (Exception e) {
                System.out.println("Error");
            }
        }

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String login(@QueryParam("id") String idName, @QueryParam("pw") String pw) {
        //TODO return proper representation object
        System.out.println("Login Success id : " + idName + " pw " + pw);
        //
       int id; 
        //return "<h3> SUccess " +idName + pw+ "</h3>";
        if ((id = peopleFacade.findResultWithID_PW(idName, pw)) ==-1) 
            return "false";
        else{
            String newId = Integer.toString(id);
            return newId;
        }
            
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public String joinMember(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        //TODO return proper representation object
        System.out.println("File Post !!!");
        String fileName = fileDetail.getFileName();
        
        String fileLocation = "C://LoginFile/" + fileName;
        String [] splitLabel = fileName.split("\\.");
       
        
        saveToFile(uploadedInputStream, fileLocation);
        int label = Integer.parseInt(splitLabel[0]);
        int predictLabel = faceManager.predict(fileLocation);
        if(label == predictLabel)
            return "success";
        else
            return "fail";
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
