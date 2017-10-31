package net.zomis.spring.games.generic.v2;

public class InternalActionResult {

    private final boolean ok;
    private final String status;
    private final Object resultData;

    public InternalActionResult(boolean ok, String status) {
        this(ok, status, null);
    }

    public InternalActionResult(boolean ok, String status, Object resultData) {
        this.ok = ok;
        this.status = status;
        this.resultData = resultData;
    }

    public ActionResult toActionResult() {
        return new ActionResult(ok, status);
    }

    public boolean isOk() {
        return ok;
    }

    public String getStatus() {
        return status;
    }

    public Object getResultData() {
        return resultData;
    }

    @Override
    public String toString() {
        return "InternalActionResult{" +
                "ok=" + ok +
                ", status='" + status + '\'' +
                ", resultData=" + resultData +
                '}';
    }
}
