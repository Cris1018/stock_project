public enum Status {
    /***
     * SUCCESS : 交易成功
     * PENDING : 交易正在等待中 (等待直到设定价格)
     * BUY : 此交易是用于买入
     * SELL : 此交易是用于卖出
     * CANCEL : 订单被取消
     * INVALID_Number : 无效的价格或者数量
     * INVALID_Code : 无效的股票代码或者名字
     * INSUFFICIENT_Fund : account内资金不够
     * INSUFFICIENT_SHARES : account内卖出时 某一之股票数量小于卖出股票数量
     */
    SUCCESS,
    PENDING,
    BUY,
    SELL,
    CANCEL,
    INVALID_Number,
    INVALID_Code,
    INSUFFICIENT_Fund,
    INSUFFICIENT_SHARES;

}
