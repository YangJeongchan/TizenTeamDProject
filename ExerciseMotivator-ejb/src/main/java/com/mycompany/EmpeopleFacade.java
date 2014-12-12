/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany;

import com.mycompany.emremote.Empeople;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author JHS-Home
 */
@Stateless
public class EmpeopleFacade extends AbstractFacade<Empeople> implements EmpeopleFacadeRemote {
    @PersistenceContext(unitName = "com.mycompany_ExerciseMotiator-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmpeopleFacade() {
        super(Empeople.class);
    }
    @Override
    public Empeople getPeopleByIdName(String idName) {
        List<Empeople> l = findAll();
        
        for (Empeople p : findAll()) {
            if (p.getIdname().equals(idName)) {
                return p;
            }
        }
        return null;
    }
    @Override
    public Empeople getPeopleById(String id) {
        List<Empeople> l = findAll();
        System.out.println("TEst");
        for (Empeople p : findAll()) {
            if (p.getId().equals((long)Integer.parseInt(id))) {
                return p;
            }
        }
        return null;
    }
    @Override
    public int findResultWithID_PW(String id, String pw) {

        for (Empeople p : findAll()) {
            if (p.getIdname().equals(id) && p.getPw().equals(pw)) {
                return p.getId().intValue();
            }
        }
        return -1;
    }
}
