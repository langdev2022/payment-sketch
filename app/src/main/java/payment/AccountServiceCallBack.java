package payment;

import transaction.Transaction;

public interface AccountServiceCallBack {

    void onAccountOperation(boolean status, Transaction transaction);


}
