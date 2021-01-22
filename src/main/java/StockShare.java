public class StockShare {
    String stock_name;
    String stock_code;
    Account account;
    String url;
    String url_ALL;

    /**
     * margin_profit :指的是净利润，只有当股票清空的时候 他的值才会改变
     * 其作用是用于反应这只股票的历史利益
     * stock_name : 股票名字
     * stock_code : 股票代码
     * cost : 股票成本 (可以为负数)
     * account : 股票持有账户
     * profit : 指的是浮盈，具体的值可以通过update函数更新 浮盈指的是当前市值下的盈利
     * profit_rate : 指的是浮盈率，具体的值可以通过update函数获取 浮盈指的是当前市值下的盈利率
     */

    Integer numberOfHolding = 0;
    Double margin_profit = 0.0;
    Double profit = 0.0;
    Double profit_rate = 0.0;
    Double cost = 0.0;
    Double total_value = 0.0;

    public StockShare(String stock_name, String stock_code ,Double cost, Integer number,Double margin_profit,Account account,String url,String url_ALL) {
        this.stock_name = stock_name;
        this.stock_code = stock_code;
        this.cost = cost;
        this.numberOfHolding = number;
        this.margin_profit = margin_profit;
        this.account = account;
        this.url = url;
        this.url_ALL = url_ALL;
    }



    public StockShare(String stock_name, String stock_code, Account account,String url,String url_ALL) {
        this.stock_name = stock_name;
        this.stock_code = stock_code;
        this.account = account;
        this.url = url;
        this.url_ALL = url_ALL;
    }

    public Status sell(Integer number,Double price,Share share){
        /***
         * ---- 无需手动调用
         * 卖出股票 并返回状态
         */
        if(number <= 0 || price <= 0){
            System.out.println("the amount of shares much greater than zero");
            return Status.INVALID_Number;
        }
        if(numberOfHolding < number){
            System.out.println("insufficient shares");
            return Status.INSUFFICIENT_SHARES;
        }
        if(share.currentPrice < price || share.tradable != false){
            System.out.println("pending");
            return Status.PENDING;
        }else {
            price = share.currentPrice;
            cost = ( cost * numberOfHolding + number * price ) / (numberOfHolding + number);
            account.balance += number * price;
            numberOfHolding -= number;
            if(numberOfHolding == 0){
                cost = 0.0;
                System.out.printf("%s 已经清仓\n",stock_name);
            }else {
                System.out.println("new cost is " + cost);

            }

            return Status.SUCCESS;
        }
    }

    public Status sell_silence(Integer number, Double price, Share share){
        /***
         * ---- 无需手动调用
         * 卖出股票 并返回状态
         * 没有print
         */
        if(number <= 0 || price <= 0){
            return Status.INVALID_Number;
        }
        if(numberOfHolding < number){
            return Status.INSUFFICIENT_SHARES;
        }
        if(share.currentPrice < price || share.tradable != false){
            return Status.PENDING;
        }else {
            price = share.currentPrice;
            cost = ( cost * numberOfHolding + number * price ) / (numberOfHolding + number);
            account.balance += number * price;
            numberOfHolding -= number;
            if(numberOfHolding == 0){
                cost = 0.0;
            }
            return Status.SUCCESS;
        }
    }
    public Status buy_silence(Integer number,Double price,Share share){
        /***
         * ---- 无需手动调用
         * 买入股票 并返回状态
         * 没有print
         */
        if(number <= 0 || price <= 0){
            return Status.INVALID_Number;
        }
        if((account.balance - number * share.currentPrice )< 0){
            return Status.INSUFFICIENT_Fund;
        }
        if(share.currentPrice > price || share.tradable != false){
            account.balance -= number * price;
            return Status.PENDING;
        }else {
            price = share.currentPrice;
            cost = ( cost * numberOfHolding + number * price ) / (numberOfHolding + number);
            account.balance -= number * price;
            numberOfHolding += number;
            return Status.SUCCESS;
        }

    }

    public Status buy(Integer number,Double price,Share share){
        /***
         * ---- 无需手动调用
         * 买入股票 并返回状态
         */
        if(number <= 0 || price <= 0){
            System.out.println("the amount of shares much greater than zero");
            return Status.INVALID_Number;
        }
        if((account.balance - number * share.currentPrice )< 0){
            System.out.println("insufficient fund");
            return Status.INSUFFICIENT_Fund;
        }
        if(share.currentPrice > price || share.tradable != false){
            account.balance -= number * price;
            System.out.println("pending");
            return Status.PENDING;
        }else {
            price = share.currentPrice;
            cost = ( cost * numberOfHolding + number * price ) / (numberOfHolding + number);
            account.balance -= number * price;
            numberOfHolding += number;
            System.out.println("new cost is " + cost);

            return Status.SUCCESS;
        }

    }

    public Boolean update() throws Exception {
        /***
         *  ---- 无需手动调用
         * 更新 最新的动态的市值 盈利 和 盈利率的情况，这些值都在attribute里面
         */
        Share share = GetShareInfo.getShare_url(url);
        if(share.stock_name.equals(this.stock_name)){
            profit = (share.currentPrice - this.cost) * numberOfHolding;
            profit_rate = (share.currentPrice / this.cost) - 1;
            profit_rate = profit_rate * 100;
            total_value = share.currentPrice * numberOfHolding;
            profit_rate = Math.round(profit_rate * 100.0) / 100.0;
            total_value = Math.round(total_value * 100.0) / 100.0;

            return true;
        }else {
            return false;
        }


    }

    @Override
    public String toString() {
        return "StockShare{" +
                "stock_name='" + stock_name + '\'' +
                ", stock_code='" + stock_code + '\'' +
                ", account=" + account +
                ", url='" + url + '\'' +
                ", url_ALL='" + url_ALL + '\'' +
                ", numberOfHolding=" + numberOfHolding +
                ", margin_profit=" + margin_profit +
                ", profit=" + profit +
                ", profit_rate=" + profit_rate +
                ", cost=" + cost +
                ", total_value=" + total_value +
                '}';
    }

    public static void main(String[] args) {


    }
}
