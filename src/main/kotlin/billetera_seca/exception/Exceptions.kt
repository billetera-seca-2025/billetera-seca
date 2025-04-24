package billetera_seca.exception

// Exception when a user already exists
class UserAlreadyExistsException(message: String) : RuntimeException(message)

// Exception when the email format is invalid
class InvalidEmailFormatException(message: String) : RuntimeException(message)

// Exception when the password is too short
class WeakPasswordException(message: String) : RuntimeException(message)