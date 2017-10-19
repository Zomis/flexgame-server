package net.zomis.spring.games.generic.v2;

public class ActionResult {

    private final boolean ok;
    private final String status;

    public ActionResult(boolean ok, String status) {
        this.ok = ok;
        this.status = status;
    }

    public boolean isOk() {
        return ok;
    }

    public String getStatus() {
        return status;
    }

}
