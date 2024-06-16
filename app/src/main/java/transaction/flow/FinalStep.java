package transaction.flow;

import transaction.Transaction;
import transaction.TransactionStep;

public class FinalStep extends TransactionStep {

    private final boolean success;

    public FinalStep(boolean success){
        this.success = success;
    }


    @Override
    public TransactionStep getNext(Object response, Transaction transaction) {
        return null;
    }

    @Override
    public void exec(Transaction context) {
        System.out.println("already completed");
    }
}
