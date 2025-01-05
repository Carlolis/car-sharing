package services;

import com.edgedb.driver.annotations.EdgeDBDeserializer;
import com.edgedb.driver.annotations.EdgeDBName;
import com.edgedb.driver.annotations.EdgeDBType;

@EdgeDBType
public class PersonJava {
	public String name;

	@EdgeDBDeserializer
	public PersonJava(@EdgeDBName("name") String name) {
		this.name = name;

	}
}
