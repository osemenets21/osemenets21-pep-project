package Service;  // Ensure this matches your package structure

import DAO.AccountDAO;  // Import the AccountDAO class
import Model.Account;  // Import the Account class


public class AccountService {
    private AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public Account register(Account account) {
        if (account.getUsername() == null || account.getUsername().isBlank() ||
            account.getPassword() == null || account.getPassword().length() < 4 ||
            accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return null;
        }
        if (accountDAO.createAccount(account)) {
            return account;
        }
        return null;
    }

    public Account login(String username, String password) {
        return accountDAO.verifyLogin(username, password);
    }
}
