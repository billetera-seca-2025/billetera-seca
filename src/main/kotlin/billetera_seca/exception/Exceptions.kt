package billetera_seca.exception

// Exception when a user already exists
class UserAlreadyExistsException(message: String) : RuntimeException(message)

// Exception when the email format is invalid
class InvalidEmailFormatException(message: String) : RuntimeException(message)

// Exception when the password is too short
class WeakPasswordException(message: String) : RuntimeException(message)

// Exception when a user is not found
class UserNotFoundException(message: String) : RuntimeException(message)

// Exception when the wallet balance is insufficient
class InsufficientBalanceException(message: String) : RuntimeException(message)

// Exception when the wallet is not found
class WalletNotFoundException(message: String) : RuntimeException(message)