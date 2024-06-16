package transaction.flow;

import transaction.Transaction;
import transaction.TransactionStep;
import payment.WithdrawalAdapter;
import withdrawal.WithdrawalService;

public class ExternalWithdraw extends TransactionStep {

    private final TransactionStep success;
    private final TransactionStep failed;

    private final WithdrawalAdapter withdrawalAdapter;

    public ExternalWithdraw(WithdrawalAdapter withdrawalAdapter, TransactionStep success, TransactionStep failed){
        this.success = success;
        this.failed = failed;
        this.withdrawalAdapter = withdrawalAdapter;
    }

    @Override
    public TransactionStep getNext(Object response, Transaction context) {
        WithdrawalService.WithdrawalState state = (WithdrawalService.WithdrawalState)response;
        return state == WithdrawalService.WithdrawalState.COMPLETED?success:failed;
    }

    @Override
    public void exec(Transaction transaction) {
        withdrawalAdapter.withdraw(transaction);
    }
}

