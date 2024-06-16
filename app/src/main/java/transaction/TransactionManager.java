package transaction;

import payment.WithdrawExternalCallback;
import payment.WithdrawalAdapter;
import transaction.flow.*;
import payment.AccountService;
import payment.AccountServiceCallBack;
import withdrawal.WithdrawalService;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager implements AccountServiceCallBack, WithdrawExternalCallback {

    private final AccountService accountService;
    private final WithdrawalAdapter withdrawalAdapter;

    private final TransactionStep internalTransfer;
    private final TransactionStep externalDeposit;
    private final TransactionStep externalWithdraw;

    private final ConcurrentHashMap<Transaction.TransactionId, Transaction> transactions = new ConcurrentHashMap<>();

    public TransactionManager(AccountService accountService, WithdrawalAdapter withdrawalAdapter,
                              TransactionStep internalTransfer, TransactionStep externalDeposit,
             TransactionStep externalWithdraw){
        this.accountService = accountService;
        this.accountService.setCallback(this);

        this.withdrawalAdapter = withdrawalAdapter;
        this.withdrawalAdapter.setCallback(this);

        this.internalTransfer = internalTransfer;
        this.externalDeposit = externalDeposit;
        this.externalWithdraw = externalWithdraw;
    }


    public void internalTransfer(AccountService.AccountId sourceId, AccountService.AccountId destinationId, long amount){

        Transaction.TransactionId trId =  new Transaction.TransactionId(UUID.randomUUID().toString());

        Transaction transaction = new Transaction(trId,sourceId, destinationId, internalTransfer, amount);
        transactions.put(transaction.id, transaction);
        internalTransfer.exec(transaction);
    }

    public void externalWithdraw(WithdrawalService.Address sourceAddress, AccountService.AccountId destinationAddress, long amount){

        Transaction.TransactionId trId =  new Transaction.TransactionId(UUID.randomUUID().toString());

        Transaction transaction = new Transaction(trId, sourceAddress, destinationAddress, externalWithdraw, amount);
        transactions.put(transaction.id, transaction);
        externalWithdraw.exec(transaction);
    }


    public void externalDeposit(AccountService.AccountId source, WithdrawalService.Address destination, long amount){

        Transaction.TransactionId trId =  new Transaction.TransactionId(UUID.randomUUID().toString());

        Transaction transaction = new Transaction(trId, source, destination, externalDeposit, amount);
        transactions.put(transaction.id, transaction);
        externalDeposit.exec(transaction);
    }


    @Override
    public void onAccountOperation(boolean status, Transaction transaction) {
        TransactionStep next = transaction.state.getNext(status, transaction);
        System.out.printf( "switching state from %s to %s\n",transaction.state, next );

        if (!(next instanceof FinalStep)){
            transaction.state = next;
            transaction.state.exec(transaction);
        }
    }

    @Override
    public void onExternalOperation(WithdrawalService.WithdrawalState state, Transaction transaction) {
        TransactionStep next = transaction.state.getNext(state, transaction);
        System.out.printf( "switching state from %s to %s\n",transaction.state, next );

        if (!(next instanceof FinalStep)){
            transaction.state = next;
            transaction.state.exec(transaction);
        }
    }


}

