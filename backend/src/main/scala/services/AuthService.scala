package services

import zio._
import models._
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.time.Instant

trait AuthService {
  def register(user: UserCreate): Task[User]
  def login(credentials: UserLogin): Task[String]
  def authenticate(token: String): Task[Option[User]]
}

class AuthServiceLive(users: Ref[Map[String, User]]) extends AuthService {
  private val secretKey = "your-secret-key-here"
  private val algorithm = Algorithm.HMAC256(secretKey)

  override def register(userCreate: UserCreate): Task[User] = {
    for {
      userMap <- users.get
      _ <- ZIO
        .fail(new Exception("Username already taken"))
        .when(userMap.contains(userCreate.username))
      newUser = User(
        Some(userMap.size + 1L),
        userCreate.username,
        userCreate.email,
        userCreate.password
      )
      _ <- users.update(_ + (userCreate.username -> newUser))
    } yield newUser
  }

  override def login(credentials: UserLogin): Task[String] = {
    (for {
      userMap <- users.get
      user <- ZIO
        .fromOption(userMap.get(credentials.username))
        .orElseFail(new Exception("User not found"))
      _ <- ZIO
        .fail(new Exception("Invalid password"))
        .when(user.password != credentials.password)
      token <- ZIO.attempt(createToken(credentials.username))
    } yield token).tapError(error => ZIO.logError(error.getMessage))
  }

  override def authenticate(token: String): Task[Option[User]] = {
    ZIO.attempt {
      val verifier = JWT.require(algorithm).build()
      val decoded = verifier.verify(token)
      val username = decoded.getSubject
      users.get.map(_.get(username))
    }.flatten
  }

  private def createToken(username: String): String = {
    JWT
      .create()
      .withSubject(username)
      .withIssuedAt(Instant.now())
      .withExpiresAt(Instant.now().plusSeconds(3600)) // Token expires in 1 hour
      .sign(algorithm)
  }
}

object AuthService {
  def layer: ZLayer[Any, Nothing, AuthService] =
    ZLayer {
      for {
        ref <- Ref.make(Map.empty[String, User])
      } yield AuthServiceLive(ref)
    }
}
