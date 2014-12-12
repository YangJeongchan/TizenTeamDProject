/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.emremote.Empeople;
import com.restfb.BinaryAttachment;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author JHS-Home
 */
@Stateless
public class FacebookManager implements FacebookManagerRemote {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    String defaultFacebookPath = "https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token";
    final static String APP_SECRET = "71994217976c472c995a8f91fa44642b";
    final static String APP_ID = "302523623289784";
    final static String JoinPath = "C://JoinFile/";
    private final String blameMessage = "You are pig , lazy , you should be blamed ";

    List<Empeople> peopleList; // have access Token

    FaceManagerRemote faceManager;

    EmpeopleFacadeRemote facade;
    private ByteArrayOutputStream byteStream;

    public FacebookManager() {
        byteStream = new ByteArrayOutputStream();

        try {
            javax.naming.Context con = new InitialContext();
            facade = (EmpeopleFacadeRemote) con.lookup("com.mycompany.EmpeopleFacadeRemote");
            faceManager = (FaceManagerRemote) con.lookup("com.mycompany.FaceManagerRemote");
        } catch (NamingException ex) {
            Logger.getLogger(FacebookManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.peopleList = facade.findAll();
    }

    //@Schedule(second = "59" , minute = "59" ,hour = "23")
    @Override
    public void upLoadAllPeople() {
        for (Empeople p : peopleList) {
            try {
                FacebookClient facebookClient = new DefaultFacebookClient(p.getToken(), APP_SECRET);
                String fileName = p.getFilename();
                postMessageOnFacebook(facebookClient, fileName);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(FacebookManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FacebookManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void postMessageOnFacebook(FacebookClient client, String fileName) throws FileNotFoundException, IOException {

        byte[] image;
     
        Path path = Paths.get(fileName);
        image = Files.readAllBytes(path);
        client.publish("me/photos", FacebookType.class, BinaryAttachment.with("composite.png", image), Parameter.with("message", blameMessage));
    }
    
    @Override
    public void postMessageOnFacebook(String token, String fileName) throws FileNotFoundException, IOException {

        byte[] image;
        FacebookClient facebookClient = new DefaultFacebookClient(token, APP_SECRET);
        Path path = Paths.get(fileName);
        image = Files.readAllBytes(path);
        facebookClient.publish("me/photos", FacebookType.class, BinaryAttachment.with("composite.png", image), Parameter.with("message", blameMessage));
    }
    @Override
    public String getLongAccessToken(String accessToken) {
        String GET_PATH = defaultFacebookPath + "&client_id=" + APP_ID + "&client_secret=" + APP_SECRET + "&fb_exchange_token=" + accessToken;

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(GET_PATH);

        get.setHeader("Accept", "application/json");

        HttpEntity entity;
        try {
            HttpResponse response = client.execute(get);

            if (response.getStatusLine().getStatusCode() != 200) {
                System.out.println("GET> Unexpected status code: " + response.getStatusLine().getStatusCode());
            }

            entity = response.getEntity();
            String data = EntityUtils.toString(entity);

            String[] at = data.split("=");
            String newTok = at[1];
            at = newTok.split("&");
            
            return at[0];  //at[1] == Long accessToken get by facebook 

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
}
