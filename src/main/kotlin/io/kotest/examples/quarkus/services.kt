package io.kotest.examples.quarkus

import javax.enterprise.context.ApplicationScoped

data class User(val name: String)

interface UserRepository {
   fun findUser(): User
}

@ApplicationScoped
class DefaultRepository : UserRepository {
   override fun findUser(): User = User("system_user")
}

@ApplicationScoped
class UserService(val repository: DefaultRepository)

@ApplicationScoped
class MockableService {
   fun greet() = "Hello"
}
