package payment;

import transaction.Transaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AccountService {

    private volatile AccountServiceCallBack callback;
    private final ExecutorService executor;

    public AccountService() {
        this.executor = Executors.newFixedThreadPool(3);
    }

    private final ConcurrentHashMap<AccountId, Account> accounts = new ConcurrentHashMap<>();

    public void setCallback(AccountServiceCallBack callback) {
        this.callback = callback;
    }

    public void block(AccountId accountId, long amount, Transaction transaction) {
        Account account = accounts.get(accountId);

        boolean result;

        synchronized (account) {
            if (account.deposit >= amount) {
                account.blocked += amount;
                account.deposit -= amount;
                result = true;
            } else {
                result = false;
            }
        }

        executor.execute(() -> callback.onAccountOperation(result, transaction));
    }


    public void revertBlock(AccountId accountId, long amount, Transaction transaction) {
        Account account = accounts.get(accountId);

        synchronized (account) {

            account.blocked -= amount;
            account.deposit += amount;

        }

        executor.execute(() -> callback.onAccountOperation(true, transaction));
    }


    public void move(AccountId sourceId, AccountId destinationId, long amount, Transaction transaction) {
        Account source = accounts.get(sourceId);
        Account destination = accounts.get(destinationId);

        boolean result;

        synchronized (source) {
            if (source.deposit >= amount) {
                destination.deposit += amount;
                source.deposit -= amount;
                result = true;
            } else {
                result = false;
            }
        }

        executor.execute(() -> callback.onAccountOperation(result, transaction));
    }

    public void deposit(AccountId accountId, long amount, Transaction transaction) {
        Account account = accounts.get(accountId);

        synchronized (account) {
            account.deposit += amount;
        }

        executor.execute(() -> callback.onAccountOperation(true, transaction));
    }


    public void withdraw(AccountId accountId, long amount, Transaction transaction) {
        Account account = accounts.get(accountId);

        boolean result;

        synchronized (account) {
            if (account.blocked >= amount) {
                account.blocked -= amount;
                result = true;
            } else {
                result = false;
            }
        }

        executor.execute(() -> callback.onAccountOperation(result, transaction));

    }

    public AccountId createAccount(String accountId, long deposit) {

        AccountId id = new AccountId(accountId);
        Account acc = new Account(id, deposit);
        accounts.put(id, acc);
        return id;
    }

    public record AccountId(String id) {
    }

    public static class Account {

        public final AccountId id;
        public volatile long deposit;
        public volatile long blocked;

        public Account(AccountId id, long deposit) {
            this.id = id;
            this.deposit = deposit;

        }
    }

}
