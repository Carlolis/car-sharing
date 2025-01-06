package models

import com.edgedb.driver.annotations.{
  EdgeDBDeserializer,
  EdgeDBName,
  EdgeDBType
}
import java.util.UUID

@EdgeDBType
class PersonEdge @EdgeDBDeserializer() (var id: UUID, var name: String)
