package transaction.flow;


import payment.AccountService;
import transaction.Transaction;
import transaction.TransactionStep;

public class InternalWithdraw extends TransactionStep {

    private final AccountService accountService;
    private final TransactionStep nextState;

    public InternalWithdraw(AccountService accountService, TransactionStep nextState) {
        this.accountService = accountService;
        this.nextState = nextState;
    }


    @Override
    public TransactionStep getNext(Object response, Transaction transaction) {
        System.out.println("received " + response);
        System.out.println("request to withdraw");

        return nextState;
    }

    @Override
    public void exec(Transaction transaction) {
        accountService.withdraw((AccountService.AccountId) transaction.source, transaction.amount, transaction);
    }
}