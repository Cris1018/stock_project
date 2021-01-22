public class Share {
    String currency;
    String stock_name;
    String stock_code;
    Double currentPrice;
    Double change_value;
    Double change_percentage;
    Double price_lowest;
    Double price_highest;
    String PERate_TTM;
    String PERate_static;
    String amplitude;
    Double price_begin;
    String trade_status;
    String currentTime;
    Boolean tradable;
    String url;
    String url_ALL;

    public Share(String currency, String stock_name, String stock_code, Double currentPrice, Double change_value, Double change_percentage, Double price_lowest, Double price_highest, String PERate_TTM, String PERate_static, String amplitude, Double price_begin, String trade_status, String currentTime, Boolean tradable) {
        this.currency = currency;
        this.stock_name = stock_name;
        this.stock_code = stock_code;
        this.currentPrice = currentPrice;
        this.change_value = change_value;
        this.change_percentage = change_percentage;
        this.price_lowest = price_lowest;
        this.price_highest = price_highest;
        this.PERate_TTM = PERate_TTM;
        this.PERate_static = PERate_static;
        this.amplitude = amplitude;
        this.price_begin = price_begin;
        this.trade_status = trade_status;
        this.currentTime = currentTime;
        this.tradable = tradable;
    }

    public Share(String stock_name, String stock_code, String url, String url_ALL) {
        this.stock_name = stock_name;
        this.stock_code = stock_code;
        this.url = url;
        this.url_ALL = url_ALL;
    }

    public Share(String currency, String stock_name, String stock_code, Double currentPrice, Double change_value, Double change_percentage, Double price_lowest, Double price_highest, String PERate_TTM, String PERate_static, String amplitude, Double price_begin, String trade_status, String currentTime, Boolean tradable, String url, String url_ALL) {
        this.currency = currency;
        this.stock_name = stock_name;
        this.stock_code = stock_code;
        this.currentPrice = currentPrice;
        this.change_value = change_value;
        this.change_percentage = change_percentage;
        this.price_lowest = price_lowest;
        this.price_highest = price_highest;
        this.PERate_TTM = PERate_TTM;
        this.PERate_static = PERate_static;
        this.amplitude = amplitude;
        this.price_begin = price_begin;
        this.trade_status = trade_status;
        this.currentTime = currentTime;
        this.tradable = tradable;
        this.url = url;
        this.url_ALL = url_ALL;
    }

    @Override
    public String toString() {
        return "Share{" +
                "currency='" + currency + '\'' +
                ", stock_name='" + stock_name + '\'' +
                ", currentPrice=" + currentPrice +
                ", change_value=" + change_value +
                ", change_percentage=" + change_percentage +
                ", price_lowest=" + price_lowest +
                ", price_highest=" + price_highest +
                ", PERate_TTM='" + PERate_TTM + '\'' +
                ", PERate_static='" + PERate_static + '\'' +
                ", amplitude='" + amplitude + '\'' +
                ", price_begin=" + price_begin +
                ", trade_status='" + trade_status + '\'' +
                ", currentTime='" + currentTime + '\'' +
                ", tradable=" + tradable +
                '}';
    }
}
