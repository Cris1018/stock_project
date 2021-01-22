import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GetShareInfo {
    public static Share getShare(String code) throws InterruptedException {
        /**
         * ---- 无需手动调用
         * 已经弃用的方法,但是仍然可以使用
         * 只股票代码 (可以查询A股美股)
         * 查询速度比较慢，但是能显示全部信息
         */
        String url = String.format("https://xueqiu.com/S/%s",code);
        Document document = null;
        try {
            document = Jsoup.parse(new URL(url), 1000);
        } catch (IOException e) {
            System.out.printf("invalid shares code : %s,please reenter\n",code);
            return null;
        }
        String price = document.getElementsByClass("stock-current").first().text();
        String name = document.getElementsByClass("stock-name").first().text().split("\\(")[0];
        String begin_price_raw = document.select("#app > div.container-lg.clearfix > div.container-sm.float-left.stock__main > div.quote-container").first().text();
        String price_highest_raw = document.select("#app > div.container-lg.clearfix > div.container-sm.float-left.stock__main > div.quote-container > table > tbody > tr:nth-child(1) > td:nth-child(1) > span").get(0).text();
        String price_lowest_raw = document.select("#app > div.container-lg.clearfix > div.container-sm.float-left.stock__main > div.quote-container > table > tbody > tr.separateTop > td:nth-child(1) > span").get(0).text();
        String market_status = document.getElementsByClass("quote-market-status").get(0).text();
        String ttm = document.getElementsByClass("separateBottom").get(0).text();
        Element element = document.select("#app > div.container-lg.clearfix > div.container-sm.float-left.stock__main > div.quote-container > table > tbody > tr:nth-child(4) > td:nth-child(3)").get(0);
        String currency = String.valueOf(begin_price_raw.charAt(0));
        String stock_name = name;
        Double currentPrice = Double.valueOf(begin_price_raw.replace(currency,"").split(" ")[0]);
        Double change_value = Double.valueOf(begin_price_raw.replace(currency,"").split(" ")[1]);
        Double change_percentage = Double.valueOf(begin_price_raw.replace("%","").split(" ")[2]);
        Double price_lowest = Double.valueOf(price_lowest_raw);
        Double price_highest = Double.valueOf(price_highest_raw);
        String PERate_TTM = element.text();
        String PERate_static = ttm.split(" ")[2];
        String amplitude = document.select("#app > div.container-lg.clearfix > div.container-sm.float-left.stock__main > div.quote-container > table > tbody > tr.separateTop > td:nth-child(4) > span").get(0).text();
        Double price_begin = Double.valueOf(document.select("#app > div.container-lg.clearfix > div.container-sm.float-left.stock__main > div.quote-container > table > tbody > tr:nth-child(1) > td:nth-child(2) > span").get(0).text());
        String trade_status = market_status.split(" ")[0];
        String currentTime = market_status.split(" ",2)[1];
        Boolean tradable = trade_status.equals("交易中");
        Share share = new Share(currency, stock_name,code, currentPrice, change_value, change_percentage, price_lowest, price_highest, PERate_TTM, PERate_static, amplitude, price_begin, trade_status, currentTime, tradable);
        return share;
    }

    public static Share getShare(Account account,String name) throws Exception {
        /**
         * ---- 无需手动调用
         * 用于第一次获取股票信息 (不在stock 或者 watchlist之内)
         * 接受股票名字或者代码 (只对A股查询)
         * 查询速度非常快，但是部分信息无法显示
         */
        Statement statement = account.database.connection.createStatement();
        String sqlQuery = String.format("Select * From ShareAll where shareName = '%s' OR shareCode = '%s'",name,name);
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        String url;
        if(resultSet.next()){
            url = resultSet.getString("shareURL");
            System.out.println(resultSet.getString("shareCode"));
        }else {
            System.out.printf("找不到代码或者名字为 %s 的股票\n",name);
            return null;
        }
        Document document = null;
        document = Jsoup.connect(url).ignoreContentType(true).get();
        String body = document.getElementsByTag("body").get(0).text();
        JSONObject jsonObject = new JSONObject(body);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject dataset = (JSONObject) data.getJSONArray("list").get(0);
        String currency = "¥";
        String stock_name = dataset.getString("name");
        String stock_code = dataset.getString("symbol");
        Double currentPrice = dataset.getDouble("current");
        Double change_value = dataset.getDouble("chg");
        Double change_percentage = dataset.getDouble("percent");
        Double price_lowest = 0.00;
        Double price_highest = 0.00;
        String PERate_TTM = dataset.getDouble("pe_ttm") + "";
        String PERate_static = "N/A";
        String amplitude = dataset.getDouble("amplitude") + "";
        Double price_begin = null;
        String trade_status = "N/A";
        String currentTime = "N/A";
        Boolean tradable = dataset.getBoolean("has_follow");
        String url_ALL = String.format("https://xueqiu.com/S/%s",stock_code);
        Share share = new Share(currency, stock_name,stock_code, currentPrice, change_value, change_percentage, price_lowest, price_highest, PERate_TTM, PERate_static, amplitude, price_begin, trade_status, currentTime, tradable, url, url_ALL);
        return share;
    }

    public static ArrayList<Share> getShareRE(Account account,String name) throws Exception {
        /**
         * 需要手动调用 来实现联想显示股票名
         * 如果输入的string为空字符串 将不做显示 返回空arraylist
         * 反之 返回所有包含这个关键字的股票
         */
        ArrayList<Share> shares = new ArrayList<>();
        if(name.equals("")){
            return shares;
        }
        Statement statement = account.database.connection.createStatement();
        String sqlQuery = String.format("Select * From ShareAll where shareName LIKE '%s%s%s'","%",name,"%");
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        while (resultSet.next()){
            shares.add(new Share(resultSet.getString("shareName"),resultSet.getString("shareCode"),resultSet.getString("shareURL"),resultSet.getString("shareURL_ALL")));

        }
        return shares;
    }

    public static ArrayList<Share> getShareAll(Account account) throws Exception {
        /**
         * ---- 无需手动调用
         * ---- 测试用
         * 用于获得全部share的数据
         * 接受股票名字或者代码 (只对A股查询)
         * 查询速度非常快，但是部分信息无法显示
         */
        Statement statement = account.database.connection.createStatement();
        String sqlQuery = String.format("Select * From ShareAll");
        ResultSet resultSet = statement.executeQuery(sqlQuery);
        String url;
        ArrayList<Share> shares = new ArrayList<>();
        if(resultSet.next()){
            shares.add(new Share(resultSet.getString("shareName"),resultSet.getString("shareCode"),resultSet.getString("shareURL"),resultSet.getString("shareURL_ALL")));
        }
        return shares;
    }

    public static Share getShare_url(String url) throws Exception {
        /**
         * ---- 无需手动调用
         * 用于最快速获取股票信息 (在stock 或者 watchlist之内)
         * 接受股票名字或者代码 (只对A股查询)
         * 查询速度非常快，但是部分信息无法显示
         * 不会返回 null 因为 这是内部调用的url,输入的值一定是正确的
         * 不需要连接数据库
         */
        Document document = null;
        document = Jsoup.connect(url).ignoreContentType(true).get();
        String body = document.getElementsByTag("body").get(0).text();
        JSONObject jsonObject = new JSONObject(body);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject dataset = (JSONObject) data.getJSONArray("list").get(0);
        String currency = "¥";
        String stock_name = dataset.getString("name");
        String stock_code = dataset.getString("symbol");
        Double currentPrice = dataset.getDouble("current");
        Double change_value = dataset.getDouble("chg");
        Double change_percentage = dataset.getDouble("percent");
        Double price_lowest = 0.00;
        Double price_highest = 0.00;
        String PERate_TTM = dataset.get("pe_ttm").toString() + "";
        String PERate_static = "N/A";
        String amplitude = dataset.get("amplitude").toString() + "";
        Double price_begin = null;
        String trade_status = "N/A";
        String currentTime = "N/A";
        Boolean tradable = dataset.getBoolean("has_follow");
        String url_ALL = String.format("https://xueqiu.com/S/%s",stock_code);
        Share share = new Share(currency, stock_name,stock_code, currentPrice, change_value, change_percentage, price_lowest, price_highest, PERate_TTM, PERate_static, amplitude, price_begin, trade_status, currentTime, tradable, url, url_ALL);
        return share;
    }

    public static void main(String[] args) throws URISyntaxException, IOException {
    }
}
