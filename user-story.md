# User Stories for E-Wallet Application


This document outlines the key user stories for the e-wallet application, focusing on the core functionalities required for the mobile app, web app, and backend API.

## Account Management

*   **As a new user, I want to create a virtual account using my email and a password, so that I can start using the e-wallet.**
    *   *Acceptance Criteria:*
        *   The system validates the email format.
        *   The system requires a secure password.
        *   A unique virtual account is created and linked to the email.
        *   The user receives a confirmation (e.g., email, in-app message).
*   **As a registered user, I want to log in to my account using my email and password, so that I can access my wallet.**
    *   *Acceptance Criteria:*
        *   The system validates the credentials.
        *   Upon successful login, the user is directed to their account dashboard/overview.
*   **As a registered user, I want to view my current account balance, so that I know how much money I have available.**
    *   *Acceptance Criteria:*
        *   The balance is clearly displayed after login or on demand.
        *   The balance reflects all completed transactions accurately.
*   **As a registered user, I want to view my transaction history, so that I can track my deposits, withdrawals, and transfers.**
    *   *Acceptance Criteria:*
        *   A list of recent transactions is displayed.
        *   Each transaction entry shows the type (deposit, withdrawal, transfer), amount, date/time, and involved parties (if applicable).

## Peer-to-Peer (P2P) Transfers

*   **As a registered user, I want to send money to another registered user using their email or unique ID, so that I can transfer funds easily.**
    *   *Acceptance Criteria:*
        *   The system validates the recipient's email or ID exists.
        *   The system checks if the sender has sufficient funds.
        *   The specified amount is deducted from the sender's balance.
        *   The specified amount is added to the recipient's balance.
        *   Both users receive a notification about the transfer.
*   **As a registered user (sender), I want the P2P transfer I initiated to appear in my transaction history, so that I have a record of sending money.**
    *   *Acceptance Criteria:*
        *   The transaction history shows a debit entry with transfer details (recipient, amount, date/time).
*   **As a registered user (recipient), I want the P2P transfer I received to appear in my transaction history, so that I have a record of receiving money.**
    *   *Acceptance Criteria:*
        *   The transaction history shows a credit entry with transfer details (sender, amount, date/time).

## External Integration (Simulated)

*   **As a registered user, I want to simulate adding funds to my wallet from an external source (like a credit card or bank account), so that I can increase my balance.**
    *   *Acceptance Criteria:*
        *   The user interface provides an option to simulate adding funds.
        *   The user specifies the amount to add.
        *   The system updates the user's balance accordingly (simulation).
        *   The simulated deposit appears in the transaction history.
*   **As a registered user, I want to simulate withdrawing funds from my wallet to an external bank account, so that I can move my money out of the wallet.**
    *   *Acceptance Criteria:*
        *   The user interface provides an option to simulate withdrawing funds.
        *   The user specifies the amount to withdraw.
        *   The system checks if the user has sufficient funds.
        *   The system updates the user's balance accordingly (simulation).
        *   The simulated withdrawal appears in the transaction history.

## Non-Functional / Technical Stories (Derived from Requirements)

*   **As a developer, I want the backend API to serve both the mobile and web applications, so that we maintain a single source of business logic.**
*   **As a QA engineer, I want comprehensive unit tests for backend logic, so that we ensure individual components work correctly.**
*   **As a QA engineer, I want integration tests covering API endpoints and database interactions, so that we verify components work together.**
*   **As a QA engineer, I want end-to-end tests using Cypress (for web) and Appium (for mobile), so that we validate user flows through the actual UI.**
*   **As a performance engineer, I want stress and load tests using Locust against the API, so that we understand the system's behavior under load and identify bottlenecks.**
*   **As the development team, we want all code versioned in Git using SemVer, so that we can track changes and manage releases effectively.**
*   **As the development team, we want CI/CD pipelines in our Git provider, so that code is automatically built, tested, and packaged (Docker image) upon changes.**
*   **As the development team, we want the entire application stack defined in Docker Compose, so that development and deployment environments are consistent and easy to manage.**
