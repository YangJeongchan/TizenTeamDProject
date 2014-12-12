package com.example.exercise_motivator;

import java.io.File;
import java.io.Serializable;



public class Empeople implements Serializable {
   
    private Long id;
    
    public void setId(Long id) {
		this.id = id;
	}

	private String idname;
    
    
    private String pw;
    
    
    private String token;
    
    
    private String fileName;
    
    private String calorie;
    
    public String getCalorie() {
		return calorie;
	}

	public void setCalorie(String calorie) {
		this.calorie = calorie;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Empeople() {
    }

    public Empeople(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public String getIdname() {
        return idname;
    }

    public void setIdname(String idname) {
        this.idname = idname;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empeople)) {
            return false;
        }
        Empeople other = (Empeople) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.Empeople[ id=" + id + " ]";
    }
    
}
