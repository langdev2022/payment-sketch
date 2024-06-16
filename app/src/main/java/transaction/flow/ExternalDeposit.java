package transaction.flow;

import payment.WithdrawalAdapter;
import transaction.Transaction;
import transaction.TransactionStep;
import withdrawal.WithdrawalService;

public class ExternalDeposit extends TransactionStep {

    private final TransactionStep success;
    private final TransactionStep failed;

    private final WithdrawalAdapter withdrawalAdapter;

    public ExternalDeposit(WithdrawalAdapter withdrawalAdapter, TransactionStep success, TransactionStep failed) {
        this.success = success;
        this.failed = failed;
        this.withdrawalAdapter = withdrawalAdapter;
    }


    @Override
    public TransactionStep getNext(Object response, Transaction transaction) {
        WithdrawalService.WithdrawalState state = (WithdrawalService.WithdrawalState) response;

        if (state == WithdrawalService.WithdrawalState.COMPLETED) {
            return success;
        } else {
            return failed;
        }

    }

    @Override
    public void exec(Transaction context) {
        withdrawalAdapter.deposit(context);
    }

}

