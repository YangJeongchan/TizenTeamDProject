/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.emremote.Empeople;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author JHS-Home
 */
@Remote
public interface EmpeopleFacadeRemote {

    void create(Empeople empeople);

    void edit(Empeople empeople);

    void remove(Empeople empeople);

    Empeople find(Object id);

    List<Empeople> findAll();

    List<Empeople> findRange(int[] range);

    int count();

    public Empeople getPeopleById(String id);

    public int findResultWithID_PW(String id, String pw);
     public Empeople getPeopleByIdName(String idName);
}
