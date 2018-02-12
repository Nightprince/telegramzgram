package edit;

public class AlarmResponse {
    private Integer displayCount;
    private Boolean exitOnDismiss;
    private Long id;
    private String imageUrl;
    private String message;
    private String negativeBtnAction;
    private String negativeBtnText;
    private String negativeBtnUrl;
    private String positiveBtnAction;
    private String positiveBtnText;
    private String positiveBtnUrl;
    private Integer showCount;
    private Integer targetNetwork;
    private Integer targetVersion;
    private String title;

    public AlarmResponse(Long var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, Integer var11, Boolean var12, Integer var13, Integer var14, Integer var15) {
        this.id = var1;
        this.title = var2;
        this.message = var3;
        this.imageUrl = var4;
        this.positiveBtnText = var5;
        this.positiveBtnAction = var6;
        this.positiveBtnUrl = var7;
        this.negativeBtnText = var8;
        this.negativeBtnAction = var9;
        this.negativeBtnUrl = var10;
        this.showCount = var11;
        this.exitOnDismiss = var12;
        this.targetNetwork = var13;
        this.displayCount = var14;
        this.targetVersion = var15;
    }

    public Integer getDisplayCount() {
        return this.displayCount;
    }

    public Boolean getExitOnDismiss() {
        return this.exitOnDismiss;
    }

    public Long getId() {
        return this.id;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getMessage() {
        return this.message;
    }

    public String getNegativeBtnAction() {
        return this.negativeBtnAction;
    }

    public String getNegativeBtnText() {
        return this.negativeBtnText;
    }

    public String getNegativeBtnUrl() {
        return this.negativeBtnUrl;
    }

    public String getPositiveBtnAction() {
        return this.positiveBtnAction;
    }

    public String getPositiveBtnText() {
        return this.positiveBtnText;
    }

    public String getPositiveBtnUrl() {
        return this.positiveBtnUrl;
    }

    public Integer getShowCount() {
        return this.showCount;
    }

    public Integer getTargetNetwork() {
        return this.targetNetwork;
    }

    public Integer getTargetVersion() {
        return this.targetVersion;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDisplayCount(Integer var1) {
        this.displayCount = var1;
    }

    public void setExitOnDismiss(Boolean var1) {
        this.exitOnDismiss = var1;
    }

    public void setId(Long var1) {
        this.id = var1;
    }

    public void setImageUrl(String var1) {
        this.imageUrl = var1;
    }

    public void setMessage(String var1) {
        this.message = var1;
    }

    public void setNegativeBtnAction(String var1) {
        this.negativeBtnAction = var1;
    }

    public void setNegativeBtnText(String var1) {
        this.negativeBtnText = var1;
    }

    public void setNegativeBtnUrl(String var1) {
        this.negativeBtnUrl = var1;
    }

    public void setPositiveBtnAction(String var1) {
        this.positiveBtnAction = var1;
    }

    public void setPositiveBtnText(String var1) {
        this.positiveBtnText = var1;
    }

    public void setPositiveBtnUrl(String var1) {
        this.positiveBtnUrl = var1;
    }

    public void setShowCount(Integer var1) {
        this.showCount = var1;
    }

    public void setTargetNetwork(Integer var1) {
        this.targetNetwork = var1;
    }

    public void setTargetVersion(Integer var1) {
        this.targetVersion = var1;
    }

    public void setTitle(String var1) {
        this.title = var1;
    }
}
