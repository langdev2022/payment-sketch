package transaction.flow;

import payment.AccountService;
import transaction.Transaction;
import transaction.TransactionStep;

public class InternalBlock extends TransactionStep {

    private final AccountService accountService;
    private final TransactionStep nextStep;

    public InternalBlock(AccountService accountService, TransactionStep nextStep){
        this.accountService = accountService;
        this.nextStep = nextStep;
    }


    @Override
    public TransactionStep getNext(Object response, Transaction transaction) {
        boolean success = (boolean)response;
        return nextStep;
    }

    @Override
    public void exec(Transaction transaction) {
        accountService.block((AccountService.AccountId) transaction.source, transaction.amount, transaction);
    }
}
