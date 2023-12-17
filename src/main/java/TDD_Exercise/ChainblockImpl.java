package TDD_Exercise;

import java.util.*;
import java.util.stream.Collectors;

public class ChainblockImpl implements Chainblock{

    private Map<Integer, Transaction> transactionsMap;

    public ChainblockImpl() {
        this.transactionsMap = new LinkedHashMap<>();
    }

    @Override
    public Map<Integer, Transaction> getTransactionsMap() {
        return transactionsMap;
    }

    @Override
    public int getCount() {
        return this.transactionsMap.size();
    }

    @Override
    public void add(Transaction transaction) {
        this.transactionsMap.putIfAbsent(transaction.getId(), transaction);
    }

    @Override
    public boolean contains(Transaction transaction) {
        return this.transactionsMap.containsKey(transaction.getId());

    }

    @Override
    public boolean contains(int id) {
        return this.transactionsMap.containsKey(id);
    }

    public void changeTransactionStatus(int id, TransactionStatus newStatus) {

        if(this.transactionsMap.containsKey(id)) {
            this.transactionsMap.get(id).setStatus(newStatus);
        } else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public void removeTransactionById(int id) {
        if(!this.transactionsMap.containsKey(id)) {
            throw new IllegalArgumentException();
        }

        this.transactionsMap.remove(id);
    }

    @Override
    public Transaction getById(int id) {
        if(!this.transactionsMap.containsKey(id)) {
            throw new IllegalArgumentException();
        }

        return this.transactionsMap.get(id);
    }

    @Override
    public Iterable<Transaction> getByTransactionStatus(TransactionStatus status) {

        List<Transaction> filteredTransactions = this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getStatus().equals(status))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        if(filteredTransactions.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return filteredTransactions;
    }

    @Override
    public Iterable<String> getAllSendersWithTransactionStatus(TransactionStatus status) {
         List<String> sendersList = this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getStatus().equals(status))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .map(Transaction::getFrom)
                .collect(Collectors.toList());

         if(sendersList.isEmpty()) {
             throw new IllegalArgumentException();
         }

         return sendersList;
    }

    @Override
    public Iterable<String> getAllReceiversWithTransactionStatus(TransactionStatus status) {
        List<String> receiversList = this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getStatus().equals(status))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .map(Transaction::getTo)
                .collect(Collectors.toList());

        if(receiversList.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return receiversList;
    }

    @Override
    public Iterable<Transaction> getAllOrderedByAmountDescendingThenById() {
        return this.transactionsMap
                .values()
                .stream()
                .sorted(Comparator.comparing(Transaction::getAmount)
                        .reversed()
                        .thenComparing(Transaction::getId))
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Transaction> getBySenderOrderedByAmountDescending(String sender) {
        List<Transaction> findBySenders = this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getFrom().equals(sender))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        if(findBySenders.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return findBySenders;
    }

    @Override
    public Iterable<Transaction> getByReceiverOrderedByAmountThenById(String receiver) {
        List<Transaction> findByReceivers = this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getTo().equals(receiver))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()
                        .thenComparing(Transaction::getId))
                .collect(Collectors.toList());

        if(findByReceivers.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return findByReceivers;
    }

    @Override
    public Iterable<Transaction> getByTransactionStatusAndMaximumAmount(TransactionStatus status, double amount) {

        return this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getStatus().equals(status) && t.getAmount() <= amount)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<Transaction> getBySenderAndMinimumAmountDescending(String sender, double amount) {

        List<Transaction> findBySender = this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getFrom().equals(sender) && t.getAmount() > amount)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        if(findBySender.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return findBySender;
    }

    @Override
    public Iterable<Transaction> getByReceiverAndAmountRange(String receiver, double lo, double hi) {

        List<Transaction> findByReceiver = this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getTo().equals(receiver) && t.getAmount() >= lo && t.getAmount() < hi)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()
                        .thenComparing(Transaction::getId))
                .collect(Collectors.toList());

        if(findByReceiver.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return findByReceiver;
    }

    @Override
    public Iterable<Transaction> getAllInAmountRange(double lo, double hi) {
        return this.transactionsMap
                .values()
                .stream()
                .filter(t -> t.getAmount() >= lo && t.getAmount() <= hi)
                .collect(Collectors.toList());
    }


}
