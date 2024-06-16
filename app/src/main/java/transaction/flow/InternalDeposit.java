package transaction.flow;


import payment.AccountService;
import transaction.Transaction;
import transaction.TransactionStep;

public class InternalDeposit extends TransactionStep {

    private final AccountService accountService;
    private final TransactionStep nextState;

    public InternalDeposit(AccountService accountService, TransactionStep nextState) {
        this.accountService = accountService;
        this.nextState = nextState;
    }

    @Override
    public TransactionStep getNext(Object message, Transaction context) {
        System.out.println("received " + message);
        System.out.println("request to withdraw");

        return nextState;
    }

    @Override
    public void exec(Transaction transaction) {
        accountService.deposit((AccountService.AccountId) transaction.destination, transaction.amount, transaction);
    }
}
