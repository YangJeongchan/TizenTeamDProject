/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.restfb.FacebookClient;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.ejb.Remote;

/**
 *
 * @author JHS-Home
 */
@Remote
public interface FacebookManagerRemote {
    
     public String getLongAccessToken(String accessToken);
     public void postMessageOnFacebook(FacebookClient client, String fileName) throws FileNotFoundException, IOException;
     public void upLoadAllPeople() ;
     public void postMessageOnFacebook(String token, String fileName) throws FileNotFoundException, IOException;
}
