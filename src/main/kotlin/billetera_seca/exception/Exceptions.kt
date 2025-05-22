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

//Exception when destination wallet is self
class SelfTransferException(message: String) : RuntimeException(message)

//Exception when negative amount is used
class NegativeOrZeroAmountException(message: String) : RuntimeException(message)

//Exception when password is empty
class EmptyPasswordException(message: String) : RuntimeException(message)