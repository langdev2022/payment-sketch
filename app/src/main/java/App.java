import payment.AccountService;
import transaction.TransactionManager;
import payment.WithdrawalAdapter;
import transaction.TransactionStep;
import transaction.flow.*;
import withdrawal.WithdrawalService;
import withdrawal.WithdrawalServiceStub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class App {

    public static void main(String[] args) throws IOException, InterruptedException {

        AccountService accService = new AccountService();
        WithdrawalAdapter withdrawalAdapter = new WithdrawalAdapter(new WithdrawalServiceStub());
        withdrawalAdapter.startCheck();

        TransactionStep completed = new FinalStep(true);
        TransactionStep failed = new FinalStep(false);

        RevertBlock revertBlock = new RevertBlock(accService, completed);


        TransactionStep externalDeposit = new ExternalDeposit(withdrawalAdapter, new InternalWithdraw(accService, completed), revertBlock);
        TransactionStep internalDeposit = new InternalDeposit(accService, completed);

        TransactionStep internalTransferFlow = new InternalMove(accService, completed);
        // withdraw from internal deposit to external
        TransactionStep externalDepositFlow = new InternalBlock(accService, externalDeposit);
        // withdraw from external and deposit to internal
        TransactionStep externalWithdrawFlow = new ExternalWithdraw(withdrawalAdapter, internalDeposit, failed );

        TransactionManager transactionManager = new TransactionManager(accService, withdrawalAdapter,
                internalTransferFlow, externalDepositFlow, externalWithdrawFlow
        );

        AccountService.AccountId internalOne = accService.createAccount("int-one",4000);
        AccountService.AccountId internalTwo = accService.createAccount("int-two",5000);
        AccountService.AccountId internalThree = accService.createAccount("int-three",6000);

        WithdrawalService.Address externalOne = new WithdrawalService.Address("ext-one");
        WithdrawalService.Address externalTwo = new WithdrawalService.Address("ext-two");

        transactionManager.internalTransfer(internalOne, internalTwo, 25);
        transactionManager.externalWithdraw(externalOne, internalOne, 35);
        transactionManager.externalDeposit(internalThree, externalTwo, 105);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press enter to exit");
        reader.readLine();
        System.out.println("stopping..");

        withdrawalAdapter.stopCheck();
        System.out.println("stopped");

    }
}
