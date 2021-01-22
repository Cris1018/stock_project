import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.*;
import java.util.ArrayList;

public class ServerInteract {
    Connection connection;
    Account account;
    boolean loggedIn;

    public void updateStockShare(StockShare stockShare, Share share) throws Exception {
        /***
         * ---- 无需手动调用
         * 必须要登陆了才允许使用
         * 用于sql数据库更新 stock信息 和account信息 调用时 transaction状态必须为 "finished"
         *
         */
        Statement statement = connection.createStatement();
        // 如果database里已经存在,更新该信息段
        String sqlQuery = String.format("Select * From Stock where shareCode = '%s' AND userCode = %s",stockShare.stock_code,account.accountID);
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        // 判断stock里是否存在这只股票
        if(resultSet.next()){
            // 存在这只股票,修改已有的数据
            resultSet.close();
            sqlQuery = String.format("UPDATE Stock SET shareCost = %s , shareNumber = %s , margin = %s " +
                            "WHERE shareCode = '%s' AND userCode = %s"
                    ,stockShare.cost,stockShare.numberOfHolding,stockShare.margin_profit
                    ,stockShare.stock_code,account.accountID);
            statement.executeUpdate(sqlQuery);
        }else {
            // 不存在这只股票,添加新的数据
            resultSet.close();
            sqlQuery = String.format("INSERT Stock " + "VALUES (%s, %s, %s, %s, '%s' ,'%s')"
                    ,account.accountID,stockShare.cost,stockShare.numberOfHolding,stockShare.margin_profit,stockShare.stock_code,stockShare.stock_name);
            statement.executeUpdate(sqlQuery);
        }
        // 更新账户余额
        updateAccount();
        statement.close();
        resultSet.close();
    }
    public void updateAccount() throws Exception{
        /***
         * ---- 无需手动调用
         * 必须要登陆了才允许使用
         * 用于sql数据库更新 account余额
         */
        Statement statement = connection.createStatement();
        String sqlQuery = String.format("UPDATE Account SET balance = %s WHERE accountNumber = %s",account.balance,account.accountID);
        statement.executeUpdate(sqlQuery);
        statement.close();
    }

    public void updateTransaction(Transaction transaction) throws Exception{
        /***
         * ---- 无需手动调用
         * 必须要登陆了才允许使用
         * 用于sql数据库更新 stock信息,调用时 transaction状态必须为 SUCCESS 或者 PENDING
         *
         */
        Statement statement = connection.createStatement();
        // 如果database里已经存在,更新该信息段
        if(transaction.transactionPrimaryKey != null){
            String sqlQuery = String.format("UPDATE Transaction SET price = %s , amount = %s , time = '%s' , stauts = '%s' " +
                            "WHERE pk = %s "
                            ,transaction.price,transaction.total_price,transaction.time,transaction.executionStatus,transaction.transactionPrimaryKey);
            statement.executeUpdate(sqlQuery);
        }else {
            String sqlQuery = String.format("INSERT Transaction " + "VALUES ('%s', '%s', %s, %s, %s, '%s', %s , '%s' ,'%s' , 0)"
                    ,transaction.share.stock_name,transaction.share.stock_code,transaction.account.accountID,transaction.amount
                    ,transaction.share.currentPrice,transaction.time,transaction.amount * transaction.share.currentPrice,transaction.executionStatus,transaction.type);
            statement.executeUpdate(sqlQuery);
        }
        statement.close();
    }
    public void updateTransactionPending(Transaction transaction) throws Exception{
        /***
         * ---- 无需手动调用
         * 必须要登陆了才允许使用
         * 用于sql数据库更新 stock信息,调用时 transaction状态必须为 PENDING
         *
         */
        Statement statement = connection.createStatement();
        // 如果database里已经存在,更新该信息段
        String sqlQuery = String.format("INSERT Transaction " + "VALUES ('%s', '%s', %s, %s, %s, '%s', %s , '%s' ,'%s' , 0)"
                ,transaction.share.stock_name,transaction.share.stock_code,transaction.account.accountID,transaction.amount
                ,transaction.price,transaction.time,transaction.amount * transaction.price,transaction.executionStatus,transaction.type);
        System.out.println(sqlQuery);
        statement.executeUpdate(sqlQuery);
        statement.close();
    }
    public ServerInteract(String name, String password) throws ClassNotFoundException {
        /***
         * 用于连接sql database
         * 连接初始化，参数名为 用户名和密码 (eg ServerInteract root = new ServerInteract("root", "Pkdyc159357!@#");)
         * 如果连接成功 为connection赋值
         */
        // Class.forName("com.mysql.cj.jdbc.Driver");

        Class.forName("com.mysql.jdbc.Driver");
        try {
            connection = DriverManager.getConnection("jdbc:mysql://cdb-73efu6td.gz.tencentcdb.com:10036/Testing", name,password);
            System.out.println("successfully connected to the server");
        } catch (SQLException exception) {
            System.out.println("invalid password");
        }
    }

