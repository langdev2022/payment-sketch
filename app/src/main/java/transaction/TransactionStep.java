package transaction;

public abstract class TransactionStep {

    public TransactionStep(){}

    public abstract TransactionStep getNext(Object response, Transaction transaction);

    public abstract void exec(Transaction transaction);

}
