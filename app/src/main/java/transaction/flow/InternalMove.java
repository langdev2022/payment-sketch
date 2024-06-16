package transaction.flow;


import payment.AccountService;
import transaction.Transaction;
import transaction.TransactionStep;

public class InternalMove extends TransactionStep {

    private final AccountService accountService;

    private final TransactionStep finalState;

    public InternalMove(AccountService accountService, TransactionStep finalState) {
        this.accountService = accountService;
        this.finalState = finalState;
    }

    @Override
    public TransactionStep getNext(Object response, Transaction transaction) {
        return finalState;
    }

    @Override
    public void exec(Transaction transaction) {
        var source = (AccountService.AccountId)transaction.source;
        var destination = (AccountService.AccountId)transaction.destination;
        accountService.move(source, destination, transaction.amount, transaction);
    }

}