    public Boolean logIN(String username,String password) throws SQLException {
        /***
         * 用于登录账户 (你需要调用这个函数来实现用户登录的功能)
         * 返回值为 是否成功登录
         */
        Statement statement = connection.createStatement();
        String sqlQuery = String.format("SELECT * FROM Account where name = '%s'",username);
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        while (resultSet.next()){
            String name = resultSet.getString("name");
            String userPassword = resultSet.getString("password");
            Double balance = resultSet.getDouble("balance");
            Integer key = resultSet.getInt("accountNumber");
            account = new Account(balance,name,key,this);
            if(name.equals(username) && password.equals(userPassword)){
                System.out.println("successfully log in ");
                account = new Account(balance,name,key,this);
                System.out.printf("current login : %s   holding balance : %s\n",account.accountName,account.balance);
                resultSet.close();
                statement.close();
                this.loggedIn = true;
                return true;
            }
        }
        resultSet.close();
        statement.close();
        System.out.println("unable to login,it might due to incorrect password");
        return false;
    }

    public Boolean registration(String username, String password, double balance) throws SQLException {
        /***
         * 此功能是用于用户注册(你只需要调用这个函数来实现用户注册的功能)
         * 同时，这也会检测数据库内是否存在相同用户，如果存在 将会返回false，反之 返回true
         * 返回值为 是否成功创建用户
         */
        Statement statement = connection.createStatement();
        String sqlQuery = String.format("SELECT * FROM Account where name = '%s'",username);
        ResultSet resultSet = statement.executeQuery(sqlQuery);

//        if(resultSet.next()){
//            System.out.println("existed username,please login or reenter");
//            return false;
//        }

        if (resultSet.next()){
            System.out.println("existed username,please login or reenter");
            return false;
        }

        int currentAccounts = 0;
        ResultSet rs1 = statement.executeQuery("SELECT * FROM Account");
        rs1.first();

        while (rs1.next()){
            currentAccounts++;
        }


        // sqlQuery = String.format("INSERT Account " + "VALUES ('%s', '%s', '%s')",username,balance,password);
        // statement.executeUpdate(sqlQuery);
        // statement.close();

        statement.executeUpdate(String.format("INSERT INTO Account " + "VALUES ('%s', '%.2f', '%s', '%d')", username, balance, password, 1+currentAccounts));
        statement.close();
        return true;
    }

