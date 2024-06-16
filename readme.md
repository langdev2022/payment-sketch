## Ideas and Assumptions

Considering the test task as a real business case and extrapolating possible future requirements from the business, it can be assumed that business withdrawal scenarios may vary significantly (complex scenarios, with fees, different bonuses, etc.). Additionally, the number of payment/withdrawal services may vary. From a technical perspective, payment services tend to be high load and sensitive to throughput. Consider this solution as a sketch for the next product evolution, since it lacks many aspects (testing and multithread safety).

## Architecture and Design

To implement the business assumptions, it is worth isolating the building of business logic from its processing, such as switching state machines, logging, etc. Business logic should be agnostic to the dispatching engine. The ideal solution is to have a payment business flow like a graph (state machine), and a dispatching engine that accepts this graph.

In the current implementation, the execution graph is fairly simple and is implemented by descendants of transaction.TransactionStep. They are loosely coupled to each other, and the business flow can be implemented by combining existing steps and creating new ones. In a real project, I would consider a framework that covers this requirement rather than implementing it from scratch with an internal team.

To allow the solution to process a large number of transactions, it should be implemented in an event-based asynchronous approach, to avoid spending system resources and better flow control management.

The interface of every payment/withdrawal service should be adapted to the internal interface of the execution flow from the dispatching aspect. Business logic is encapsulated in particular transaction steps.

### TransactionManager

The core components of the current dispatching engine are TransactionManager, Transaction, and TransactionStep. TransactionManager is responsible for accepting and routing events for the particular transaction and switching states. A Transaction encapsulates the state and required data for the transaction. In the current implementation, the number of instances for the TransactionStep corresponds to the number of TransactionStep types. It's better to avoid creating a separate graph per transaction, as it will cause memory traffic or require more complex solutions with custom allocators, etc.

Any queries about the transaction state should be implemented in TransactionManager via call/event or both semantics (not yet implemented).


### TransactionStep

I have implemented three scenarios: internal amount move, withdrawal from external, and deposit to external. They are configured in the App class.


### Payment Providers

External systems should be agnostic to TransactionManager and the transaction flow. For the withdrawal service, a WithdrawalAdapter is implemented, which converts the interface of the withdrawal to internal requirements. The same approach is used for the account service. Despite that WithdrawalAdapter and AccountService contain Transaction as a parameter in the public interface, consider it as an operation identity (key or tag) to map requests and responses.

## More Complex Design Solutions

### Performance

A multithreaded approach with blocking and concurrent dictionaries is a relatively slow solution. To improve performance, the execution context should be single-threaded. To marshal requests to a single thread, the LMAX Disruptor or a similar approach can be used.


### Flexible Scenarios

Since accounting can also be complex and depend on the requirements, the following solutions can be considered:
    Usage of a grammar framework to describe payment grammar and compile it as usual imperative or functional code.
    Creating an LLVM frontend in payment terms.