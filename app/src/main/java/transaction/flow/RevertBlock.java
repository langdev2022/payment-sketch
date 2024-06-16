package transaction.flow;

import payment.AccountService;
import transaction.Transaction;
import transaction.TransactionStep;

public class RevertBlock extends TransactionStep {

    private final AccountService accService;
    private final TransactionStep nextStep;

    public RevertBlock(AccountService accService, TransactionStep nextStep){
        this.accService = accService;
        this.nextStep = nextStep;
    }


    @Override
    public TransactionStep getNext(Object response, Transaction transaction) {
        return nextStep;
    }

    @Override
    public void exec(Transaction context) {
        accService.revertBlock((AccountService.AccountId)context.source, context.amount, context);
    }
}
