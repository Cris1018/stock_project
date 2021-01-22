import java.sql.Connection;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Transaction {
    Account account;
    Status type;
    String type_string;
    String code;
    String name;
    Double price;
    Integer amount;
    Status executionStatus;
    Boolean complete = false;
    Connection connection;
    String time;
    Share share;
    Double total_price;
    String status_string;
    Integer account_id;
    Integer transactionPrimaryKey = null;
    public Transaction(Account account, Double price, Integer amount, String code,Status type, Connection connection) {
        this.account = account;
        this.price = price;
        this.amount = amount;
        this.code = code;
        this.connection = connection;
        this.type = type;
    }

    public Transaction(String type, String code, String name, Double price, Integer amount, String time, Double total_price, String status, Integer account_id , Integer transactionPrimaryKey ) {
        this.type_string = type;
        this.code = code;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.time = time;
        this.total_price = total_price;
        this.status_string = status;
        this.account_id = account_id;
        this.transactionPrimaryKey = transactionPrimaryKey;
    }

    public Status execution() throws Exception {
        /***
         * ---- 无需手动调用
         * transaction 只保留有效的transaction
         * 只保留status 为 SUCCESS和PENDING的
         * 如果status的值为PENDING,将会被放入ServerInteract里的pending list,等待进一步处理
         */

        Share share = GetShareInfo.getShare(account,code);
        this.share = share;
        StockShare stockShares = account.database.getStockShares(code);
        Status result = null;
        if(type == Status.BUY){
            result = stockShares.buy(amount, price,share);
            // 交易成功
        }else {
            result = stockShares.sell(amount, price,share);
        }

        if(result == Status.SUCCESS){
            // 更新交易成功的时间
            executionStatus = Status.SUCCESS;
            complete = true;
            updateTheTime();
            account.database.updateTransaction(this);
        }
        if(result == Status.PENDING){
            executionStatus = Status.PENDING;
            account.database.updateTransactionPending(this);
        }
        account.database.updateStockShare(stockShares,share);
        return result;
    }

    public void PendingProcessing() throws Exception{
        /***
         * ---- 无需手动调用
         * 用于刷新pending状态
         */
        StockShare stockShares = account.database.getStockShares(code);
        share = GetShareInfo.getShare(account,code);
        // 如果状态为pending 而且类型为buy
        if(type_string.equals("BUY") && status_string.equals("PENDING")){
            // 先为account 添加一部分钱
            account.balance += price * amount;
            Status status = stockShares.buy_silence(amount, price, share);
            if (status == Status.SUCCESS){
                executionStatus = status;
                account.database.updateTransaction(this);
                account.database.updateStockShare(stockShares,share);
                System.out.printf("%s 交易成功 成交价为 : %s  交易类型为 %s\n",name,share.currentPrice,type_string);
            }else {
                // 交易失败 收回这部分钱 并不做改变
                account.balance -= price * amount;
            }
        }else if(type_string.equals("SELL") && status_string.equals("PENDING")){
            // 先为stockShare 添加一部分share
            stockShares.numberOfHolding += amount;
            Status status = stockShares.sell_silence(amount, price, share);
            if (status == Status.SUCCESS){
                executionStatus = status;
                account.database.updateTransaction(this);
                account.database.updateStockShare(stockShares,share);
                System.out.printf("%s 交易成功 成交价为 : %s  交易类型为 %s\n",name,share.currentPrice,type_string);
            }else {
                // 交易失败 收回这部分股份 并不做改变
                stockShares.numberOfHolding -= amount;
            }
        }
    }


    public void changeStatus(Status targetStatus) throws Exception{
        /***
         * 这个会接受一个 Status,并且强行转换自身status
         * 但是 出于实际考虑 这个只能实现把 pending —> success 或者是 pending -> cancel 的转变
         * 无效的转换会被忽略
         * (被设计出来只是为了测试)
         * -- 无需手动调用
         */
        if(targetStatus == Status.CANCEL && !status_string.equals("CANCEL")){
            StockShare stockShares = account.database.getStockShares(code);
            executionStatus = Status.CANCEL;
            // 如果状态为pending 而且类型为buy
            if(type_string.equals("BUY") && status_string.equals("PENDING")){
                // 先为account 添加一部分钱 并更新
                account.balance += price * amount;
                executionStatus = Status.CANCEL;
                account.database.updateTransaction(this);
                account.database.updateAccount();
                System.out.printf("SUCCESS : current account balance is %s \n:",account.balance);
            }else if(type_string.equals("SELL") && status_string.equals("PENDING")){
                // 先为stockShare 添加一部分share
                stockShares.numberOfHolding += amount;
                account.database.updateTransaction(this);
                share = GetShareInfo.getShare(account,code);
                account.database.updateStockShare(stockShares,share);
                System.out.printf("SUCCESS : current holding numbers of %s is %s \n:",stockShares.stock_name,stockShares.numberOfHolding);
            }
        }else {
            System.out.println("FAILURE : already in cancel status");
        }
    }


    public void updateTheTime(){
        /***
         * ---- 无需手动调用
         * 用于获取当前时间
         */
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        time =  sdf.format(date);
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", time='" + time + '\'' +
                ", total_price=" + total_price +
                ", status_string='" + status_string + '\'' +
                ", account_id=" + account_id +
                '}';
    }

    public static Transaction producedTransaction(Account account, Double price, Integer amount, String code, Status type, Connection connection){
        /**
         *  ---- 无需手动调用
         * 用于生成Transaction
         */
        Transaction transaction = new Transaction(account, price, amount,code,type,connection);
        transaction.updateTheTime();
        return transaction;
    }
}
