/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import java.io.IOException;
import javax.ejb.Remote;

/**
 *
 * @author JHS-Home
 */
@Remote
public interface FaceManagerRemote {
    
    
    int predict(String fileName);
    public void makeCompositePic(String fileName) throws IOException;
}
