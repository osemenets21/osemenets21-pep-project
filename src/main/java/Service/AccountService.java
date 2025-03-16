package Service;  // Ensure this matches your package structure

import DAO.AccountDAO;
import Model.Account;
import org.mindrot.jbcrypt.BCrypt;

public class AccountService {
    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    /**
     * Registers a new user with secure password hashing.
     */
    public Account register(Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank()) {
            System.out.println("Error: Username cannot be blank.");
            return null;
        }

        if (account.getPassword() == null || account.getPassword().length() < 4) {
            System.out.println("Error: Password must be at least 4 characters long.");
            return null;
        }

        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            System.out.println("Error: Username already exists.");
            return null;
        }

        // Hash the password before storing
        String hashedPassword = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setPassword(hashedPassword);

        if (accountDAO.createAccount(account)) {
            return account;
        }

        System.out.println("Error: Account creation failed.");
        return null;
    }

    /**
     * Verifies login credentials securely.
     */
    public Account login(String username, String password) {
        Account account = accountDAO.getAccountByUsername(username);
        if (account == null) {
            System.out.println("Error: Username not found.");
            return null;
        }

        // Check password against stored hashed password
        if (!BCrypt.checkpw(password, account.getPassword())) {
            System.out.println("Error: Incorrect password.");
            return null;
        }

        return account;
    }
}
