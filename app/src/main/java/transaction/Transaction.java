package transaction;


public class Transaction{

    public  TransactionStep state;
    public final TransactionId id;
    public final long amount;

    public final Object source;
    public final Object destination;

    public Transaction(TransactionId transactionId, Object source, Object destination,
                       TransactionStep initial, long amount){

        this.id = transactionId;
        this.state = initial;
        this.amount = amount;
        this.source = source;
        this.destination = destination;
    }

    public record TransactionId(String id){}
}