    public Boolean addWatchList(String shareCode) throws Exception {
        /***
         * 添加某一只股票进入watchlist (你只需要调用这个函数来实现添加watchlist的功能)
         * 必须要login了才能使用 (因为会用到account的信息)
         * 这里是采用快速搜索
         */
        Share share = getRawShare(shareCode);
        if(share != null){
            Statement statement = connection.createStatement();
            String sqlQuery = String.format("Select * From WatchList where shareCode = '%s' AND userCode = %s",share.stock_code,account.accountID);
            ResultSet resultSet = statement.executeQuery(sqlQuery);
            if(resultSet.next()){
                System.out.printf("Failure : %s already existed in %s's watchlist\n",share.stock_name,account.accountName);
                return false;
            }
            sqlQuery = String.format("INSERT WatchList " + "VALUES (%s , '%s')",account.accountID,share.stock_code);
            statement.executeUpdate(sqlQuery);
            statement.close();
            System.out.printf("%s 已经成功添加到 %s 的watchlist中\n",share.stock_name,account.accountName);
            return true;
        }else {
            System.out.printf("there is no such share namely %s \n",shareCode);
        }

        return false;
    }

    public ArrayList<StockShare> getALLStockShares() throws Exception {
        /***
         * 获取stock里全部的股票 (你只需要调用这个函数来实现获取stock列表的功能)
         * 返回值为一个arraylist,存储了stock里全部股票的信息
         * 必须要login了才能使用 (因为会用到account的信息)
         */
        ArrayList<StockShare> allStockShares = new ArrayList<>();
        Statement statement = connection.createStatement();
        String sqlQuery = String.format("Select * From Stock where userCode = %s",account.accountID);
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        StockShare stockShare = null;
        while (resultSet.next()){
            Share share = getRawShare(resultSet.getString("shareCode"));
            stockShare = new StockShare(share.stock_name,share.stock_code,resultSet.getDouble("shareCost"),resultSet.getInt("shareNumber"),
                    resultSet.getDouble("margin"),account,share.url,share.url_ALL);
            allStockShares.add(stockShare);
        }

        resultSet.close();
        statement.close();
        return allStockShares;
    }


    public StockShare getStockShares(String code) throws Exception {
        /**
         * 用于获取某个stock内的股票
         * 如果当前stock没有这个股票 就新建一个StockShare instance 基于 GetShareInfo.getShare(account, code);
         * 如果当前stock有这个股票 就新建一个StockShare instance 基于 sql database的已有信息
         */
        Statement statement = connection.createStatement();
        String sqlQuery = String.format("Select * From Stock where (shareCode = '%s' OR shareName = '%s' ) AND userCode = %s",code,code,account.accountID);
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        StockShare stockShare = null;
        Share share = getRawShare(code);
        if(resultSet.next() == false){
            // not having shares before
            stockShare = new StockShare(share.stock_name,share.stock_code,account,share.url,share.url_ALL);
        }else {
            stockShare = new StockShare(resultSet.
                    getString("shareName"),resultSet.getString("shareCode"),
                    resultSet.getDouble("shareCost"),resultSet.getInt("shareNumber"),
                    resultSet.getDouble("margin"),account,share.url,share.url_ALL);
        }

        resultSet.close();
        statement.close();
        return stockShare;
    }


    public Status buyShares(String shareCode,Double price,Integer amount) throws Exception {
        /***
         * 买入股票,返回值为交易状态 (你只需要调用这个函数来实现买的功能)
         * 如果getShare的返回值为null,这就说明没有找到当前股票
         * 这里是采用快速搜索
         */
        Share share = GetShareInfo.getShare(account,shareCode);
        Status execution_result = null;
        if(share != null){
            System.out.println("before transaction");
            System.out.println(account);
            Transaction buy = Transaction.producedTransaction(account, price, amount, shareCode, Status.BUY, connection);
            execution_result = buy.execution();
            System.out.println("after transaction");
            System.out.println(account);
            System.out.println("execution status " + execution_result);
        }else {
            execution_result = Status.INVALID_Code;
        }
        return execution_result;
    }
    public Status sellShares(String shareCode,Double price,Integer amount) throws Exception {
        /***
         * 卖出股票,返回值为交易状态 (你需要调用这个函数来实现卖的功能)
         * 如果getShare的返回值为null,这就说明没有找到当前股票
         * 这里是采用快速搜索
         */
        Share share = GetShareInfo.getShare(account,shareCode);
        Status execution_result = null;
        if(share != null){
            System.out.println("before transaction");
            System.out.println(account);
            Transaction sell = Transaction.producedTransaction(account, price, amount, shareCode, Status.SELL, connection);
            execution_result = sell.execution();
            System.out.println("after transaction");
            System.out.println(account);
            System.out.println("execution status " + execution_result);
        }else {
            execution_result = Status.INVALID_Code;
        }
        return execution_result;
    }

