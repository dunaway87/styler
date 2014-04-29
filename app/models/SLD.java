package models;

import java.util.UUID;

import javax.persistence.Entity;

import play.db.jpa.Model;




@Entity
public class SLD extends Model{

	public String uuid;
	public String tablename;
	public String columnName;

}
