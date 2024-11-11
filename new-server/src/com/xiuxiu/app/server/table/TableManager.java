package com.xiuxiu.app.server.table;

public class TableManager {
    private static class TableManagerHolder {
        private static TableManager instance = new TableManager();
    }

    public static TableManager I = TableManagerHolder.instance;

    private DiamondCost diamondCost = new DiamondCost();
    private CowMultiple cowMultiple = new CowMultiple();
    private TbMail tbMail = new TbMail();
    private TbKWXFang tbKWXFang = new TbKWXFang();
    private TbHundredCowMultiple tbHundredCowMultiple = new TbHundredCowMultiple();
    private TbPai9 tbPai9 = new TbPai9();
    private SharkMultiple sharkMultiple = new SharkMultiple();
    private TbGameInfo gameInfo = new TbGameInfo();
    private TbWHMJStartHu whmjStartHu = new TbWHMJStartHu();
    private GoodPoker goodPoker = new GoodPoker();

    private TableManager() {
    }

    public void loadAll() {
        this.diamondCost.read("cnf/table");
        DiamondCostManager.I.init(this.diamondCost);
        this.cowMultiple.read("cnf/table");
        CowMultipleManager.I.init(this.cowMultiple);
        this.tbMail.read("cnf/table");
        TbMailManager.I.init(this.tbMail);
        this.tbKWXFang.read("cnf/table");
        TbKWXFangManager.I.init(this.tbKWXFang);
        this.tbHundredCowMultiple.read("cnf/table");
        TbHundredCowMultipleManager.I.init(this.tbHundredCowMultiple);
        this.tbPai9.read("cnf/table");
        TbPai9Manager.I.init(this.tbPai9);
        this.sharkMultiple.read("cnf/table");
        SharkMultipleManager.I.init(this.sharkMultiple);
        this.gameInfo.read("cnf/table");
        TbGameInfoManager.I.init(this.gameInfo);
        this.whmjStartHu.read("cnf/table");
        TbWHMJStartHuManager.I.init(this.whmjStartHu);
        this.goodPoker.read("cnf/table");
        GoodPokerManager.I.init(this.goodPoker);
    }
}
