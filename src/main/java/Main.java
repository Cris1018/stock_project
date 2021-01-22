import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main extends Application implements EventHandler<ActionEvent> {

    Button login = new Button("log in");
    Button quit = new Button("quit");
    Button register = new Button("register now");
    Button confirmToRegister = new Button("sign up!");
    Button viewWatchlist = new Button("view watchlist");
    Button buy = new Button("buy in stocks");
    Button sell = new Button("sell stocks");
    Button confirmToBuy = new Button("buy");
    Button confirmToSell = new Button("sell");
    Button backToMenu = new Button("back to menu");
    Button backToMenu1 = new Button("back to menu");
    Button backToMenu2 = new Button("back to menu");
    Button backToMenu3 = new Button("back to menu");
    Button cancelPendingTransaction = new Button("cancel one transaction");
    Button buttonViewAllTransactions = new Button("history transactions");
    Button buttonAddToWatchlist = new Button("add share to watchlist");
    Button buttonConfirmToAdd = new Button("add to the watchlist");
    Button confirmToRemoveTransaction = new Button("confirm to remove");
    Button buttonBackToTransaction = new Button("back to transaction");
    Button buttonSearchToAdd = new  Button("search");
    Button backToWatchlist = new Button("back to watch list");

    Stage window;
    ServerInteract connection;
    Scene yourAccount, loginPage, watchList, sellEntrance, buyEntrance, newUserRegister, historyTransactions, addToWatchlistPage, removeTransactionPage;
    TextField username = new TextField();
    TextField password = new TextField();
    TextField createNewUser = new TextField();
    TextField createPassword = new TextField();
    TextField confirmPassword = new TextField();
    TextField initialBal = new TextField();
    ChoiceBox<String> nameOfShare = new ChoiceBox<>();
    ChoiceBox<Integer> chooseOnePendingTransaction = new ChoiceBox<>();
    TextField chooseOneShareToBuy = new TextField();
    TextField priceToBuy = new TextField();
    TextField amountToBuy = new TextField();
    TextField price = new TextField();
    TextField amount = new TextField();
    ComboBox<String> chooseOneToAdd = new ComboBox<>();
    GridPane loginInterface = new GridPane();
    GridPane accountInterface = new GridPane();
    GridPane registerInterface = new GridPane();
    GridPane watchlistInterface = new GridPane();
    GridPane historyTransactionInterface = new GridPane();
    GridPane sellInterface = new GridPane();
    GridPane buyInterface = new GridPane();
    GridPane chooseTransactionToRemoveInterface = new GridPane();
    GridPane addToWatchlistInterface = new GridPane();

    public void settingActions(){
        login.setOnAction(this);
        register.setOnAction(this);
        buttonSearchToAdd.setOnAction(this);
        backToWatchlist.setOnAction(this);
        quit.setOnAction(this);
        viewWatchlist.setOnAction(this);
        buy.setOnAction(this);
        sell.setOnAction(this);
        backToMenu.setOnAction(this);
        backToMenu1.setOnAction(this);
        backToMenu2.setOnAction(this);
        buttonBackToTransaction.setOnAction(this);
        backToMenu3.setOnAction(this);
        confirmToBuy.setOnAction(this);
        confirmToSell.setOnAction(this);
        confirmToRegister.setOnAction(this);
        nameOfShare.setOnAction(this);
        cancelPendingTransaction.setOnAction(this);
        buttonViewAllTransactions.setOnAction(this);
        buttonAddToWatchlist.setOnAction(this);
        confirmToRemoveTransaction.setOnAction(this);
        buttonConfirmToAdd.setOnAction(this);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setResizable(false);

        this.settingActions();
        this.setLoginInterface();
        this.setRegisterInterface();

        try {
            connection = new ServerInteract("root", "Pkdyc159357!@#");
        } catch (ClassNotFoundException e){

        }

        loginPage.getStylesheets().add("LogIn.css");

        window.setScene(loginPage);
        window.show();
    }

    public void verify(TextField username, TextField password){

        try{
            connection.logIN(username.getText(), password.getText());

            if (connection.loggedIn){

                accountInterface.setPadding(new Insets(20, 20, 20, 20));
                accountInterface.setVgap(20);
                accountInterface.setHgap(20);

                Account thisAccount = connection.account;
                Text greet = new Text("Hello, "+thisAccount.accountName);
                Text showBalance = new Text("your balance is: "+thisAccount.balance);

                GridPane.setConstraints(greet, 0, 1);
                GridPane.setConstraints(showBalance, 0, 2);
                GridPane.setConstraints(viewWatchlist, 0, 5);
                GridPane.setConstraints(buy, 0, 6);
                GridPane.setConstraints(sell, 0, 7);
                GridPane.setConstraints(quit, 0, 9);
                GridPane.setConstraints(buttonViewAllTransactions, 0, 8);

                this.setSellInterface();
                this.setBuyInterface();
                this.setWatchlistInterface();
                this.setAddToWatchlistInterface();
                this.setHistoryTransactionsInterface();
                this.setRemoveTransactionInterface();

                accountInterface.getChildren().addAll(greet, showBalance, viewWatchlist, buy, sell, buttonViewAllTransactions, quit);
                yourAccount = new Scene(accountInterface, 500, 500);
                yourAccount.getStylesheets().add("Themes.css");
                window.setScene(yourAccount);
                window.show();
            }

        } catch (SQLException e){
            // TODO - then requiring to sign up
            // pop up box!!!!
        }
    }

    public void setLoginInterface(){
        loginInterface.setPadding(new Insets(20, 20, 20, 20));
        loginInterface.setVgap(20);
        loginInterface.setHgap(20);

        Label usernamelabel = new Label("Username: ");
        username.setPromptText("username");
        Label passwordlabel = new Label("Password: ");
        password.setPromptText("password");

        GridPane.setConstraints(usernamelabel, 0, 0);
        GridPane.setConstraints(username, 1, 0);
        GridPane.setConstraints(passwordlabel, 0, 1);
        GridPane.setConstraints(password, 1, 1);
        GridPane.setConstraints(login, 1, 4);
        GridPane.setConstraints(register, 2, 4);

        loginInterface.getChildren().addAll(usernamelabel, username, passwordlabel,  password, login, register);
        loginPage = new Scene(loginInterface, 500, 500);
    }


    public void setRegisterInterface(){
        registerInterface.setPadding(new Insets(20, 20, 20, 20));
        registerInterface.setVgap(17);
        registerInterface.setHgap(17);

        Label newUsername = new Label("Your username: ");
        GridPane.setConstraints(newUsername, 0, 0);
        GridPane.setConstraints(createNewUser, 1, 0);

        Label passwordFirstTime = new Label("Your password: ");
        GridPane.setConstraints(passwordFirstTime, 0, 1);
        GridPane.setConstraints(createPassword, 1, 1);

        Label passwordSecondTime = new Label("re-enter your password: ");
        GridPane.setConstraints(passwordSecondTime, 0, 2);
        GridPane.setConstraints(confirmPassword, 1, 2);

        Label enterInitialBal = new Label("initial balance: ");
        GridPane.setConstraints(enterInitialBal, 0, 3);
        GridPane.setConstraints(initialBal, 1, 3);

        GridPane.setConstraints(confirmToRegister, 1, 4);

        registerInterface.getChildren().addAll(newUsername, createNewUser, passwordFirstTime, createPassword, passwordSecondTime, confirmPassword, confirmToRegister, enterInitialBal, initialBal);
        newUserRegister = new Scene(registerInterface, 500, 500);
    }

    public void setRemoveTransactionInterface(){
        chooseTransactionToRemoveInterface.setPadding(new Insets(10, 10, 10, 10));
        chooseTransactionToRemoveInterface.setVgap(10);
        chooseTransactionToRemoveInterface.setHgap(10);

        ArrayList<Transaction> allTransactions = new ArrayList<>();

        try {
            allTransactions = connection.getAllTransactions();
            allTransactions.stream().filter(e->e.status_string.equalsIgnoreCase("PENDING")).forEach(e->chooseOnePendingTransaction.getItems().add(e.transactionPrimaryKey));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allTransactions.size()>0){
           chooseOnePendingTransaction.setValue(allTransactions.get(0).transactionPrimaryKey);
        }

        GridPane.setConstraints(chooseOnePendingTransaction, 3, 3);
        GridPane.setConstraints(confirmToRemoveTransaction, 3, 5);
        GridPane.setConstraints(buttonBackToTransaction, 3, 6);

        chooseTransactionToRemoveInterface.getChildren().addAll(confirmToRemoveTransaction, chooseOnePendingTransaction, buttonBackToTransaction);
        removeTransactionPage = new Scene(chooseTransactionToRemoveInterface, 500, 500);
    }

    public void setWatchlistInterface(){
        watchlistInterface.setPadding(new Insets(10, 10, 10, 10));
        watchlistInterface.setVgap(5);
        watchlistInterface.setHgap(5);

        GridPane.setConstraints(backToMenu, 0, 9);
        GridPane.setConstraints(buttonAddToWatchlist, 0, 11);

        watchlistInterface.getChildren().addAll(backToMenu, buttonAddToWatchlist);
        watchList = new Scene(watchlistInterface, 500, 500);
        watchList.getStylesheets().add("Themes.css");
    }

    public void setAddToWatchlistInterface(){
        chooseOneToAdd.setEditable(true);
        addToWatchlistInterface.setPadding(new Insets(10, 10, 10, 10));
        addToWatchlistInterface.setVgap(10);
        addToWatchlistInterface.setHgap(10);

        GridPane.setConstraints(chooseOneToAdd, 1, 4);
        GridPane.setConstraints(buttonSearchToAdd, 1, 5);
        GridPane.setConstraints(buttonConfirmToAdd, 1, 6);
        GridPane.setConstraints(backToWatchlist, 1, 7);

        addToWatchlistInterface.getChildren().addAll(chooseOneToAdd, buttonConfirmToAdd, buttonSearchToAdd, backToWatchlist);
        addToWatchlistPage = new Scene(addToWatchlistInterface,  500, 500);
    }

    public void setHistoryTransactionsInterface(){
        historyTransactionInterface.setPadding(new Insets(10, 10, 10, 10));
        historyTransactionInterface.setVgap(5);
        historyTransactionInterface.setHgap(5);

        GridPane.setConstraints(backToMenu2, 0, 9);
        GridPane.setConstraints(cancelPendingTransaction, 0, 10);

        historyTransactionInterface.getChildren().addAll(backToMenu2, cancelPendingTransaction);
        historyTransactions = new Scene(historyTransactionInterface, 500, 500);
    }

    public void setSellInterface(){
        Label shareNameInput = new Label("please choose one share: ");
        Label enterPrice = new Label("please enter the price: ");
        Label enterAmount = new Label("please enter an amount: ");
        sellInterface.setPadding(new Insets(20, 20, 20, 20));
        sellInterface.setVgap(17);
        sellInterface.setHgap(17);

        ArrayList<StockShare> holdingShares = null;

        try {
            holdingShares = connection.getALLStockShares();
            holdingShares.forEach(e->nameOfShare.getItems().add(e.stock_name));
        } catch (Exception e) {
            e.printStackTrace();
        }

        GridPane.setConstraints(shareNameInput, 1, 3);
        GridPane.setConstraints(nameOfShare, 3, 3);

        if (holdingShares.size()>0){
            nameOfShare.setValue(holdingShares.get(0).stock_name);
        }

        GridPane.setConstraints(enterPrice, 1, 4);
        GridPane.setConstraints(price, 3, 4);

        GridPane.setConstraints(enterAmount, 1, 5);
        GridPane.setConstraints(amount, 3, 5);

        GridPane.setConstraints(confirmToSell, 3, 6);
        GridPane.setConstraints(backToMenu1, 3, 8);

        sellInterface.getChildren().addAll(shareNameInput, nameOfShare, enterPrice, price, enterAmount, amount, confirmToSell, backToMenu1);
        sellEntrance = new Scene(sellInterface, 500, 500);
        sellEntrance.getStylesheets().add("SellAndBuy.css");
    }

    public void setBuyInterface(){
        Label chooseOneShare = new Label("please choose one available share: ");
        Label enterPrice = new Label("please enter the price: ");
        Label enterAmount = new Label("please enter an amount: ");
        buyInterface.setPadding(new Insets(20, 20, 20, 20));
        buyInterface.setVgap(17);
        buyInterface.setHgap(17);

        GridPane.setConstraints(chooseOneShare, 1, 3);
        GridPane.setConstraints(chooseOneShareToBuy, 3, 3);

        GridPane.setConstraints(enterPrice, 1, 4);
        GridPane.setConstraints(priceToBuy, 3, 4);

        GridPane.setConstraints(enterAmount, 1, 5);
        GridPane.setConstraints(amountToBuy, 3, 5);

        GridPane.setConstraints(confirmToBuy, 3, 6);
        GridPane.setConstraints(backToMenu3, 3, 8);

        buyInterface.getChildren().addAll(chooseOneShare, chooseOneShareToBuy, enterPrice, priceToBuy, enterAmount, amountToBuy, confirmToBuy, backToMenu3);
        buyEntrance = new Scene(buyInterface, 500, 500);
        buyEntrance.getStylesheets().add("SellAndBuy.css");
    }

    public void setWindow(Scene scene){
        window.setScene(scene);
        window.show();
    }

    public void updateWatchlist(){
        try{
            ArrayList<Share> currentWatchlist = connection.getAllWatchlistInfo(true);
            ArrayList<String> view = new ArrayList<>();

            view.add("stock code\t\t\t\tchange in percentage\t\tstock name");
            currentWatchlist.forEach(e->{
                view.add(e.stock_code+"\t\t\t\t\t"+e.change_percentage+"\t\t\t\t\t"+e.stock_name);
            });

            ObservableList<String> observables = FXCollections.observableArrayList(view);
            ListView<String> infos = new ListView<>(observables);
            infos.setOrientation(Orientation.VERTICAL);
            infos.setPrefSize(400, 400);
            watchlistInterface.getChildren().add(infos);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void search(){
        try {
            ArrayList<Share> allShares = GetShareInfo.getShareRE(connection.account, chooseOneToAdd.getValue());
            ArrayList<String> name = new ArrayList<>();
            allShares.forEach(e->name.add(e.stock_name));
            ObservableList<String> infos = FXCollections.observableArrayList(name);
            chooseOneToAdd.setItems(infos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTransactions(){
        try {
            ArrayList<Transaction> historyTransactions = connection.getAllTransactions();
            ArrayList<String> view = new ArrayList<>();

            historyTransactions.forEach(t->{
                view.add(t.name+"\t\t\t\t\t"+t.time+"\t\t\t\t\t"+t.status_string+"\t\t\t\t"+t.amount+"\t\t\t\t"+t.transactionPrimaryKey);
            });

            ObservableList<String> observables = FXCollections.observableArrayList(view);
            ListView<String> infos = new ListView<>(observables);
            infos.setOrientation(Orientation.VERTICAL);
            infos.setPrefSize(400, 400);
            historyTransactionInterface.getChildren().add(infos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(ActionEvent event) {
        if (event.getSource() == login){
            verify(this.username, this.password);
        }

        else if (event.getSource() == quit){
            window.close();
        }

        else if (event.getSource() == viewWatchlist || event.getSource() == backToWatchlist) {
            updateWatchlist();
            setWindow(watchList);
        }

        else if (event.getSource() == buttonViewAllTransactions){
            updateTransactions();
            setWindow(historyTransactions);
        }

        else if (event.getSource() == sell){
            setWindow(sellEntrance);
        }

        else if (event.getSource() == buy){
            setWindow(buyEntrance);
        }

        else if (event.getSource() == register){
            setWindow(newUserRegister);
        }

        else if (event.getSource() == confirmToSell){
            while (true){
                try {
                    connection.sellShares(this.nameOfShare.getValue(), Double.parseDouble(this.price.getText()), Integer.parseInt(this.amount.getText()));
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
        }

        else if (event.getSource() == confirmToBuy){
            while (true){
                try {
                    connection.buyShares(this.chooseOneShareToBuy.getText(), Double.parseDouble(this.priceToBuy.getText()), Integer.parseInt(this.amountToBuy.getText()));
                    break;
                } catch (Exception e) {
                    continue;
                }
            }
        }

        else if (event.getSource() == confirmToRegister){
            if (!createNewUser.getText().trim().isEmpty() && !createPassword.getText().trim().isEmpty() && !initialBal.getText().trim().isEmpty() && !confirmPassword.getText().trim().isEmpty()){

                try {
                    connection.registration(createNewUser.getText(), createPassword.getText(), Double.parseDouble(initialBal.getText()));
                    setWindow(loginPage);
                } catch (SQLException throwables) {
                    
                }
            }
        }

        else if (event.getSource() == buttonAddToWatchlist){
            setWindow(addToWatchlistPage);
        }

        else if (event.getSource() == backToMenu || event.getSource() == backToMenu1 || event.getSource() == backToMenu2 || event.getSource() == backToMenu3){
            setWindow(yourAccount);
        }

        else if (event.getSource() == cancelPendingTransaction){
            this.updateTransactions();
            setWindow(removeTransactionPage);
        }

        else if (event.getSource() == confirmToRemoveTransaction){
            try{
                connection.TransactionOperation(chooseOnePendingTransaction.getValue(), Status.CANCEL);
                System.out.println("successfully removed!");
            } catch(Exception e){
                System.out.println("unable to operate:(");
            }
        }

        else if (event.getSource() == buttonBackToTransaction){
            this.updateTransactions();
            setWindow(historyTransactions);
        }

        else if (event.getSource() == buttonSearchToAdd){
            this.search();
            chooseOneToAdd.show();
        }

        else if (event.getSource() == buttonConfirmToAdd){
            if (!chooseOneToAdd.getValue().trim().isEmpty()){
                try {
                    connection.addWatchList(chooseOneToAdd.getValue());
                    System.out.println("successfully added :D");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

//        else if (event.getSource() == backToWatchlist){
//            this.updateWatchlist();
//            setWindow(watchList);
//        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