    public ArrayList<Share> getAllWatchlistInfo(Boolean isFast) throws Exception {
        /**
         * 用于获取watchlist的股票的信息 (你需要调用这个函数来实现获取watchlist信息)
         * 支持快速查询和详细信息查询 (快速查询的input为True,详细查询的信息的input为False)
         * 只是用于获取watchlist的信息,跟stock无关
         * 你需要自己处理return size 为 0 的情况
         */
        Statement statement = connection.createStatement();
        String sqlString = String.format("Select * From WatchList where userCode = %s",account.accountID);
        ResultSet resultSet = statement.executeQuery(sqlString);
        ArrayList<Share> sharesFromWatchlist = new ArrayList<>();
        while (resultSet.next()){
            if(isFast){
                String url = getRawShare(resultSet.getString("shareCode")).url;
                Share share = GetShareInfo.getShare_url(url);
                sharesFromWatchlist.add(share);
            }else {
                String code = resultSet.getString("code");
                Share share = GetShareInfo.getShare(code);
                sharesFromWatchlist.add(share);
            }
        }
        statement.close();
        resultSet.close();
        return sharesFromWatchlist;
    }

    public ArrayList<Transaction> getAllTransactions() throws Exception {
        /***
         * 用于获取所有transaction的股票的信息 (你需要调用这个函数来实现获取所有transaction的信息)
         * 通过这个method从database中获取的transaction instance都有一个特殊的attribute -- transactionPrimaryKey (只有通过这种方式获取的有)
         * 这个值默认状态下是 null，当然通过这种方式获得的值将会是一个 unique number,这个值会对应某一个transaction 并且只跟他对应。
         * 所以 操作某一个 transaction就可以通过这个unique number来实现。 这个unique number的专业名词是 primary key
         */
        Statement statement = connection.createStatement();
        String sqlString = String.format("Select * From Transaction where traderKey = %s",account.accountID);
        ResultSet resultSet = statement.executeQuery(sqlString);
        ArrayList<Transaction> allTransaction = new ArrayList<>();
        while (resultSet.next()){
            Double price = resultSet.getDouble("price");
            Integer amount = resultSet.getInt("numbers");
            Transaction transaction = new Transaction(resultSet.getString("type"), resultSet.getString("code"), resultSet.getString("name"), price,
                    amount, resultSet.getString("time"), price * amount, resultSet.getString("stauts"), account.accountID,resultSet.getInt("pk"));
            transaction.account = account;
            allTransaction.add(transaction);
        }
        statement.close();
        resultSet.close();
        return allTransaction;

    }

    public Transaction getOneTransactions(int pk) throws Exception {
        /***
         * 用于获取某一个transaction的股票的信息
         * 通过这个method从database中获取的transaction instance都有一个特殊的attribute -- transactionPrimaryKey (只有通过这种方式获取的有)
         * 这个值默认状态下是 null，当然通过这种方式获得的值将会是一个 unique number,这个值会对应某一个transaction 并且只跟他对应。
         * 所以 操作某一个 transaction就可以通过这个unique number来实现。 这个unique number的专业名词是 primary key
         * -- 无需手动调用
         */
        Statement statement = connection.createStatement();
        String sqlString = String.format("Select * From Transaction where pk = %s",pk);
        ResultSet resultSet = statement.executeQuery(sqlString);
        Transaction transaction = null;
        while (resultSet.next()){
            Double price = resultSet.getDouble("price");
            Integer amount = resultSet.getInt("numbers");
            transaction = new Transaction(resultSet.getString("type"), resultSet.getString("code"), resultSet.getString("name"), price,
                    amount, resultSet.getString("time"), price * amount, resultSet.getString("stauts"), account.accountID,resultSet.getInt("pk"));
            transaction.account = account;
            break;
        }
        statement.close();
        resultSet.close();
        return transaction;

    }

