package services

import models._
import zio._
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}
import java.time.Instant

trait AuthService {
  def register(userCreate: UserCreate): Task[User]
  def login(credentials: UserLogin): Task[String]
  def authenticate(token: String): Task[Option[User]]
}

case class AuthServiceLive() extends AuthService {
  private val secretKey = "your-secret-key" // À changer en production
  private val algorithm = JwtAlgorithm.HS256
  
  // TODO: Implement actual database storage
  private var users: List[User] = List.empty

  override def register(userCreate: UserCreate): Task[User] = {
    ZIO.succeed {
      val newUser = User(
        id = Some(users.length + 1L),
        username = userCreate.username,
        email = userCreate.email,
        password = userCreate.password // TODO: Hash password
      )
      users = users :+ newUser
      newUser
    }
  }

  override def login(credentials: UserLogin): Task[String] = {
    for {
      userOpt <- ZIO.succeed(users.find(u => 
        u.username == credentials.username && 
        u.password == credentials.password // TODO: Verify hashed password
      ))
      user <- ZIO.fromOption(userOpt).orElseFail(new Exception("Invalid credentials"))
      token = createToken(user.username)
    } yield token
  }

  override def authenticate(token: String): Task[Option[User]] = {
    ZIO.succeed {
      // TODO: Implement proper token verification
      users.headOption
    }
  }

  private def createToken(username: String): String = {
    val claim = JwtClaim(
      expiration = Some(Instant.now.plusSeconds(3600).getEpochSecond),
      issuedAt = Some(Instant.now.getEpochSecond),
      subject = Some(username)
    )
    JwtCirce.encode(claim, secretKey, algorithm)
  }
}

object AuthService {
  val layer: ULayer[AuthService] = ZLayer.succeed(AuthServiceLive())
}
