package TDD_Exercise;

import TDD_Lab.Product;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class ChainblockImplTest {
    private static final Transaction[] TRANSACTIONS =
            { new TransactionImpl(1001, TransactionStatus.SUCCESSFUL, "Ivan", "Misho", 83.98),
            new TransactionImpl(1003, TransactionStatus.FAILED, "Pesho", "Ivan", 24.07),
            new TransactionImpl(1006, TransactionStatus.FAILED, "Sasho", "Ivan", 124.07),
            new TransactionImpl(1005, TransactionStatus.FAILED, "Sasho", "Ivan", 124.07),
            new TransactionImpl(1002, TransactionStatus.FAILED, "Simeon", "Kiril", 1.1),
            new TransactionImpl(1004, TransactionStatus.UNAUTHORIZED, "Sasho", "Kiril", 324.07)
    };

    private static final Transaction NOT_AVAILABLE_TRANSACTION = new TransactionImpl
            (555353, TransactionStatus.SUCCESSFUL, "Simo", "Kiro", 65.07);

    private static final Transaction AVAILABLE_TRANSACTION = new TransactionImpl
            (1002, TransactionStatus.FAILED, "Simeon", "Kiril", 24.07);


    private Chainblock chainblock;

    private Map<Integer, Transaction> returnMapAndFillChainblockWithTransactions(Chainblock chainblock) {

        Map<Integer, Transaction> transactionMap = new LinkedHashMap<>();

        for (int i = 0; i < TRANSACTIONS.length; i++) {
            transactionMap.putIfAbsent(TRANSACTIONS[i].getId(), TRANSACTIONS[i]);
            chainblock.add(TRANSACTIONS[i]);
        }

        return transactionMap;
    }

    private static <T> List<T> fillListFromCollection(Iterable<T> iterable) {
        Assert.assertNotNull(iterable);

        List<T> elements = new ArrayList<>();
        for (T e : iterable) {
            elements.add(e);
        }
        return elements;
    }

    @Before
    public void start() {
        this.chainblock = new ChainblockImpl();
    }

    @Test
    public void addCommandShouldAddTransactionsToTheCollectionAndNotAddTransactionsWithEqualsId() {
        Transaction transaction = new TransactionImpl
                (1, TransactionStatus.SUCCESSFUL, "Georgi", "Dido", 25.8);

        Assert.assertFalse(this.chainblock.contains(transaction));
        this.chainblock.add(transaction);
        this.chainblock.add(transaction);
        Assert.assertEquals(1, this.chainblock.getTransactionsMap().size());
    }

    @Test
    public void containsMethodShouldReturnTrueIfTransactionIsPresentInRecord() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        Assert.assertTrue(this.chainblock.contains(AVAILABLE_TRANSACTION));
    }

    @Test
    public void containsMethodShouldReturnFalseIfTransactionDoNotPresentInRecord() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        Assert.assertFalse(this.chainblock.contains(NOT_AVAILABLE_TRANSACTION));
    }

    @Test
    public void containsMethodShouldReturnTrueIfIdIsPresentInRecord() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        Assert.assertTrue(this.chainblock.contains(AVAILABLE_TRANSACTION.getId()));
    }

    @Test
    public void containsMethodShouldReturnFalseIfIdDoNotPresentInRecord() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        Assert.assertFalse(this.chainblock.contains(NOT_AVAILABLE_TRANSACTION.getId()));
    }

    @Test
    public void getCountMethodShouldReturnNumberOfTransactionsInTheRecord() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        Assert.assertEquals(transactionMap.size(), this.chainblock.getCount());
    }

    @Test
    public void testShouldChangeTransactionStatus() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus newStatus = TransactionStatus.UNAUTHORIZED;

        this.chainblock.changeTransactionStatus(AVAILABLE_TRANSACTION.getId(), newStatus);

        Assert.assertEquals(newStatus,
                this.chainblock.getTransactionsMap().get(AVAILABLE_TRANSACTION.getId()).getStatus());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testShouldThrowExceptionIfNoSuchTransactionInRecord() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        this.chainblock.changeTransactionStatus(NOT_AVAILABLE_TRANSACTION.getId(), TransactionStatus.FAILED);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testShouldThrowExceptionWhenTryToRemoveTransactionWithInvalidId() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        this.chainblock.removeTransactionById(NOT_AVAILABLE_TRANSACTION.getId());
    }

    @Test
    public void testShouldRemoveTransactionFromRecord() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        this.chainblock.removeTransactionById(AVAILABLE_TRANSACTION.getId());

        Assert.assertEquals(transactionMap.size() - 1, this.chainblock.getCount());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testShouldThrowExceptionWhenNoTransactionWithGivenId() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        this.chainblock.getById(NOT_AVAILABLE_TRANSACTION.getId());
    }

    @Test
    public void testShouldReturnTransactionByGivenId() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        Transaction actualTransaction = this.chainblock.getById(AVAILABLE_TRANSACTION.getId());
        Assert.assertNotNull(actualTransaction);
        Assert.assertEquals(AVAILABLE_TRANSACTION.getId(), actualTransaction.getId());

        //!!!!!!!!! Мога ли да сравня двата мапа по стойностите им ??????
    }

    @Test
    public void returnTransactionsByStatusAndOrderedByAmountDescending() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus status = TransactionStatus.FAILED;
        List<Transaction> expectedTransactions = transactionMap
                .values()
                .stream()
                .filter(transaction -> transaction.getStatus().equals(status))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> transactions = this.chainblock.getByTransactionStatus(status);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertEquals(expectedTransactions, actualTransactions);

    }

    @Test (expected = IllegalArgumentException.class)
    public void throwsExceptionWhenNoTransactionsWithGivenStatus() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        this.chainblock.getByTransactionStatus(TransactionStatus.ABORTED);
    }

    @Test
    public void returnsCollectionOfAllSendersForRequiredTransactionStatus() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus status = TransactionStatus.FAILED;

        List<String> expectedSenders = transactionMap
                .values()
                .stream()
                .filter(t -> t.getStatus().equals(status))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .map(Transaction::getFrom)
                .collect(Collectors.toList());

        Iterable<String> transactions = this.chainblock.getAllSendersWithTransactionStatus(status);

        List<String> actualSenders = fillListFromCollection(transactions);

        Assert.assertEquals(expectedSenders, actualSenders);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDoNotHaveSendersWithRequiredStatusTransactions() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus status = TransactionStatus.ABORTED;
        this.chainblock.getAllSendersWithTransactionStatus(status);
    }

    @Test
    public void returnsCollectionOfAllReceiversForRequiredTransactionStatus() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus status = TransactionStatus.FAILED;

        List<String> expectedReceivers = transactionMap
                .values()
                .stream()
                .filter(t -> t.getStatus().equals(status))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .map(Transaction::getTo)
                .collect(Collectors.toList());

        Iterable<String> transactions = this.chainblock.getAllReceiversWithTransactionStatus(status);

        List<String> actualReceivers = fillListFromCollection(transactions);

        Assert.assertEquals(expectedReceivers, actualReceivers);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDoNotHaveReceiversWithRequiredStatusTransactions() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus status = TransactionStatus.ABORTED;
        this.chainblock.getAllReceiversWithTransactionStatus(status);
    }

    @Test
    public void sortAllTransactionsByAmountInDescendingAndThenById() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        List<Transaction> expectedSorting = transactionMap.values()
                .stream()
                .sorted(Comparator.comparing(Transaction::getAmount)
                        .reversed()
                        .thenComparing(Transaction::getId))
                .collect(Collectors.toList());

        Iterable<Transaction> transactions = this.chainblock.getAllOrderedByAmountDescendingThenById();

        List<Transaction> actualSorting = fillListFromCollection(transactions);

        Assert.assertEquals(expectedSorting, actualSorting);
    }

    @Test
    public void filterAllTransactionsBySenderAndSortByAmountInDescending() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String sender = "Sasho";

        List<Transaction> expectedTransactions = transactionMap
                .values()
                .stream()
                .filter(t -> t.getFrom().equals(sender))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> transactions = this.chainblock.getBySenderOrderedByAmountDescending(sender);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertEquals(expectedTransactions, actualTransactions);
    }

    @Test (expected = IllegalArgumentException.class)
    public void throwsExceptionIfNotMatchedTransactionsBySender() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String sender = "John";
        Iterable<Transaction> transactions = this.chainblock.getBySenderOrderedByAmountDescending(sender);

    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenEmptyCollectionIsReturned() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String receiver = "John";
        Iterable<Transaction> transactions = this.chainblock.getByReceiverOrderedByAmountThenById(receiver);
    }

    @Test
    public void filtersAllTransactionsByReceiverThenSortingByAmountDescendingAndByIdAscending() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String receiver = "Ivan";

        List<Transaction> expectedTransactions = transactionMap
                .values()
                .stream()
                .filter(t -> t.getTo().equals(receiver))
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()
                        .thenComparing(Transaction::getId))
                .collect(Collectors.toList());

        Iterable<Transaction> transactions = this.chainblock.getByReceiverOrderedByAmountThenById(receiver);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertEquals(expectedTransactions, actualTransactions);
    }

    @Test
    public void returnsAllTransactionsByStatusAndMaximumAmount() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus status = TransactionStatus.FAILED;
        double maximumAllowedAmount = 220.56;

        List<Transaction> expectedTransactions = transactionMap
                .values()
                .stream()
                .filter(t -> t.getStatus().equals(status) && t.getAmount() <= maximumAllowedAmount)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> transactions =
                this.chainblock.getByTransactionStatusAndMaximumAmount(status, maximumAllowedAmount);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertEquals(expectedTransactions, actualTransactions);
    }

    @Test
    public void returnsEmptyCollectionIfNoMatchedTransactionsByStatus() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        TransactionStatus status = TransactionStatus.ABORTED;
        double maximumAllowedAmount = 220.56;
        Iterable<Transaction> transactions =
                this.chainblock.getByTransactionStatusAndMaximumAmount(status, maximumAllowedAmount);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertTrue(actualTransactions.isEmpty());
    }

    @Test
    public void returnsAllTransactionsBySenderAndMinimumAmountDescending() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String sender = "Sasho";
        double minimumAllowedAmount = 21.44;

        List<Transaction> expectedTransactions = transactionMap
                .values()
                .stream()
                .filter(t -> t.getFrom().equals(sender) && t.getAmount() > minimumAllowedAmount)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed())
                .collect(Collectors.toList());

        Iterable<Transaction> transactions =
                this.chainblock.getBySenderAndMinimumAmountDescending(sender, minimumAllowedAmount);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertEquals(expectedTransactions, actualTransactions);
    }

    @Test (expected = IllegalArgumentException.class)
    public void returnsExceptionIfNoMatchedTransactionsFoundBySender() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String sender = "John";
        double minimumAllowedAmount = 21.44;

        Iterable<Transaction> transactions =
                this.chainblock.getBySenderAndMinimumAmountDescending(sender, minimumAllowedAmount);
    }

    @Test
    public void returnsAllTransactionsByReceiverAndAmountOfRange() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String receiver = "Ivan";
        double minimumAmount = 21.44;
        double maximumAmount = 212.44;

        List<Transaction> expectedTransactions = transactionMap
                .values()
                .stream()
                .filter(t -> t.getTo().equals(receiver) && t.getAmount() >= minimumAmount && t.getAmount() < maximumAmount)
                .sorted(Comparator.comparing(Transaction::getAmount).reversed()
                        .thenComparing(Transaction::getId))
                .collect(Collectors.toList());

        Iterable<Transaction> transactions =
                this.chainblock.getByReceiverAndAmountRange(receiver, minimumAmount, maximumAmount);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertEquals(expectedTransactions, actualTransactions);
    }

    @Test (expected = IllegalArgumentException.class)
    public void returnsExceptionIfNoMatchedTransactionsFoundByReceiver() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        String receiver = "John";
        double minimumAmount = 21.44;
        double maximumAmount = 212.44;

        Iterable<Transaction> transactions =
                this.chainblock.getByReceiverAndAmountRange(receiver, minimumAmount, maximumAmount);
    }

    @Test
    public void returnsAllTransactionsInGivenRange() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        double lo = 14.66;
        double hi = 220.56;

        List<Transaction> expectedTransactions = transactionMap
                .values()
                .stream()
                .filter(t -> t.getAmount() >= lo && t.getAmount() <= hi)
                .collect(Collectors.toList());

        Iterable<Transaction> transactions =
                this.chainblock.getAllInAmountRange(lo, hi);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertEquals(expectedTransactions, actualTransactions);
    }

    @Test
    public void returnsEmptyCollectionIfNoMatchedTransactionsInGivenRange() {
        Map<Integer, Transaction> transactionMap = returnMapAndFillChainblockWithTransactions(this.chainblock);

        double lo = 514.66;
        double hi = 720.56;

        Iterable<Transaction> transactions =
                this.chainblock.getAllInAmountRange(lo, hi);

        List<Transaction> actualTransactions = fillListFromCollection(transactions);

        Assert.assertTrue(actualTransactions.isEmpty());
    }




}
