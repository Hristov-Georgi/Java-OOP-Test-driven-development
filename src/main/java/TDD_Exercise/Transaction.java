package TDD_Exercise;

public interface Transaction {

    int getId();

    TransactionStatus getStatus();

    void setStatus(TransactionStatus status);

    double getAmount();

    String getFrom();

    String getTo();

}
