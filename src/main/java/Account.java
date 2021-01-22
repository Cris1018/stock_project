import java.util.ArrayList;

public class Account {
    ArrayList<Share> shares_watchingList = new ArrayList<>();
    Double balance;
    String accountName;
    int accountID;
    ServerInteract database;

    public Account(Double balance, String accountName, int accountID , ServerInteract serverInteract) {
        this.balance = balance;
        this.accountName = accountName;
        this.accountID = accountID;
        this.database = serverInteract;
    }

    @Override
    public String toString() {
        return "Account{" +
                ", shares_watchingList=" + shares_watchingList +
                ", balance=" + balance +
                ", accountName='" + accountName + '\'' +
                ", accountID=" + accountID +
                '}';
    }
}