    public void deleteAll() throws Exception{
        /***
         * 删除所有的数据
         * -- 无需手动调用
         */
        Statement statement = connection.createStatement();
        String sql = "DELETE FROM ShareAll";
        statement.executeUpdate(sql);
        statement.close();
    }

    public void refreshAllChina_zh_sh() throws Exception {
        /**
         * 获取A股所有股票信息 (每天开启前都用一次！！！)
         * 别用！！！ 数据库已经更新过了
         */
        deleteAll();
        Statement statement = connection.createStatement();
        Document document = null;
        document = Jsoup.connect("https://xueqiu.com/service/v5/stock/screener/quote/list?page=1&size=1&order=desc&order_by=symbol&type=sh_sz").ignoreContentType(true).get();
        String raw = document.getElementsByTag("body").get(0).text();
        JSONObject jsonObject = new JSONObject(raw);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject list;
        int size = data.getInt("count");
        System.out.println("size is " + size);
        for (int i = 1; i < size; i++) {
            String url = String.format("https://xueqiu.com/service/v5/stock/screener/quote/list?page=%s&size=1&order=desc&order_by=symbol&type=sh_sz",i);
            document = Jsoup.connect(url).ignoreContentType(true).get();
            String body = document.getElementsByTag("body").get(0).text();
            jsonObject = new JSONObject(body);
            data = jsonObject.getJSONObject("data");
            list = (JSONObject) data.getJSONArray("list").get(0);
            String name = list.getString("name");
            String code = list.getString("symbol");
            String url_ALL = String.format("https://xueqiu.com/S/%s",code);
            String sql = String.format("INSERT ShareAll " + "VALUES ('%s', '%s', '%s' ,'%s')",name,code,url,url_ALL);
            statement.executeUpdate(sql);
            System.out.printf("爬取进度 : %s / %s  (%s : %s)\n",i,size,name,code);
        }
        System.out.println("更新完毕");
    }

    public Share getRawShare(String code) throws Exception{
        /***
         * 用于返回url的地址
         * 参数为 股票代码或者股票名字，第二个参数为 返回url 还是url_ALL  (false 为 url， true 为 url_all)
         * -- 无需手动调用
         */
        Statement statement = connection.createStatement();
        String sqlQuery = String.format("Select * From ShareAll where ( shareCode = '%s' OR shareName = '%s')",code,code);
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        Share share = null;
        if(resultSet.next()){
            share = new Share(resultSet.getString("shareName"), resultSet.getString("shareCode"), resultSet.getString("shareURL"), resultSet.getString("shareURL_ALL"));
        }
        statement.close();
        resultSet.close();
        return share;

    }
    public void refreshAll_US() throws Exception {
        /**
         * 获取美股所有股票信息 (别用！！！)
         * 别用！！！ 数据库已经更新过了
         */
        Statement statement = connection.createStatement();
        for (int i = 1; i < 8347; i++) {
            String url = String.format("https://xueqiu.com/service/v5/stock/screener/quote/list?page=%s&size=1&order=desc&order_by=symbol&type=us",i);
            Document document = null;
            document = Jsoup.connect(url).ignoreContentType(true).get();
            String body = document.getElementsByTag("body").get(0).text();
            JSONObject jsonObject = new JSONObject(body);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject list = (JSONObject) data.getJSONArray("list").get(0);
            String name = list.getString("name");
            String code = list.getString("symbol");
            name = name.replace("'","''");
            url = url.replace("'","\'");
            String url_ALL = String.format("https://xueqiu.com/S/%s",code);
            String sql = String.format("INSERT ShareAll_US " + "VALUES ('%s', '%s', '%s' ,'%s')",name,code,url,url_ALL);
            System.out.println(sql);
            statement.executeUpdate(sql);
            System.out.printf("爬取进度 : %s / 8346  (%s : %s)\n",i,name,code);
        }
        System.out.println("更新完毕");
    }

