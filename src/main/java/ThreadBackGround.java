public class ThreadBackGround implements Runnable{
    ServerInteract serverInteract;
    ThreadBackGround(ServerInteract serverInteract){
        this.serverInteract = serverInteract;
    }
    @Override
    public void run() {
        try {
            serverInteract.transactionBackGroundRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
