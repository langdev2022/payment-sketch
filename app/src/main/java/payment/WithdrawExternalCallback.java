package payment;

import transaction.Transaction;
import withdrawal.WithdrawalService;

public interface WithdrawExternalCallback {


    void onExternalOperation(WithdrawalService.WithdrawalState status, Transaction transaction);
}