    public void transactionBackGroundRefresh() throws Exception{
        /***
         * 后台处理pending的transaction
         * 处理完一轮订单后会睡眠1000ms (为了保证效率)
         * 使用方法在开头添加         new ThreadBackGround(root).run();
         */
        Statement statement = connection.createStatement();
        ArrayList<Integer> transactionId = new ArrayList<>();
        while (true){
            String sqlString = String.format("Select * From Transaction where stauts = 'PENDING' ");
            ResultSet resultSet = statement.executeQuery(sqlString);
            ArrayList<Transaction> allTransaction = new ArrayList<>();
            while (resultSet.next()){
                Double price = resultSet.getDouble("price");
                Integer amount = resultSet.getInt("numbers");
                Transaction transaction = new Transaction(resultSet.getString("type"), resultSet.getString("code"), resultSet.getString("name"), price,
                        amount, resultSet.getString("time"), price * amount, resultSet.getString("stauts"), account.accountID,resultSet.getInt("pk"));
                transaction.account = account;
                if(!transactionId.contains(transaction.transactionPrimaryKey)){
                    allTransaction.add(transaction);
                }
            }
            for (Transaction transaction : allTransaction) {
                transaction.PendingProcessing();
            }
            Thread.sleep(1000);
            resultSet.close();
        }
    }

    public void TransactionOperation (int order, Status status) throws Exception{
        /***
         * 用于修改transaction的status, 也就是这个指定transaction的执行状态 (你需要调用这个函数来实现修改所有transaction的信息)
         *
         * 这个method可以用于测试,不过他的初衷是为了实现 pending -> success 或者是 pending -> cancel 的转变
         * 这里需要解释一下,在股市中 比如说你有 100股茅台 当前价格是1500。 你设置了 1600卖出100股茅台
         * 这时候库存茅台就会显示为0.同时你的balance也没有增加。这是因为这个transaction是处于pending状态的 需要等待到股价到达1600或以上的时候 成功卖出后
         * 你的balance才会增加。当然 你可以选择cancel transaction,这样的话当前transaction 就会从 pending -> cancel,然后 你的股票数量就从 0 -> 100了
         *
         * 但是 出于实际考虑 这个只能实现把 pending —> success 或者是 pending -> cancel 的转变,就像刚刚那个例子一样, 如果强行转换某一个pending -> success的话
         * 因为当前价格高于市价 所以我就设计成按照设定价格交易成功 (也就是以1600的价格交易成功)
         * 关于这个事情,我会在main函数里给出例子
         *
         * 通过这个method从database中获取的transaction instance都有一个特殊的attribute -- transactionPrimaryKey (只有通过这种方式获取的有)
         * 这个值默认状态下是 null，当然通过这种方式获得的值将会是一个 unique number,这个值会对应某一个transaction 并且只跟他对应。
         * 所以 操作某一个 transaction就可以通过这个unique number来实现。 这个unique number的专业名词是 primary key
         */
        Transaction transaction = getOneTransactions(order);
        transaction.changeStatus(status);

    }

    public static void main(String[] args) throws Exception {
        ServerInteract root = new ServerInteract("root", "Pkdyc159357!@#");
        // root.logIN("pkdyc","123456");
        // root.TransactionOperation(30,Status.CANCEL);

        root.registration("yw", "2222", 1000.0);
        System.out.println("success");

        // root.refreshAllChina_zh_sh();
        // 多线程后台处理开启
        // new ThreadBackGround(root).run();
    }
}
