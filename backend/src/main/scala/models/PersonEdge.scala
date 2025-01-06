package models

import com.edgedb.driver.annotations.{
  EdgeDBDeserializer,
  EdgeDBName,
  EdgeDBType
}
import java.util.UUID

@EdgeDBType
case class PersonEdge @EdgeDBDeserializer() (id: UUID, name: String)
