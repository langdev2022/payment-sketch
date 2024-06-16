package payment;

import transaction.Transaction;
import withdrawal.WithdrawalService;

import java.util.UUID;
import java.util.concurrent.*;

public class WithdrawalAdapter {

    private final WithdrawalService service;

    private volatile WithdrawExternalCallback callback;
    private final ConcurrentHashMap<WithdrawalService.WithdrawalId, Container> pendingTransactions = new ConcurrentHashMap<>();

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> puller;


    private volatile boolean working;

    public WithdrawalAdapter(WithdrawalService service) {
        this.service = service;
    }

    public void setCallback(WithdrawExternalCallback callback) {
        this.callback = callback;
    }

    public void withdraw(Transaction transaction) {
        var id = new WithdrawalService.WithdrawalId(UUID.fromString(transaction.id.id()));
        service.requestWithdrawal(id, (WithdrawalService.Address) transaction.source, -transaction.amount);
        pendingTransactions.put(id, new Container(id, transaction));
    }

    public void deposit(Transaction transaction) {
        var id = new WithdrawalService.WithdrawalId(UUID.fromString(transaction.id.id()));
        service.requestWithdrawal(id, (WithdrawalService.Address) transaction.destination, transaction.amount);
        pendingTransactions.put(id, new Container(id, transaction));
    }

    public void startCheck() {
        working = true;
        puller = executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                checkPending();
            }
        }, 0, 2, TimeUnit.SECONDS);
    }

    public void stopCheck() {
        working = false;
        puller.cancel(false);

    }

    private void checkPending() {
        while (working) {
            for (Container c : pendingTransactions.values()) {

                if (!working) {
                    break;
                }

                WithdrawalService.WithdrawalState state = service.getRequestState(c.id());

                if (state != WithdrawalService.WithdrawalState.PROCESSING) {
                    pendingTransactions.remove(c.id);
                } else {
                    continue;
                }

                callback.onExternalOperation(state, c.transaction);
            }
        }
    }

    record Container(WithdrawalService.WithdrawalId id, Transaction transaction) {
    }
}
